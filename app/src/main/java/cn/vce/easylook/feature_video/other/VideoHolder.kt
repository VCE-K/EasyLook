package cn.vce.easylook.feature_video.other

import cn.vce.easylook.R


fun getVideoHolderXML(type: String): Int {
    return when(type){
        "squareCardCollection" -> R.layout.video_index_video_card //ITEM_CARD
        "textCard" -> R.layout.video_index_video_title //ITEM_TITLE
        "followCard" -> R.layout.video_index_video_video //ITEM_FOLLOW
        "videoSmallCard" -> R.layout.video_index_video_video //ITEM_VIDEO
        "banner2", "banner" -> R.layout.video_index_video_banner //ITEM_BANNER
        "video" -> R.layout.unknown_layout //ITEM_CARD_VIDEO
        "video_header" -> R.layout.unknown_layout //ITEM_VIDEO_HEADER
        "NORMAL" -> R.layout.video_index_video_video //ITEM_NOIMAL
        else -> R.layout.unknown_layout
    }
}
