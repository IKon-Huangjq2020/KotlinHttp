package com.hjq.kotlinhttp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hjq.kotlinhttp.R
import com.hjq.kotlinhttp.entity.NewData

/**
 *
 * @Description:     类作用描述
 * @Author:         hjq
 * @CreateDate:     2020/12/14 17:32
 *
 */
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private var mDataList: MutableList<NewData> = arrayListOf()


    fun setData(dataList: List<NewData>) {
        mDataList.removeAll(mDataList)
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val mView = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_list_item_view, parent, false)
        return NewsViewHolder(mView)
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val itemBean = mDataList[position]
        Glide.with(holder.itemView.context).load(itemBean.thumbnail_pic_s)
            .into(holder.mImageView)
        holder.mTitleText.text = itemBean.title
        holder.mDateText.text = itemBean.date
    }

    class NewsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val mImageView = v.findViewById<ImageView>(R.id.news_item_image)!!
        val mTitleText = v.findViewById<TextView>(R.id.news_item_title)!!
        val mDateText = v.findViewById<TextView>(R.id.news_item_date)!!
    }
}