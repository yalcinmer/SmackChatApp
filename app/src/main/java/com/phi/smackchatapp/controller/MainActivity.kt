package com.phi.smackchatapp.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phi.smackchatapp.R
import com.phi.smackchatapp.databinding.ActivityMainBinding
import com.phi.smackchatapp.service.AuthService
import com.phi.smackchatapp.service.UserDataService
import com.phi.smackchatapp.utilities.BROADCAST_USER_DATA_CHANGE

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn) {
                binding.navDrawerHeaderInclude.usernameNavHeader.text = UserDataService.name
                binding.navDrawerHeaderInclude.userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                binding.navDrawerHeaderInclude.userImageNavHeader.setImageResource(resourceId)
                binding.navDrawerHeaderInclude.userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                binding.navDrawerHeaderInclude.loginButtonNavHeader.text = "LOGOUT"
            }
        }
    }

    fun loginButtonNavClicked(view: View) {

        if(AuthService.isLoggedIn) {
            UserDataService.logout()
            binding.navDrawerHeaderInclude.usernameNavHeader.text = ""
            binding.navDrawerHeaderInclude.userEmailNavHeader.text = ""
            binding.navDrawerHeaderInclude.userImageNavHeader.setImageResource(R.drawable.profiledefault)
            binding.navDrawerHeaderInclude.userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            binding.navDrawerHeaderInclude.loginButtonNavHeader.text = "LOGIN"
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View) {

    }

    fun sendMessageButtonClicked(view: View) {

    }
}