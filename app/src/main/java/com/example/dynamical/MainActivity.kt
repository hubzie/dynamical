package com.example.dynamical

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dynamical.databinding.MainActivityBinding
import com.example.dynamical.mesure.Tracker

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainView =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navigationBar = binding.navigationBar
        navigationBar.setupWithNavController(mainView.navController)
    }

    val resultLauncher = registerForActivityResult(StartIntentSenderForResult()) { _ ->
        Tracker.getTracker(application).forceStart()
    }
}