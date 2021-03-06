package com.iven.musicplayergo.musicloadutils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import com.iven.musicplayergo.R
import com.iven.musicplayergo.utils.MusicUtils
import com.iven.musicplayergo.utils.Utils
import java.io.File

class MusicLibrary {

    var allSongsUnfiltered: MutableList<Music> = mutableListOf()

    var allSongsFiltered: MutableList<Music>? = null

    //keys: artist || value: its songs
    var allSongsByArtist: Map<String?, List<Music>>? = null

    //keys: artist || value: albums
    val allAlbumsByArtist: MutableMap<String, List<Album>>? = mutableMapOf()

    //keys: artist || value: songs contained in the folder
    var allSongsByFolder: Map<String, List<Music>>? = null

    val randomMusic get() = allSongsFiltered?.random()

    @SuppressLint("InlinedApi")
    fun queryForMusic(context: Context) = try {

        val musicCursor = MusicUtils.getMusicCursor(context.contentResolver)

        // Query the storage for music files
        // If query result is not empty
        musicCursor?.use { cursor ->

            val artistIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val yearIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
            val trackIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
            val titleIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val displayNameIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val durationIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val albumIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val relativePathIndex =
                cursor.getColumnIndexOrThrow(MusicUtils.getPathColumn())
            val idIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)

            while (cursor.moveToNext()) {

                // Now loop through the music files
                val audioArtist = cursor.getString(artistIndex)
                val audioYear = cursor.getInt(yearIndex)
                val audioTrack = cursor.getInt(trackIndex)
                val audioTitle = cursor.getString(titleIndex)
                val audioDisplayName = cursor.getString(displayNameIndex)
                val audioDuration = cursor.getLong(durationIndex)
                val audioAlbum = cursor.getString(albumIndex)
                val audioRelativePath = cursor.getString(relativePathIndex)
                val audioId = cursor.getLong(idIndex)

                val audioFolderName =
                    if (Utils.isAndroidQ()) {
                        audioRelativePath ?: context.getString(R.string.slash)
                    } else {
                        val returnedPath = File(audioRelativePath).parentFile?.name
                            ?: context.getString(R.string.slash)
                        if (returnedPath != "0") returnedPath else context.getString(
                            R.string.slash
                        )
                    }

                // Add the current music to the list
                allSongsUnfiltered.add(
                    Music(
                        audioArtist,
                        audioYear,
                        audioTrack,
                        audioTitle,
                        audioDisplayName,
                        audioDuration,
                        audioAlbum,
                        audioFolderName,
                        audioId
                    )
                )
            }
        }

        // Removing duplicates by comparing everything except path which is different
        // if the same song is hold in different paths
        allSongsFiltered =
            allSongsUnfiltered.distinctBy { it.artist to it.year to it.track to it.title to it.duration to it.album }
                .toMutableList()

        allSongsByArtist = allSongsFiltered?.groupBy { it.artist }

        allSongsByArtist?.keys?.iterator()?.forEach {
            it?.let { artist ->
                allAlbumsByArtist.apply {
                    this?.set(
                        artist, MusicUtils.buildSortedArtistAlbums(
                            context.resources,
                            allSongsByArtist?.getValue(artist)
                        )
                    )
                }
            }
        }

        allSongsByFolder = allSongsFiltered?.groupBy {
            it.relativePath!!
        }
        allSongsByFolder
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
