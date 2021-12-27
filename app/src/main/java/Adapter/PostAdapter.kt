package Adapter

import Model.Post
import Model.User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
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
import kotlinx.android.synthetic.main.activity_account_setting.*

class PostAdapter
(private val context: Context,private val postList:List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.post_layout,parent,false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = postList[position]
        Glide.with(context).load(post.getPostimage()).into(holder.postImage)
        publisherInfo(holder.proFileImage,holder.userName,holder.publisher,post.getPublisher())
    }


    override fun getItemCount(): Int {
        return postList.size
    }

    inner class PostViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView) {
        var proFileImage : CircleImageView
        var postImage : ImageView
        var likeButton : ImageView
        var commentButton : ImageView
        var saveButton : ImageView
        var userName : TextView
        var likes : TextView
        var publisher : TextView
        var description : TextView
        var comments : TextView
        init {
            proFileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }
    }

    private fun publisherInfo(proFileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    Glide.with(context).load(user!!.getImage()).into(proFileImage)
                    userName.text = user!!.getUserName()
                    publisher.text = user!!.getFullName()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}