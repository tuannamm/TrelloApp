package eu.tutorials.trelloapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import eu.tutorials.trelloapp.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Click event for Sign Up button
        binding.btnSignUpIntro.setOnClickListener {
            val intent = Intent(this@IntroActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Click event for Sign In button
        binding.btnSignInIntro.setOnClickListener{
            val intent = Intent(this@IntroActivity, SignInActivity::class.java)
            startActivity(intent)
        }
        
    }


}