package kr.co.hhsoft.mjstagram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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