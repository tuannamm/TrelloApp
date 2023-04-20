package eu.tutorials.trelloapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.adapters.CardMemberListsAdapter
import eu.tutorials.trelloapp.databinding.ActivityCardDetailsBinding
import eu.tutorials.trelloapp.dialogs.LabelColorListDialog
import eu.tutorials.trelloapp.dialogs.MembersListDialog
import eu.tutorials.trelloapp.firebase.FireStoreClass
import eu.tutorials.trelloapp.models.*
import eu.tutorials.trelloapp.utils.Constants
import kotlinx.coroutines.selects.select

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        setupActionBar()

//        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)

//        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        binding.btnUpdateCardDetails.setOnClickListener {
            if (binding.etNameCardDetails.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please enter a card name.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()
    }

    private fun colorList(): ArrayList<String> {
        val colorList: ArrayList<String> = ArrayList()
        colorList.add("#000000")
        colorList.add("#FF0000")
        colorList.add("#FF7F00")
        colorList.add("#FFFF00")
        colorList.add("#00FF00")
        colorList.add("#0000FF")
        colorList.add("#4B0082")
        colorList.add("#9400D3")
        return colorList
    }

    private fun setColor() {
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Card Details"
//            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
//            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            Toast.makeText(this, "Board details found.", Toast.LENGTH_SHORT).show()
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList =
                intent.getParcelableArrayListExtra(Constants.CARD_LIST_ITEM_POSITION)!!
        }
    }

    private fun membersListDialog() {
        var cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member),
        ) {
            override fun onItemSelected(user: User, action: String) {
               if(action == Constants.SELECT) {
                   if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)) {
                       mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                   }
               } else {
                   mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                   for (i in mMembersDetailList.indices) {
                       if(mMembersDetailList[i].id == user.id) {
                           mMembersDetailList[i].selected = false
                       }
                   }
               }
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun updateCardDetails() {
        val card = Card(
            binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    @SuppressLint("StringFormatInvalid")
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorList()

        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMembers = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMembers)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding.rvSelectedMembersList.visibility = View.GONE
            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListsAdapter(this, selectedMembersList, true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListsAdapter.OnClickListener {
                    override fun onClick(position: Int, model: SelectedMembers) {
                        membersListDialog()
                    }
                }
            )
        } else {
            binding.rvSelectedMembersList.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }
}