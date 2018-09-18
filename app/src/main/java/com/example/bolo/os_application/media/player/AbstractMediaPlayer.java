package com.example.bolo.os_application.media.player;

import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by mac on 18/4/29.
 */


public abstract class AbstractMediaPlayer implements IMediaPlayer {
    private IMediaPlayer.IOnPreparedListener mOnPreparedListener;
    private IMediaPlayer.IOnCompletionListener mOnCompletionListener;
    private IMediaPlayer.IOnBufferingUpdateListener mOnBufferingUpdateListener;
    private IMediaPlayer.IOnSeekCompleteListener mOnSeekCompleteListener;
    private IMediaPlayer.IOnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private IMediaPlayer.IOnErrorListener mOnErrorListener;
    private IMediaPlayer.IOnInfoListener mOnInfoListener;
    private IMediaPlayer.IOnTimedTextListener mOnTimedTextListener;


    public AbstractMediaPlayer() {
    }


    public void resetListeners() {
        this.mOnPreparedListener = null;
        this.mOnBufferingUpdateListener = null;
        this.mOnCompletionListener = null;
        this.mOnSeekCompleteListener = null;
        this.mOnVideoSizeChangedListener = null;
        this.mOnErrorListener = null;
        this.mOnInfoListener = null;
        this.mOnTimedTextListener = null;
    }

    protected final void notifyOnPrepared() {
        if (this.mOnPreparedListener != null) {
            this.mOnPreparedListener.onPrepared(this);
        }

    }

    protected final void notifyOnCompletion() {
        if (this.mOnCompletionListener != null) {
            this.mOnCompletionListener.onCompletion(this);
        }

    }

    protected final void notifyOnBufferingUpdate(int percent) {
        if (this.mOnBufferingUpdateListener != null) {
            this.mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }

    }

    protected final void notifyOnSeekComplete() {
        if (this.mOnSeekCompleteListener != null) {
            this.mOnSeekCompleteListener.onSeekComplete(this);
        }

    }

    protected final void notifyOnVideoSizeChanged(int width, int height, int sarNum, int sarDen) {
        if (this.mOnVideoSizeChangedListener != null) {
            this.mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height, sarNum, sarDen);
        }

    }

    protected final boolean notifyOnError(int what, int extra) {
        return this.mOnErrorListener != null && this.mOnErrorListener.onError(this, what, extra);
    }

    protected final boolean notifyOnInfo(int what, int extra) {
        return this.mOnInfoListener != null && this.mOnInfoListener.onInfo(this, what, extra);
    }

    protected final void notifyOnTimedText(IjkTimedText text) {
        if (this.mOnTimedTextListener != null) {
            this.mOnTimedTextListener.onTimedText(this, text);
        }

    }

    @Override
    public void setOnPreparedListener(IOnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    @Override
    public void setOnCompletionListener(IOnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    @Override
    public void setOnBufferingUpdateListener(IOnBufferingUpdateListener onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;

    }

    @Override
    public void setOnSeekCompleteListener(IOnSeekCompleteListener onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;

    }

    @Override
    public void setOnVideoSizeChangedListener(IOnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnErrorListener(IOnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    @Override
    public void setOnInfoListener(IOnInfoListener infoListener) {
        mOnInfoListener = infoListener;
    }

    @Override
    public void setOnTimedTextListener(IOnTimedTextListener onTimedTextListener) {
        mOnTimedTextListener = onTimedTextListener;
    }

    public void setDataSource(IMediaDataSource mediaDataSource) {
        throw new UnsupportedOperationException();
    }

}
