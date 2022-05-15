package com.example.dynamical.settings.auth

import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.dynamical.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthView private constructor(
    val view: View,
    private val onSuccessCallback: () -> Unit
) {
    private val emailTextView: TextView = view.findViewById(R.id.sign_in_email)
    private val passwordTextView: TextView = view.findViewById(R.id.sign_in_password)
    private val progressBar: ProgressBar = view.findViewById(R.id.sign_in_progress)

    private val signInButton: Button = view.findViewById(R.id.sign_in_button)
    private val signUpButton: Button = view.findViewById(R.id.sign_up_button)
    private val resetPasswordButton: Button = view.findViewById(R.id.reset_password_button)

    private val auth = Firebase.auth

    private val email: String get() = emailTextView.text.toString()
    private val password: String get() = passwordTextView.text.toString()

    init {
        signInButton.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(view.context, R.string.incorrect_email_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(view.context, R.string.missing_password_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            disableView()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) onSignInSuccessful()
                    else onSignInFailure()
                }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(view.context, SignUpActivity::class.java)
            view.context.startActivity(intent)
        }

        resetPasswordButton.setOnClickListener {
            val intent = Intent(view.context, ResetPasswordActivity::class.java)
            view.context.startActivity(intent)
        }
    }

    fun clear() {
        enableView()
        emailTextView.text = ""
        passwordTextView.text = ""
    }

    private fun enableView() {
        signInButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        signUpButton.visibility = View.VISIBLE
        resetPasswordButton.visibility = View.VISIBLE

        emailTextView.isFocusable = true
        emailTextView.isFocusableInTouchMode = true
        emailTextView.isEnabled = true
        passwordTextView.isFocusable = true
        passwordTextView.isFocusableInTouchMode = true
        passwordTextView.isEnabled = true
    }

    private fun disableView() {
        signInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        signUpButton.visibility = View.GONE
        resetPasswordButton.visibility = View.GONE

        emailTextView.isFocusable = false
        emailTextView.isEnabled = false
        passwordTextView.isFocusable = false
        passwordTextView.isEnabled = false
    }

    private fun onSignInSuccessful() = onSuccessCallback()

    private fun onSignInFailure() {
        Toast.makeText(view.context, R.string.invalid_credentials_error, Toast.LENGTH_SHORT).show()
        enableView()
    }

    companion object {
        fun createAuthView(parent: ViewGroup, onSuccessCallback: () -> Unit): AuthView {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.auth_view, parent, false)
            return AuthView(view, onSuccessCallback)
        }
    }
}