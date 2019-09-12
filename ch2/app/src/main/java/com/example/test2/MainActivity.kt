package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var firestore:FirebaseFirestore
    lateinit var messageEt:EditText
    lateinit var submitBtn: Button
    lateinit var messageList:ArrayList<Message>
    lateinit var messageRv:RecyclerView
    lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //채팅 메세지데이터를 관리하는 리스트 초기화
        messageList=ArrayList()

        //UI에 사용될 위젯 초기화
        messageEt=findViewById(R.id.message_tv)
        submitBtn=findViewById(R.id.submit_btn)
        messageRv=findViewById(R.id.message_rv)

        //Adapter초기화
        messageAdapter= MessageAdapter(this,messageList)
        messageRv.adapter=messageAdapter
        messageRv.layoutManager=LinearLayoutManager(this)

        //Firestore 초기화
        firestore= FirebaseFirestore.getInstance()

        //Message Collection의 변경사항 리스너 등록
        firestore?.collection("message").whereEqualTo("type",1).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (querySnapshot != null) {
                //변경사항을 메세지 리스트에 추가함
                for(dc in querySnapshot.documentChanges){
                    var message=dc.document.toObject(Message::class.java)
                    messageList.add(message)
                    messageAdapter.notifyDataSetChanged()
                    messageRv.scrollToPosition(messageAdapter.itemCount-1)
                }
            }
        }

        //전송 버튼을 눌렀을때
        submitBtn.setOnClickListener{ onClickSubmitBtn() }

    }

    fun onClickSubmitBtn(){
        var msg=messageEt.text.toString()
        if("".equals(msg)){
            return
        }
        //사용자가 입력한 메세지로 Message 인스턴스 생성
        var message=Message(msg)
        messageEt.setText("")   //메세지 입력창 초기화

        firestore?.collection("message").document().set(message)
            .addOnCompleteListener { task->
                if(!task.isSuccessful){
                    Toast.makeText(this,"네트워크가 원활하지 않습니다",Toast.LENGTH_SHORT).show()
                }

            }
    }
}
