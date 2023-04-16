package eu.tutorials.trelloapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.ActivityMyProfileBinding
import eu.tutorials.trelloapp.firebase.FireStoreClass
import eu.tutorials.trelloapp.models.User
import eu.tutorials.trelloapp.utils.Constants

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FireStoreClass().loadUserData(this)

        binding.ivProfileUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
               Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser()
                }
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun showImageChooser() {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null) {
            mSelectedImageFileUri = data.data!!
            try {
                        Glide
                            .with(this@MyProfileActivity)
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(binding.ivProfileUserImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        private fun setupActionBar() {
            setSupportActionBar(binding.toolbarMyProfileActivity)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = resources.getString(R.string.my_profile_title)
            }
            binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
        }

        fun setUserDataInUI(user: User) {
            mUserDetails = user
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)

            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            if (user.mobile != 0L) { // long value
                binding.etMobile.setText(user.mobile.toString())
            }
        }

        private fun updateUserProfileData() {
            val userHashMap = HashMap<String, Any>()

            if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
                userHashMap[Constants.IMAGE] = mProfileImageURL
            }

            if (binding.etName.text.toString() != mUserDetails.name) {
                userHashMap[Constants.NAME] = binding.etName.text.toString()
            }

            if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
                userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
            }


            FireStoreClass().updateUserProfileData(this, userHashMap)

        }

        private fun uploadUserImage() {
            showProgressDialog(resources.getString(R.string.please_wait))
            if (mSelectedImageFileUri != null) {
                val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri!!)
                )
                sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                    Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                        Log.i("Downloadable Image URL", uri.toString())
                        hideProgressDialog()
                        mProfileImageURL = uri.toString()
                        updateUserProfileData()
                        Toast.makeText(this@MyProfileActivity, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    exception ->
                    hideProgressDialog()
                    Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun profileUpdateSuccess() {
            hideProgressDialog()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
