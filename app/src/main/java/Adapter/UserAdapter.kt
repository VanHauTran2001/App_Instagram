package Adapter

import Fragment.ProfileFragment
import Model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private var context :Context,
                  private var mUser:List<User>,
                  private var isFragment :Boolean = false):RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userName.text = user.getUserName()
        holder.fullName.text = user.getFullName()

        Glide.with(context).load(user.getImage()).into(holder.userProfileImage)
        //click follow
        checkFollowing(user.getUid(),holder.btnFollowSearch)
        holder.itemView.setOnClickListener (View.OnClickListener {
            val pref = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileId",user.getUid())
            pref.apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_Contener,ProfileFragment()).commit()
        })

        holder.btnFollowSearch.setOnClickListener {
            if (holder.btnFollowSearch.text.toString() == "Follow"){
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if(task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if(task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun checkFollowing(uid: String, btnFollowSearch: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.child(uid).exists()){
                  btnFollowSearch.text = "Following"
              }else{
                  btnFollowSearch.text = "Follow"
              }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun getItemCount(): Int {
        return mUser.size
    }
    class ViewHolder(@NonNull itemView : View) :RecyclerView.ViewHolder(itemView){
        var userName : TextView = itemView.findViewById(R.id.txt_username_search)
        var fullName : TextView = itemView.findViewById(R.id.txt_fullname_search)
        var userProfileImage : CircleImageView = itemView.findViewById(R.id.user_profile_search)
        var btnFollowSearch : Button = itemView.findViewById(R.id.btn_follow_search)

    }
}
