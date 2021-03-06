package com.febers.uestc_bbs.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import android.util.Log.i
import android.view.View
import com.febers.uestc_bbs.base.*
import com.febers.uestc_bbs.module.login.model.LoginContext
import com.febers.uestc_bbs.module.image.AppImgViewer
import com.febers.uestc_bbs.module.message.view.PMDetailActivity
import com.febers.uestc_bbs.module.post.view.PostDetailActivity
import com.febers.uestc_bbs.module.post.view.edit.PostEditActivity
import com.febers.uestc_bbs.module.user.view.UserDetailActivity
import org.jetbrains.anko.browse

object ViewClickUtils {

    /**
     * TODO 点击站内链接时自动跳转，无需打开浏览器
     */
    fun linkClick(url: String, context: Context) {
//        i("Link ", url)
        if (url.endsWith(".gif")) {
            return
        }
        context.browse(url, true)
    }

    fun clickToImageViewerByUid(uid: Int?, context: Context?) {
        uid ?: return
        context ?: return
        clickToImageViewer(url = "http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=$uid&size=big", context = context)
    }

    /**
     * 打开ImageViewer，目前调用一个Activity进行展示，待改进
     * @param url
     * @param context
     * @param transitionView 进行页面共享的view
     * @param transitionViewName 进行页面共享的view的name
     * 页面共享的文章见:https://blog.csdn.net/sinat_31057219/article/details/78095038
     * 目前没有进行相应的调用
     */
    fun clickToImageViewer(url: String?, context: Context?, transitionView: View? = null, transitionViewName: String? = null) {
        url ?: return
        context ?: return
        var bundle: Bundle? = null
        if (transitionView != null && transitionViewName != null) {
            if (context is Activity)
                bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(context, transitionView, transitionViewName).toBundle()
            context.startActivity(Intent(context, AppImgViewer::class.java).apply {
                putExtra(IMAGE_URL, url)
            }, bundle)
            (context as Activity).overridePendingTransition(0, 0)
        } else {
            context.startActivity(Intent(context, AppImgViewer::class.java).apply {
                putExtra(IMAGE_URL, url)
            })
        }
    }


    fun clickToUserDetail(context: Context?, uid: Int?) {
        context ?: return
        if (uid == null) return
        if (!LoginContext.userState(context)) return
        context.startActivity(Intent(context, UserDetailActivity::class.java).apply {
            putExtra(USER_IT_SELF, false)
            putExtra(USER_ID, uid)
        })

    }

    fun clickToPostDetail(context: Context?, fid: Int?) {
        context ?: return
        if (fid == 0 || fid == null) return
        if (!LoginContext.userState(context)) return
        context.startActivity(Intent(context, PostDetailActivity::class.java).apply {
            putExtra(FID, fid)
        })
    }

    fun clickToPrivateMsg(context: Context?, uid: Int?, userName: String?) {
        context ?: return
        userName ?: return
        if (uid == 0 ||uid == null) return
        if (!LoginContext.userState(context)) return
        context.startActivity(Intent(context, PMDetailActivity::class.java).apply {
            putExtra(USER_ID, uid)
            putExtra(USER_NAME, userName)
        })
    }

    fun clickToPostEdit(context: Context?, fid: Int, title: String) {
        context ?:return
        if (!LoginContext.userState(context)) return
        context.startActivity(Intent(context, PostEditActivity::class.java).apply {
            putExtra(FID, fid)
            putExtra(TITLE, title)
        })
    }

    fun clickToAppWeb(context: Context?, url: String?) {
        context ?: return
        url ?: return
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
//        context.startActivity(Intent(context, AppWebViewer::class.java).apply {
//            putExtra(URL, url)
//        })

    }
}