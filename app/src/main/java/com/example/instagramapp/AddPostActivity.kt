package com.example.instagramapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_add_post.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddPostActivity : AppCompatActivity() {
    private var mUrl = ""
    private var imgUri : Uri? = null
    private var storagePost : StorageReference? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        storagePost = FirebaseStorage.getInstance().reference.child("Posts Pictures")
        img_save_addPost.setOnClickListener {
            upLoadImage()
        }
        CropImage.activity()
            .setAspectRatio(2,1)
            .start(this)
    }

    private fun upLoadImage() {
        when{
            imgUri == null -> Toast.makeText(this,"Please select imageview !", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(edt_desciption_post.text.toString()) -> Toast.makeText(this,"Please wirte fullname !", Toast.LENGTH_SHORT).show()
            else ->{
                val progressDilog = ProgressDialog(this)
                progressDilog.setTitle("Adding New Post")
                progressDilog.setMessage("Please wait !! we are adding your picture post....")
                progressDilog.show()

                val fileRef = storagePost!!.child(System.currentTimeMillis().toString() + "jpg")
                var upLoadTask : StorageTask<*>
                upLoadTask = fileRef.putFile(imgUri!!)
                upLoadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progressDilog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful){
                        val downLoadUrl = task.result
                        mUrl = downLoadUrl.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key
                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = edt_desciption_post.text.toString().toLowerCase()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = mUrl
                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(baseContext, "Post successfully",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressDilog.dismiss()
                    }else{
                        progressDilog.dismiss()
                    }
                })
            }
        }
    }

    //open thu vien anh trong may
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imgUri = result.uri
             image_post.setImageURI(imgUri)

        }else{

        }
    }
}