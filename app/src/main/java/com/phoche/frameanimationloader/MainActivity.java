package com.phoche.frameanimationloader;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int[] mResId = {R.drawable.v01, R.drawable.v02, R.drawable.v03,
            R.drawable.v04, R.drawable.v05, R.drawable.v06,
            R.drawable.v07, R.drawable.v08, R.drawable.v09,
            R.drawable.v10, R.drawable.v11};


    private FrameImageView mImageView;
    private FrameAnimLoader mAnimLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (FrameImageView) findViewById(R.id.imageview);

        String path = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "frame_03";
        mImageView.setAnimDir(path);
        mImageView.setOneShot(false);
        mImageView.setTotalDuration(3000);
        mImageView.playFrame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
