package com.wintech.diydr

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.wintech.diydr.Model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private var close: ImageView? = null
    private var imageProfile: CircleImageView? = null
    private var save: TextView? = null
    private var changePhoto: TextView? = null
    private var fullname: MaterialEditText? = null
    private var username: MaterialEditText? = null
    private var bio: MaterialEditText? = null
    private var fUser: FirebaseUser? = null
    private var mImageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null
    private var storageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        close = findViewById(R.id.close)
        imageProfile = findViewById(R.id.image_profile)
        save = findViewById(R.id.save)
        changePhoto = findViewById(R.id.change_photo)
        fullname = findViewById(R.id.fullname)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.bio)
        fUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference.child("Uploads")
        FirebaseDatabase.getInstance().reference.child("Users").child(fUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                fullname.setText(user!!.name)
                username.setText(user.username)
                bio.setText(user.bio)
                Picasso.get().load(user.imageurl).into(imageProfile)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        close.setOnClickListener(View.OnClickListener { finish() })
        changePhoto.setOnClickListener(View.OnClickListener { CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(this@EditProfileActivity) })
        imageProfile.setOnClickListener(View.OnClickListener { CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(this@EditProfileActivity) })
        save.setOnClickListener(View.OnClickListener { updateProfile() })
    }

    private fun updateProfile() {
        val map = HashMap<String, Any>()
        map["fullname"] = fullname!!.text.toString()
        map["username"] = username!!.text.toString()
        map["bio"] = bio!!.text.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(fUser!!.uid).updateChildren(map)
    }

    private fun uploadImage() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()
        if (mImageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpeg")
            uploadTask = fileRef.putFile(mImageUri!!)
            uploadTask.continueWithTask(object : Continuation<Any?, Any?> {
                @Throws(Exception::class)
                override fun then(task: Task<*>): Any? {
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    return fileRef.downloadUrl
                }
            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri.toString()
                    FirebaseDatabase.getInstance().reference.child("Users").child(fUser!!.uid).child("imageurl").setValue(url)
                    pd.dismiss()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Upload failed!", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            mImageUri = result.uri
            uploadImage()
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}