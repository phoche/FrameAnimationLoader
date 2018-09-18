package com.example.bolo.os_application.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ProgressBar

/**
 * Create by bolo on 17/09/2018
 */
class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            val view = ProgressBar(it)
            AlertDialog.Builder(it).setView(view).create()
        } ?: super.onCreateDialog(savedInstanceState)
    }
}