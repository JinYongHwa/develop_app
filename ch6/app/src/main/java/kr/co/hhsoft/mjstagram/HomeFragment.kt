package kr.co.hhsoft.mjstagram

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment: Fragment() {

    lateinit var feedRv:RecyclerView
    lateinit var feedAdapter:FeedAdapter
    var postList=ArrayList<Post>()
    lateinit var firestore:FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.fragment_home,container,false)
        feedRv=view.findViewById(R.id.feed_rv)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore= FirebaseFirestore.getInstance()
        feedAdapter=FeedAdapter(context!!,postList)
        feedRv.adapter=feedAdapter
        feedRv.layoutManager=LinearLayoutManager(context)

        firestore.collection("Post").addSnapshotListener {
                querySnapshot, firebaseFirestoreException ->
            if(querySnapshot!=null){

                for(dc in querySnapshot.documentChanges){
                    var post=dc.document.toObject(Post::class.java)

                    if(dc.type==DocumentChange.Type.ADDED){
                        postList.add(0,post)
                    }
                }
                feedAdapter.notifyDataSetChanged()
            }
        }



    }



}