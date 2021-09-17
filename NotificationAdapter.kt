package com.wintech.diydr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wintech.diydr.Fragments.PostDetailFragment
import com.wintech.diydr.Fragments.ProfileFragment
import com.wintech.diydr.Model.Notification
import com.wintech.diydr.Model.Post
import com.wintech.diydr.Model.User
import com.wintech.diydr.R

class NotificationAdapter(private val mContext: Context, private val mNotifications: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotifications[position]
        getUser(holder.imageProfile, holder.username, notification.userid)
        holder.comment.text = notification.text
        if (notification.isIsPost) {
            holder.postImage.visibility = View.VISIBLE
            getPostImage(holder.postImage, notification.postid)
        } else {
            holder.postImage.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            if (notification.isIsPost) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("postid", notification.postid).apply()
                (mContext as FragmentActivity).supportFragmentManager
                        .beginTransaction().replace(R.id.fragment_container, PostDetailFragment()).commit()
            } else {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", notification.userid).apply()
                (mContext as FragmentActivity).supportFragmentManager
                        .beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotifications.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: ImageView
        var postImage: ImageView
        var username: TextView
        var comment: TextView

        init {
            imageProfile = itemView.findViewById(R.id.image_profile)
            postImage = itemView.findViewById(R.id.post_image)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)
        }
    }

    private fun getPostImage(imageView: ImageView, postId: String) {
        FirebaseDatabase.getInstance().reference.child("Posts").child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(Post::class.java)
                Picasso.get().load(post!!.imageurl).placeholder(R.mipmap.ic_launcher).into(imageView)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getUser(imageView: ImageView, textView: TextView, userId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user!!.imageurl == "default") {
                    imageView.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Picasso.get().load(user.imageurl).into(imageView)
                }
                textView.text = user.username
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}