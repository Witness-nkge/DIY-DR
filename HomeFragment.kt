package com.wintech.diydr.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wintech.diydr.Adapter.PostAdapter
import com.wintech.diydr.Model.Post
import com.wintech.diydr.R
import java.util.*

class HomeFragment : Fragment() {
    private var recyclerViewPosts: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post?>? = null
    private var followingList: MutableList<String?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts)
        recyclerViewPosts.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerViewPosts.setLayoutManager(linearLayoutManager)
        postList = ArrayList()
        postAdapter = PostAdapter(context!!, postList)
        recyclerViewPosts.setAdapter(postAdapter)
        followingList = ArrayList()
        checkFollowingUsers()
        return view
    }

    private fun checkFollowingUsers() {
        FirebaseDatabase.getInstance().reference.child("Follow").child(FirebaseAuth.getInstance()
                .currentUser!!.uid).child("following").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                followingList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    followingList!!.add(snapshot.key)
                }
                followingList!!.add(FirebaseAuth.getInstance().currentUser!!.uid)
                readPosts()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readPosts() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    for (id in followingList!!) {
                        if (post!!.publisher == id) {
                            postList!!.add(post)
                        }
                    }
                }
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}