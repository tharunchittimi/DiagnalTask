package com.example.diagnaltask

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver

class MyApplication : Application(), LifecycleObserver {
    init {
        myApplication = this
    }

    companion object {
        private lateinit var myApplication: Application
        fun getApplicationContext(): Context {
            return myApplication
        }
    }

}