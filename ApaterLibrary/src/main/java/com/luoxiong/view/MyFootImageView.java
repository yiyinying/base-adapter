package com.luoxiong.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ================================================
 * 作    者：lx
 * 创建日期：2016/10/29
 * 描    述：
 * ================================================
 */
public class MyFootImageView extends ImageView {

    private AnimationDrawable mAnim=null;

    public MyFootImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAnim = (AnimationDrawable) getBackground();
        mAnim.start();
    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (mAnim != null) {
//            if (mAnim.isRunning()) {
//                mAnim.stop();
//            }
//        }
//    }
//
//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        int a=visibility;
//        if (a == 0) {
//            if (mAnim != null) {
//                if (!mAnim.isRunning()) {
//                    mAnim.start();
//                }
//            }else{
//                mAnim = (AnimationDrawable) getBackground();
//                mAnim.start();
//            }
//        }
//    }
}
