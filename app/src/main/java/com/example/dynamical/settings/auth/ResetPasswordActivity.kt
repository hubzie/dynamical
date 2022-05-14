package com.example.dynamical.settings.auth

import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamical.R
import com.example.dynamical.databinding.ResetPasswordActivityBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ResetPasswordActivityBinding

    private fun disableView() {
        binding.apply {
            confirmButton.visibility = View.GONE
            confirmProgress.visibility = View.VISIBLE

            email.isFocusable = false
            email.isEnabled = false
        }
    }

    private fun enableView() {
        binding.apply {
            confirmButton.visibility = View.VISIBLE
            confirmProgress.visibility = View.GONE

            email.isFocusable = true
            email.isFocusableInTouchMode = true
            email.isEnabled = true
        }
    }

    private fun resetPasswordRequest() {
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text).matches())
            binding.email.error = getString(R.string.incorrect_email_error)
        else {
            disableView()
            Firebase.auth.sendPasswordResetEmail(binding.email.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(applicationContext, R.string.email_sent, Toast.LENGTH_LONG).show()
                        finish()
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

        binding = ResetPasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmButton.setOnClickListener { resetPasswordRequest() }

        setTitle(R.string.reset_password_title)
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