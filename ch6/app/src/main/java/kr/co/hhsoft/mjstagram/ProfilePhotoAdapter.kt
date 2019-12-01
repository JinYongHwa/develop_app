package kr.co.hhsoft.mjstagram

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfilePhotoAdapter(var context: Context,var postList:ArrayList<Post> ) : RecyclerView.Adapter<ProfilePhotoAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_profile,parent,false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imageIv: ImageView =itemView.findViewById(R.id.image_iv)

        fun bind(post:Post){
            Glide.with(imageIv).load(post.imageUrl).into(imageIv)
        }
    }
}