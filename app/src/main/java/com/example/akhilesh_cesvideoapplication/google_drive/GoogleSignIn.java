package com.example.akhilesh_cesvideoapplication.google_drive;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import com.example.akhilesh_cesvideoapplication.files.VideoFileList;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;

/**
 * Created by Akhilesh on 4/14/19.
 */

public class GoogleSignIn {
    public static final int REQUEST_CODE_SIGN_IN = 0;
    private Context mContext;

    public static GoogleSignIn getInstance() {
        return new GoogleSignIn();
    }

    public void signIn(VideoFileList fragment) {
        mContext = fragment.getContext();
        GoogleSignInClient googleSignInClient = buildGoogleSignInClient();
        Intent intent = googleSignInClient.getSignInIntent();
        fragment.startActivityForResult(intent, GoogleSignIn.REQUEST_CODE_SIGN_IN);
    }

    public GoogleSignInClient getSignInClient(VideoFileList fragment) {
        mContext = fragment.getContext();
        return buildGoogleSignInClient();
    }

    /**
     * Build a Google SignIn client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();

        return com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(mContext, signInOptions);
    }
}
