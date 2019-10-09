### AndroidManifest.xml
``` xml
<!--추가-->
 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
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

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/message_rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <EditText
            android:id="@+id/message_tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:ems="10"
            android:inputType="textPersonName" />

        <Button
            android:id="@+id/submit_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="전송" />
    </LinearLayout>
</LinearLayout>
```

### item_message.xml
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/linearLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="10dp">

    <TextView
        android:id="@+id/message_tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:background="#eeee55"
        android:padding="10dp"
        android:text="안녕하세요"
        tools:layout_conversion_absoluteHeight="56dp"
        tools:layout_conversion_absoluteWidth="371dp"
        tools:layout_editor_absoluteX="20dp"
        tools:layout_editor_absoluteY="20dp" />

    <TextView
        android:id="@+id/time_tv"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:gravity="bottom"
        android:text="13:00"
        tools:layout_conversion_absoluteHeight="56dp"
        tools:layout_conversion_absoluteWidth="0dp"
        tools:layout_editor_absoluteX="391dp"
        tools:layout_editor_absoluteY="20dp" />

    <View
        android:id="@+id/view"
        android:layout_width="30dp"
        android:layout_height="match_parent"
        tools:layout_conversion_absoluteHeight="56dp"
        tools:layout_conversion_absoluteWidth="0dp"
        tools:layout_editor_absoluteX="391dp"
        tools:layout_editor_absoluteY="20dp" />
</LinearLayout>
```

### Message.kt
```
import java.util.*

class Message(var message:String="",var date:Date=Date(),var type:Int=1) {

}
```

### MessageAdapter.kt
``` kt

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(val context: Context,val messageList:ArrayList<Message> ) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    //ViewHolder 와 Message 인스턴스를 연결함
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var message=getItem(position)
        holder.bind(message)
    }
    //ViewHolder를 생성시킴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_message,parent,false)
        return ViewHolder(view)
    }

    //index값에 해당하는 Message 인스턴스를 반환함
    fun getItem(index:Int): Message{
        return messageList.get(index)
    }

    //RecyclerView 에 그려야할 전체 아이템 갯수를 반환함
    override fun getItemCount(): Int {
        return messageList.size
    }

    //한개의 아이템을 관리해주는 ViewHolder 클래스
    inner class ViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView!!){
        var messageTv =itemView?.findViewById<TextView>(R.id.message_tv)
        var timeTv =itemView?.findViewById<TextView>(R.id.time_tv)
        fun bind(message:Message){
            messageTv?.setText(message.message)
            timeTv?.setText(String.format("%02d:%02d",message.date.hours,message.date.minutes))
        }
    }
}
```

### MainActivity.kt
``` kt

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
        firestore?.collection("message").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
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

```
