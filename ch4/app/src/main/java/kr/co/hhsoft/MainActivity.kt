package kr.co.hhsoft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(),OnPhotoClickListener {


    lateinit var addPhotoBtn: Button
    lateinit var photoRv:RecyclerView

    lateinit var photoList:ArrayList<Photo>
    lateinit var photoAdapter:PhotoAdapter
    lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore= FirebaseFirestore.getInstance()
        addPhotoBtn=findViewById(R.id.add_photo_btn)
        photoRv=findViewById(R.id.photo_rv)

        addPhotoBtn.setOnClickListener{ addPhoto() }

        photoList=ArrayList()
        photoAdapter= PhotoAdapter(this,photoList)
        photoRv.adapter=photoAdapter
        photoAdapter.onPhotoClickListener=this
        photoRv.layoutManager= GridLayoutManager(this,3)
        firestore.collection("photo").addSnapshotListener{
            querySnapshot, firebaseFirestoreException ->
            if(querySnapshot!=null){
                for(dc in querySnapshot.documentChanges){
                    if(dc.type== DocumentChange.Type.ADDED){
                        var photo=dc.document.toObject(Photo::class.java)
                        photo.id=dc.document.id
                        photoList.add(photo)
                        photoAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun addPhoto(){
        var intent= Intent(this,AddPhotoActivity::class.java)
        startActivity(intent)
    }
    override fun onClick(photo: Photo) {
        var intent=Intent(this,PhotoActivity::class.java)
        intent.putExtra("id",photo.id)
        startActivity(intent)
    }

}
