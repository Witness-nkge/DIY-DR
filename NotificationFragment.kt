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
import com.wintech.diydr.Adapter.NotificationAdapter
import com.wintech.diydr.Model.Notification
import com.wintech.diydr.R
import java.util.*

class NotificationFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<Notification?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(context!!, notificationList)
        recyclerView.setAdapter(notificationAdapter)
        readNotifications()
        return view
    }

    private fun readNotifications() {
        FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    notificationList!!.add(snapshot.getValue(Notification::class.java))
                }
                Collections.reverse(notificationList)
                notificationAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}