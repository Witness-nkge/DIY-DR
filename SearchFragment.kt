package com.wintech.diydr.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView
import com.wintech.diydr.Adapter.TagAdapter
import com.wintech.diydr.Adapter.UserAdapter
import com.wintech.diydr.Model.User
import com.wintech.diydr.R
import java.util.*

class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var mUsers: MutableList<User?>? = null
    private var userAdapter: UserAdapter? = null
    private var recyclerViewTags: RecyclerView? = null
    private var mHashTags: MutableList<String?>? = null
    private var mHashTagsCount: MutableList<String>? = null
    private var tagAdapter: TagAdapter? = null
    private var search_bar: SocialAutoCompleteTextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_users)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerViewTags = view.findViewById(R.id.recycler_view_tags)
        recyclerViewTags.setHasFixedSize(true)
        recyclerViewTags.setLayoutManager(LinearLayoutManager(context))
        mHashTags = ArrayList()
        mHashTagsCount = ArrayList()
        tagAdapter = TagAdapter(context!!, mHashTags, mHashTagsCount!!)
        recyclerViewTags.setAdapter(tagAdapter)
        mUsers = ArrayList()
        userAdapter = UserAdapter(context!!, mUsers, true)
        recyclerView.setAdapter(userAdapter)
        search_bar = view.findViewById(R.id.search_bar)
        readUsers()
        readTags()
        search_bar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchUser(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
        return view
    }

    private fun readTags() {
        FirebaseDatabase.getInstance().reference.child("HashTags").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mHashTags!!.clear()
                mHashTagsCount!!.clear()
                for (snapshot in dataSnapshot.children) {
                    mHashTags!!.add(snapshot.key)
                    mHashTagsCount!!.add(snapshot.childrenCount.toString() + "")
                }
                tagAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readUsers() {
        val reference = FirebaseDatabase.getInstance().reference.child("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (TextUtils.isEmpty(search_bar!!.text.toString())) {
                    mUsers!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        mUsers!!.add(user)
                    }
                    userAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun searchUser(s: String) {
        val query = FirebaseDatabase.getInstance().reference.child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    mUsers!!.add(user)
                }
                userAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun filter(text: String) {
        val mSearchTags: MutableList<String?> = ArrayList()
        val mSearchTagsCount: MutableList<String> = ArrayList()
        for (s in mHashTags!!) {
            if (s!!.toLowerCase().contains(text.toLowerCase())) {
                mSearchTags.add(s)
                mSearchTagsCount.add(mHashTagsCount!![mHashTags!!.indexOf(s)])
            }
        }
        tagAdapter!!.filter(mSearchTags, mSearchTagsCount)
    }
}