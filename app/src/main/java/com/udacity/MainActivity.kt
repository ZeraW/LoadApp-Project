package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var fileName: String = ""
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var manger: DownloadManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.toolbar.title = "LoadApp"

        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.success_notification_channel_id),
            getString(R.string.success_notification_channel_name)
        )

        custom_button.setOnClickListener {
            when (download_radio.checkedRadioButtonId) {
                1 -> {
                    download(glideUrl)
                    fileName = getString(R.string.glide)
                }
                2 -> {
                    download(loadAppUrl)
                    fileName = getString(R.string.loadapp)
                }
                3 -> {
                    download(retrofitUrl)
                    fileName = getString(R.string.retrofit)
                }
                else -> Toast.makeText(
                    applicationContext,
                    "You need to pick something to download",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = Query()
            id?.let { query.setFilterById(it) }
            val cur: Cursor = manger.query(query)
            if (cur.moveToFirst()) {
                val columnIndex: Int = cur.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                    custom_button.updateBtnState(ButtonState.Completed)
                    sendNotification("Success")
                } else {
                    custom_button.updateBtnState(ButtonState.Completed)
                    sendNotification("Fail")
                }
            }
        }
    }

    private fun sendNotification(status: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(
            applicationContext.getText(R.string.notification_description).toString(),
            fileName,
            status,
            applicationContext
        )
    }

    private fun download(downloadUrl: String) {
        custom_button.updateBtnState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        manger = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manger.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val glideUrl =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val loadAppUrl =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val retrofitUrl =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.success_notification_channel_id)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}


