package com.example.webserver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast


class MainActivity : AppCompatActivity() {

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


        //fetching all the audio files in phon
        allAudios.getAllAudioFromDevice(this)
        audio_size.text = allAudios.AudioList.size.toString()

        //getting wifi address
        val wifiMan = this.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        val ip = String.format(
            "%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
        wifi_address.text = ip


        //start server
        ServerHolder.RunServer()

        //initialize player
        MusicPlayer.initializeMusicPlayer(applicationContext, ip)


        //display title_text
        title_text.text = allAudios.AudioList[100]._name



        /*
        sync_button.setOnClickListener{
            if(MusicPlayer.syncMusic()) {
                Toast.makeText(this, "Sync", Toast.LENGTH_SHORT).show()
            }
        }
        */
        //button listener
        play_button.setOnClickListener{
                MusicPlayer.StartMusic()
                Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()
        }
        pause_button.setOnClickListener{
            MusicPlayer.PauseMusic()
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

    override fun onDestroy(){
        ServerHolder.StopServer()
        Log.d("server_status", "onDestroy() called")
        super.onDestroy()
    }
    override fun onStop(){
        Log.d("server_status", "onStop() called")
        super.onStop()

    }

}
