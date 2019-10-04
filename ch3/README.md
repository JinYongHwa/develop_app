### MessageAdapter.kt

``` kt
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


    var itemClickListener: ItemClickListener? = null



    //한개의 아이템을 관리해주는 ViewHolder 클래스
    inner class ViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView!!){
        var messageTv =itemView?.findViewById<TextView>(R.id.message_tv)
        var timeTv =itemView?.findViewById<TextView>(R.id.time_tv)
        fun bind(message:Message){
            messageTv?.setText(message.message)
            timeTv?.setText(String.format("%02d:%02d",message.date.hours,message.date.minutes))
            messageTv?.setOnLongClickListener {
                longClick(message)
                true
            }
        }
        fun longClick(message:Message){
            itemClickListener?.onLongClick(message)
        }

    }
    interface ItemClickListener{
        fun onLongClick(message:Message)
    }
}
```

### strings.xml
``` xml
  
<resources>
    <string name="app_name">test2</string>
    <string-array name="chat_menu">
        <item>삭제</item>
        <item>수정</item>
    </string-array>
</resources>
```

### Message.kt
``` kt
import java.util.*

class Message(var message:String="",var date:Date=Date(),var type:Int=1,var id:String="") {

}
```

### MainActivity.kt
``` kt
class MainActivity : AppCompatActivity() {

    lateinit var firestore:FirebaseFirestore
    lateinit var messageEt:EditText
    lateinit var submitBtn: Button
    lateinit var messageList:ArrayList<Message>
    lateinit var messageRv:RecyclerView
    lateinit var messageAdapter: MessageAdapter
    lateinit var instance:MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance=this

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

                for(dc in querySnapshot.documentChanges){
                    //Firebase에 추가된 메세지를 messageList에 추가
                    if(dc.type==DocumentChange.Type.ADDED){
                        var firebaseMessage=dc.document.toObject(Message::class.java)
                        firebaseMessage.id=dc.document.id
                        messageList.add(firebaseMessage)
                        messageAdapter.notifyDataSetChanged()
                        messageRv.scrollToPosition(messageAdapter.itemCount-1)
                    }
                    //Firebase에 삭제된 메세지를 messageList에서도 삭제
                    if(dc.type==DocumentChange.Type.REMOVED){
                        var findedMessage=messageList.filter{message-> message.id==dc.document.id}
                        messageList.remove(findedMessage[0])
                        messageAdapter.notifyDataSetChanged()
                    }
                    //Firebase에 수정된 메세지를 messageList에서도 수정
                    if(dc.type==DocumentChange.Type.MODIFIED){
                        var firebaseMessage=dc.document.toObject(Message::class.java)
                        var findedMessage=messageList.filter{message-> message.id==dc.document.id}
                        var messageIndex=messageList.indexOf(findedMessage[0])
                        messageList.get(messageIndex).message=firebaseMessage.message
                        messageAdapter.notifyDataSetChanged()
                    }

                }
            }
        }

        //전송 버튼을 눌렀을때
        submitBtn.setOnClickListener{ onClickSubmitBtn() }

        //메세지를 길게 클릭했을때
        messageAdapter.itemClickListener=object : MessageAdapter.ItemClickListener {
            override fun onLongClick(message: Message) {

                AlertDialog.Builder(instance)
                    .setItems(R.array.chat_menu,{dialog,which->
                        if(which==0){
                           firestore?.collection("message").document(message.id).delete()
                        }
                        else if(which==1){
                            val input = EditText(instance)
                            AlertDialog.Builder(instance)
                                .setView(input)
                                .setPositiveButton("확인",{dialog, which ->
                                    firestore?.collection("message").document(message.id).update("message",input.text.toString())
                                    dialog.dismiss()
                                })
                                .show()
                        }
                    })
                    .setNegativeButton("취소",{dialog,which->
                        dialog.dismiss()
                    })
                    .show()


            }
        }

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
