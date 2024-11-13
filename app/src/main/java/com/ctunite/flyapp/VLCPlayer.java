package com.ctunite.flyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.ctunite.flyapp.util.Utils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * VLC播放视频工具类
 */
public class VLCPlayer{

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;

    private int videoWidth = 0;  //视频宽度
    private int videoHeight = 0; //视频高度

    //初始化vlc参数
    public VLCPlayer(Context context) {
        ArrayList<String> options = new ArrayList<>();
        options.add("--no-drop-late-frames"); //防止掉帧
        options.add("--no-skip-frames"); //防止掉帧
        options.add("--rtsp-tcp");//强制使用TCP方式
        options.add("--avcodec-hw=any"); //尝试使用硬件加速
        options.add("--live-caching=0");//缓冲时长
        options.add("--file-caching=500"); //

        libVLC = new LibVLC(context, options);
        mediaPlayer = new MediaPlayer(libVLC);

    }

    /**
     * 设置播放视图
     * @param textureView
     */
    public void setVideoSurface(TextureView textureView) {
        mediaPlayer.getVLCVout().setVideoView(textureView);
//        mediaPlayer.getVLCVout().setVideoSurface(textureView.getSurfaceTexture());
        mediaPlayer.getVLCVout().setWindowSize(textureView.getWidth(), textureView.getHeight());

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // 获取新的宽度和高度
                int newWidth = right - left;
                int newHeight = bottom - top;
                // 设置VLC播放器的宽高参数
                mediaPlayer.getVLCVout().setWindowSize(newWidth, newHeight);
            }
        });
        mediaPlayer.getVLCVout().attachViews();
    }

//    @Override
//    public void onSurFaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height){
//
//    }

    /**
     * 设置播放地址
     * @param url
     */
    public void setDataSource(String url) {
        try {
            Media media = new Media(libVLC, Uri.parse(url));
            media.setHWDecoderEnabled(false, false);
            mediaPlayer.setMedia(media);
            media.release();
        }catch (Exception e){
            Log.e("VLCPlayer",e.getMessage(),e);
        }
    }

    /**
     * 播放
     */
    public void play() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.play();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.pause();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
    }


    /**
     * 释放资源
     */
    public void release() {
        if(mediaPlayer!=null) {
            mediaPlayer.release();
        }
        if(libVLC!=null) {
            libVLC.release();
        }
    }

    /**
     * 录制视频
     * @param filePath 保存文件的路径
     */
    public boolean startRecording(String filePath) {
        if (mediaPlayer == null) {
            return false;
        }
        return mediaPlayer.record(filePath);
    }

    /**
     * 停止录制
     *
     */
    public void stopRecording(){
        if (mediaPlayer == null){
            return;
        }
        mediaPlayer.record(null);
    }

    /**
     * 截图
     *
     */
    /**
     * 截图保存
     *
     * @param textureView
     */
    public boolean takeSnapShot(TextureView textureView, String path) {
        videoWidth = textureView.getWidth();
        videoHeight = textureView.getHeight();
        if(videoHeight == 0 || videoWidth == 0){
            return false;
        }
        Bitmap snapshot = textureView.getBitmap();
        if (snapshot != null) {
            // 获取TextureView的尺寸和视频的尺寸
            int viewWidth = textureView.getWidth();
            int viewHeight = textureView.getHeight();

            // 计算视频在TextureView中的实际显示区域
            float viewAspectRatio = (float) viewWidth / viewHeight;
            float videoAspectRatio = (float) videoWidth / videoHeight;

            int left, top, width, height;
            if (viewAspectRatio > videoAspectRatio) {
                // 视频在TextureView中是上下居中显示的
                width = viewWidth; // 宽度为屏幕宽度
                height = viewWidth * videoHeight / videoWidth; // 计算对应的高度
                left = 0; // 起始位置为左边
                top = (viewHeight - height) / 2; // 计算上边距，保证视频在TextureView中居中
            } else {
                // 视频在TextureView中是左右居中显示的
                width = viewWidth;
                height = viewWidth * videoHeight / videoWidth;
                left = 0;
                top = (viewHeight - height) / 2;
            }

            // 截取视频的实际显示区域
            Bitmap croppedSnapshot = Bitmap.createBitmap(snapshot, left, top, width, height);

            try {
                File snapshotFile = new File(path, "IMG_"+Utils.getDateStr()+"_"+(Math.random()*9+1)*10000+".jpg");
                FileOutputStream outputStream = new FileOutputStream(snapshotFile);
                boolean result = croppedSnapshot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                if (result) {
                    Log.i("fff", "保存图片" + path);
                    return true;
                }else {
                    return false;
                }

            } catch (IOException e) {
                Log.e("VlcPlayer",e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    //通知手机广播

//    @Override
//    public void onEvent(MediaPlayer.Event event) {
//        switch (event.type) {
//            case MediaPlayer.Event.Buffering:
//                // 处理缓冲事件
//                if (callback != null) {
//                    callback.onBuffering(event.getBuffering());
//                }
//                break;
//            case MediaPlayer.Event.EndReached:
//                // 处理播放结束事件
//                if (callback != null) {
//                    callback.onEndReached();
//                }
//                break;
//            case MediaPlayer.Event.EncounteredError:
//                // 处理播放错误事件
//                if (callback != null) {
//                    callback.onError();
//                }
//                break;
//            case MediaPlayer.Event.TimeChanged:
//                // 处理播放进度变化事件
//                if (callback != null) {
//                    callback.onTimeChanged(event.getTimeChanged());
//                }
//                break;
//            case MediaPlayer.Event.PositionChanged:
//                // 处理播放位置变化事件
//                if (callback != null) {
//                    callback.onPositionChanged(event.getPositionChanged());
//                }
//                break;
//            case MediaPlayer.Event.Vout:
////                //在视频开始播放之前，视频的宽度和高度可能还没有被确定，因此我们需要在MediaPlayer.Event.Vout事件发生后才能获取到正确的宽度和高度
////                IMedia.VideoTrack vtrack = (IMedia.VideoTrack) mediaPlayer.getSelectedTrack(Media.Track.Type.Video);
////                videoWidth = vtrack.width;
////                videoHeight = vtrack.height;
//                break;
//        }
//    }


    private VLCPlayerCallback callback;

    public void setCallback(VLCPlayerCallback callback) {
        this.callback = callback;
    }

    public interface VLCPlayerCallback {
        void onBuffering(float bufferPercent);
        void onEndReached();
        void onError();
        void onTimeChanged(long currentTime);
        void onPositionChanged(float position);
    }

}
