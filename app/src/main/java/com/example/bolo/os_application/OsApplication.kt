package com.example.bolo.os_application

import android.app.Application
import android.content.Context
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

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

        PlayerFactory.setPlayManager(SystemPlayerManager())

    }

}