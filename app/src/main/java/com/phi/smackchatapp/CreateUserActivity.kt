package com.phi.smackchatapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.phi.smackchatapp.databinding.ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun generateUserAvatar(view: View) {

    }

    fun generateColorClicked(view: View) {

    }

    fun createUserClicked(view: View) {

    }
}