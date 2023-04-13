package eu.tutorials.trelloapp.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.DialogProgressBinding

open class BaseActivity : AppCompatActivity() {


    private var doubleBackToExitPressedOnce = false

    // A global variable for progress dialog instance
    private lateinit var mProgressDialog: Dialog

    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        // Set the screen content from a layout resource.
        // The resource will be inflated, adding all top-level views to the screen.
        mProgressDialog.setContentView(R.layout.dialog_progress)


        mProgressDialog.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_progress_text).text = text

        // Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    // show progress dialog with default text
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    // get the current user id
    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    // press back button twice to exit the app
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        // Show the toast message.
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        // Handler to make the toast message display for the second time.
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce= false
        }, 2000)

        // user did not press back button twice for a while, reset anythings
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce= false
        }, 2000)
    }

    // function to launch the sign in activity
    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackBar.show()
    }



}