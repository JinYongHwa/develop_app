package kr.co.hhsoft.mjstagram

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class StarFragment: Fragment() {

    lateinit var profileIv:ImageView
    lateinit var emailTv:TextView

    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view=inflater.inflate(R.layout.fragment_star,container,false)
        profileIv= view.findViewById(R.id.profile_iv)
        emailTv= view.findViewById(R.id.email_tv)

        auth= FirebaseAuth.getInstance()

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        emailTv.text=auth.currentUser?.email
    }

}