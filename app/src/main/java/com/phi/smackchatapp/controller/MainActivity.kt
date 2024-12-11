package com.phi.smackchatapp.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phi.smackchatapp.R
import com.phi.smackchatapp.databinding.ActivityMainBinding
import com.phi.smackchatapp.model.Channel
import com.phi.smackchatapp.service.AuthService
import com.phi.smackchatapp.service.MessageService
import com.phi.smackchatapp.service.UserDataService
import com.phi.smackchatapp.utilities.BROADCAST_USER_DATA_CHANGE
import com.phi.smackchatapp.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    private lateinit var binding: ActivityMainBinding
    private lateinit var channelAdapter : ArrayAdapter<Channel>

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

        socket.connect()
        socket.on("channelCreated", onNewChannel)

        setupAdapters()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        binding.channelList.adapter = channelAdapter
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if(App.prefs.isLoggedIn) {
                binding.navDrawerHeaderInclude.usernameNavHeader.text = UserDataService.name
                binding.navDrawerHeaderInclude.userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                binding.navDrawerHeaderInclude.userImageNavHeader.setImageResource(resourceId)
                binding.navDrawerHeaderInclude.userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                binding.navDrawerHeaderInclude.loginButtonNavHeader.text = "LOGOUT"

                if (context != null) {
                    MessageService.getChannels(context) { complete ->
                        if(complete) {
                            channelAdapter.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
    }

    fun loginButtonNavClicked(view: View) {

        if(App.prefs.isLoggedIn) {
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
        if(App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { dialogInterface, i ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { dialogInterface, i ->

                }.show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelId = args[0] as String
            val channelName = args[1] as String
            val channelDescription = args[2] as String

            val newChannel = Channel(channelId, channelName, channelDescription)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    fun sendMessageButtonClicked(view: View) {
        hideKeyboard()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}