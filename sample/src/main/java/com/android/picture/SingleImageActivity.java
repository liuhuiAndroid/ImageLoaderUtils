package com.android.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
    private ImageView mImageView2;
    private CircleProgressView mProgressView;
    private CircleProgressView mProgressView2;
    private View mMaskView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mProgressView = (CircleProgressView) findViewById(R.id.progressView);
        mProgressView2 = (CircleProgressView) findViewById(R.id.progressView2);
        mMaskView = findViewById(maskView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SingleImageActivity.this);
            }
        });

        ImageLoaderUtils.getInstance().loadImageWithProgress(girl, mImageView, new ProgressListener() {
            @Override
            public void onProgress(int percent, boolean isDone) {
                mProgressView.setProgress(percent);
                mProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
                mMaskView.setVisibility(isDone ? View.GONE : View.VISIBLE);
            }
        }, girl_thumbnail);

      /*  ImageLoaderUtils.getInstance().loadImageWithProgress(cat, mImageView2, new ProgressListener() {
            @Override
            public void onProgress(final long bytesRead, final long totalBytes, final boolean isDone) {


                        final int percent = (int) ((bytesRead * 1.0f / totalBytes) * 100.0f);
                        Log.i("TAG", "test SingleImageActivity percent2:" + percent);
                        mProgressView2.setProgress(percent);
                        mProgressView2.setVisibility(isDone ? View.GONE : View.VISIBLE);
                        mMaskView.setVisibility(isDone ? View.GONE : View.VISIBLE);

            }
        }, cat_thumbnail);*/

    }

}
