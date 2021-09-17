package com.wintech.diydr

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wintech.diydr.Adapter.CommentAdapter
import com.wintech.diydr.Model.Comment
import com.wintech.diydr.Model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class CommentActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<Comment?>? = null
    private var addComment: EditText? = null
    private var imageProfile: CircleImageView? = null
    private var post: TextView? = null
    private var postId: String? = null
    private var authorId: String? = null
    var fUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Comments")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        val intent = intent
        postId = intent.getStringExtra("postId")
        authorId = intent.getStringExtra("authorId")
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList, postId!!)
        recyclerView.setAdapter(commentAdapter)
        addComment = findViewById(R.id.add_comment)
        imageProfile = findViewById(R.id.image_profile)
        post = findViewById(R.id.post)
        fUser = FirebaseAuth.getInstance().currentUser
        userImage
        post.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(addComment.getText().toString())) {
                Toast.makeText(this@CommentActivity, "No comment added!", Toast.LENGTH_SHORT).show()
            } else {
                putComment()
            }
        })
        comment
    }

    private val comment: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    commentList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun putComment() {
        val map = HashMap<String, Any?>()
        val ref = FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        val id = ref.push().key
        map["id"] = id
        map["comment"] = addComment!!.text.toString()
        map["publisher"] = fUser!!.uid
        addComment!!.setText("")
        ref.child(id!!).setValue(map).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@CommentActivity, "Comment added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@CommentActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val userImage: Unit
        private get() {
            FirebaseDatabase.getInstance().reference.child("Users").child(fUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user!!.imageurl == "default") {
                        imageProfile!!.setImageResource(R.mipmap.ic_launcher)
                    } else {
                        Picasso.get().load(user.imageurl).into(imageProfile)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
}