package cn.vce.easylook.feature_music.other

class LRUCacheLyric {
    private var map: LinkedHashMap<String, String>? = null
    private var capacity = 0

    companion object{
        private var instance: LRUCacheLyric? = null
        @JvmStatic
        fun getInstance(capacity: Int = 30): LRUCacheLyric{
            return instance ?: synchronized(this){
                instance ?: LRUCacheLyric(capacity).also { instance = it }
            }
        }
    }


    private constructor(capacity: Int) {
        this.capacity = capacity
        map = object : LinkedHashMap<String, String>(capacity, 0.75f, true){
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
                return size > capacity
            }
        }
    }

    operator fun get(key: String): String? {
        return if (map!!.getOrDefault(key, "-1") == "-1"){
            null
        }else{
            map!![key]
        }
    }

    fun put(key: String, value: String) {
        map!![key] = value
    }
}
