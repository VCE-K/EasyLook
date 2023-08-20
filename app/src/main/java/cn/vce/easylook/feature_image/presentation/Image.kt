package cn.vce.easylook.feature_image.presentation

import java.io.Serializable

class Image(val name: String, var imageUrl: String, val desc: String? = name): Serializable