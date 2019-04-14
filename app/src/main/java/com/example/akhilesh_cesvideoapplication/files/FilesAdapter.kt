package com.example.akhilesh_cesvideoapplication.files

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.akhilesh_cesvideoapplication.R

class FilesAdapter(internal var mCtx: Context?, internal var videosFiles: List<VideoFileModel>?) :
    RecyclerView.Adapter<FilesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesAdapter.MyViewHolder {
        return FilesAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.video_cell,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilesAdapter.MyViewHolder, position: Int) {
        holder.fileName.text = videosFiles!![position].videoName

        if (videosFiles!![position].isFileUploaded == 0) {
            holder.fileUpdateStatus.text = this.mCtx?.getText(R.string.pending)
        } else if (videosFiles!![position].isFileUploaded == 1) {
            holder.fileUpdateStatus.text = this.mCtx?.getText(R.string.uploaded)
        } else if (videosFiles!![position].isFileUploaded == 2) {
            holder.fileUpdateStatus.text = this.mCtx?.getText(R.string.uploading)
            holder.fileUpdateStatus.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return videosFiles?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileName: TextView = itemView.findViewById(R.id.fileName)
        var fileUpdateStatus: TextView = itemView.findViewById(R.id.file_status)

    }
}