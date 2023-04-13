package eu.tutorials.trelloapp.firebase

import android.media.session.MediaSessionManager.RemoteUserInfo
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import eu.tutorials.trelloapp.activities.BaseActivity
import eu.tutorials.trelloapp.activities.SignInActivity
import eu.tutorials.trelloapp.activities.SignUpActivity
import eu.tutorials.trelloapp.models.User
import eu.tutorials.trelloapp.utils.Constants

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Sign Up", "Error while registering the user.", e)
            }
    }

    fun signInUser(activity: SignInActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                activity.signInSuccess(loggedInUser)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Sign In", "Error while signing in the user.", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}