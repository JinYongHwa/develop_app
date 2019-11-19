package kr.co.hhsoft.mjstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var tabLayout:TabLayout
    lateinit var mainPageAdapter:MainPageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPageAdapter= MainPageAdapter(supportFragmentManager)
        viewPager=findViewById(R.id.viewpager)
        viewPager.adapter=mainPageAdapter
        tabLayout=findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)


        tabLayout.getTabAt(0)?.setIcon(R.drawable.baseline_home_black_48)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.baseline_search_black_48)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.baseline_add_circle_outline_black_48)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.baseline_star_border_black_48)
        tabLayout.getTabAt(4)?.setIcon(R.drawable.baseline_person_outline_black_48)

    }
    fun moveTab(index:Int){
        viewPager.setCurrentItem(index)
    }

}
