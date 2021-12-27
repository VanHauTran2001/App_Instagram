package Fragment

import Adapter.UserAdapter
import Model.User
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {
    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var userList : ArrayList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recyler_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        userList = ArrayList()
        userAdapter = context?.let { UserAdapter(it, userList as ArrayList<User>,true)  }
        recyclerView?.adapter = userAdapter

        view.edt_search.addTextChangedListener(object :TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.edt_search.text.toString()==""){

                }else{
                    recyclerView?.visibility = View.VISIBLE
                    RetriveUser()
                    searchUser(s.toString().toLowerCase())
                }
            }
            override fun afterTextChanged(s: Editable?)
            {
            }
        })
        return view
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .orderByChild("fullname")
                        .startAt(input)
                        .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                userList?.clear()
                for (datasnapshot in snapshot.children){
                    val user = datasnapshot.getValue(User::class.java)
                    if (user!=null){
                        userList?.add(user)
                    }
                }
                recyclerView?.adapter = userAdapter

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun RetriveUser() {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (view?.edt_search?.text.toString()== ""){
                    userList?.clear()
                    for (datasnapshot in snapshot.children){
                        val user = datasnapshot.getValue(User::class.java)
                        if (user!=null){
                            userList?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}