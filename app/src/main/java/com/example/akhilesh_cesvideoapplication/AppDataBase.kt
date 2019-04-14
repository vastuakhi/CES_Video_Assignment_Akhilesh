package com.example.akhilesh_cesvideoapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.akhilesh_cesvideoapplication.files.VideoFileModel

class AppDataBase(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "video_details"
        private val TABLE_VIDEOS = "VideoTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_PATH = "path"
        private val KEY_IS_FILE_UPLOADED = "is_file_uploaded"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_VIDEO_TABLE = ("CREATE TABLE " + TABLE_VIDEOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_PATH + " TEXT," + KEY_IS_FILE_UPLOADED + " INTEGER" + ")")
        db?.execSQL(CREATE_VIDEO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS)
        onCreate(db)
    }

    fun addVideoInfo(video: VideoFileModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, video.videoName)
        contentValues.put(KEY_PATH, video.videoPath)
        contentValues.put(KEY_IS_FILE_UPLOADED, video.isFileUploaded)
        val success = db.insertOrThrow(TABLE_VIDEOS, null, contentValues)
        db.close()
        return success
    }

    fun loadAllVideoFiles(callback: IGenericCallback) {
        val videoDetailsList: ArrayList<VideoFileModel> = ArrayList<VideoFileModel>()
        val selectQuery = "SELECT  * FROM $TABLE_VIDEOS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var videoId: Int
        var videoPath: String
        var videoName: String
        var isVideoUploaded: Int
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                            videoId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                            videoPath = cursor.getString(cursor.getColumnIndex(KEY_PATH))
                            videoName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                            isVideoUploaded = cursor.getInt(cursor.getColumnIndex(KEY_IS_FILE_UPLOADED))
                            val file = VideoFileModel(
                                videoFileId = videoId,
                                videoPath = videoPath,
                                videoName = videoName,
                                isFileUploaded = isVideoUploaded
                            )
                            videoDetailsList.add(file)
                        } while (cursor.moveToNext())
            }
        }
        callback.onTaskFinish(videoDetailsList)
    }

    fun updateVideoInfo(videoFile: VideoFileModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, videoFile.videoName)
        contentValues.put(KEY_PATH, videoFile.videoPath)
        contentValues.put(KEY_IS_FILE_UPLOADED, videoFile.isFileUploaded)
        val success = db.update(TABLE_VIDEOS, contentValues, "id=" + videoFile.videoFileId, null)
        db.close()
        return success
    }


}