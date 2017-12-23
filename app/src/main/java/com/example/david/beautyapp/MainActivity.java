package com.example.david.beautyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // request permission
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    // privacy policy URL
    private static final String PRIVACY_POLICY = "https://github.com/Scowluga/BeautyApp/blob/master/PRIVACYPOLICY.md";
    private static final String GITHUB_URL = "https://github.com/Scowluga/BeautyApp";

    // view with camera
    private static CameraView view;

    // for requesting permission again
    private static boolean requestAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // request permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) { // NOT GRANTED
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            setup();
        }
    }

    private void setup() {
        // load camera
        view = new CameraView(MainActivity.this);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout)findViewById(R.id.frameLayout)).addView(view, 0);

        // set on click listeners
        Button star = (Button)findViewById(R.id.sbtn);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        Button play = (Button)findViewById(R.id.gbtn);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
                startActivity(intent);
            }
        });

        Button question = (Button)findViewById(R.id.qbtn);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("What's this?")
                        .setMessage("The World's Most Beautiful App is one with you. No filters, no enhancements, just you!" )
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestAgain) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) { // NOT GRANTED
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            requestAgain = false;
        }
        if (view != null && view.mCamera != null) {
            CameraView.setCameraDisplayOrientation(MainActivity.this, view.mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (view != null && view.mCamera != null) {
            view.mCamera.stopPreview();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (view != null && view.mCamera != null) {
//            CameraView.setCameraDisplayOrientation(MainActivity.this, view.mCamera);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setup();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("What's this?")
                                    .setMessage("The World's Most Beautiful App is one with you. No filters, no enhancements, just you!" )
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }
                    }, 5000);

                } else { // not allowed
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Sorry");
                    alertDialog.setMessage("We need permission to access your camera for this application to work.");
//                    alertDialog.setIcon();
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Why", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestAgain = true;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY));
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();
                }

        }
    }
}
