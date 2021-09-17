package com.wintech.diydr

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wintech.diydr.Adapter.UserAdapter
import com.wintech.diydr.Model.User
import java.util.*

class FollowersActivity : AppCompatActivity() {
    private var id: String? = null
    private var title: String? = null
    private var idList: MutableList<String?>? = null
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUsers: MutableList<User?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)
        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        mUsers = ArrayList()
        userAdapter = UserAdapter(this, mUsers, false)
        recyclerView.setAdapter(userAdapter)
        idList = ArrayList()
        when (title) {
            "followers" -> followers
            "followings" -> followings
            "likes" -> likes
        }
    }

    private val followers: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Follow").child(id!!).child("followers").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val followings: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Follow").child(id!!).child("following").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    private val likes: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Likes").child(id!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    idList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        idList!!.add(snapshot.key)
                    }
                    showUsers()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun showUsers() {
        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    for (id in idList!!) {
                        if (user!!.id == id) {
                            mUsers!!.add(user)
                        }
                    }
                }
                Log.d("list f", mUsers.toString())
                userAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}