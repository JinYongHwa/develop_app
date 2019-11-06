package com.example.push

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast


import com.google.firebase.internal.FirebaseAppHelper.getToken
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.Switch
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    lateinit var allowPushSw:Switch
    lateinit var firestore:FirebaseFirestore
    var firebaseToken:FirebaseToken?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore= FirebaseFirestore.getInstance()
        allowPushSw=findViewById(R.id.allow_push_sw)

        allowPushSw.setOnCheckedChangeListener { compoundButton, b ->
            if(firebaseToken!=null){
                firebaseToken?.allow=b
                firestore.collection("FirebaseToken").document(firebaseToken?.token!!).set(firebaseToken!!)
            }
        }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("cloudMessage", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!.token

                if(token !=null){

                    firestore.collection("FirebaseToken").document(token).get()
                        .addOnSuccessListener {documentSnapshot ->
                                firebaseToken=documentSnapshot .toObject(FirebaseToken::class.java)
                                if(firebaseToken==null){
                                    firebaseToken=FirebaseToken(token)
                                    firestore.collection("FirebaseToken").document(token).set(firebaseToken!!)
                                }
                                else{
                                    allowPushSw.isChecked=firebaseToken?.allow!!
                                }

                            }
                }

            })
    }
}
