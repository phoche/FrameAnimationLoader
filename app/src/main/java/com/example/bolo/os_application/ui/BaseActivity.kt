package com.example.bolo.os_application.ui

import android.content.ContextWrapper
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity

/**
 * Create by bolo on 08/10/2018
 */
abstract class BaseActivity : AppCompatActivity() {

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog()
    }

    private var mInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        if (!mInitialized) {
            init()
            mInitialized = true
        }
    }

    protected fun showLoading() {
        mLoadingDialog.show(supportFragmentManager, "loading")
    }

    protected fun hideLoading() {
        mLoadingDialog.dismiss()
    }


    abstract fun init()
}

fun ContextWrapper.getVideoInRaw(@IdRes id: Int) = "android.resource://$packageName/$id"