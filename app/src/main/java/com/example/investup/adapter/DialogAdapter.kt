package com.example.investup.adapter




import android.graphics.text.LineBreaker
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.ChatItemBinding
import com.example.investup.databinding.CommentItemBinding
import com.example.investup.databinding.PostItemBinding
import com.example.investup.fragments.ChatFragment
import com.example.investup.publicObject.ConstNavigation
import com.example.investup.retrofit.dataClass.*

import com.squareup.picasso.Picasso
import org.json.JSONObject


class DialogAdapter(val listener: Listener) :
    RecyclerView.Adapter<DialogAdapter.DialogHolder>() {
    private var dialogList = ArrayList<Dialog>()


    class DialogHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ChatItemBinding.bind(item)
        fun bind(dialog: Dialog, listener: Listener) = with(binding) {
            dialog.let {

                Picasso.get().load(it.user.avatar).into(avatarImageView)
                nameSurnameLabel.text = ("${it.user.firstName} ${it.user.lastName}")

                if (it.unreadableMessages>0){
                    cardMessagesCount.visibility = View.VISIBLE
                    unreadMessageCountLabel.text = it.unreadableMessages.toString()
                }
                else cardMessagesCount.visibility = View.INVISIBLE
                val formatedDate = it.lastMessage.createdAt.replace("T", "/").substring(0, 16)
                dateLabel.text = formatedDate
                lastMessageLabel.text = it.lastMessage.text

                itemView.setOnClickListener{
                    listener.onClickDialog(dialog)
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return DialogHolder(view)
    }

    override fun getItemCount(): Int {
        return dialogList.size
    }

    override fun onBindViewHolder(holder: DialogHolder, position: Int) {
        holder.bind(dialogList[position], listener)
    }

    fun addDialogs(dialogs: ArrayList<Dialog>) {
        dialogList.clear()
        dialogList.addAll(dialogs)
        notifyDataSetChanged()
    }
    fun bumpDialog(message: String, isNew: Boolean) {
        println(message)
        val id = JSONObject(message).getString("dialogId")

        val text = JSONObject(message).getString("text")
        val time = JSONObject(message).getString("createdAt")

        val element = dialogList.find { it.id == id }
        element!!.unreadableMessages +=1
        element!!.lastMessage.text = text
        element.lastMessage.createdAt = time
        if(!isNew) dialogList.remove(element)
        dialogList.add(0, element!!)
        notifyDataSetChanged()

    }







    interface Listener {
        fun onClickDialog(dialog: Dialog)

    }


}