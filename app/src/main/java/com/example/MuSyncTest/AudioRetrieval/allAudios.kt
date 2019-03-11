package com.example.MuSyncTest.AudioRetrieval

import android.content.Context
import android.provider.MediaStore
import android.util.Log

object allAudios {
    private const val LOG_TAG = ".448AllAudios"

    val AudioList = mutableListOf<AudioModel>()

    fun getAllAudioFromDevice(context: Context) {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST
        )
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val c = context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            sortOrder
        )
        Log.d(LOG_TAG,"before checking the 'c'")

        if (c != null) {
            while (c.moveToNext()) {
                Log.d(LOG_TAG,"geting one file")

                val path = c.getString(0)   // Retrieve path.
                val name = c.getString(1)   // Retrieve name.
                val album = c.getString(2)  // Retrieve album name.
                val artist = c.getString(3) // Retrieve artist name.

                val oneAudio = AudioModel(path, name, album, artist)

                Log.d(LOG_TAG,"Name :$name, Album :$album")
                Log.d(LOG_TAG,"Path :$path, Artist :$artist")

                // Add the model object to the list .
                AudioList.add(oneAudio)
            }
            c.close()
        }
    }
}