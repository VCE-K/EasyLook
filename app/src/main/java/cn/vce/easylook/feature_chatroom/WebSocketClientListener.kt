package cn.vce.easylook.feature_chatroom

import org.java_websocket.handshake.ServerHandshake


interface WebSocketClientListener {
    fun onOpen(handshakedata: ServerHandshake?)
    fun onMessage(message: String?)
    fun onClose(code: Int, reason: String?, remote: Boolean)
    fun onError(ex: Exception?)
}