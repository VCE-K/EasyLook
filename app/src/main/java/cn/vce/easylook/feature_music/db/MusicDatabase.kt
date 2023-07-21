package cn.vce.easylook.feature_music.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.models.PlaylistInfo
import cn.vce.easylook.feature_music.models.PlaylistType

@Database(
    entities = [MusicInfo::class, PlaylistInfo::class],
    version = 9
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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //1.创建一个新表WordTemp，只设定想要的字段
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS new_MusicInfo (\n" +
                            "    id TEXT NOT NULL,\n" +
                            "    songId TEXT,\n" +
                            "    name TEXT,\n" +
                            "    artists TEXT,\n" +
                            "    vendor TEXT,\n" +
                            "    dl INTEGER NOT NULL DEFAULT(0),\n" +
                            "    cp INTEGER NOT NULL DEFAULT(0),\n" +
                            "    album_id TEXT,\n" +
                            "    album_name TEXT,\n" +
                            "    cover TEXT,\n" +
                            "    high INTEGER DEFAULT(0),\n" +
                            "    hq INTEGER DEFAULT(0),\n" +
                            "    sq INTEGER DEFAULT(0),\n" +
                            "    pid TEXT NOT NULL, PRIMARY KEY(id, pid)\n" +
                            ")"
                )

                //2.将原来表中的数据复制过来，
                database.execSQL(" INSERT INTO new_MusicInfo (id,songId,name,artists,vendor,dl,cp,pid,album_id,album_name,cover,high,hq,sq) " +
                        "SELECT id,songId,name,artists,vendor,dl,cp,pid,album_id,album_name,cover,high,hq,sq FROM MusicInfo ")

                //3. 将原表删除
                database.execSQL(" DROP TABLE MusicInfo")

                //4.将新建的表改名
                database.execSQL(" ALTER TABLE new_MusicInfo RENAME to MusicInfo")

                database.execSQL("DROP TABLE Playlist")
                database.execSQL("DROP TABLE Song")
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE MusicInfo ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE PlaylistInfo ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0")
                val currentTimeMillis = System.currentTimeMillis()
                database.execSQL("UPDATE MusicInfo SET timestamp = $currentTimeMillis")
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis")
           }
        }


        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                var currentTimeMillis = System.currentTimeMillis()
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.HISPLAY.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOVE.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOCAL.toString()+"'")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'LOCAL', '本地歌曲', '本地歌曲', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'LOCAL')")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'LOVE', '收藏', '收藏', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'LOCAL')")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'HISPLAY', '最近播放', '最近播放', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'LOCAL')")

                var currentTimeMillis = System.currentTimeMillis()
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.HISPLAY.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOVE.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOCAL.toString()+"'")

                database.execSQL("ALTER TABLE MusicInfo ADD COLUMN source TEXT NOT NULL DEFAULT 'NETEASE'")

            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'LOCAL', '本地歌曲', '本地歌曲', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'LOCAL')")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'LOVE', '收藏', '收藏', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'LOVE')")
                database.execSQL("INSERT INTO PlaylistInfo (id, name, description, cover, " +
                        "playCount, total, timestamp) SELECT 'HISPLAY', '最近播放', '最近播放', '', 0, 0, 0 WHERE NOT EXISTS" +
                        " (SELECT 1 FROM PlaylistInfo WHERE id = 'HISPLAY')")

                var currentTimeMillis = System.currentTimeMillis()
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.HISPLAY.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOVE.toString()+"'")
                currentTimeMillis += 1
                database.execSQL("UPDATE PlaylistInfo SET timestamp = $currentTimeMillis where id = '" + PlaylistType.LOCAL.toString()+"'")
            }
        }

        /*val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("UPDATE MusicInfo SET source = ${MusicSourceType.BLIBLI} where source is null or source=''")
            }
        }*/
    }

}