package com.example.akhilesh_cesvideoapplication

import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast


open class BaseFragment : Fragment(), FragmentManager.OnBackStackChangedListener {
    override fun onBackStackChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val rlProgress: RelativeLayout? = null
    private val mainLayout: View? = null
    private var mProgressDialog: ProgressDialog? = null

    protected val isNetworkAvailable: Boolean
        get() {
            if (Utils.isNetworkAvailable(context!!)) {
                return true
            } else {
                Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                return false
            }
        }


    fun showProgressDialog() {
        showProgressDialog(getString(R.string.progress_message))
    }

    protected fun showProgressDialog(message: String) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog(context)
            }
            mProgressDialog!!.setMessage(message)
            mProgressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    /**
     * it is used to set visible status of main layout and progress layout
     *
     * @param mainViewStatus
     * @param progressStatus
     */
    fun setVisibilityOfProgress(mainViewStatus: Int, progressStatus: Int) {
        if (mainLayout != null && rlProgress != null) {
            mainLayout.visibility = mainViewStatus
            rlProgress.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            setVisibilityOfProgress(progressStatus)
        }
    }

    /**
     * it is used to set visibility status of progress layout..
     *
     * @param visibleStatus
     * @return
     */
    private fun setVisibilityOfProgress(visibleStatus: Int) {
        if (rlProgress != null)
            rlProgress.visibility = visibleStatus
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermission(context: Context, permission: String, permissionCode: Int): Boolean {
        try {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(permission), permissionCode)
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }
}