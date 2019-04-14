package com.example.akhilesh_cesvideoapplication


import android.content.Context
import android.net.ConnectivityManager


class Utils {

    companion object {

        fun isNetworkAvailable(context: Context?): Boolean {
            var isConnected = false
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val netInfo = cm.activeNetworkInfo
                isConnected = netInfo != null && netInfo.isConnectedOrConnecting
            }
            return isConnected
        }
    }
}
