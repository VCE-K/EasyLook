package cn.vce.easylook.feature_music.data.remote

import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import cn.vce.easylook.feature_music.data.entities.Song
import cn.vce.easylook.feature_music.other.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    private val remoteJsonSource: Uri =
        Uri.parse("https://storage.googleapis.com/uamp/catalog.json")

    suspend fun getAllSongs(): List<Song> {
        return try {
            val mediaSource  = downloadJson(remoteJsonSource)
            val songs = ArrayList<Song>()
            mediaSource.music.map { music ->
                music.apply {
                    val song = Song(id, title, "", source, image)
                    songs.add(song)
                }
            }
            songCollection.get().await().toObjects(Song::class.java)
            songs
        } catch(e: Exception) {
            emptyList()
        }
    }

    @Throws(IOException::class)
    private fun downloadJson(catalogUri: Uri): JsonCatalog {
        val catalogConn = URL(catalogUri.toString())
        val reader = BufferedReader(InputStreamReader(catalogConn.openStream()))
        return Gson().fromJson(reader, JsonCatalog::class.java)
    }
}

class JsonCatalog {
    var music: List<JsonMusic> = ArrayList()
}


@Suppress("unused")
class JsonMusic {
    var id: String = ""
    var title: String = ""
    var album: String = ""
    var artist: String = ""
    var genre: String = ""
    var source: String = ""
    var image: String = ""
    var trackNumber: Long = 0
    var totalTrackCount: Long = 0
    var duration: Long = C.TIME_UNSET
    var site: String = ""
}