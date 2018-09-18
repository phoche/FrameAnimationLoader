/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bolo.os_application.media.helper;

import android.view.View;

import com.example.bolo.os_application.media.IRenderView;

import java.lang.ref.WeakReference;


public final class MeasureHelper {
    private WeakReference<View> mWeakView;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;

    public MeasureHelper(View view) {
        mWeakView = new WeakReference<View>(view);
    }

    public View getView() {
        if (mWeakView == null)
            return null;
        return mWeakView.get();
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * https://www.cnblogs.com/bloodofhero/p/4920659.html
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }

        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mCurrentAspectRatio == IRenderView.AR_MATCH_PARENT) {//非等比例拉伸画面填满整个View
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            //wrap_content
            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                //计算view本身的宽高比
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                float displayAspectRatio;
                switch (mCurrentAspectRatio) {//宽高比
                    case IRenderView.AR_16_9_FIT_PARENT:
                        displayAspectRatio = 16.0f / 9.0f;
                        //如果画面旋转，则宽高比对调
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case IRenderView.AR_4_3_FIT_PARENT:
                        displayAspectRatio = 4.0f / 3.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        //保持视频本身真实的宽高比
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        if (mVideoSarNum > 0 && mVideoSarDen > 0)
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        break;
                }
                //视频比View的宽高比>View的宽高比
//
//                boolean shouldBeWider = mVideoWidth / mVideoHeight > widthSpecSize／heightSpecSize;
//                float wRatio = (float) mVideoWidth / (float) widthSpecSize;
//                float hRatio = (float) mVideoHeight / (float) heightSpecSize;
//                boolean shouldBeWider = wRatio > hRatio;
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;
                switch (mCurrentAspectRatio) {
                    case IRenderView.AR_ASPECT_FIT_PARENT://
                    case IRenderView.AR_16_9_FIT_PARENT:
                    case IRenderView.AR_4_3_FIT_PARENT:
                        if (shouldBeWider) {
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }

                        break;
                    case IRenderView.AR_ASPECT_FILL_PARENT://等比例缩放视频
                        //如果宽高变化率，宽的变化率较大，则以变化率较小的高度为基准进行缩放
                        if (shouldBeWider) {
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {//如果宽高变化率，高的变化率较大，则宽度为基准进行缩放
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }

                        break;
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        if (shouldBeWider) {
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                //http://cyhmna.iteye.com/blog/1493449
                // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
//                float wRatio = (float) mVideoWidth / (float) width;
//                float hRatio = (float) mVideoHeight / (float) height;
//
//                if (mVideoWidth > width || mVideoHeight > height) {//视频缩小
//                    // 选择大的一个进行作为基准缩放
//                    float ratio = Math.max(wRatio, hRatio);
//                    width = (int) Math.ceil((float) mVideoWidth / ratio);
//                    height = (int) Math.ceil((float) mVideoHeight / ratio);
//                } else if (mVideoWidth <= width && mVideoHeight <= height) {//视频需要放大
//                    float ratio = Math.max(wRatio, hRatio);
//                    width = (int) Math.ceil((float) mVideoWidth / ratio);
//                    height = (int) Math.ceil((float) mVideoHeight / ratio);
//                }


//                //视频的宽高比小于View的宽高比： // 选择大的一个进行作为基准缩放，可能有裁剪
                if (mVideoWidth * height < width * mVideoHeight) {//wRatio<hRation
                    width = height * mVideoWidth / mVideoHeight;//mVideoWidth*hRation
                } else if (mVideoWidth * height > width * mVideoHeight) {//wRatio>hRation
                    height = width * mVideoHeight / mVideoWidth;//mVideoHeight*wRation
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }
        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
    }
}
