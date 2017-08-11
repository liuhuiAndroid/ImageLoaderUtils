package com.android.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lh.picture.ImageLoaderUtils;
import com.lh.picture.listener.ProgressListener;
import com.lh.picture.widget.CircleProgressView;

import static com.android.picture.MainActivity.girl;
import static com.android.picture.MainActivity.girl_thumbnail;
import static com.android.picture.R.id.maskView;

/**
 * Created by lh on 2017/8/10.
 */

public class SingleImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private CircleProgressView mProgressView;
    private View mMaskView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mProgressView = (CircleProgressView) findViewById(R.id.progressView);
        mMaskView = findViewById(maskView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SingleImageActivity.this);
            }
        });
        mProgressView.setVisibility(View.VISIBLE);
        ImageLoaderUtils.getInstance().loadImageWithProgress(girl, mImageView, new ProgressListener() {
            @Override
            public void onProgress(String imageUrl, int percent, boolean isDone) {
                Log.i("TAG", "onProgress :" + percent + " ,testChangeThread : " + Thread.currentThread().getName()+" ,isDone : "+isDone);
                mProgressView.setProgress(percent);
                mProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
                mMaskView.setVisibility(isDone ? View.GONE : View.VISIBLE);
            }
        }, girl_thumbnail);
    }

}
