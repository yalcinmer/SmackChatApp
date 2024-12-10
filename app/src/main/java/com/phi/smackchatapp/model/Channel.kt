package com.phi.smackchatapp.model

class Channel(val id: String, val name: String, val description: String) {

    override fun toString(): String {
        return "#$name"
    }
}