package com.phi.smackchatapp.controller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phi.smackchatapp.databinding.ActivityCreateUserBinding
import com.phi.smackchatapp.service.AuthService
import com.phi.smackchatapp.utilities.BROADCAST_USER_DATA_CHANGE
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    private lateinit var binding: ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.INVISIBLE
    }

    @SuppressLint("DiscouragedApi")
    fun generateUserAvatar(view: View) {
        val random = Random
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = if(color == 0) { "light$avatar" } else { "dark$avatar" }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        binding.createAvatarImageView.setImageResource(resourceId)
    }

    fun generateColorClicked(view: View) {

        val random = Random
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        binding.createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))
        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserClicked(view: View) {

        val username = binding.createUserNameText.toString()
        val email = binding.createEmailText.toString()
        val password = binding.createPasswordText.toString()

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(email, password) { registerSuccess ->
                if(registerSuccess) {
                    AuthService.loginUser(email, password) { loginSuccess ->
                        if(loginSuccess) {
                            AuthService.createUser(username, email, userAvatar, avatarColor) { createSuccess ->
                                if(createSuccess) {
                                    val intent = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                                    enableSpinner(false)
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
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Make sure username, email and password are filled in.", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(isEnabled: Boolean) {

        if(isEnabled) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }

        binding.createUserButton.isEnabled = !isEnabled
        binding.createAvatarImageView.isEnabled = !isEnabled
        binding.backgroundColorButton.isEnabled = !isEnabled
    }
}