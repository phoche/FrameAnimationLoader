package com.example.bolo.os_application

import android.app.Application
import android.content.Context

/**
 * Create by bolo on 17/09/2018
 */
class OsApplication : Application() {

    companion object {
        lateinit var mApplication: Context
    }

    override fun onCreate() {
        super.onCreate()

        mApplication = this

    }

}