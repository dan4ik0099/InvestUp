package com.example.investup.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.ChatItemBinding
import com.example.investup.databinding.MessageItemBinding
import com.example.investup.retrofit.dataClass.Dialog
import com.example.investup.retrofit.dataClass.Message
import com.squareup.picasso.Picasso
import org.json.JSONObject


class MessageAdapter(val listener: Listener) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>() {
    private var messageList = ArrayList<Message>()
    lateinit var authorId: String

    class MessageHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = MessageItemBinding.bind(item)
        fun bind(message: Message, authorId: String, listener: Listener) = with(binding) {
            message.let {




                textLabel.text = (it.text)
                if (it.user.id != authorId){
                    val layoutParams = cardMessage.layoutParams as FrameLayout.LayoutParams
                    layoutParams.gravity = Gravity.END
                    cardMessage.layoutParams = layoutParams
                    val color = Color.parseColor("#E1F1B6")
                    cardMessage.setCardBackgroundColor(color)
                    itemView.setOnLongClickListener{
                        listener.onLongClickMessage(message)
                        true
                    }

                }else{
                    val layoutParams = cardMessage.layoutParams as FrameLayout.LayoutParams
                    layoutParams.gravity = Gravity.START
                    cardMessage.layoutParams = layoutParams
                    val color = Color.parseColor("#F2F2F2")
                    cardMessage.setCardBackgroundColor(color)
                }

                val formatedDate = it.createdAt.replace("T", "/").substring(11, 16)
                dateLabel.text = formatedDate

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(messageList[position], authorId ,listener)
    }

    fun addMessages(messages: ArrayList<Message>,  authorId: String) {
        this.authorId = authorId
        messageList.clear()
        messageList.addAll(messages)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message):Int {


            messageList.add(0, message)
            notifyItemInserted(0)

            return messageList.size

    }

    fun removeMessage(message: Message){


        messageList.remove(message)
        notifyItemRemoved(messageList.indexOf(message))



    }




    interface Listener {
        fun onLongClickMessage(message: Message)
    }


}
