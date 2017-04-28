package com.phoche.frameanimationloader;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Create by qinpc on 2017/4/25
 */
public class FrameAnimLoader {

    // 子线程用于获取AmimationDrawable
    private Thread mBitmapThread;
    // 开始动画
    private UIHandler mUIHandler;
    private ImageView mView;
    // 动画对应的view的size
    private ViewSize mViewSize;

    private volatile CustomAnimationDrawable mAnim;
    private boolean mOneShot = true;
    private int mDuration;
    private OnFrameAnimListener mOnFrameAnimListener;
    private Semaphore mSemaphore = new Semaphore(0);

    public FrameAnimLoader() {
        init();
    }

    private void init() {

        mUIHandler = new UIHandler(this);
    }

    public synchronized void startAnim(final String dir) {
        acquireSemaphore();
        mBitmapThread = new Thread() {
            @Override
            public void run() {
                mAnim = getFrameDrawable(dir);
                mUIHandler.sendEmptyMessage(0x110);
            }
        };
        mBitmapThread.start();
    }

    private void acquireSemaphore() {
        try {
            if (null != mBitmapThread && mBitmapThread.isAlive()) {
                mSemaphore.acquire();
            }
        } catch (InterruptedException e) {
        }
    }

    public synchronized void startAnim(@DrawableRes final int[] ids) {
        acquireSemaphore();
        mBitmapThread = new Thread() {
            @Override
            public void run() {
                mAnim = getFrameDrawable(ids);
                mUIHandler.sendEmptyMessage(0x110);
            }
        };
        mBitmapThread.start();
    }


    private static class UIHandler extends Handler {

        private WeakReference<FrameAnimLoader> ref;

        public UIHandler(FrameAnimLoader act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            FrameAnimLoader loader = ref.get();
            if (loader == null) {
                return;
            }
            loader.mView.setImageDrawable(null);
            loader.start();
        }
    }

    private void start() {
        mAnim.setOneShot(mOneShot);
        mView.setImageDrawable(mAnim);
        mAnim.setCallback2(new CustomAnimationDrawable.CallBack() {
            @Override
            public void onEnd() {
                if (mOneShot) {
                    if (mOnFrameAnimListener != null) {
                        mOnFrameAnimListener.onEnd();
                    }
                    cleanAnim();
                    mSemaphore.release();
                }
            }
        });
        mAnim.start();
    }

    public void cleanAnim() {
        if (null != mAnim) {
            mView.setImageDrawable(null);
            mAnim.trimCache();
            mAnim = null;
        }
    }


    public static class Builder {

        private FrameAnimLoader mFrameAnimLoader;

        public Builder(ImageView imageView) {
            mFrameAnimLoader = new FrameAnimLoader();
            mFrameAnimLoader.mView = imageView;
            mFrameAnimLoader.mViewSize = mFrameAnimLoader.getViewSize(imageView);
        }

        /**
         * 每帧时间 ms
         *
         * @param millisecond
         * @return
         */
        public Builder setDuration(int millisecond) {
            mFrameAnimLoader.mDuration = millisecond;
            return this;
        }

        /**
         * 是否只播放一次
         *
         * @param oneShot false -- 一直播放
         * @return
         */
        public Builder setOneShot(boolean oneShot) {
            mFrameAnimLoader.mOneShot = oneShot;
            return this;
        }

        /**
         * 动画结束监听
         * @param listener
         * @return
         */
        public Builder addEndListener(OnFrameAnimListener listener) {
            mFrameAnimLoader.mOnFrameAnimListener = listener;
            return this;
        }

        public FrameAnimLoader build() {

            return mFrameAnimLoader;
        }
    }


    private CustomAnimationDrawable getFrameDrawable(int[] ids) {
        CustomAnimationDrawable anim = new CustomAnimationDrawable(mViewSize);
        for (int id : ids) {
            anim.addFrame(mView.getContext(), id);
        }
        anim.setDuration(mDuration);
        anim.beforeStart();
        return anim;

    }

    private CustomAnimationDrawable getFrameDrawable(String dir) {
        CustomAnimationDrawable anim = new CustomAnimationDrawable(mViewSize);
        try {
            File file = new File(dir);
            List<File> fileList = Arrays.asList(file.listFiles());
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (File file1 : fileList) {
                String name = file1.getName();
                if (name.endsWith(".png") || name.endsWith(".jpg")) {
                    anim.addFrame(file1.getAbsolutePath());
                }
            }
            anim.setDuration(mDuration);
            anim.beforeStart();
        } catch (Exception e) {
        }
        return anim;
    }


    /**
     * 获取到Imageview的宽高
     * @param view
     * @return
     */
    private ViewSize getViewSize(ImageView view) {
        ViewSize viewSize = new ViewSize();
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();

        ViewGroup.LayoutParams params = view.getLayoutParams();
        int width = view.getWidth();
        if (width <= 0) width = params.width;
        if (width <= 0) width = getImageViewFieldValue(view, "mMaxWidth");
        if (width <= 0) width = displayMetrics.widthPixels;
        viewSize.width = width;

        int height = view.getHeight();
        if (height <= 0) height = params.height;
        if (height <= 0) height = getImageViewFieldValue(view, "mMaxHeight");
        if (height <= 0) height = displayMetrics.heightPixels;
        viewSize.height = height;

        return viewSize;
    }

    private int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;

        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }

        return value;
    }


    public static class ViewSize {
        int width;
        int height;
    }

    public interface OnFrameAnimListener {
        void onEnd();
    }

}
