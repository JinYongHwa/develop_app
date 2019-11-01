package kr.co.hhsoft

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PhotoActivity:AppCompatActivity() {

    lateinit var imageIv:ImageView
    lateinit var descriptionTv: TextView

    lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        firestore= FirebaseFirestore.getInstance()
        imageIv=findViewById(R.id.image_iv)
        descriptionTv=findViewById(R.id.description_tv)

        var id=intent.getStringExtra("id")
        firestore.collection("photo").document(id).get().addOnCompleteListener {
            task->
            if(task.isSuccessful){
                var snapshot= task.result
                var imageUrl=snapshot?.get("imageUrl")
                var description=snapshot?.get("description")
                Glide.with(imageIv).load(imageUrl).into(imageIv)
                if(description!=null){
                    descriptionTv.text=description as String
                }

            }
        }

    }
}