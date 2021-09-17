package com.wintech.diydr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wintech.diydr.R

class TagAdapter(private val mContext: Context, private var mTags: List<String>, private var mTagsCount: List<String>) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag.text = "# " + mTags[position]
        holder.noOfPosts.text = mTagsCount[position] + " posts"
    }

    override fun getItemCount(): Int {
        return mTags.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag: TextView
        var noOfPosts: TextView

        init {
            tag = itemView.findViewById(R.id.hash_tag)
            noOfPosts = itemView.findViewById(R.id.no_of_posts)
        }
    }

    fun filter(filterTags: List<String>, filterTagsCount: List<String>) {
        mTags = filterTags
        mTagsCount = filterTagsCount
        notifyDataSetChanged()
    }
}