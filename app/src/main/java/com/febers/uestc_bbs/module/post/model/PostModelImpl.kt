/*
 * Created by Febers at 18-8-17 下午8:46.
 * Copyright (c). All rights reserved.
 * Last modified 18-8-17 下午8:46.
 */

package com.febers.uestc_bbs.module.post.model

import com.febers.uestc_bbs.base.*
import com.febers.uestc_bbs.entity.PostDetailBean
import com.febers.uestc_bbs.entity.PostFavResultBean
import com.febers.uestc_bbs.entity.PostVoteResultBean
import com.febers.uestc_bbs.entity.PostSendResultBean
import com.febers.uestc_bbs.module.post.contract.PostContract
import com.febers.uestc_bbs.module.post.model.http_interface.PostInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostModelImpl(val postPresenter: PostContract.Presenter): BaseModel(), PostContract.Model {

    override fun postDetailService(postId: Int, page: Int, authorId: Int, order: Int) {
        mPostId = postId.toString()
        mPage = page.toString()
        mAuthorId = authorId.toString()
        mOrder = order.toString()
        Thread(Runnable { getPost() }).start()
    }

    override fun postReplyService(isQuote: Int, replyId: Int, vararg contents: Pair<Int, String>) {
        Thread(Runnable {
            reply(isQuote, replyId, *contents)
        }).start()
    }

    override fun postVoteService(pollItemId: List<Int>) {
        Thread(Runnable { postVote(pollItemId) }).start()
    }

    override fun postFavService(action: String) {
        Thread(Runnable {
            getRetrofit().create(PostInterface::class.java)
                    .postFavorite(action = action,
                            id = mPostId)
                    .enqueue(object : Callback<PostFavResultBean> {
                        override fun onFailure(call: Call<PostFavResultBean>, t: Throwable) {
                            postPresenter.errorResult(t.toString())
                        }

                        override fun onResponse(call: Call<PostFavResultBean>, response: Response<PostFavResultBean>) {
                            val result = response.body()
                            if (result == null) {
                                postPresenter.postFavResult(BaseEvent(BaseCode.FAILURE, PostFavResultBean().apply {
                                    errcode = SERVICE_RESPONSE_NULL
                                }))
                                return
                            }
                            if (result.rs != REQUEST_SUCCESS_RS) {
                                postPresenter.postFavResult(BaseEvent(BaseCode.FAILURE, result))
                                return
                            }
                            postPresenter.postFavResult(BaseEvent(BaseCode.SUCCESS, result))
                        }
                    })
        }).start()
    }

    private fun getPost() {
        val postRequest = getRetrofit().create(PostInterface::class.java)
        val call = postRequest.getPostDetail(
                topicId = mPostId,
                authorId = mAuthorId,
                order = mOrder,
                page = mPage,
                pageSize = COMMON_PAGE_SIZE.toString())
        call.enqueue(object : Callback<PostDetailBean> {
            override fun onFailure(call: Call<PostDetailBean>?, t: Throwable?) {
                postPresenter.errorResult(SERVICE_RESPONSE_ERROR)
            }

            override fun onResponse(call: Call<PostDetailBean>?, response: Response<PostDetailBean>?) {
                val postResultBean = response?.body()
                if (postResultBean == null) {
                    postPresenter.errorResult(SERVICE_RESPONSE_NULL)
                    return
                }
                if (postResultBean.rs != REQUEST_SUCCESS_RS) {
                    postPresenter.errorResult(postResultBean.head?.errInfo.toString())
                    return
                }
                if (postResultBean.has_next != HAVE_NEXT_PAGE) {
                    postPresenter.postDetailResult(BaseEvent(BaseCode.SUCCESS_END, postResultBean))
                    return
                }
                postPresenter.postDetailResult(BaseEvent(BaseCode.SUCCESS, postResultBean))
            }
        })
    }

    private fun reply(isQuote: Int, replyId: Int, vararg contents: Pair<Int, String>) {
        val stContents = StringBuilder()
        contents.forEach {
            stContents.append("""{"type":${it.first},"infor":"${it.second}"},""")
        }
        //清除末尾的逗号
        stContents.deleteCharAt(stContents.lastIndex)
        getRetrofit().create(PostInterface::class.java)
                .postReply(json = """
                    {"body":
                        {"json":
                            {
                                "tid":$mPostId,
                                "isAnonymous":0,
                                "isOnlyAuthor":0,
                                "isQuote":$isQuote,
                                "replyId":$replyId,
                                "content":"[$stContents]"
                            }
                        }
                    }
                        """.trimIndent())
                .enqueue(object : Callback<PostSendResultBean> {
                    override fun onFailure(call: Call<PostSendResultBean>, t: Throwable?) {
                        postPresenter.errorResult(t.toString())
                    }

                    override fun onResponse(call: Call<PostSendResultBean>, response: Response<PostSendResultBean>?) {
                        val replySendResultBean = response?.body()
                        if (replySendResultBean == null) {
                            postPresenter.errorResult(SERVICE_RESPONSE_NULL)
                            return
                        }
                        if (replySendResultBean.rs != REQUEST_SUCCESS_RS) {
                            postPresenter.errorResult(replySendResultBean.head?.errInfo.toString())
                            return
                        }
                        postPresenter.postReplyResult(BaseEvent(BaseCode.SUCCESS, replySendResultBean))
                    }
                })
    }

    private fun postVote(pollItemId: List<Int>) {
        getRetrofit().create(PostInterface::class.java)
                //将list.toString之后前后的[]删去
                .postVote(tid = mPostId, options = pollItemId.toString().replace("[","").replace("]",""))
                .enqueue(object : Callback<PostVoteResultBean> {
                    override fun onFailure(call: Call<PostVoteResultBean>, t: Throwable) {
                        postPresenter.errorResult(SERVICE_RESPONSE_ERROR)
                    }

                    override fun onResponse(call: Call<PostVoteResultBean>, response: Response<PostVoteResultBean>) {
                        val result = response.body()
                        if (result == null) {
                            postPresenter.errorResult(SERVICE_RESPONSE_NULL)
                            return
                        }
                        if (result.rs != REQUEST_SUCCESS_RS) {
                            postPresenter.errorResult(result.head?.errInfo.toString())
                            return
                        }
                        postPresenter.postVoteResult(BaseEvent(BaseCode.SUCCESS, result))
                    }
                })
    }
}