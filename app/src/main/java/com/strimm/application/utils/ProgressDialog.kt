package com.strimm.application.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.strimm.application.R

// class to show/hide progressbar
class ProgressDialog(activity: Activity) {


    private val dialog: Dialog = Dialog(activity)

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        if (dialog.window != null) {
            dialog.window?.requestFeature(1)
        }
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
    }
}