### activity_add_photo.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    android:padding="10dp"
    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/image_iv"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginRight="10dp"
        app:srcCompat="@mipmap/ic_launcher" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <EditText
            android:id="@+id/description_tv"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ems="10"
            android:gravity="start|top"
            android:inputType="textMultiLine"
            android:lines="5" />

        <Button
            android:id="@+id/submit_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:text="사진올리기" />
    </LinearLayout>
</LinearLayout>
```




### AddPhotoActivity.kt
``` kt
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
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

            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                it->
                val photo=Photo(descriptionTv.text.toString(),it.toString())
                firestore.collection("photo").document().set(photo)
                    .addOnSuccessListener { Unit->
                        setResult(RESULT_OK)
                        finish()
                    }
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
```
### item_photo.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <ImageView
        android:id="@+id/image_iv"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:padding="3dp"
        android:scaleType="centerCrop"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@mipmap/ic_launcher" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### PhotoAdapter.kt
``` kt
class PhotoAdapter(var context: Context, var photoList:ArrayList<Photo> ) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var onPhotoClickListener:OnPhotoClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemVIew=LayoutInflater.from(context).inflate(R.layout.item_photo,parent,false)
        return ViewHolder(itemVIew)
    }



    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imageIv: ImageView =itemView.findViewById(R.id.image_iv)
        fun bind(content:Photo){
            Glide.with(itemView).load(content.imageUrl).into(imageIv)
            imageIv.setOnClickListener{
                if(onPhotoClickListener!=null){
                    onPhotoClickListener?.onClick(content)
                }
            }


        }

    }

}


interface OnPhotoClickListener{
    fun onClick(photo:Photo)
}
```
### activity_main.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="10dp"
    tools:context=".MainActivity" >

    <Button
        android:id="@+id/add_photo_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="추가" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/photo_rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1" />

</LinearLayout>
```

### MainActivity.kt
``` kt
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
```
```class MainActivity : AppCompatActivity(),OnPhotoClickListener {


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
```

### Photo.kt
``` kt
import java.util.*

class Photo(
    var description:String="",
      var imageUrl:String?=null,
      var date: Date=Date()) {}
```
 
 
 ### PhotoActivity.kt
 ``` kt
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

            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                it->
                val photo=Photo(descriptionTv.text.toString(),it.toString())
                firestore.collection("photo").document().set(photo)
                    .addOnSuccessListener { Unit->
                        setResult(RESULT_OK)
                        finish()
                    }
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
```
