package cn.vce.easylook.feature_music.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo

@Database(
    entities = [MusicInfo::class, PlaylistInfo::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class MusicDatabase: RoomDatabase() {

    abstract val musicDao: MusicDao

    companion object {
        const val DATABASE_NAME = "MUSICS_DB"
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE Playlist")
                database.execSQL("DROP TABLE Song")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS PlaylistInfo (\n" +
                            "  `id` TEXT NOT NULL PRIMARY KEY,\n" +
                            "  `name` TEXT NOT NULL,\n" +
                            "  `description` TEXT,\n" +
                            "  `cover` TEXT,\n" +
                            "  `playCount` INTEGER NOT NULL DEFAULT(0),\n" +
                            "  `total` INTEGER NOT NULL DEFAULT(0)\n" +
                            ")"
                )

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS MusicInfo (\n" +
                            "    id TEXT NOT NULL PRIMARY KEY,\n" +
                            "    songId TEXT,\n" +
                            "    name TEXT,\n" +
                            "    artists TEXT,\n" +
                            "    album TEXT,\n" +
                            "    vendor TEXT,\n" +
                            "    dl INTEGER NOT NULL DEFAULT(0),\n" +
                            "    cp INTEGER NOT NULL DEFAULT(0),\n" +
                            "    quality TEXT,\n" +
                            "    songUrl TEXT,\n" +
                            "    pid TEXT\n" +
                            ")"
                )
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, playCount, total) VALUES ('LOCAL', '本地歌曲', '本地歌曲', '', 0, 0)")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, playCount, total) VALUES ('LOVE', '收藏', '收藏', '', 0, 0)")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, playCount, total) VALUES ('HISPLAY', '最近播放', '最近播放', '', 0, 0)")
            }
        }
    }

}