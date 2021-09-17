package com.wintech.diydr.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.squareup.picasso.Picasso
import com.wintech.diydr.Adapter.PostAdapter.Viewholder
import com.wintech.diydr.CommentActivity
import com.wintech.diydr.FollowersActivity
import com.wintech.diydr.Fragments.PostDetailFragment
import com.wintech.diydr.Fragments.ProfileFragment
import com.wintech.diydr.Model.Post
import com.wintech.diydr.Model.User
import com.wintech.diydr.R
import java.util.*

class PostAdapter(private val mContext: Context, private val mPosts: List<Post>) : RecyclerView.Adapter<Viewholder>() {
    private val firebaseUser: FirebaseUser?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post = mPosts[position]
        Picasso.get().load(post.imageurl).into(holder.postImage)
        holder.description.text = post.description
        FirebaseDatabase.getInstance().reference.child("Users").child(post.publisher).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user!!.imageurl == "default") {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Picasso.get().load(user.imageurl).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile)
                }
                holder.username.text = user.username
                holder.author.text = user.name
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        isLiked(post.postid, holder.like)
        noOfLikes(post.postid, holder.noOfLikes)
        getComments(post.postid, holder.noOfComments)
        isSaved(post.postid, holder.save)
        holder.like.setOnClickListener {
            if (holder.like.tag == "like") {
                FirebaseDatabase.getInstance().reference.child("Likes")
                        .child(post.postid).child(firebaseUser!!.uid).setValue(true)
                addNotification(post.postid, post.publisher)
            } else {
                FirebaseDatabase.getInstance().reference.child("Likes")
                        .child(post.postid).child(firebaseUser!!.uid).removeValue()
            }
        }
        holder.comment.setOnClickListener {
            val intent = Intent(mContext, CommentActivity::class.java)
            intent.putExtra("postId", post.postid)
            intent.putExtra("authorId", post.publisher)
            mContext.startActivity(intent)
        }
        holder.noOfComments.setOnClickListener {
            val intent = Intent(mContext, CommentActivity::class.java)
            intent.putExtra("postId", post.postid)
            intent.putExtra("authorId", post.publisher)
            mContext.startActivity(intent)
        }
        holder.save.setOnClickListener {
            if (holder.save.tag == "save") {
                FirebaseDatabase.getInstance().reference.child("Saves")
                        .child(firebaseUser!!.uid).child(post.postid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child("Saves")
                        .child(firebaseUser!!.uid).child(post.postid).removeValue()
            }
        }
        holder.imageProfile.setOnClickListener {
            mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                    .edit().putString("profileId", post.publisher).apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
        }
        holder.username.setOnClickListener {
            mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                    .edit().putString("profileId", post.publisher).apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
        }
        holder.author.setOnClickListener {
            mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                    .edit().putString("profileId", post.publisher).apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
        }
        holder.postImage.setOnClickListener {
            mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.postid).apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailFragment()).commit()
        }
        holder.noOfLikes.setOnClickListener {
            val intent = Intent(mContext, FollowersActivity::class.java)
            intent.putExtra("id", post.publisher)
            intent.putExtra("title", "likes")
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: ImageView
        var postImage: ImageView
        var like: ImageView
        var comment: ImageView
        var save: ImageView
        var more: ImageView
        var username: TextView
        var noOfLikes: TextView
        var author: TextView
        var noOfComments: TextView
        var description: SocialTextView

        init {
            imageProfile = itemView.findViewById(R.id.image_profile)
            postImage = itemView.findViewById(R.id.post_image)
            like = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            save = itemView.findViewById(R.id.save)
            more = itemView.findViewById(R.id.more)
            username = itemView.findViewById(R.id.username)
            noOfLikes = itemView.findViewById(R.id.no_of_likes)
            author = itemView.findViewById(R.id.author)
            noOfComments = itemView.findViewById(R.id.no_of_comments)
            description = itemView.findViewById(R.id.description)
        }
    }

    private fun isSaved(postId: String, image: ImageView) {
        FirebaseDatabase.getInstance().reference.child("Saves").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_save_black)
                    image.tag = "saved"
                } else {
                    image.setImageResource(R.drawable.ic_save)
                    image.tag = "save"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isLiked(postId: String, imageView: ImageView) {
        FirebaseDatabase.getInstance().reference.child("Likes").child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(firebaseUser!!.uid).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked)
                    imageView.tag = "liked"
                } else {
                    imageView.setImageResource(R.drawable.ic_like)
                    imageView.tag = "like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun noOfLikes(postId: String, text: TextView) {
        FirebaseDatabase.getInstance().reference.child("Likes").child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                text.text = dataSnapshot.childrenCount.toString() + " likes"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getComments(postId: String, text: TextView) {
        FirebaseDatabase.getInstance().reference.child("Comments").child(postId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                text.text = "View All " + dataSnapshot.childrenCount + " Comments"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addNotification(postId: String, publisherId: String) {
        val map = HashMap<String, Any>()
        map["userid"] = publisherId
        map["text"] = "liked your post."
        map["postid"] = postId
        map["isPost"] = true
        FirebaseDatabase.getInstance().reference.child("Notifications").child(firebaseUser!!.uid).push().setValue(map)
    }

    init {
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }
}