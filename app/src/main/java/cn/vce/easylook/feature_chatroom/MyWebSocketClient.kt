package cn.vce.easylook.feature_chatroom

import android.os.Message
import cn.vce.easylook.R
import cn.vce.easylook.utils.getString
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.util.*

class MyWebSocketClient(serverUri: URI): WebSocketClient(serverUri) {

    private val listeners: MutableList<WebSocketClientListener> = mutableListOf()
    companion object {
        private var webSocketClient: MyWebSocketClient? = null
        @JvmStatic
        fun getInstance(): MyWebSocketClient{
            return webSocketClient?: synchronized(this){
                val serverURI = URI.create("ws://${getString(R.string.ip)}:${getString(R.string.socket_port)}/websocket")
                webSocketClient?: MyWebSocketClient(serverURI).also {
                    webSocketClient = it
                }
            }
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        listeners.forEach { listener ->
            listener.onOpen(handshakedata)
        }
    }

    override fun onMessage(message: String?) {
        listeners.forEach { listener ->
            listener.onMessage(message)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        listeners.forEach { listener ->
            listener.onClose(code, reason, remote)
        }
    }

    override fun onError(ex: Exception?) {
        listeners.forEach { listener ->
            listener.onError(ex)
        }
    }

    fun addListener(listener: WebSocketClientListener){
        listeners.add(listener)
    }

    fun removeListener(listener: WebSocketClientListener){
        listeners.add(listener)
    }
}