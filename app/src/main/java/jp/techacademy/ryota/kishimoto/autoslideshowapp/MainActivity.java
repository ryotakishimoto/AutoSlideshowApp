package jp.techacademy.ryota.kishimoto.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    Cursor cursor = null;
    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された");
                } else {
                    Log.d("ANDROID", "許可されなかった");
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            setImageView();
        }
        //cursor.close();
    }

    @Override
    public void onClick(View v) {
        if (cursor == null) {
            ContentResolver resolver = getContentResolver();
            cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                    null, // 項目(null = 全項目)
                    null, // フィルタ条件(null = フィルタなし)
                    null, // フィルタ用パラメータ
                    null // ソート (null ソートなし)
            );
        }
        if (v.getId() == R.id.button1) {
            if (cursor.moveToPrevious()) {
                setImageView();
            } else if (cursor.moveToLast()) {
                setImageView();
            }
            //cursor.close();

            }else if(v.getId() == R.id.button2) {
                if (cursor != null) {
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cursor.moveToNext()) {
                                            setImageView();
                                        } else if (cursor.moveToFirst()) {
                                            setImageView();
                                        }

                                        Button button2 = (Button) findViewById(R.id.button2);
                                        button2.setText("停止");
                                        Button button1 = (Button) findViewById(R.id.button1);
                                        button1.setEnabled(false);
                                        Button button3 = (Button) findViewById(R.id.button3);
                                        button3.setEnabled(false);
                                    }
                                });
                            }
                        }, 100, 2000);
                    } else if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                                Button button2 = (Button) findViewById(R.id.button2);
                                button2.setText("再生");
                                Button button1 = (Button) findViewById(R.id.button1);
                                button1.setEnabled(true);
                                Button button3 = (Button) findViewById(R.id.button3);
                                button3.setEnabled(true);
                    }
                }

            }else if(v.getId() == R.id.button3) {
                        if (cursor != null) {
                            if (cursor.moveToNext()) {
                                setImageView();
                            } else if (cursor.moveToFirst()) {
                                setImageView();
                            }
                        }
            }
                        //cursor.close();
        }

        private void setImageView() {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }





