### build.gradle(Module: app)
``` gradle
dependencies {
    implementation 'com.google.android.gms:play-services-auth:17.0.0'
}
```


### activity_login.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center_horizontal|center_vertical"
        android:orientation="vertical"
        android:paddingLeft="20dp"
        android:paddingRight="20dp">

        <EditText
            android:id="@+id/email_et"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ems="10"
            android:hint="이메일을 입력해주세요"
            android:inputType="textPersonName" />

        <EditText
            android:id="@+id/password_et"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ems="10"
            android:hint="패스워드를 입력해주세요"
            android:inputType="textPassword" />

        <Button
            android:id="@+id/email_login_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="이메일로 로그인" />

        <Button
            android:id="@+id/google_login_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="30dp"
            android:text="구글 아이디로 로그인" />
    </LinearLayout>

    <ProgressBar
        android:id="@+id/loading_pb"
        style="?android:attr/progressBarStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
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
    tools:context=".MainActivity" >

    <androidx.viewpager.widget.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1" />

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.tabs.TabItem
            android:id="@+id/home_tab"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:icon="@drawable/baseline_home_black_48" />

        <com.google.android.material.tabs.TabItem
            android:id="@+id/search_tab"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:icon="@drawable/baseline_search_black_48" />

        <com.google.android.material.tabs.TabItem
            android:id="@+id/add_tab"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:icon="@drawable/baseline_add_circle_outline_black_48" />

        <com.google.android.material.tabs.TabItem
            android:id="@+id/star_tab"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:icon="@drawable/baseline_star_border_black_48" />

        <com.google.android.material.tabs.TabItem
            android:id="@+id/profile_tab"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:icon="@drawable/baseline_person_outline_black_48" />
    </com.google.android.material.tabs.TabLayout>
</LinearLayout>
```

### LoginActivity.kt
``` kt
class LoginActivity : AppCompatActivity() {

    lateinit var emailEt:EditText
    lateinit var passwordEt:EditText

    lateinit var emailLoginBtn: Button

    lateinit var loadingPb:ProgressBar

    lateinit var auth: FirebaseAuth

    lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEt=findViewById(R.id.email_et)
        passwordEt=findViewById(R.id.password_et)
        emailLoginBtn=findViewById(R.id.email_login_btn)

        loadingPb=findViewById(R.id.loading_pb)

        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()



        emailLoginBtn.setOnClickListener {
            emailLogin()
        }


        moveMain(auth.currentUser)

    }

    fun startLoading(){
        loadingPb.visibility=VISIBLE
    }
    fun endLoading(){
        loadingPb.visibility=GONE
    }

    fun emailLogin(){
        var email=emailEt.text.toString()
        var password=passwordEt.text.toString()

        if(email.equals("")){
            return Toast.makeText(this,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()
        }
        if(password.length<6){
            return Toast.makeText(this,"패스워드는 6자이상이여야합니다",Toast.LENGTH_SHORT).show()
        }
        startLoading()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
                endLoading()
                if(task.isSuccessful){

                    var user=User(auth.currentUser?.email!!)
                    startLoading()
                    firestore.collection("User").document(auth.currentUser?.email!!).set(user)
                        .addOnCompleteListener {
                            task->
                            endLoading()
                            moveMain(auth.currentUser)
                        }

                }
                //로그인시 오류가 발생했을경우
                else if(task.exception?.message.isNullOrEmpty()){
                    Toast.makeText(this,"로그인 오류가 발생했습니다 ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                }
                //이미 가입된 회원이 있을경우
                else{
                    signinEmail()
                }
        }
    }
    //이메일 로그인하기
    fun signinEmail(){
        var email=emailEt.text.toString()
        var password=passwordEt.text.toString()
        startLoading()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                task->

            endLoading()
            if(task.isSuccessful){
                if(auth!=null){

                    moveMain(auth.currentUser)
                }
            }
            else if(task.exception?.message?.isNotEmpty()!!){
                Toast.makeText(this,"로그인에 실패하였습니다",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"로그인에 실패하였습니다 ${task.result.toString()}",Toast.LENGTH_SHORT).show()
            }


        }
    }

    fun moveMain(user:FirebaseUser?){
        if(user!=null){
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
```

### MainActivity.kt
``` kt
class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var tabLayout:TabLayout
    lateinit var mainPageAdapter:MainPageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPageAdapter= MainPageAdapter(supportFragmentManager)
        viewPager=findViewById(R.id.viewpager)
        viewPager.adapter=mainPageAdapter
        tabLayout=findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)


        tabLayout.getTabAt(0)?.setIcon(R.drawable.baseline_home_black_48)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.baseline_search_black_48)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.baseline_add_circle_outline_black_48)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.baseline_star_border_black_48)
        tabLayout.getTabAt(4)?.setIcon(R.drawable.baseline_person_outline_black_48)

    }
    fun moveTab(index:Int){
        viewPager.setCurrentItem(index)
    }

}
```

### MainPageAdapter
``` kt
class MainPageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return HomeFragment()
    }

    override fun getCount(): Int {
        return 5
    }
}
```

### HomeFragment.kt
``` kt
class HomeFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

}
```
### ProfileFragment.kt
``` kt
class ProfileFragment : Fragment() {
    lateinit var profileIv: ImageView
    lateinit var emailTv: TextView

    lateinit var auth: FirebaseAuth
    lateinit var firestore:FirebaseFirestore
    lateinit var storage:FirebaseStorage

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
        emailTv.text=auth.currentUser?.email

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
```

### AddFragment.kt
``` kt

class AddFragment : Fragment() {

    lateinit var imageIv:ImageView
    lateinit var descriptionEt: EditText
    lateinit var uploadBtn: Button

    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var storage: FirebaseStorage

    val IMAGE_PICK=1001

    lateinit var post:Post


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==IMAGE_PICK&&resultCode==RESULT_OK){
            var imageUri=data?.data
            imageIv.setImageURI(imageUri)
            if(imageUri!=null){

                storage.getReference().child("post").child(UUID.randomUUID().toString()).putFile(imageUri)
                    .addOnSuccessListener {
                            it->
                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                downloadUrl->
                            post.imageUrl=downloadUrl.toString()

                        }
                    }
            }
        }
    }

}
### item_feed.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">


    <LinearLayout
        android:id="@+id/user_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:padding="10dp">

        <ImageView
            android:id="@+id/profile_iv"
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:scaleType="centerCrop" />

        <TextView
            android:id="@+id/name_tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginLeft="10dp" />
    </LinearLayout>

    <ImageView
        android:id="@+id/image_iv"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:scaleType="centerCrop"
        app:layout_constraintDimensionRatio="1:1"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"

        app:layout_constraintTop_toBottomOf="@id/user_container" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

```
### FeedAdapter
``` kt
class FeedAdapter(var context: Context,var postList:ArrayList<Post> ) :RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    var firestore= FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_feed,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var profileIv:ImageView=itemView.findViewById(R.id.profile_iv)
        var nameTv: TextView =itemView.findViewById(R.id.name_tv)
        var imageIv:ImageView=itemView.findViewById(R.id.image_iv)

        fun bind(post:Post){
            firestore.collection("User").document(post.userId!!).get()
                .addOnCompleteListener { task->
                    var user=task.result?.toObject(User::class.java)
                    nameTv.text=user?.email
                    Glide.with(profileIv).load(user?.imageUrl).into(profileIv)
                }
            Glide.with(imageIv).load(post.imageUrl).into(imageIv)


        }
    }
}
```
### HomeFragment
