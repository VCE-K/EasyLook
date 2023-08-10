package cn.vce.easylook.feature_chatroom

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentChatRoomBinding
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG_BIND
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG_EMPTY
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG_HEART
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG_IMG
import cn.vce.easylook.feature_chatroom.ChatRoomFragment.MType.MSG_INIT
import cn.vce.easylook.feature_chatroom.model.ChatMessage
import cn.vce.easylook.feature_chatroom.model.ChatModel
import cn.vce.easylook.utils.hideSoftInput
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ChatRoomFragment : BaseVmFragment<FragmentChatRoomBinding>() {
    object MType{
        const val MSG_HEART = 1
        const val MSG_EMPTY = 2
        const val MSG = 3

        const val MSG_IMG = 4
        const val MSG_INIT = 5
        const val MSG_BIND = 6
    }
    private val model = ChatModel()
    private var webSocketConnectNumber: Int = 0
    private var RECONNECT_DELAYED_TIME: Long = 0L

    private var webSocketClient: MyWebSocketClient = MyWebSocketClient.getInstance()

    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    private val mHandler  = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                MSG ->{
                    /*"type","msg"
                    "msg",map.get("msg")
                    "sendUser",user.getNickname()
                    "shake",map.get("shake") //shake是具体的表情 */
                    val map = (msg.obj as Map<String, String>)
                    val content: String = map["msg"]!!
                    val userId = map["sendUser"]!!
                    binding.rv.addModels(listOf(ChatMessage(content, userId)), index = 0) // 添加一条消息
                    binding.rv.scrollToPosition(0) // 保证最新一条消息显示
                }
                MSG_IMG -> {
                    /*"type","img"
                    "msg",map.get("msg")
                    "sendUser",user.getNickname()*/
                    val map = (msg.obj as Map<String, String>)
                    val content: String = map["msg"]!!
                    val userId = map["sendUser"]!!
                    binding.rv.addModels(listOf(ChatMessage(content, userId)), index = 0) // 添加一条消息
                    binding.rv.scrollToPosition(0) // 保证最新一条消息显示
                }
                MSG_INIT -> {
                    /*"type","init"
                    "msg",nick+"离开房间"
                    "sendUser","系统消息"*/
                    val map = (msg.obj as Map<String, String>)
                    val content: String = map["msg"]!!
                    val userId = map["sendUser"]!!
                    binding.rv.addModels(listOf(ChatMessage(content, userId)), index = 0) // 添加一条消息
                    binding.rv.scrollToPosition(0) // 保证最新一条消息显示
                }
                MSG_BIND -> {
                    /*"type","bing"
                    "msg", url
                    "sendUser","系统消息"
                    "id",session.getId()*/
                    val map = (msg.obj as Map<String, String>)
                    model.currentUserId = map["id"]!!
                    val content: String = map["msg"]!!
                    val userId = map["id"]!!
                    binding.rv.addModels(listOf(ChatMessage(map.toString(), userId)), index = 0) // 添加一条消息
                    binding.rv.scrollToPosition(0) // 保证最新一条消息显示
                }
                MSG_HEART -> {//心跳连接
                    try {
                        //ping
                        val result: MutableMap<String, String> = HashMap()
                        result["type"] = "ping"
                        webSocketClient.send(result.toString())
                        //webSocketClient.sendPing()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        webSocketClient.reconnect()
                    } finally {
                        removeMessages(MSG_HEART)
                        sendEmptyMessageDelayed(MSG_HEART, 30 * 1000)
                    }
                }
                MSG_EMPTY -> {
                    if (webSocketConnectNumber <= 10) {
                        if (!webSocketClient.isOpen) {
                            webSocketClient.reconnect()
                        } else if (webSocketClient.isClosing || webSocketClient.isClosed) {
                            webSocketClient.closeBlocking()
                            webSocketClient.reconnect()
                            //将连接次数自增
                            webSocketConnectNumber++
                        }
                    } else {
                        //超过设置次数则清空重连消息队列，并将mInstance置为null（待外部初始化连接）
                        removeMessages(MSG_EMPTY)
                        //webSocketClient = null
                    }
                }
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        binding.v = this
        binding.m = model
        binding.rv.setup {
            addType<ChatMessage> {
                if (model.currentUserId == userId){
                    R.layout.item_msg_right // 我发的消息
                } else {
                    R.layout.item_msg_left // 对方发的消息
                }
            }
        }
        binding.rv.setOnTouchListener { v, _ ->
            v.clearFocus() // 清除文字选中状态
            mActivity.hideSoftInput() // 隐藏键盘
            false
        }
        webSocketClient.addListener(object: WebSocketClientListener {
            override fun onOpen(handshakedata: ServerHandshake?) {
                webSocketConnectNumber = 0
                RECONNECT_DELAYED_TIME = 50
                mHandler.removeMessages(MSG_EMPTY)

                val map : MutableMap<String, String> = HashMap()
                map["type"] = MSG.toString()
                map["msg"] = "onOpen"
                map["sendUser"] = "系统消息"
                val type = MSG
                val handlerMessage = Message.obtain()
                handlerMessage.what = type
                handlerMessage.obj = map
                mHandler.sendMessage(handlerMessage)

                //这里是为了open马上拿到当前用户ID
                val result: MutableMap<String, String> = HashMap()
                result["type"] = "bind"
                result["msg"] = model.input
                result["sendUser"] = model.currentUserId
                /*var json = JSONObject(result as Map<*, *>?)
                webSocketClient.send(json.toString())
                val str = Gson().toJson(result)
                webSocketClient.send(str)*/
                webSocketClient.send(Map2Json(result))
            }

            override fun onMessage(message: String?) {
                val map: Map<String, String> = Gson().fromJson(
                    message,
                    object : TypeToken<HashMap<String?, String?>?>() {}.type
                )
                val type = when(map["type"]){
                    "msg" -> MSG
                    "init" -> MSG_INIT
                    "img" -> MSG_IMG
                    "bind" -> MSG_BIND
                    else -> -1
                }
                val handlerMessage = Message.obtain()
                handlerMessage.what = type
                handlerMessage.obj = map
                mHandler.sendMessage(handlerMessage)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                mHandler.removeMessages(MSG_EMPTY)
                mHandler.sendEmptyMessageDelayed(MSG_EMPTY, RECONNECT_DELAYED_TIME)
                //将时间间隔翻倍
                RECONNECT_DELAYED_TIME *= 2

                val map : MutableMap<String, String> = HashMap()
                map["type"] = MSG.toString()
                map["msg"] = "onClose：$code \n $reason \n $remote"
                map["sendUser"] = "系统消息"
                val type = MSG
                val handlerMessage = Message.obtain()
                handlerMessage.what = type
                handlerMessage.obj = map
                mHandler.sendMessage(handlerMessage)
            }

            override fun onError(ex: Exception?) {
                val map : MutableMap<String, String> = HashMap()
                map["type"] = MSG.toString()
                map["msg"] = "onError：$ex"
                map["sendUser"] = "系统消息"
                val type = MSG
                val handlerMessage = Message.obtain()
                handlerMessage.what = type
                handlerMessage.obj = map
                mHandler.sendMessage(handlerMessage)
            }
        })
        if (!webSocketClient.isOpen){
            webSocketClient.connect()
        }
    }

    override fun getLayoutId(): Int?  = R.layout.fragment_chat_room


    fun Map2Json(map: MutableMap<String, String>): String {
        var str = "{"
        map.forEach {
            str += "\"" + it.key + "\"" + ":" + "\"" + it.value + "\","
        }
        str = str.substring(0,str.length-1)
        str +="}"
        return str
    }

    override fun onClick(v: View) {
        binding.run {
            when (v) {
                binding.btnSend -> {
                    val result: MutableMap<String, String> = HashMap()
                    result["type"] = "msg"
                    result["msg"] = model.input
                    result["sendUser"] = model.currentUserId
                    /*var json = JSONObject(result as Map<*, *>?)
                    webSocketClient.send(json.toString())
                    val str = Gson().toJson(result)
                    webSocketClient.send(str)*/
                    webSocketClient.send(Map2Json(result))

                    binding.rv.addModels(model.getMessages(), index = 0) // 添加一条消息
                    binding.rv.scrollToPosition(0) // 保证最新一条消息显示

                }
                binding.rv -> {
                    mActivity.hideSoftInput() // 隐藏键盘
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketClient.close()
    }

}