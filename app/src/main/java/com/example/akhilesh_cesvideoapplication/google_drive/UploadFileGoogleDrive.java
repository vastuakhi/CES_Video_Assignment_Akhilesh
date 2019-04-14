package com.example.akhilesh_cesvideoapplication.google_drive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.akhilesh_cesvideoapplication.files.VideoFileList;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.OnChangeListener;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.io.*;
import java.util.Objects;

/**
 * Created by Akhilesh on 4/14/19.
 */

public class UploadFileGoogleDrive {
    public static final int REQUEST_CODE_CREATOR = 2;
    private static final String TAG = "UploadFileGoogleDrive";
    private Fragment mFragment;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    public static UploadFileGoogleDrive getUploadFileGoogleDrive() {
        return new UploadFileGoogleDrive();
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive(final String fileUri) {
        createDriveClient();

        //mDriveResourceClient.openFile(new File(fileUri), DriveFile.MODE_READ_WRITE, openCallback);
        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                return UploadFileGoogleDrive.this.createFileIntentSender(task.getResult(), fileUri);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Failed to create new contents.", e);
                            }
                        });

    }


    private Task<Void> createFileIntentSender(DriveContents driveContents, String fileUri) throws IOException {
        Log.i(TAG, "New contents created.");
        OutputStream outputStream = driveContents.getOutputStream();

        File file = new File(fileUri);
        InputStream fileInputStream = new FileInputStream(file);

        byte fileStreamArray[] = IOUtils.toByteArray(fileInputStream);

        try {
            outputStream.write(fileStreamArray);
        } catch (IOException e) {
            Log.w(TAG, "Unable to write file contents.", e);
        }

        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType("application/pdf")
                        //.setTitle("Attendance Report.pdf")
                        .setTitle(file.getName())
                        .build();

        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {
                                mFragment.startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0, null);
                                return null;
                            }
                        });
    }

    private void createDriveClient() {
        OpenFileCallback openCallback = new OpenFileCallback() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                // Update progress dialog with the latest progress.
                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                Log.d(TAG, String.format("Loading progress: %d percent", progress));
                //mProgressBar.setProgress(progress);
                sendBroadcast("Progress " + progress);
            }

            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                sendBroadcast("Progress 100");
            }

            @Override
            public void onError(@NonNull Exception e) {
                sendBroadcast("Error occurred.");
            }
        };

        mDriveClient = Drive.getDriveClient(mFragment.getContext(), Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(mFragment.getContext()))));
        mDriveResourceClient = Drive.getDriveResourceClient(mFragment.getContext(), Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(mFragment.getContext())));
    }

    @SuppressLint("StaticFieldLeak")
    public void saveFileToDrive(Fragment fragment, final String filePath) {
        mFragment = fragment;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                saveFileToDrive(filePath);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void sendDriveId(Intent data) {
        if (data != null) {
            DriveId mFileId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
            mDriveResourceClient.addChangeListener(mFileId.asDriveResource(), new OnChangeListener() {
                @Override
                public void onChange(ChangeEvent event) {
                    if (event != null && event.getDriveId() != null && event.getDriveId().getResourceId() != null) {
                        String url = "https://drive.google.com/open?id=" + event.getDriveId().getResourceId();
                        ((VideoFileList) mFragment).uploadComplete(url);
                    }
                }
            });
        }
    }

    private void sendBroadcast(String message) {
        Intent intent = new Intent("custom-event");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(mFragment.getContext())).sendBroadcast(intent);
    }

}
