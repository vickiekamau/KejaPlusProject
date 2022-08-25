package com.kejaplus.utils

import android.content.Context
import android.graphics.Color
import cn.pedant.SweetAlert.SweetAlertDialog

/**
     * Musoni Tera Collect
     * @author Mambo Bryan
     * @email mambobryan@gmail.com
     * Created 7/20/22 at 8:08 AM
     */


object SweetAlerts : SweetsImpl {

    private var sweetAlertDialog: SweetAlertDialog? = null

    private enum class DialogType {
        LOADING, SUCCESS, ERROR, CONFIRM, NORMAL, CUSTOM_IMAGE
    }

    private fun getDialog(
        context: Context,
        type: DialogType
    ): SweetAlertDialog {

        return when (sweetAlertDialog) {
            null -> {
                when (type) {
                    DialogType.LOADING -> SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    DialogType.SUCCESS -> SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    DialogType.ERROR -> SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    DialogType.CONFIRM -> SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    DialogType.NORMAL -> SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    DialogType.CUSTOM_IMAGE -> SweetAlertDialog(
                        context,
                        SweetAlertDialog.CUSTOM_IMAGE_TYPE
                    )
                }
            }
            else -> {
                when (type) {
                    DialogType.LOADING -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.PROGRESS_TYPE)
                    DialogType.SUCCESS -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    DialogType.ERROR -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    DialogType.CONFIRM -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    DialogType.NORMAL -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.NORMAL_TYPE)
                    DialogType.CUSTOM_IMAGE -> sweetAlertDialog!!.changeAlertType(SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                }
                sweetAlertDialog!!
            }
        }.apply {
            setCancelable(false)
            setOnDismissListener {
                reset()
            }
        }.also {
            sweetAlertDialog = it
        }

    }

    private fun reset() {
        sweetAlertDialog = null
    }


    override fun loading(context: Context, msg: String) {
        with(getDialog(context, DialogType.LOADING)) {
            progressHelper.barColor = Color.parseColor("#41c300")
            titleText = msg
            show()
        }
    }

    fun loadingNavigation(context: Context, msg: String, dismiss: (() -> Unit)?) {
        with(getDialog(context, DialogType.LOADING)) {
            progressHelper.barColor = Color.parseColor("#41c300")
            titleText = msg

            setOnDismissListener {
                reset()
                dismiss?.invoke()
            }
            show()
        }
    }

    override fun error(context: Context, title: String, msg: String, dismiss: (() -> Unit)?) {
        with(getDialog(context, DialogType.ERROR)) {
            titleText = title ?: ""
            contentText = msg
            setCancelClickListener {
                dismissDialog()
            }
            setOnDismissListener {
                reset()
                dismiss?.invoke()
            }
            show()
        }

    }

    override fun success(context: Context, title: String, msg: String, dismiss: (() -> Unit)?) {
        with(getDialog(context, DialogType.SUCCESS)) {
            titleText = title ?: ""
            contentText = msg
            setConfirmClickListener {
                dismissDialog()
            }
            setCancelClickListener {
                dismissDialog()

            }
            setOnDismissListener {
                reset()
                dismiss?.invoke()
            }
            show()
        }

    }

    override fun confirm(context: Context, title: String?, msg: String,cancelText:String, confirm: () -> Unit) {
        with(getDialog(context, DialogType.CONFIRM)) {
            titleText = title ?: ""
            contentText = msg
            setConfirmClickListener {
                confirm.invoke()
                dismissDialog()
            }


            cancelButtonBackgroundColor = Color.parseColor("#ff0000")
            setCancelButton(cancelText) {
                dismissDialog()
            }
            setCancelClickListener { dismissDialog() }

            show()
        }

    }

    override fun dismissDialog() {
        sweetAlertDialog?.dismissWithAnimation()
    }



}

interface SweetsImpl {
    fun loading(context: Context, msg: String = "Loading...")
    fun error(
        context: Context,
        title: String = "Failed",
        msg: String,
        dismiss: (() -> Unit)? = null
    )

    fun success(
        context: Context,
        title: String = "Success",
        msg: String,
        dismiss: (() -> Unit)? = null
    )

    fun confirm(context: Context, title: String? = null, msg: String,cancelText: String, confirm: () -> Unit)
    fun dismissDialog()
}