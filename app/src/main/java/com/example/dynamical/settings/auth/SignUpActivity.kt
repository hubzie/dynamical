package com.example.dynamical.settings.auth

import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamical.R
import com.example.dynamical.databinding.SignUpActivityBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignUpActivityBinding

    private fun disableView() {
        binding.apply {
            confirmButton.visibility = View.GONE
            confirmProgress.visibility = View.VISIBLE

            listOf(name, email, password, confirmPassword)
                .forEach { box ->
                    box.isFocusable = false
                    box.isEnabled = false
                }
        }
    }

    private fun enableView() {
        binding.apply {
            confirmButton.visibility = View.VISIBLE
            confirmProgress.visibility = View.GONE

            listOf(name, email, password, confirmPassword)
                .forEach { box ->
                    box.isFocusable = true
                    box.isFocusableInTouchMode = true
                    box.isEnabled = true
                }
        }
    }

    private fun signUpRequest() {
        var correct = true

        if (binding.name.text.isEmpty()) {
            binding.name.error = getString(R.string.empty_name_error)
            correct = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text).matches()) {
            binding.email.error = getString(R.string.incorrect_email_error)
            correct = false
        }

        if (binding.password.text.isEmpty()) {
            binding.password.error = getString(R.string.missing_password_error)
            correct = false
        }

        if (binding.password.text.toString() != binding.confirmPassword.text.toString()) {
            binding.confirmPassword.error = getString(R.string.password_not_match_error)
            correct = false
        }

        if (correct) {
            disableView()
            Firebase.auth.createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        user!!.updateProfile(userProfileChangeRequest {
                            displayName = binding.name.text.toString()
                        }).addOnCompleteListener { finish() }
                    } else {
                        task.exception?.localizedMessage.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
                        }
                        enableView()
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmButton.setOnClickListener { signUpRequest() }

        setTitle(R.string.sign_up_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}