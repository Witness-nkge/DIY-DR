package com.wintech.diydr

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rishav.firebasedemo.Fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private var selectorFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.nav_home -> selectorFragment = HomeFragment()
                    R.id.nav_search -> selectorFragment = SearchFragment()
                    R.id.nav_add -> {
                        selectorFragment = null
                        startActivity(Intent(this@MainActivity, PostActivity::class.java))
                    }
                    R.id.nav_heart -> selectorFragment = NotificationFragment()
                    R.id.nav_profile -> selectorFragment = ProfileFragment()
                }
                if (selectorFragment != null) {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectorFragment!!).commit()
                }
                return true
            }
        })
        val intent: Bundle = intent.extras
        if (intent != null) {
            val profileId: String = intent.getString("publisherId")
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            bottomNavigationView.setSelectedItemId(R.id.nav_profile)
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        }
    }
}