package com.ctunite.flyapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
    public static String TAG = "Utils";

    public static void getVideoInfo(Context context, MediaPlayer mMediaPlayer) {
        String info = "";
        Media media = (Media)mMediaPlayer.getMedia();
        for (int i=0; i<media.getTrackCount();i++){
            if (media.getTrack(i).type == 0) {
                Media.AudioTrack audioTrack = (Media.AudioTrack) media.getTrack(i);
                Log.d(TAG, "audioTrack rate:" + audioTrack.rate + " channels:" + audioTrack.channels +
                        " codec:" +  audioTrack.codec);
                info = info + "音频"+i+"\n" + "编码：" + audioTrack.codec + "\n"+ "声道：" + audioTrack.channels + "\n"
                        + "采样率：" + audioTrack.rate + "Hz\n\n";
            }
            else if (media.getTrack(i).type == 1) {
                Media.VideoTrack videoTrack = (Media.VideoTrack) media.getTrack(i);
                Log.d(TAG, "videoTrack width:" + videoTrack.width + " height:" +  videoTrack.height
                        + " codec:" +  videoTrack.codec + " language:" +  videoTrack.language + " level:" +  videoTrack.level);
                info = info + "视频"+i+"\n" + "编码：" + videoTrack.codec + "\n"+
                        "分辨率：" + videoTrack.width + "*" + videoTrack.height  + "\n"
                        + "帧率：" + videoTrack.frameRateNum + "\n\n";
            }
        }
        dialogInfo(context,"Video Info", info);
    }

    public static AlertDialog.Builder dialogInfo;
    public static void dialogInfo(Context context, String title, String message){
        dialogInfo = new AlertDialog.Builder(context)
                .setTitle(title)//设置title
                .setMessage(message)
                .setCancelable(false)//表示点击dialog其它部分不能取消(除了“取消”，“确定”按钮)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialogInfo.show();
    }

    public static AlertDialog.Builder dialogAudio;
    public static void dialogAudio(Context context, final MediaPlayer mediaPlayer){
        final MediaPlayer.TrackDescription[] audioTracks = mediaPlayer.getAudioTracks();
        int curAudio = mediaPlayer.getAudioTrack();

        String[] items = new String[audioTracks.length];
        int select = 0;
        for (int i = 0; i < audioTracks.length; i++) {
            Log.d(TAG, "audioTracks name:" + audioTracks[i].name + " id:" + audioTracks[i].id);
            items[i] = audioTracks[i].name;
            if (audioTracks[i].id == curAudio){
                select = i;
            }
        }

        dialogAudio = new AlertDialog.Builder(context);
        dialogAudio.setTitle("音轨设置");
        dialogAudio.setSingleChoiceItems(items, select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mediaPlayer.setAudioTrack(audioTracks[i].id);
            }
        });
        dialogAudio.show();
    }

    public static AlertDialog.Builder dialogSubtitle;
    public static void dialogSubtitle(Context context, final MediaPlayer mediaPlayer){
        final MediaPlayer.TrackDescription[] spuTracks = mediaPlayer.getSpuTracks();
        int curSubtitle = mediaPlayer.getSpuTrack();

        String[] items = new String[spuTracks.length];
        int select = 0;
        for (int i = 0; i < spuTracks.length; i++) {
            Log.d(TAG, "spuTracks name:" + spuTracks[i].name + " id:" + spuTracks[i].id);
            items[i] = spuTracks[i].name;
            if (spuTracks[i].id == curSubtitle){
                select = i;
            }
        }

        dialogAudio = new AlertDialog.Builder(context);
        dialogAudio.setTitle("字幕设置");
        dialogAudio.setSingleChoiceItems(items, select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mediaPlayer.setSpuTrack(spuTracks[i].id);
            }
        });
        dialogAudio.show();
    }

    //获取sd卡
    public static String getSDPath(){
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
//            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        }
        Log.d(TAG, "sdDir:" + sdDir);
        return sdDir;
    }

    /**
     * 返回指定格式的当前日期时间字符串
     *
     * @param format
     * @return
     */
    public static String getDateTimeStr(String format) {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat(format);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 返回缺省格式的当前日期时间字符串 默认格式:yyyy-mm-dd hh:mm:ss
     *
     * @return String
     */
    public static String getDateTimeStr() {
        return getDateTimeStr("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回自定义格式的当前日期时间字符串
     *
     * @param format
     *            格式规则
     * @return String 返回当前字符串型日期时间
     */
    public static String getDateStr(String format) {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat(format);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    /**
     * 返回当前日期字符串 缺省格式：yyyy-MM-dd
     *
     * @return String
     */
    public static String getDateStr() {
        return getDateStr("yyyy-MM-dd");
    }

    /**
     * 返回当前日期Date对象
     */
    public static Date getDate() {
        Object obj = TypeConvertUtil.convert(getDateStr(), "Date", "yyyy-MM-dd");
        if (obj != null)
            return (Date) obj;
        else
            return null;
    }

    /**
     * 返回当前日期Timestamp对象
     */
    public static Timestamp getDateTime() {
        Object obj = TypeConvertUtil.convert(getDateTimeStr(), "Timestamp", "yyyy-MM-dd HH:mm:ss");
        if (obj != null)
            return (Timestamp) obj;
        else
            return null;
    }
    /**
     * 返回字符串日期Timestamp对象
     * 字符串：yyyyMMddHHmmss
     */
    public static Timestamp getDateTime(String strDate) {
        Date date = stringToDate(strDate, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
        Timestamp dateStamp = new Timestamp(date.getTime());
        if (dateStamp != null)
            return dateStamp;
        else
            return null;
    }
    /**
     * 将字符串型日期转换为日期型
     *
     * @param strDate
     *            字符串型日期
     * @param srcDateFormat
     *            源日期格式
     * @param dstDateFormat
     *            目标日期格式
     * @return Date 返回的util.Date型日期
     */
    public static Date stringToDate(String strDate, String srcDateFormat, String dstDateFormat) {
        Date rtDate = null;
        Date tmpDate = (new SimpleDateFormat(srcDateFormat)).parse(strDate, new ParsePosition(0));
        String tmpString = null;
        if (tmpDate != null)
            tmpString = (new SimpleDateFormat(dstDateFormat)).format(tmpDate);
        if (tmpString != null)
            rtDate = (new SimpleDateFormat(dstDateFormat)).parse(tmpString, new ParsePosition(0));
        return rtDate;
    }


}
