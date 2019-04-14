package com.example.akhilesh_cesvideoapplication.files

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.akhilesh_cesvideoapplication.*
import com.example.akhilesh_cesvideoapplication.google_drive.GoogleDriveCallback
import com.example.akhilesh_cesvideoapplication.google_drive.GoogleSignIn
import com.example.akhilesh_cesvideoapplication.google_drive.UploadFileGoogleDrive
import com.google.android.gms.common.api.ApiException


class VideoFileList : BaseFragment(), View.OnClickListener, GoogleDriveCallback {
    private var mFilePath: String? = null
    private var mUploadFileGoogleDrive: UploadFileGoogleDrive? = null
    private var signInEmail: String? = null
    var preferenceKay = "preferenceData"
    var userAccount = "userAccount"
    var activeIndex: Int = -1
    var mListAdapter: FilesAdapter? = null


    override fun onClick(v: View?) {

    }

    override fun uploadError(error: Exception?) {

    }

    override fun uploadComplete(url: String?) {

    }

    var recyclerView: RecyclerView? = null
    var videoFileList: List<VideoFileModel>? = null

    fun onInit(view: View?) {
        if (context == null)
            return

        initViews(view)
    }

    private fun initVideoFiles() {
        val databaseHandler = AppDataBase(getContext())
        databaseHandler.loadAllVideoFiles(object : IGenericCallback {
            override fun onTaskFinish(o: Any?) {
                videoFileList = o as List<VideoFileModel>
                setListAdapterView()
            }
        })
    }

    private fun initViews(view: View?) {
        recyclerView = view?.findViewById(R.id.recyclerview) as RecyclerView?

        recyclerView!!.addOnItemTouchListener(
            RecyclerItemClickListener(context!!,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val dialogBuilder = AlertDialog.Builder(context)
                        dialogBuilder.setMessage(R.string.upload_message)
                            .setCancelable(false)
                            .setPositiveButton(
                                getString(R.string.upload)
                            ) { dialog, id ->
                                mFilePath = videoFileList?.get(position)?.videoPath
                                activeIndex = position
                                signInSilently()
                            }
                            .setNegativeButton(
                                getString(R.string.close)
                            ) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.setTitle(getString(R.string.alert_title))
                        alert.show()
                    }

                })
        )
        initVideoFiles()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_list, container, false)
        onInit(view)
        return view
    }


    private fun setListAdapterView() {
        if (videoFileList.isNullOrEmpty())
            return

        mListAdapter = FilesAdapter(context, videoFileList)
        val mLayoutManager = LinearLayoutManager(context!!)
        recyclerView!!.layoutManager = mLayoutManager as RecyclerView.LayoutManager?
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mListAdapter
        mListAdapter?.notifyDataSetChanged()
    }


    private fun signInSilently() {
        val signInClient = GoogleSignIn.getInstance().getSignInClient(this)
        signInClient.silentSignIn().addOnCompleteListener(
            activity!!
        ) { task ->
            if (task.isSuccessful()) {
                // The signed in account is stored in the task's result.
                mUploadFileGoogleDrive = UploadFileGoogleDrive.getUploadFileGoogleDrive()
                mUploadFileGoogleDrive?.saveFileToDrive(this, mFilePath)

                videoFileList?.get(activeIndex)?.isFileUploaded = 2
                mListAdapter?.notifyDataSetChanged()

                val account = task.getResult()

                if (account != null) {
                    signInEmail = account.getEmail()
                    if (activity != null) {
                        activity!!.getSharedPreferences(preferenceKay, Context.MODE_PRIVATE).edit()
                            .putString(userAccount, signInEmail).apply()
                    }
                }
            } else {
                googleSignIn()
            }
        }
    }

    private fun googleSignIn() {
        val googleSignIn = GoogleSignIn.getInstance()
        googleSignIn.signIn(this)
    }

    private fun showProgressBar(text: String) {

    }

    private fun hideProgressBar() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED)
            return

        when (requestCode) {
            GoogleSignIn.REQUEST_CODE_SIGN_IN ->

                if (resultCode == Activity.RESULT_OK) {

                    if (Utils.isNetworkAvailable(context)) {
                        try {
                            mUploadFileGoogleDrive = UploadFileGoogleDrive.getUploadFileGoogleDrive()
                            mUploadFileGoogleDrive?.saveFileToDrive(this, mFilePath)
                            val task =
                                com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
                            val account = task.getResult(ApiException::class.java)

                            videoFileList?.get(activeIndex)?.isFileUploaded = 2
                            mListAdapter?.notifyDataSetChanged()

                            if (account != null) {
                                signInEmail = account.email
                                if (activity != null)
                                    activity!!.getSharedPreferences(
                                        preferenceKay,
                                        Context.MODE_PRIVATE
                                    ).edit().putString(userAccount, signInEmail).apply()
                            }

                        } catch (e: ApiException) {
                            e.printStackTrace()
                        }
                    } else
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.no_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                } else
                    Toast.makeText(activity, "Sign in issue.", Toast.LENGTH_SHORT).show()

            UploadFileGoogleDrive.REQUEST_CODE_CREATOR -> {
                if (resultCode == Activity.RESULT_OK) {
                    showProgressBar(resources.getString(R.string.uploading_google_drive))
                    mUploadFileGoogleDrive?.sendDriveId(data)
                }
            }
        }
    }
}