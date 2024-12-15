package com.phi.smackchatapp.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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

        binding.loginSpinner.visibility = View.INVISIBLE
    }

    fun loginButtonClicked(view: View) {
        enableSpinner(true)
        val email = binding.loginEmailText.text.toString()
        val password = binding.loginPasswordText.text.toString()

        hideKeyboard()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(email, password) { success ->
                if (success) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in both email and password!", Toast.LENGTH_LONG).show()
        }
    }

    fun loginCreateUserButtonClicked(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(isEnabled: Boolean) {

        if(isEnabled) {
            binding.loginSpinner.visibility = View.VISIBLE
        } else {
            binding.loginSpinner.visibility = View.INVISIBLE
        }

        binding.loginButton.isEnabled = !isEnabled
        binding.loginCreateUserButton.isEnabled = !isEnabled
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}