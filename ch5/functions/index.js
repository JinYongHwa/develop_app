var admin = require("firebase-admin");

var serviceAccount = require("./jyh2007261051-800e2-firebase-adminsdk-t7ut7-0295ab8914.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://jyh2007261051-800e2.firebaseio.com"
});


var firestore=admin.firestore()
firestore.collection("FirebaseToken").where("allow","==",true).get()
.then(result=>{
  var messages={
    notification: {
      title: 'test',
      body: 'test',
    },
    tokens:[]
  }
  result.forEach(item => {
    messages.tokens.push(item.data().token)

  });
  if(messages.tokens.length>0){
    admin.messaging().sendMulticast(messages).then((response) => {
      // Response is a message ID string.
      console.log('Successfully sent message:', response);
    })
    .catch((error) => {
      console.log('Error sending message:', error);
    });
  }


})
