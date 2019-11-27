package kr.co.hhsoft.mjstagram

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddFragment : Fragment() {

    lateinit var imageIv:ImageView
    lateinit var descriptionEt: EditText
    lateinit var uploadBtn: Button

    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var storage: FirebaseStorage

    val IMAGE_PICK=1001

    lateinit var post:Post
    var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storage= FirebaseStorage.getInstance()

        post=Post()


        var view=inflater.inflate(R.layout.fragment_add,container,false)

        imageIv=view.findViewById(R.id.image_iv)
        descriptionEt=view.findViewById(R.id.description_et)
        uploadBtn=view.findViewById(R.id.upload_btn)




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageIv.setOnClickListener{
            var intent= Intent(ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,IMAGE_PICK)
        }
        uploadBtn.setOnClickListener {
            var description=descriptionEt.text.toString()
            if(description.equals("")){
                Toast.makeText(activity,"문구를 입력해주세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(imageUri!=null){

                storage.getReference().child("post").child(UUID.randomUUID().toString()).putFile(imageUri!!)
                    .addOnSuccessListener {
                            it->
                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                downloadUrl->
                            post.imageUrl=downloadUrl.toString()

                            post.description=description
                            post.userId=auth.currentUser?.email

                            firestore.collection("Post").document().set(post)
                                .addOnSuccessListener {
                                    imageIv.setImageDrawable(activity?.resources?.getDrawable(R.drawable.baseline_add_circle_outline_black_48))
                                    descriptionEt.text.clear()
                                    var mainActivity=activity as MainActivity
                                    mainActivity.moveTab(0)
                                }
                        }
                    }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==IMAGE_PICK&&resultCode==RESULT_OK){
            imageUri=data?.data
            imageIv.setImageURI(imageUri)

        }
    }

}