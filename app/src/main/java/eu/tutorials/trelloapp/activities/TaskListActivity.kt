package eu.tutorials.trelloapp.activities

import android.os.Bundle
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.ActivityTaskListBinding
import eu.tutorials.trelloapp.firebase.FireStoreClass
import eu.tutorials.trelloapp.models.Board
import eu.tutorials.trelloapp.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, boardDocumentId)

    }

    private fun setupActionBar(title: String) {
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }
}