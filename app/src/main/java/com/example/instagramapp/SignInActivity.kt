package com.example.instagramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        btn_signup_link.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        btn_login.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = edt_email_login.text.toString()
        val password = edt_password_login.text.toString()
        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password name is empty", Toast.LENGTH_SHORT).show()
            else ->{
                val progressDiag = ProgressDialog(this@SignInActivity)
                progressDiag.setTitle("Login")
                progressDiag.setMessage("Please wait.......")
                progressDiag.setCanceledOnTouchOutside(false)
                progressDiag.show()
                //dang nhap
                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            progressDiag.dismiss()
                            val intent = Intent(this@SignInActivity,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDiag.dismiss()

                        }
                    }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (FirebaseAuth.getInstance().currentUser !=null){
//            val intent = Intent(this@SignInActivity,MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//        }
//    }
}