package com.phi.smackchatapp.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.phi.smackchatapp.databinding.ActivityLoginBinding
import com.phi.smackchatapp.service.AuthService

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun loginButtonClicked(view: View) {
        val email = binding.loginEmailText.text.toString()
        val password = binding.loginPasswordText.text.toString()

        AuthService.loginUser(this, email, password) { success ->
            if(success) {
                AuthService.findUserByEmail(this) { findSuccess ->
                    if(findSuccess) {
                        finish()
                    }
                }
            }
        }
    }

    fun loginCreateUserButtonClicked(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}