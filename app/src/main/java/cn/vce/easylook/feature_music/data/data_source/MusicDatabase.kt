package cn.vce.easylook.feature_music.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.vce.easylook.feature_music.domain.entities.Playlist
import cn.vce.easylook.feature_music.domain.entities.Song

@Database(
    entities = [Song::class, Playlist::class],
    version = 1
)
abstract class MusicDatabase: RoomDatabase() {

    abstract val musicDao: MusicDao

    companion object{
        const val DATABASE_NAME = "MUSICS_DB"
    }
}