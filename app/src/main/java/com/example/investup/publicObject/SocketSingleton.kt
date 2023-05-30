package com.example.investup.publicObject

import com.example.investup.other.OnMessageListener
import okhttp3.*
import org.json.JSONObject

object SocketSingleton {
    private var webSocket: WebSocket? = null
    private var callback: OnMessageListener? = null
    private var tok: String? = null

    fun closeConnection() {
        webSocket!!.close(3000, "bye")
        webSocket = null
    }

    fun connectSocket(token: String) {
        tok = token
        println("старт1")
        val clientChat = OkHttpClient()
        val url = "ws://45.9.43.5:4000"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token)
            .build()

        webSocket = clientChat.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("старт2")

            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val event = JSONObject(text).getString("event")
                    callback?.onMessage(text, event)

                } catch (e: Exception) {

                }

                println("старт3")
                println("Received message: $text")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                connectSocket(tok!!)
                sendConnection(tok!!, SocketEvents.CONNECTION.s)
            }
        })
    }


    fun setCallback(callback: OnMessageListener) {
        this.callback = callback
    }




    fun sendMessage(token: String ,dialogId: String, text: String, event: String) {
        if (webSocket!=null ) {
            println("старт4")
            val jsonObject = JSONObject()
            jsonObject.put("token", token)
            jsonObject.put("dialogId", dialogId)
            jsonObject.put("text", text)
            jsonObject.put("event", event)

            val jsonString = jsonObject.toString()

            webSocket!!.send(jsonString)
        } else {
            println("very bad socket2")
        }
    }

    fun sendReadMessage(token: String ,messageId: String, event: String) {
        if (webSocket!=null ) {

            val jsonObject = JSONObject()
            jsonObject.put("token", token)
            jsonObject.put("messageId", messageId)
            jsonObject.put("event", event)

            val jsonString = jsonObject.toString()

            webSocket!!.send(jsonString)
        } else {
            println("very bad socket2")
        }
    }

    fun sendDeleteMessage(token: String ,messageId: String, event: String) {
        if (webSocket!=null ) {

            val jsonObject = JSONObject()
            jsonObject.put("token", token)
            jsonObject.put("messageId", messageId)
            jsonObject.put("event", event)

            val jsonString = jsonObject.toString()

            webSocket!!.send(jsonString)
        } else {
            println("very bad socket2")
        }
    }

    fun sendConnection(token: String ,event: String) {
        if (webSocket!=null ) {

            val jsonObject = JSONObject()
            jsonObject.put("token", token)
            jsonObject.put("event", event)

            val jsonString = jsonObject.toString()
            println(jsonString)

            webSocket!!.send(jsonString)
        } else {
            println("very bad socket")
        }
    }





}