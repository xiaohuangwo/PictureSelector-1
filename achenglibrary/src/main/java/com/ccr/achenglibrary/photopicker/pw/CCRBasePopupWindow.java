/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ccr.achenglibrary.photopicker.pw;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 在此写用途
 *
 * @Author: Acheng
 * @Email: 345887272@qq.com
 * @Date: 2017-09-22 16:47
 * @Version: V1.0 <描述当前版本功能>
 */
public abstract class CCRBasePopupWindow extends PopupWindow implements View.OnClickListener {
    protected Activity mActivity;
    protected View mWindowRootView;
    protected View mAnchorView;

    public CCRBasePopupWindow(Activity activity, @LayoutRes int layoutId, View anchorView, int width, int height) {
        super(View.inflate(activity, layoutId, null), width, height, true);
        init(activity, anchorView);

        initView();
        setListener();
        processLogic();
    }

    private void init(Activity activity, View anchorView) {
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        // 如果想让在点击别的地方的时候 关闭掉弹出窗体 一定要记得给mPopupWindow设置一个背景资源
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAnchorView = anchorView;
        mActivity = activity;
        mWindowRootView = activity.getWindow().peekDecorView();
    }

    protected abstract void initView();

    protected abstract void setListener();

    protected abstract void processLogic();

    public abstract void show();

    @Override
    public void onClick(View view) {
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) getContentView().findViewById(id);
    }
}