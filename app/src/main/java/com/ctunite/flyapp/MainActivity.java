package com.ctunite.flyapp;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.ctunite.flyapp.util.Utils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.util.VLCVideoLayout;


import java.io.File;
import java.util.ArrayList;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private NodePlayerView nodePlayerView;
    private NodePlayer nodePlayer;
    private Button startLive;
    private Button playVideo;
    private Button stopVideo;
    private Button startRecording;
    private Button stopRecording;
    private Button snapShot;
    private EditText rtspAddress;
    private LibVLC libVLC;
    private VLCVideoLayout vlcVideoLayout;
    private TextureView videoView;
    private MediaPlayer mediaPlayer;
    private  String rtspAddressString;

    private boolean isRecord = false;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private ProgressBar progressBar;
    private VLCPlayer vlcPlayer;
    private boolean isRecording;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playVideo = (Button) findViewById(R.id.play_vedio);
        stopVideo = (Button) findViewById(R.id.stop_vedio);
        rtspAddress = (EditText) findViewById(R.id.rtsp_address);
        startRecording = (Button) findViewById(R.id.start_recording);
        stopRecording = (Button) findViewById(R.id.stop_recording);
        snapShot = (Button) findViewById(R.id.snapShot);
        videoView = findViewById(R.id.video_view);

        //点击播放按钮，获取输入的rtsp地址，播放视频
        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rtspAddressString = rtspAddress.getText().toString();
                playVideo();
            }
        });

        //点击停止按钮
        stopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });

        //点击录制按钮
        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限
                requestStoragePermission();
                startRecording(Utils.getSDPath());
            }
        });

        //点击停止
        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        //点击截图
        snapShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限
                requestStoragePermission();
                boolean result = vlcPlayer.takeSnapShot(videoView, Utils.getSDPath());
                if (result) {
                    refreshCamera();
                    Toast.makeText(MainActivity.this, "截图已保存到"+Utils.getSDPath(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "截图失败", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    //通知手机更新图片
    private void refreshCamera(){
        //通知手机相册更新图片
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(Utils.getSDPath()));
        intent.setData(uri);
        sendBroadcast(intent);
    }

    //vlc播放视频
    private void playVideo(){
        //初始化vlc
        vlcPlayer = new VLCPlayer(this);
        //设置播放视图
        vlcPlayer.setVideoSurface(videoView);
        //设置播放地址
        vlcPlayer.setDataSource(rtspAddressString);
        //播放
        vlcPlayer.play();
    }
    //停止播放
    private void stopPlay(){
        vlcPlayer.stop();
    }

    //录制屏幕
    private void startRecording(String filePath){
        if (!isRecord) {
            boolean result = vlcPlayer.startRecording(filePath);
            if (result) {
                Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "录制失败", Toast.LENGTH_SHORT).show();
            }
            isRecord = true;
        }else {
            stopRecording();
            isRecord = false;
        }
    }
    //停止录制
    private void stopRecording(){
        vlcPlayer.stopRecording();
        Toast.makeText(this, "录制结束,视频已保存到"+Utils.getSDPath(), Toast.LENGTH_SHORT).show();
        isRecord = false;
    }


    //申请权限
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }


   //回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording(Utils.getSDPath());
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
}