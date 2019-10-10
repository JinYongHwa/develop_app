package kr.co.hhsoft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(var context: Context, var photoList:ArrayList<Photo> ) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemVIew=LayoutInflater.from(context).inflate(R.layout.item_photo,parent,false)
        return ViewHolder(itemVIew)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imageIv: ImageView =itemView.findViewById(R.id.image_iv)
        fun bind(content:Photo){
            Glide.with(itemView).load(content.imageUrl).into(imageIv)
        }
    }
}