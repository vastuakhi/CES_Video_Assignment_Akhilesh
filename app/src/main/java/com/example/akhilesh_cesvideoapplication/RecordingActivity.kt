package com.example.akhilesh_cesvideoapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.akhilesh_cesvideoapplication.camera.Camera2VideoFragment

class RecordingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        savedInstanceState ?: supportFragmentManager.beginTransaction().addToBackStack("Camera2VideoFragment")
            .add(R.id.container, Camera2VideoFragment.newInstance())
            .commit()
    }

}

