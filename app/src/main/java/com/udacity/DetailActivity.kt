package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    var pop:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()

        val name = intent.getStringExtra("fileName")
        val status = intent.getStringExtra("status")

        file_name_details.text = name
        status_details.text = status
        if(status=="Fail"){
            status_details.setTextColor(getColor(R.color.redColor))
        }

        ok_btn.setOnClickListener {
            motion_details.transitionToStart()
            pop = true
        }

        val transitionListener = object : MotionLayout.TransitionListener {

            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
                //nothing to do
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                //nothing to do
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
               if(pop){
                   finish()
               }
            }
            override fun onTransitionTrigger(p0: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                //not used here
            }
        }
        motion_details.setTransitionListener(transitionListener)

    }
}
