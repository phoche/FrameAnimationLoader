package com.example.bolo.os_application.media.player;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by mac on 18/4/29.
 */

public interface IMediaPlayer {
    int MEDIA_INFO_UNKNOWN = 1;
    int MEDIA_INFO_STARTED_AS_NEXT = 2;
    int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    int MEDIA_INFO_BUFFERING_START = 701;
    int MEDIA_INFO_BUFFERING_END = 702;
    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    int MEDIA_INFO_BAD_INTERLEAVING = 800;
    int MEDIA_INFO_NOT_SEEKABLE = 801;
    int MEDIA_INFO_METADATA_UPDATE = 802;
    int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
    int MEDIA_INFO_BACK_TO_VIDEO = 1001;
    ////////上面是Ijk和android Media公用////

    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
    int MEDIA_INFO_AUDIO_RENDERING_START = 10002;
    int MEDIA_INFO_AUDIO_DECODED_START = 10003;
    int MEDIA_INFO_VIDEO_DECODED_START = 10004;
    int MEDIA_INFO_OPEN_INPUT = 10005;
    int MEDIA_INFO_FIND_STREAM_INFO = 10006;
    int MEDIA_INFO_COMPONENT_OPEN = 10007;
    int MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE = 10100;
    int MEDIA_ERROR_UNKNOWN = 1;
    int MEDIA_ERROR_SERVER_DIED = 100;
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    int MEDIA_ERROR_IO = -1004;
    int MEDIA_ERROR_MALFORMED = -1007;
    int MEDIA_ERROR_UNSUPPORTED = -1010;
    int MEDIA_ERROR_TIMED_OUT = -110;

    void setDisplay(SurfaceHolder surfaceHolder);

    void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException,
            SecurityException, IllegalStateException;

    @TargetApi(14)
    void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(FileDescriptor fileDescriptor) throws IOException,
            IllegalArgumentException, IllegalStateException;

    void setDataSource(String dataSource) throws IOException, IllegalArgumentException,
            SecurityException, IllegalStateException;

    String getDataSource();

    void prepareAsync() throws IllegalStateException;

    void start() throws IllegalStateException;

    void stop() throws IllegalStateException;

    void pause() throws IllegalStateException;

    void setScreenOnWhilePlaying(boolean var1);

    int getVideoWidth();

    int getVideoHeight();

    boolean isPlaying();

    void seekTo(long seekTo) throws IllegalStateException;

    long getCurrentPosition();

    long getDuration();

    void release();

    void reset();

    void setVolume(float var1, float var2);

    int getAudioSessionId();

    /**
     * @deprecated
     */
    @Deprecated
    boolean isPlayable();

    void setOnPreparedListener(IOnPreparedListener onPreparedListener);

    void setOnCompletionListener(IOnCompletionListener onCompletionListener);

    void setOnBufferingUpdateListener(IOnBufferingUpdateListener
                                              onBufferingUpdateListener);

    void setOnSeekCompleteListener(IOnSeekCompleteListener onSeekCompleteListener);

    void setOnVideoSizeChangedListener(IOnVideoSizeChangedListener
                                               onVideoSizeChangedListener);

    void setOnErrorListener(IOnErrorListener onErrorListener);

    void setOnInfoListener(IOnInfoListener onInfoListener);

    void setOnTimedTextListener(IOnTimedTextListener onTimedTextListener);

    void setAudioStreamType(int var1);

    /**
     * @deprecated
     */
    @Deprecated
    void setKeepInBackground(boolean var1);

    int getVideoSarNum();

    int getVideoSarDen();

    /**
     * @deprecated
     */
    @Deprecated
    void setWakeMode(Context var1, int var2);

    void setLooping(boolean var1);

    boolean isLooping();

    void setSurface(Surface var1);

    void setDataSource(IMediaDataSource var1);

    interface IOnInfoListener {
        boolean onInfo(IMediaPlayer var1, int what, int extra);
    }

    interface IOnErrorListener {
        boolean onError(IMediaPlayer var1, int var2, int var3);
    }

    interface IOnVideoSizeChangedListener {
        void onVideoSizeChanged(IMediaPlayer var1, int var2, int var3, int var4, int var5);
    }

    interface IOnSeekCompleteListener {
        void onSeekComplete(IMediaPlayer var1);
    }

    interface IOnBufferingUpdateListener {
        void onBufferingUpdate(IMediaPlayer var1, int var2);
    }

    interface IOnCompletionListener {
        void onCompletion(IMediaPlayer var1);
    }

    interface IOnPreparedListener {
        void onPrepared(IMediaPlayer var1);
    }

    interface IOnTimedTextListener {
        void onTimedText(IMediaPlayer var1, IjkTimedText var2);
    }
}
