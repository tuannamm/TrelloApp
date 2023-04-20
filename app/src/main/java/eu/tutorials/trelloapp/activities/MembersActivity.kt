package eu.tutorials.trelloapp.activities


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.trelloapp.R

import eu.tutorials.trelloapp.adapters.MemberListItemsAdapter
import eu.tutorials.trelloapp.databinding.ActivityMembersBinding
import eu.tutorials.trelloapp.firebase.FireStoreClass
import eu.tutorials.trelloapp.models.Board
import eu.tutorials.trelloapp.models.User
import eu.tutorials.trelloapp.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
//            mBoardDetails = intent.getStringArrayListExtra(Constants.DOCUMENT_ID)!! as Board
//            showProgressDialog(resources.getString(R.string.please_wait))
//            FireStoreClass().getAssignedMembersListDetails(this, intent.getStringExtra(Constants.DOCUMENT_ID)!!)

        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    fun setupMembersList(list: ArrayList<User>) {

        mAssignedMembersList = list
        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }

    fun memberDetails(user: User) {
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }
        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_member -> {
                dialogForSearchMember()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dialogForSearchMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener() {
            val email = dialog.findViewById<TextView>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)
            } else {
                Toast.makeText(this, "Please enter a email address", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setupMembersList(mAssignedMembersList)
    }


}