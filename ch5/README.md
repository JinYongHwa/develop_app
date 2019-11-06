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

    <Switch
        android:id="@+id/allow_push_sw"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="알람허용" />
</LinearLayout>
```


### FirebaseToken
``` kt
class FirebaseToken(var token:String="",var allow:Boolean=true) {}
```

### MainActivity.kt
``` kt
class MainActivity : AppCompatActivity() {

    lateinit var allowPushSw:Switch
    lateinit var firestore:FirebaseFirestore
    var firebaseToken:FirebaseToken?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore= FirebaseFirestore.getInstance()
        allowPushSw=findViewById(R.id.allow_push_sw)

        allowPushSw.setOnCheckedChangeListener { compoundButton, b ->
            if(firebaseToken!=null){
                firebaseToken?.allow=b
                firestore.collection("FirebaseToken").document(firebaseToken?.token!!).set(firebaseToken!!)
            }
        }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("cloudMessage", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result!!.token

                if(token !=null){

                    firestore.collection("FirebaseToken").document(token).get()
                        .addOnSuccessListener {documentSnapshot ->
                                firebaseToken=documentSnapshot .toObject(FirebaseToken::class.java)
                                if(firebaseToken==null){
                                    firebaseToken=FirebaseToken(token)
                                    firestore.collection("FirebaseToken").document(token).set(firebaseToken!!)
                                }
                                else{
                                    allowPushSw.isChecked=firebaseToken?.allow!!
                                }

                            }
                }

            })
    }
}
```


### MessageService.kt
``` kt
class MessageService : FirebaseMessagingService() {
    lateinit var firestore:FirebaseFirestore
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
    }
    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.d("cloudMessage",token)
        firestore= FirebaseFirestore.getInstance()
        if(token !=null){
            var firebaseToken=FirebaseToken(token)
            firestore.collection("FirebaseToken").document().set(firebaseToken)
        }

    }
}
```

### AndroidManifest.xml
``` xml
<service android:name=".MessageService">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```
