package com.bolo.ffmpeglibrary;

/**
 * Create by bolo on 15/10/2018
 */
public class FFmpegUtil {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
    }

    public native String stringFromJNI();

    public native int run(int cmdLen, String[] cmd);
}
