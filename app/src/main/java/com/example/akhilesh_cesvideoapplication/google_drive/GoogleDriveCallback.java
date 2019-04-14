package com.example.akhilesh_cesvideoapplication.google_drive;

/**
 * Created by Akhilesh on 4/14/19.
 */

public interface GoogleDriveCallback {

    void uploadError(Exception error);

    void uploadComplete(String url);
}
