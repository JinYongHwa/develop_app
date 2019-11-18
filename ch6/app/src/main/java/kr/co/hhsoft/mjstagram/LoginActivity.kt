package kr.co.hhsoft.mjstagram

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

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
                    firestore.collection("User").document().set(user)
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