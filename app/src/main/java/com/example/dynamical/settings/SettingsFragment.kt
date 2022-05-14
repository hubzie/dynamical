package com.example.dynamical.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dynamical.R
import com.example.dynamical.databinding.SettingsFragmentBinding
import com.example.dynamical.settings.auth.AuthView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment(R.layout.settings_fragment) {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private val auth = Firebase.auth
    private var _authView: AuthView? = null
    private val authView get() = _authView!!

    private fun setUserSignedIn() {
        binding.userInfoBox.visibility = View.VISIBLE
        binding.signInBox.visibility = View.GONE

        auth.currentUser?.let { user ->
            binding.userName.text =
                if(user.displayName?.isNotEmpty() == true) user.displayName
                else user.email
        }
    }

    private fun setUserSignedOut() {
        binding.userInfoBox.visibility = View.GONE
        binding.signInBox.visibility = View.VISIBLE
        authView.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)

        _authView = AuthView.createAuthView(binding.signInBox) { setUserSignedIn() }
        binding.signInBox.addView(authView.view)
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            setUserSignedOut()
        }

        if (Firebase.auth.currentUser == null) setUserSignedOut()
        else setUserSignedIn()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _authView = null
    }
}