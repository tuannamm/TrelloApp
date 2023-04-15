package eu.tutorials.trelloapp.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView

import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.trelloapp.R
import eu.tutorials.trelloapp.databinding.ActivityMainBinding
import eu.tutorials.trelloapp.firebase.FireStoreClass
import eu.tutorials.trelloapp.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val BOARD_MEMBERS_REQUEST_CODE: Int = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)

        FireStoreClass().loadUserData(this)
    }

    // A function for actionBar Setup.
    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    // A function for actionBarDrawerToggle to open/close the drawer.
    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).dontAnimate()
            .into(binding.navView.findViewById(R.id.nav_user_image))

        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text = user.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireStoreClass().loadUserData(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}