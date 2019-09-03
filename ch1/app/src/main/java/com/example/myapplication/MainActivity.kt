package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity: AppCompatActivity() {

    lateinit var emailTv: TextView
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailTv=findViewById(R.id.email_tv)
        auth= FirebaseAuth.getInstance()

        emailTv.setText(auth.currentUser?.email)
    }
}