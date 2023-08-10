package cn.vce.easylook.feature_chatroom.model



data class ChatMessage(
    val content: CharSequence,
    val userId: CharSequence,
    val avatar: String? = null
) {


    /** 渲染过后的消息 */
    fun getRichMessage(): CharSequence {
        return content
    }
}
