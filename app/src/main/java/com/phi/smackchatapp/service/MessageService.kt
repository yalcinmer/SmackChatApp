package com.phi.smackchatapp.service

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.phi.smackchatapp.controller.App
import com.phi.smackchatapp.model.Channel
import com.phi.smackchatapp.model.Message
import com.phi.smackchatapp.utilities.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete : (Boolean) -> Unit) {

        val channelRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->

            try {
                for(x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val id = channel.getString("_id")
                    val name = channel.getString("name")
                    val description = channel.getString("description")

                    val newChannel = Channel(id, name, description)
                    this.channels.add(newChannel)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not retrieve channels")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }

        App.prefs.requestQueue.add(channelRequest)
    }
}