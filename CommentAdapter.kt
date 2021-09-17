package com.wintech.diydr.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wintech.diydr.MainActivity
import com.wintech.diydr.Model.Comment
import com.wintech.diydr.Model.User
import com.wintech.diydr.R
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, private val mComments: List<Comment>, var postId: String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var fUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fUser = FirebaseAuth.getInstance().currentUser
        val comment = mComments[position]
        holder.comment.text = comment.comment
        FirebaseDatabase.getInstance().reference.child("Users").child(comment.publisher).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                holder.username.text = user!!.username
                if (user.imageurl == "default") {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Picasso.get().load(user.imageurl).into(holder.imageProfile)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        holder.comment.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherId", comment.publisher)
            mContext.startActivity(intent)
        }
        holder.imageProfile.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherId", comment.publisher)
            mContext.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            if (comment.publisher.endsWith(fUser!!.uid)) {
                val alertDialog = AlertDialog.Builder(mContext).create()
                alertDialog.setTitle("Do you want to delete?")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO") { dialog, which -> dialog.dismiss() }
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { dialog, which ->
                    FirebaseDatabase.getInstance().reference.child("Comments")
                            .child(postId).child(comment.id).removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(mContext, "Comment deleted successfully!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                }
                alertDialog.show()
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: CircleImageView
        var username: TextView
        var comment: TextView

        init {
            imageProfile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            comment = itemView.findViewById(R.id.comment)
        }
    }
}