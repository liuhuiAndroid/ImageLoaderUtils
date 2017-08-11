package com.android.picture;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lh.picture.ImageLoaderUtils;
import com.lh.picture.listener.ImageSaveListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_URL1 = "https://pic4.zhimg.com/v2-3e766383a30cda5fea087dcb3f0023c3_b.jpg";
    private static final String IMAGE_URL2 = "https://pic2.zhimg.com/v2-7e01c7a39f59743412c2c70f781879d1_b.jpg";
    private static final String IMAGE_URL3 = "https://pic1.zhimg.com/v2-196ebe4ff36284717f4cda28b26404a0_b.png";
    private static final String IMAGE_URL4 = "https://pic1.zhimg.com/v2-57061aa60f65f7a4086941d35629eaec_b.jpg";
    private static final String IMAGE_URL5 = "https://pic1.zhimg.com/v2-e93c39dac2c0840c8465933fc90d4878_b.jpg";
    private static final String IMAGE_URL6 = "http://desk.fd.zol-img.com.cn/t_s1680x1050c5/g5/M00/0F/01/ChMkJ1eYkamIMtW7ABkjmIMQx-oAAT5awCEOJAAGSOw652.jpg";

    public static final String girl = "https://raw.githubusercontent.com/sfsheng0322/GlideImageView/master/screenshot/girl.jpg";
    public static final String girl_thumbnail = "https://raw.githubusercontent.com/sfsheng0322/GlideImageView/master/screenshot/girl_thumbnail.jpg";

    public static final String cat = "https://raw.githubusercontent.com/sfsheng0322/GlideImageView/master/screenshot/cat.jpg";
    public static final String cat_thumbnail = "https://raw.githubusercontent.com/sfsheng0322/GlideImageView/master/screenshot/cat_thumbnail.jpg";


    private RxPermissions mRxPermissions;
    private ProgressBar mProgressBar;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxPermissions = new RxPermissions(this);

        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        ImageLoaderUtils.getInstance().loadImage(girl_thumbnail, mImageView1);
        ImageLoaderUtils.getInstance().loadImage(IMAGE_URL2, mImageView2);
        ImageLoaderUtils.getInstance().loadCircleImage(IMAGE_URL3, R.mipmap.ic_launcher, mImageView3);

        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SingleImageActivity.class);
                // 转场动画ActivityOptions,ActivityOptionsCompat是兼容包
                ActivityOptionsCompat compat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(MainActivity.this, mImageView1, getString(R.string.transitional_image));
                ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());
            }
        });
    }

    /**
     * 下载图片
     */
    public void downloadPic(View view) {

        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            download();
                        } else {
                            Toast.makeText(MainActivity.this, "没有SdCard权限!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void download() {
        ImageLoaderUtils.getInstance().saveImage(MainActivity.this, IMAGE_URL6,
                getExternalCacheDir().getAbsolutePath(), "pic", new ImageSaveListener() {
                    @Override
                    public void onSaveSuccess() {
                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSaveFail() {
                        Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(final int progress) {
                        mProgressBar.setProgress(progress);
                    }
                });
    }

}
