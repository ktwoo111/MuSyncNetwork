package com.example.webserver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.net.wifi.WifiInfo
import android.support.v4.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    val yo : WebServer = WebServer(8080)
    var musicPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    123)
            }
        } else {

        }

        //initializing shit
        allAudios.getAllAudioFromDevice(this) //fetching all the audio files in phone
        audio_size.text = allAudios.AudioList.size.toString()


        val wifiMan = this.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        val ip = String.format(
            "%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
        wifi_address.text = ip

        yo.start()

        musicPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(applicationContext, Uri.parse(allAudios.AudioList[1]._path))
            prepare()
        }
        title_text.text = allAudios.AudioList[1]._name

        play_button.setOnClickListener{
            musicPlayer?.start()
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()

        }
        pause_button.setOnClickListener{
            musicPlayer?.pause()
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show()
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
