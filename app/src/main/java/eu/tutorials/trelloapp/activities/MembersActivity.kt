package eu.tutorials.trelloapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call.Details
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.ActivityMembersBinding
import eu.tutorials.trelloapp.models.Board
import eu.tutorials.trelloapp.utils.Constants

class MembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.DOCUMENT_ID)!!
        }

        setupActionBar()
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
}