package com.phoche.frameanimationloader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Create by qinpc on 2017/6/14
 */
public class FrameImageView extends ImageView {

    private int mWidht;
    private int mHeight;
    private int mDuration;
    private boolean mOneShot;
    private int[] mResArr;
    private String mDir;
    private FrameAnimLoader mAnimLoader;

    public FrameImageView(Context context) {
        this(context, null);
    }

    public FrameImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidht = getWidth();
        mHeight = getHeight();
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTotalDuration(int duration) {
        mDuration = duration;
    }

    public void setOneShot(boolean oneShot) {
        mOneShot = oneShot;
    }

    public void setResources(int[] ids) {
        mResArr = ids;
    }

    public void setAnimDir(String path) {
        mDir = path;
    }

    public void playFrame() {
        mAnimLoader = new FrameAnimLoader.Builder(this)
                .setDuration(mDuration)
                .setOneShot(mOneShot)
                .addAnimDir(mDir)
//                .addResourceArr(mResArr)
                .setViewSize(new FrameAnimLoader.ViewSize(mWidht, mHeight))
                .build();
        mAnimLoader.startAnim();
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        mAnimLoader.cleanAnim();
    }
}
