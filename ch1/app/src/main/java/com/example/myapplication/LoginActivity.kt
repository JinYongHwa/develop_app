package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    //Firebase Authentication 관리클래스
    var auth:FirebaseAuth?=null
    lateinit var email_et: EditText
    lateinit var password_et: EditText
    lateinit var email_login_btn: Button
    lateinit var google_login_btn: Button

    //Google로그인 관리클래스
    var googleSignInClient: GoogleSignInClient?=null

    //Google 로그인 요청코드
    val GOOGLE_LOGIN_CODE=9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth=FirebaseAuth.getInstance()
        email_et=findViewById(R.id.email_et)
        password_et=findViewById(R.id.password_et)
        email_login_btn=findViewById(R.id.email_login_btn)
        google_login_btn=findViewById(R.id.google_login_btn)

        email_login_btn.setOnClickListener{createAndLoginEmail()}
        google_login_btn.setOnClickListener{googleLogin()}

        //구글 로그인 옵션
        var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)
    }

    //이메일회원가입 및 로그인 메소드
    fun createAndLoginEmail(){

        auth?.createUserWithEmailAndPassword(email_et.text.toString(),password_et.text.toString())
            ?.addOnCompleteListener{task->

                //아이디 생성에 성공했을경우
                if(task.isSuccessful){
                    moveMainPage(auth?.currentUser)
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

    //구글 로그인 요청
    fun googleLogin(){
        var signInIntent=googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    //이메일 로그인하기
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_et.text.toString(),password_et.text.toString())?.addOnCompleteListener{
            task->
            if(task.isSuccessful){
                if(auth!=null){
                    moveMainPage(auth?.currentUser)
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

    //로그인후 MainActivity로 이동
    fun moveMainPage(user: FirebaseUser?){
        if(user!=null){
            var intent: Intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Google 로그인 완료시 처리
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        var credetial=GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credetial)
            ?.addOnCompleteListener{task->
                if(task.isSuccessful){
                    moveMainPage(auth?.currentUser)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //구글로그인 완료시
        if(requestCode==GOOGLE_LOGIN_CODE){
            var result= Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account=result.signInAccount
                firebaseAuthWithGoogle(account!!)
            }
        }

    }
}
