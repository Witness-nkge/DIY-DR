package com.wintech.diydr.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wintech.diydr.Adapter.PostAdapter
import com.wintech.diydr.Model.Post
import com.wintech.diydr.R
import java.util.*

class PostDetailFragment : Fragment() {
    private var postId: String? = null
    private var recyclerView: RecyclerView? = null
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)
        postId = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postid", "none")
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        postList = ArrayList()
        postAdapter = PostAdapter(context!!, postList)
        recyclerView.setAdapter(postAdapter)
        FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                postList.add(dataSnapshot.getValue(Post::class.java))
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return view
    }
}