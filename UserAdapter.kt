package com.wintech.diydr.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wintech.diydr.Fragments.ProfileFragment
import com.wintech.diydr.MainActivity
import com.wintech.diydr.Model.User
import com.wintech.diydr.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class UserAdapter(private val mContext: Context, private val mUsers: List<User>, private val isFargment: Boolean) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val user = mUsers[position]
        holder.btnFollow.visibility = View.VISIBLE
        holder.username.text = user.username
        holder.fullname.text = user.name
        Picasso.get().load(user.imageurl).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile)
        isFollowed(user.id, holder.btnFollow)
        if (user.id == firebaseUser!!.uid) {
            holder.btnFollow.visibility = View.GONE
        }
        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString() == "follow") {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid).child("following").child(user.id).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id).child("followers").child(firebaseUser!!.uid).setValue(true)
                addNotification(user.id)
            } else {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid).child("following").child(user.id).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id).child("followers").child(firebaseUser!!.uid).removeValue()
            }
        }
        holder.itemView.setOnClickListener {
            if (isFargment) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.id).apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            } else {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.id)
                mContext.startActivity(intent)
            }
        }
    }

    private fun isFollowed(id: String, btnFollow: Button) {
        val reference = FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                .child("following")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id).exists()) btnFollow.text = "following" else btnFollow.text = "follow"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: CircleImageView
        var username: TextView
        var fullname: TextView
        var btnFollow: Button

        init {
            imageProfile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById(R.id.username)
            fullname = itemView.findViewById(R.id.fullname)
            btnFollow = itemView.findViewById(R.id.btn_follow)
        }
    }

    private fun addNotification(userId: String) {
        val map = HashMap<String, Any>()
        map["userid"] = userId
        map["text"] = "started following you."
        map["postid"] = ""
        map["isPost"] = false
        FirebaseDatabase.getInstance().reference.child("Notifications").child(firebaseUser!!.uid).push().setValue(map)
    }
}