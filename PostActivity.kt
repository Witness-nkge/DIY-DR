package com.wintech.diydr

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.hendraanggrian.appcompat.socialview.Hashtag
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView
import com.theartofdev.edmodo.cropper.CropImage
import com.wintech.diydr.PostActivity
import java.util.*

class PostActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var imageUrl: String? = null
    private var close: ImageView? = null
    private var imageAdded: ImageView? = null
    private var post: TextView? = null
    var description: SocialAutoCompleteTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        close = findViewById(R.id.close)
        imageAdded = findViewById(R.id.image_added)
        post = findViewById(R.id.post)
        description = findViewById(R.id.description)
        close.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostActivity, MainActivity::class.java))
            finish()
        })
        post.setOnClickListener(View.OnClickListener { upload() })
        CropImage.activity().start(this@PostActivity)
    }

    private fun upload() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()
        if (imageUri != null) {
            val filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!))
            val uploadtask: StorageTask<*> = filePath.putFile(imageUri!!)
            uploadtask.continueWithTask(object : Continuation<Any?, Any?> {
                @Throws(Exception::class)
                override fun then(task: Task<*>): Any? {
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    return filePath.downloadUrl
                }
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                val downloadUri = task.result
                imageUrl = downloadUri.toString()
                val ref = FirebaseDatabase.getInstance().getReference("Posts")
                val postId = ref.push().key
                val map = HashMap<String, Any?>()
                map["postid"] = postId
                map["imageurl"] = imageUrl
                map["description"] = description!!.text.toString()
                map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child(postId!!).setValue(map)
                val mHashTagRef = FirebaseDatabase.getInstance().reference.child("HashTags")
                val hashTags = description!!.hashtags
                if (!hashTags.isEmpty()) {
                    for (tag in hashTags) {
                        map.clear()
                        map["tag"] = tag.toLowerCase()
                        map["postid"] = postId
                        mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map)
                    }
                }
                pd.dismiss()
                startActivity(Intent(this@PostActivity, MainActivity::class.java))
                finish()
            }).addOnFailureListener(OnFailureListener { e -> Toast.makeText(this@PostActivity, e.message, Toast.LENGTH_SHORT).show() })
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.contentResolver.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imageAdded!!.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@PostActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val hashtagAdapter: ArrayAdapter<Hashtag> = HashtagArrayAdapter(applicationContext)
        FirebaseDatabase.getInstance().reference.child("HashTags").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    hashtagAdapter.add(Hashtag(snapshot.key!!, snapshot.childrenCount.toInt()))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        description!!.hashtagAdapter = hashtagAdapter
    }
}