package kr.co.hhsoft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.FirestoreClient
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity: AppCompatActivity() {

    lateinit var imageIv:ImageView
    lateinit var descriptionTv:TextView
    lateinit var submitBtn: Button

    lateinit var storage: FirebaseStorage
    lateinit var firestore:FirebaseFirestore

    val PICK_IMAGE_FROM_ALBUM=1001

    var imageUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        imageIv=findViewById(R.id.image_iv)
        descriptionTv=findViewById(R.id.description_tv)
        submitBtn=findViewById(R.id.submit_btn)

        storage= FirebaseStorage.getInstance()
        firestore=FirebaseFirestore.getInstance()


        submitBtn.setOnClickListener{ submit() }
        imageIv.setOnClickListener{ pickImage() }
    }


    fun submit(){
        if(imageUri==null){
            return
        }
        val timestamp=SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val imageFileName="JPEG_"+timestamp+"_.png"
        val storeageRef=storage.getReference().child("image").child(imageFileName)
        storeageRef.putFile(imageUri!!).addOnSuccessListener {
            taskSnapshot ->
            val content=Content(descriptionTv.text.toString(),imageUri.toString())
            firestore.document("content").set(content)
                .addOnSuccessListener { Unit->
                    setResult(RESULT_OK)
                    finish()
                }
        }
    }
    fun pickImage(){
        var intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,PICK_IMAGE_FROM_ALBUM)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM&&resultCode==RESULT_OK){
            imageIv.setImageURI(data?.data)
            imageUri=data?.data

        }
    }


}