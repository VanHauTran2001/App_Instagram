package com.example.instagramapp

import Fragment.HomeFragment
import Fragment.NotificationFragment
import Fragment.ProfileFragment
import Fragment.SearchFragment
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal var selectFragment : Fragment? = null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navi_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_search-> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_add -> {
                item.isChecked = false
                startActivity(Intent(this,AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_notification -> {
            moveToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        if(selectFragment!=null){

        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (bottonNavigate!=null){
            bottonNavigate.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout_Contener,
            HomeFragment()
        ).commit()

    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frameLayout_Contener ,fragment)
        fragmentTrans.commit()
    }

}
