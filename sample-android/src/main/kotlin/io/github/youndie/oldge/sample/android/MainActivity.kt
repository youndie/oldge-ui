package io.github.youndie.oldge.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.youndie.oldge.sample.OldgeSampleApp

/** The sample app on Android (B-41): the shared app in an activity, edge to edge. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { OldgeSampleApp() }
    }
}
