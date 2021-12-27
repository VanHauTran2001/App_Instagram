package com.example.instagramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        btn_signin_link.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
        btn_register.setOnClickListener {
            creatAccout()
        }
    }

    private fun creatAccout() {
        val fullName = edt_fullname_signup.text.toString()
        val userName = edt_username_signup.text.toString()
        val email = edt_email_signup.text.toString()
        val password = edt_password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full name is empty",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"User name is empty",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is empty",Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password name is empty",Toast.LENGTH_SHORT).show()
            else ->{
                val progressDiag = ProgressDialog(this@SignUpActivity)
                progressDiag.setTitle("SignUp")
                progressDiag.setMessage("Please wait.......")
                progressDiag.setCanceledOnTouchOutside(false)
                progressDiag.show()
                //Dang ky User with firebase
                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                                saveUserInfo(fullName,userName,email,progressDiag)


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDiag.dismiss()

                        }
                    }
            }
        }
    }
    //luu thong tin dang ky len realtime database
    private fun saveUserInfo(fullName: String, userName: String, email: String,progressDialog : ProgressDialog) {
        val currentId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentId)
        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentId
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey ! I'm clone Instagram App"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-kotlin-c9c3c.appspot.com/o/Default%20Image%2Fprofile.png?alt=media&token=816571f7-d4a0-40f2-b7d6-e06dd823212b"
        userRef.setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(baseContext, "Accout created successfully",Toast.LENGTH_SHORT).show()


                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(currentId)
                            .child("Following").child(currentId)
                            .setValue(true)

                    val intent = Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}