package com.example.dynamical.settings.auth

import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamical.R
import com.example.dynamical.databinding.ModifyUserAcitivityBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class ModifyUserActivity : AppCompatActivity() {
    private lateinit var binding: ModifyUserAcitivityBinding

    private fun disableView() {
        binding.apply {
            confirmButton.visibility = View.GONE
            confirmProgress.visibility = View.VISIBLE

            listOf(name, email, oldPassword, newPassword, confirmNewPassword)
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

            listOf(name, email, oldPassword, newPassword, confirmNewPassword)
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

        if (binding.oldPassword.text.isEmpty()) {
            binding.oldPassword.error = getString(R.string.missing_password_error)
            correct = false
        }

        if (binding.newPassword.text.toString() != binding.confirmNewPassword.text.toString()) {
            binding.confirmNewPassword.error = getString(R.string.password_not_match_error)
            correct = false
        }

        if (correct) {
            disableView()
            val user = Firebase.auth.currentUser!!

            val name = binding.name.text.toString()
            val oldEmail = user.email!!
            val email = binding.email.text.toString()
            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            val profile = userProfileChangeRequest {
                displayName = name
            }

            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { task_1 ->
                if (task_1.isSuccessful) { // Old password confirmed
                    user.updateEmail(email).addOnCompleteListener { task_2 ->
                        if (task_2.isSuccessful) { // E-mail changed
                            if(newPassword.isEmpty()) {
                                user.updateProfile(profile)
                                    .addOnCompleteListener { finish() }
                            } else {
                                user.updatePassword(newPassword).addOnCompleteListener { task_3 ->
                                    if (task_3.isSuccessful) { // Password changed
                                        user.updateProfile(profile)
                                            .addOnCompleteListener { finish() } // Name changed
                                    } else { // Password change fail
                                        task_3.exception?.localizedMessage.let {
                                            Toast.makeText(
                                                applicationContext,
                                                it,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        // Rollback
                                        user.updateEmail(oldEmail)
                                        enableView()
                                    }
                                }
                            }
                        } else { // Email change failed
                            task_2.exception?.localizedMessage.let {
                                Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
                            }
                            enableView()
                        }
                    }
                } else {
                    binding.oldPassword.error = getString(R.string.wrong_password)
                    enableView()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ModifyUserAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmButton.setOnClickListener { signUpRequest() }

        binding.name.setText(Firebase.auth.currentUser!!.displayName)
        binding.email.setText(Firebase.auth.currentUser!!.email)

        setTitle(R.string.modify_title)
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