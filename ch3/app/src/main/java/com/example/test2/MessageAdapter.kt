package com.example.test2

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