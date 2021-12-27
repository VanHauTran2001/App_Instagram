package com.example.instagramapp

import Model.User
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var mUrl = ""
    private var imgUri : Uri? = null
    private var storageProfile : StorageReference? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfile = FirebaseStorage.getInstance().reference.child("Profile Pictures")
        btn_logout_accout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity,SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        //click thay doi hinh anh
        txt_change_account.setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this)
        }
        img_save_profile.setOnClickListener {
            if (checker == "clicked"){
                upLoadImageviewInfo()
            }
            else
            {
                updateInforOnly()
            }
        }
        setUserInfo()
    }

    private fun upLoadImageviewInfo() {
        when{
            imgUri == null -> Toast.makeText(this,"Please select imageview !",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(edt_fullname_profile.text.toString()) ->Toast.makeText(this,"Please wirte fullname !",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username_profile.text.toString()) ->Toast.makeText(this,"Please wirte user name !",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(bio_profile_accout.text.toString())-> Toast.makeText(this,"Please wirte bio !",Toast.LENGTH_SHORT).show()
            else ->{

                val progressDilog = ProgressDialog(this)
                progressDilog.setTitle("Account Settings")
                progressDilog.setMessage("Please wait !! we are updating your profile")
                progressDilog.show()

                val fileRef = storageProfile!!.child(firebaseUser!!.uid + "jpg")
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
                }).addOnCompleteListener(OnCompleteListener<Uri> {task ->
                    if (task.isSuccessful){
                        val downLoadUrl = task.result
                        mUrl = downLoadUrl.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = edt_fullname_profile.text.toString().toLowerCase()
                        userMap["username"] = username_profile.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_accout.text.toString().toLowerCase()
                        userMap["image"] = mUrl
                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(baseContext, "Update successfully",Toast.LENGTH_SHORT).show()
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
            circle_account.setImageURI(imgUri)

        }
    }

    private fun updateInforOnly() {
        if (edt_fullname_profile.text.toString()== ""){
            Toast.makeText(this,"Please wirte fullname !",Toast.LENGTH_SHORT).show()
        }else if (username_profile.text.toString()== ""){
            Toast.makeText(this,"Please wirte user name !",Toast.LENGTH_SHORT).show()
        }else if (bio_profile_accout.text.toString() == ""){
            Toast.makeText(this,"Please wirte bio !",Toast.LENGTH_SHORT).show()
        }else {
            val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            val userMap = HashMap<String, Any>()
            userMap["fullname"] = edt_fullname_profile.text.toString().toLowerCase()
            userMap["username"] = username_profile.text.toString().toLowerCase()
            userMap["bio"] = bio_profile_accout.text.toString().toLowerCase()
            //cap nhat lai thong tin
            userRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(baseContext, "Update successfully",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setUserInfo(){
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Glide.with(this@AccountSettingActivity).load(user!!.getImage()).into(circle_account)
                    edt_fullname_profile.setText(user!!.getFullName())
                    username_profile.setText(user!!.getUserName())
                    bio_profile_accout.setText(user!!.getBio())
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}