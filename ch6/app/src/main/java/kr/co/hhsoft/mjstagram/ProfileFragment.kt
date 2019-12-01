package kr.co.hhsoft.mjstagram

import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    lateinit var profileIv: ImageView
    lateinit var emailTv: TextView
    lateinit var photoLIstRv:RecyclerView
    lateinit var logoutBtn: Button


    lateinit var auth: FirebaseAuth
    lateinit var firestore:FirebaseFirestore
    lateinit var storage:FirebaseStorage


    lateinit var profilePhotoAdapter:ProfilePhotoAdapter
    var postList=ArrayList<Post>()

    var user:User?=null

    val IMAGE_PICK=1001


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storage= FirebaseStorage.getInstance()


        var view= inflater.inflate(R.layout.fragment_profile,container,false)


        profileIv= view.findViewById(R.id.profile_iv)
        emailTv= view.findViewById(R.id.email_tv)
        photoLIstRv=view.findViewById(R.id.photo_list_rv)
        logoutBtn=view.findViewById(R.id.logout_btn)

        emailTv.text=auth.currentUser?.email


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePhotoAdapter=ProfilePhotoAdapter(context!!,postList)
        photoLIstRv.adapter=profilePhotoAdapter
        photoLIstRv.layoutManager=GridLayoutManager(context,3)

        firestore.collection("Post").whereEqualTo("userId",auth.currentUser?.email!!)
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot!=null){
                    for(dc in querySnapshot.documentChanges){
                        if(dc.type==DocumentChange.Type.ADDED){
                            var post=dc.document.toObject(Post::class.java)
                            postList.add(0,post)
                        }
                    }
                    profilePhotoAdapter.notifyDataSetChanged()
                }
            }

        firestore.collection("User").document(auth.currentUser?.email!!)
            .get().addOnSuccessListener {
                task->
                user=task.toObject(User::class.java)
                if(user?.imageUrl != null){
                    Glide.with(profileIv).load(user?.imageUrl).into(profileIv)
                }

            }

        profileIv.setOnClickListener{
            var intent= Intent(ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,IMAGE_PICK)
        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            var intent=Intent(context,LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        super.onCreate(savedInstanceState)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== IMAGE_PICK && resultCode== AppCompatActivity.RESULT_OK) {
            var imageUri=data?.data
            profileIv.setImageURI(imageUri)
            if(imageUri!=null){
                storage.getReference().child("profile").child(auth.currentUser?.email!!).putFile(imageUri)
                    .addOnSuccessListener {
                        it->
                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                            downloadUrl->
                            user?.imageUrl=downloadUrl.toString()
                            firestore.collection("User").document(user?.email!!).set(user!!)


                        }
                    }
            }


        }
    }

}