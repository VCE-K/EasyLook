package cn.vce.easylook.feature_music.domain.use_case

data class MusicUseCases(
    val getPlaylist: GetPlaylist,
    val getPlaylistAndSongs: GetPlaylistAndSongs,
    val insertPlaylist: InsertPlaylist,
    val searchSongInPlaylist: SearchSongInPlaylist
)