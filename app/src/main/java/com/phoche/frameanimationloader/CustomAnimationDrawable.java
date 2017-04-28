package com.phoche.frameanimationloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by qinpc on 2017/4/26
 */
public class CustomAnimationDrawable extends AnimationDrawable {

    private Resources mResources;

    public interface CallBack {

        void onEnd();
    }

    private CustomAnimationDrawable.CallBack callback;

    private int mCurrentIndex = -1;
    private int mDuration = 45;

    private List<Integer> mRes = new ArrayList<>();

    private List<String> mIds = new ArrayList<>();

    private LruCache<Object, BitmapDrawable> mCache;
    private LruCache<Object, Bitmap> mLruCache;

    private FrameAnimLoader.ViewSize mViewSize;


    public CustomAnimationDrawable(FrameAnimLoader.ViewSize viewSize) {
        mViewSize = viewSize;
        init();
    }

    private void init() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheMemory = (int) (maxMemory / 8);
        mCache = new LruCache<>(cacheMemory);
        mLruCache = new LruCache<>(cacheMemory);

    }


    @Override
    public void start() {
        super.start();
        mCurrentIndex = 0;
    }

    @Override
    public void stop() {
        super.stop();
        mCurrentIndex = 0;
    }

    @Override
    public void addFrame(Drawable frame, int duration) {
        super.addFrame(frame, duration);
    }

    public void addFrame(String path) {
        mIds.add(path);
    }

    public void addFrame(Context context, int resId) {
        this.mResources = context.getResources();
        mRes.add(resId);
    }

    public void setDuration(int millisecond) {
        mDuration = millisecond;
    }

    public void beforeStart() {
        if (mIds.size() > 0) {
            addFrameFormPath();
        } else {
            addFrameFormRes();
        }

    }

    private void addFrameFormRes() {
        for (int i = 0; i < mRes.size(); i++) {
            int id = mRes.get(i);
            addDrawable2Frame(id, null);
        }
    }

    private void addDrawable2Frame(int id, String path) {
        Object key = TextUtils.isEmpty(path) ? id : path;
        Bitmap bitmap1 = mLruCache.get(key);
        if (null == bitmap1 || bitmap1.isRecycled()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            decoeBitmap(id, path, opts);
            opts.inSampleSize = caculateInSampleSize(opts, mViewSize.width, mViewSize.height);
            opts.inJustDecodeBounds = false;
            opts.inDither = false;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            Bitmap bitmap = decoeBitmap(id, path, opts);
            BitmapDrawable frame = new BitmapDrawable(null, bitmap);
            addFrame(frame, mDuration);
            Object obj = TextUtils.isEmpty(path) ? id : path;
            if (obj != null & bitmap != null) {
                mLruCache.put(obj, bitmap);
            }

        } else {
            BitmapDrawable drawable = new BitmapDrawable(null, bitmap1);
            addFrame(drawable, mDuration);
        }
    }

    private Bitmap decoeBitmap(int id, String path, BitmapFactory.Options opts) {

        Bitmap bitmap = null;
        if (TextUtils.isEmpty(path)) {
            bitmap = BitmapFactory.decodeResource(mResources, id, opts);
        } else {
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
            bitmap = BitmapFactory.decodeFile(path, opts);
        }
        return bitmap;
    }

    private void addFrameFormPath() {
        for (int i = 0; i < mIds.size(); i++) {
            String path = mIds.get(i);
            addDrawable2Frame(0, path);
        }
    }

    @Override
    public void run() {
        super.run();
        isLast();
    }

    private boolean isLast() {
        if (getNumberOfFrames() <= 1) {
            if (null != callback) {
                callback.onEnd();
            }
            return true;
        }

        if (mCurrentIndex != -1) {
            mCurrentIndex++;
            if (mCurrentIndex >= getNumberOfFrames() - 1) {
                if (null != callback) {
                    callback.onEnd();
                }
                return true;
            }
            return false;
        }
        int currentIndex = 0;
        Field field = null;
        try {
            field = this.getClass().getSuperclass().getDeclaredField("mCurFrame");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            currentIndex = field.getInt(this);
            mCurrentIndex = currentIndex;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        field.setAccessible(false);
        if (currentIndex >= getNumberOfFrames() && !isOneShot()) {
            if (null != callback) {
                callback.onEnd();
            }
            return true;
        }

        return false;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width > reqWidth || reqHeight > reqHeight) {
            int widthRound = Math.round(width * 1.0f / reqWidth);
            int heightRound = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRound, heightRound);
        }
        return inSampleSize;
    }

    public void setCallback2(CallBack callback) {
        this.callback = callback;
    }

    public void trimCache() {
        mLruCache.evictAll();
        mLruCache = null;
    }
}
