package kr.co.hhsoft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var addPhotoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addPhotoBtn=findViewById(R.id.add_photo_btn)
        addPhotoBtn.setOnClickListener{ addPhoto() }

    }

    fun addPhoto(){
        var intent= Intent(this,AddPhotoActivity::class.java)
        startActivity(intent)
    }
}
