package com.hjq.kotlinhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import com.blankj.utilcode.util.ToastUtils
import com.hjq.kotlinhttp.adapter.NewsAdapter
import com.hjq.kotlinhttp.entity.NewListBean
import com.hjq.kotlinhttp.listener.HttpResultListener
import com.hjq.kotlinhttp.net.NewsService
import com.hjq.kotlinhttp.viewmodel.NewsViewModel
import com.hjq.kotlinhttp.viewmodel.NewsViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mNewsAdapter = NewsAdapter()

    private var mViewModel: NewsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        initView()
    }


    private fun initViewModel() {
        mViewModel =
            ViewModelProvider(viewModelStore, NewsViewModelFactory()).get(NewsViewModel::class.java)
        lifecycle.addObserver(mViewModel!!)
    }

    private fun initView() {
        news_rv.adapter = mNewsAdapter
        news_rv.layoutManager = LinearLayoutManager(this)
        //设置不上拉加载
        val refreshViewHolder = BGANormalRefreshViewHolder(this, false)
        refresh_layout.setRefreshViewHolder(refreshViewHolder)
        refresh_layout.setDelegate(object : BGARefreshLayout.BGARefreshLayoutDelegate {
            override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout?): Boolean {
                return false
            }

            override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout?) {
                mViewModel?.netAsync(object : HttpResultListener<NewListBean> {
                    override fun onResult(data: NewListBean) {
                        refresh_layout.endRefreshing()
                        if (data.result == null || data.result.data.isEmpty()) {
                            showErrorInfo(data.reason)
                            return
                        }
                        news_rv.visibility = View.VISIBLE
                        error_info.visibility = View.GONE
                        mNewsAdapter.setData(data.result.data)
                    }

                    override fun onError(errorCode: Int, errorMsg: String) {
                        refresh_layout.endRefreshing()
                        showErrorInfo(errorMsg)
                    }

                }) {
                    NewsService.getNewsService().getNewsList("5376adf29d5cc66f87e664bb2b4a078b")
                }
            }
        })
    }

    private fun showErrorInfo(errorMsg: String) {
        news_rv.visibility = View.GONE
        error_info.visibility = View.VISIBLE
        error_info.text = errorMsg
        ToastUtils.showShort("$errorMsg")
    }


    override fun onResume() {
        super.onResume()
        refresh_layout.beginRefreshing()
    }
}