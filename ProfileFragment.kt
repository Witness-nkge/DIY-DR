package com.wintech.diydr.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wintech.diydr.Adapter.PhotoAdapter
import com.wintech.diydr.EditProfileActivity
import com.wintech.diydr.FollowersActivity
import com.wintech.diydr.Model.Post
import com.wintech.diydr.Model.User
import com.wintech.diydr.OptionsActivity
import com.wintech.diydr.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ProfileFragment : Fragment() {
    private var recyclerViewSaves: RecyclerView? = null
    private var postAdapterSaves: PhotoAdapter? = null
    private var mySavedPosts: MutableList<Post?>? = null
    private var recyclerView: RecyclerView? = null
    private var photoAdapter: PhotoAdapter? = null
    private var myPhotoList: MutableList<Post?>? = null
    private var imageProfile: CircleImageView? = null
    private var options: ImageView? = null
    private var followers: TextView? = null
    private var following: TextView? = null
    private var posts: TextView? = null
    private var fullname: TextView? = null
    private var bio: TextView? = null
    private var username: TextView? = null
    private var myPictures: ImageView? = null
    private var savedPictures: ImageView? = null
    private var editProfile: Button? = null
    private var fUser: FirebaseUser? = null
    var profileId: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        fUser = FirebaseAuth.getInstance().currentUser
        val data = context!!.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none")
        if (data == "none") {
            profileId = fUser!!.uid
        } else {
            profileId = data
            context!!.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply()
        }
        imageProfile = view.findViewById(R.id.image_profile)
        options = view.findViewById(R.id.options)
        followers = view.findViewById(R.id.followers)
        following = view.findViewById(R.id.following)
        posts = view.findViewById(R.id.posts)
        fullname = view.findViewById(R.id.fullname)
        bio = view.findViewById(R.id.bio)
        username = view.findViewById(R.id.username)
        myPictures = view.findViewById(R.id.my_pictures)
        savedPictures = view.findViewById(R.id.saved_pictures)
        editProfile = view.findViewById(R.id.edit_profile)
        recyclerView = view.findViewById(R.id.recucler_view_pictures)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(GridLayoutManager(context, 3))
        myPhotoList = ArrayList()
        photoAdapter = PhotoAdapter(context!!, myPhotoList)
        recyclerView.setAdapter(photoAdapter)
        recyclerViewSaves = view.findViewById(R.id.recucler_view_saved)
        recyclerViewSaves.setHasFixedSize(true)
        recyclerViewSaves.setLayoutManager(GridLayoutManager(context, 3))
        mySavedPosts = ArrayList()
        postAdapterSaves = PhotoAdapter(context!!, mySavedPosts)
        recyclerViewSaves.setAdapter(postAdapterSaves)
        userInfo()
        followersAndFollowingCount
        postCount
        myPhotos()
        savedPosts
        if (profileId == fUser!!.uid) {
            editProfile.setText("Edit profile")
        } else {
            checkFollowingStatus()
        }
        editProfile.setOnClickListener(View.OnClickListener {
            val btnText = editProfile.getText().toString()
            if (btnText == "Edit profile") {
                startActivity(Intent(context, EditProfileActivity::class.java))
            } else {
                if (btnText == "follow") {
                    FirebaseDatabase.getInstance().reference.child("Follow").child(fUser!!.uid)
                            .child("following").child(profileId!!).setValue(true)
                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId!!)
                            .child("followers").child(fUser!!.uid).setValue(true)
                } else {
                    FirebaseDatabase.getInstance().reference.child("Follow").child(fUser!!.uid)
                            .child("following").child(profileId!!).removeValue()
                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId!!)
                            .child("followers").child(fUser!!.uid).removeValue()
                }
            }
        })
        recyclerView.setVisibility(View.VISIBLE)
        recyclerViewSaves.setVisibility(View.GONE)
        myPictures.setOnClickListener(View.OnClickListener {
            recyclerView.setVisibility(View.VISIBLE)
            recyclerViewSaves.setVisibility(View.GONE)
        })
        savedPictures.setOnClickListener(View.OnClickListener {
            recyclerView.setVisibility(View.GONE)
            recyclerViewSaves.setVisibility(View.VISIBLE)
        })
        followers.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        })
        following.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followings")
            startActivity(intent)
        })
        options.setOnClickListener(View.OnClickListener { startActivity(Intent(context, OptionsActivity::class.java)) })
        return view
    }

    private val savedPosts: Unit
        private get() {
            val savedIds: MutableList<String?> = ArrayList()
            FirebaseDatabase.getInstance().reference.child("Saves").child(fUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        savedIds.add(snapshot.key)
                    }
                    FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot1: DataSnapshot) {
                            mySavedPosts!!.clear()
                            for (snapshot1 in dataSnapshot1.children) {
                                val post = snapshot1.getValue(Post::class.java)
                                for (id in savedIds) {
                                    if (post!!.postid == id) {
                                        mySavedPosts!!.add(post)
                                    }
                                }
                            }
                            postAdapterSaves!!.notifyDataSetChanged()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun myPhotos() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                myPhotoList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post!!.publisher == profileId) {
                        myPhotoList!!.add(post)
                    }
                }
                Collections.reverse(myPhotoList)
                photoAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkFollowingStatus() {
        FirebaseDatabase.getInstance().reference.child("Follow").child(fUser!!.uid).child("following").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(profileId!!).exists()) {
                    editProfile!!.text = "following"
                } else {
                    editProfile!!.text = "follow"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val postCount: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var counter = 0
                    for (snapshot in dataSnapshot.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.publisher == profileId) counter++
                    }
                    posts!!.text = counter.toString()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val followersAndFollowingCount: Unit
        private get() {
            val ref = FirebaseDatabase.getInstance().reference.child("Follow").child(profileId!!)
            ref.child("followers").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    followers!!.text = "" + dataSnapshot.childrenCount
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            ref.child("following").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    following!!.text = "" + dataSnapshot.childrenCount
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun userInfo() {
        FirebaseDatabase.getInstance().reference.child("Users").child(profileId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Picasso.get().load(user!!.imageurl).into(imageProfile)
                username!!.text = user.username
                fullname!!.text = user.name
                bio!!.text = user.bio
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}