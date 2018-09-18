package com.example.bolo.os_application.media.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * Created by mac on 18/4/29.
 */

public class IJkMediaPlayer extends AbstractMediaPlayer {
    private IjkMediaPlayer mInternalMediaPlayer;
    private IJKPlayerListenerHolder mIjkPlayerListenerAdapter;

    public IJkMediaPlayer() {
        mInternalMediaPlayer = createMediaPlayer();
        mIjkPlayerListenerAdapter = new IJKPlayerListenerHolder(this);
        this.attachInternalListeners();
    }

    private void attachInternalListeners() {
        this.mInternalMediaPlayer.setOnPreparedListener(mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnBufferingUpdateListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnCompletionListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnSeekCompleteListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnVideoSizeChangedListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnErrorListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnInfoListener(this.mIjkPlayerListenerAdapter);
        this.mInternalMediaPlayer.setOnTimedTextListener(this.mIjkPlayerListenerAdapter);
    }

    public static IjkMediaPlayer createMediaPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        return ijkMediaPlayer;
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setDisplay(surfaceHolder);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setDataSource(context, uri);
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setDataSource(context, uri, headers);
        }
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException, IllegalArgumentException, IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setDataSource(fileDescriptor);
        }
    }

    @Override
    public void setDataSource(String dataSource) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setDataSource(dataSource);
        }
    }

    @Override
    public String getDataSource() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.getDataSource();
        }
        return "";
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.prepareAsync();
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.start();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.stop();
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.pause();
        }
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOnWhilePlaying) {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.setScreenOnWhilePlaying(screenOnWhilePlaying);
        }
    }

    @Override
    public int getVideoWidth() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(long seekTo) throws IllegalStateException {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.seekTo(seekTo);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mInternalMediaPlayer != null) {
            return mInternalMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void release() {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.release();
            mInternalMediaPlayer = null;
        }
    }

    @Override
    public void reset() {
        if (mInternalMediaPlayer != null) {
            mInternalMediaPlayer.reset();
        }
    }

    @Override
    public void setVolume(float var1, float var2) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setVolume(var1, var2);
    }

    @Override
    public int getAudioSessionId() {
        if (mInternalMediaPlayer != null)
            return mInternalMediaPlayer.getAudioSessionId();
        return -1;
    }

    @Override
    public boolean isPlayable() {
        return mInternalMediaPlayer != null && mInternalMediaPlayer.isPlayable();
    }

    @Override
    public void setAudioStreamType(int var1) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setAudioStreamType(var1);
    }

    @Override
    public void setKeepInBackground(boolean var1) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setKeepInBackground(var1);
    }

    @Override
    public int getVideoSarNum() {
        if (mInternalMediaPlayer != null)
            return mInternalMediaPlayer.getVideoSarNum();
        return 1;
    }

    @Override
    public int getVideoSarDen() {
        if (mInternalMediaPlayer != null)
            return mInternalMediaPlayer.getVideoSarDen();
        return 1;
    }

    @Override
    public void setWakeMode(Context var1, int var2) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setWakeMode(var1, var2);
    }

    @Override
    public void setLooping(boolean var1) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setLooping(var1);
    }

    @Override
    public boolean isLooping() {
        return mInternalMediaPlayer != null && mInternalMediaPlayer.isLooping();
    }

    @Override
    public void setSurface(Surface var1) {
        if (mInternalMediaPlayer != null)
            mInternalMediaPlayer.setSurface(var1);
    }

    private class IJKPlayerListenerHolder implements
            IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnCompletionListener,
            IMediaPlayer.OnBufferingUpdateListener,
            IMediaPlayer.OnSeekCompleteListener,
            IMediaPlayer.OnVideoSizeChangedListener,
            IMediaPlayer.OnErrorListener,
            IMediaPlayer.OnInfoListener,
            IMediaPlayer.OnTimedTextListener {

        public final WeakReference<IJkMediaPlayer> mWeakMediaPlayer;


        public IJKPlayerListenerHolder(IJkMediaPlayer mp) {
            this.mWeakMediaPlayer = new WeakReference(mp);
        }

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            return self != null && IJkMediaPlayer.this.notifyOnInfo(what, extra);
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            return self != null && IJkMediaPlayer.this.notifyOnError(what, extra);
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer var1, int width, int height, int var4, int var5) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IJkMediaPlayer.this.notifyOnVideoSizeChanged(width, height, 1, 1);
            }
        }

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IJkMediaPlayer.this.notifyOnSeekComplete();
            }
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IJkMediaPlayer.this.notifyOnBufferingUpdate(percent);
            }
        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IJkMediaPlayer.this.notifyOnCompletion();
            }
        }

        @Override
        public void onPrepared(IMediaPlayer mp) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IJkMediaPlayer.this.notifyOnPrepared();
            }
        }

        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            IJkMediaPlayer self = (IJkMediaPlayer) this.mWeakMediaPlayer.get();
            if (self != null) {
                IjkTimedText ijkText = null;
                if (text != null) {
                    ijkText = new IjkTimedText(text.getBounds(), text.getText());
                }

                IJkMediaPlayer.this.notifyOnTimedText(ijkText);
            }
        }
    }

}
