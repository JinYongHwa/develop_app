### Google 아이디로 로그인
> https://firebase.google.com/docs/auth/android/google-signin?hl=ko


### build.gradle(Module: app)
``` gradle
//dependencies에 추가
implementation 'com.google.firebase:firebase-auth:18.1.0'
implementation 'com.google.android.gms:play-services-auth:17.0.0'
```

### AndroidManiFest.xml
``` xml
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
```

### activity_login.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center_vertical"
    android:orientation="vertical"
    tools:context=".LoginActivity">

    <EditText
        android:id="@+id/email_et"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ems="10"
        android:hint="Email"
        android:inputType="textPersonName" />

    <EditText
        android:id="@+id/password_et"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ems="10"
        android:hint="Password"
        android:inputType="textPassword" />

    <Button
        android:id="@+id/email_login_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="15dp"
        android:text="회원가입 및 이메일 로그인" />

    <Button
        android:id="@+id/google_login_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:text="Google으로 로그인" />
</LinearLayout>
```

### activity_main.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/email_tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="TextView" />
</LinearLayout>
```

### MainActivity.kt
``` kt
class MainActivity : AppCompatActivity() {

    lateinit var emailEt: EditText
    lateinit var passwordEt:EditText
    lateinit var emailLoginBtn:Button
    lateinit var googleLoginBtn:Button

    lateinit var auth:FirebaseAuth

    lateinit var googleSignClient:GoogleSignInClient

    val GOOGLE_LOGIN_CODE=10001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth= FirebaseAuth.getInstance()

        emailEt=findViewById(R.id.email_et)
        passwordEt=findViewById(R.id.password_et)
        emailLoginBtn=findViewById(R.id.email_login_btn)
        googleLoginBtn=findViewById(R.id.google_login_btn)

        var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignClient=GoogleSignIn.getClient(this,gso)

        emailLoginBtn.setOnClickListener {

            var email=emailEt.text.toString()
            var password=passwordEt.text.toString()
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    result->
                        if(result.isSuccessful){
                            loginSuccess()
                        }
                        else if(result.exception?.message.isNullOrEmpty()){
                            Toast.makeText(this,"로그인중 오류가 발생하였습니다",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            login(email,password)
                        }
                }

        }

        googleLoginBtn.setOnClickListener {
            var signInIntent=googleSignClient.signInIntent
            startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
        }
    }
    fun login(email:String,password:String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                result->
                    if(result.isSuccessful){
                        loginSuccess()
                    }
                    else{
                        Toast.makeText(this,"로그인중 오류가 발생하였습니다",Toast.LENGTH_SHORT).show()
                    }
            }
    }


    fun loginSuccess(){
        if(auth.currentUser!=null){
            var intent= Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    loginSuccess()
                } else {
                    // If sign in fails, display a message to the user.

                    loginSuccess()
                }

                // ...
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==GOOGLE_LOGIN_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }


}
```

### SecondActivity.kt
``` kt
class SecondActivity: AppCompatActivity() {

    lateinit var emailTv: TextView
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailTv=findViewById(R.id.email_tv)
        auth= FirebaseAuth.getInstance()

        emailTv.setText(auth.currentUser?.email)
    }
}
```

