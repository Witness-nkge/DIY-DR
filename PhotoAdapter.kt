package com.wintech.diydr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wintech.diydr.Fragments.PostDetailFragment
import com.wintech.diydr.Model.Post
import com.wintech.diydr.R

class PhotoAdapter(private val mContext: Context, private val mPosts: List<Post>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPosts[position]
        Picasso.get().load(post.imageurl).placeholder(R.mipmap.ic_launcher).into(holder.postImage)
        holder.postImage.setOnClickListener {
            mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.postid).apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailFragment()).commit()
        }
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView

        init {
            postImage = itemView.findViewById(R.id.post_image)
        }
    }
}