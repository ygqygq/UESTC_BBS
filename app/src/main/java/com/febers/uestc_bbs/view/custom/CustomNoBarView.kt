package com.febers.uestc_bbs.view.custom

import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.widget.Toolbar
import android.util.AttributeSet
import com.febers.uestc_bbs.utils.StatusBarUtils

/**
 * 自定义控件，在其中令其paddingTop为statusBar的高度
 * 实现statusBar的隐藏与视图的完美显示
 */
class CustomAppBarLayout @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : AppBarLayout(mContext, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        setPadding(paddingLeft, paddingTop + StatusBarUtils.getHeight(mContext), right, bottom)
    }
}

class CustomToolbar @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Toolbar(mContext, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        setPadding(paddingLeft, paddingTop + StatusBarUtils.getHeight(mContext), right, bottom)
    }
}

class CustomTabLayout @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TabLayout(mContext, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        setPadding(paddingLeft, paddingTop + StatusBarUtils.getHeight(mContext), right, bottom)
    }
}

class CustomCollapsingToolbarLayout @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CollapsingToolbarLayout(mContext, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        setPadding(paddingLeft, paddingTop + StatusBarUtils.getHeight(mContext), right, bottom)
    }
}