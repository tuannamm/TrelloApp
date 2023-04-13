package eu.tutorials.trelloapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            Toast.makeText(this@SignUpActivity, "You have successfully registered.", Toast.LENGTH_SHORT).show()
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        Toast.makeText(this@SignUpActivity, "$name have successfully registered the email $registeredEmail.", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    } else {
                        Toast.makeText(this@SignUpActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // validate the entries of a new user
    private fun validateForm(name: String, email: String, password: String) : Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password.")
                false
            }
            else -> {
                true
            }
        }

    }}