package com.example.push

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessageService : FirebaseMessagingService() {
    lateinit var firestore:FirebaseFirestore
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
    }
    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.d("cloudMessage",token)
        firestore= FirebaseFirestore.getInstance()
        if(token !=null){
            var firebaseToken=FirebaseToken(token)
            firestore.collection("FirebaseToken").document().set(firebaseToken)
        }

    }
}