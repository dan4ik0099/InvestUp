package com.example.investup.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.CommentItemBinding
import com.example.investup.databinding.PostItemBinding
import com.example.investup.publicObject.ConstNavigation
import com.example.investup.retrofit.dataClass.Comment

import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.example.investup.retrofit.dataClass.User
import com.squareup.picasso.Picasso


class CommentAdapter(val listener: Listener) :
    RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    lateinit var idAuthor: String
    private var commentList = ArrayList<Comment>()


    class CommentHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = CommentItemBinding.bind(item)
        fun bind(comment: Comment, idAuthor: String, listener: Listener) = with(binding) {
            comment.apply {

                Picasso.get().load(user.avatar).into(userAvatarImage)
                nameSurnameLabel.text = ("${user.firstName} ${user.lastName}")
                val formatedDate = createdAt.substringBefore("T")
                dateLabel.text = formatedDate
                textLabel.text = text

                if (user.id == idAuthor) {
                    deleteCommentButton.visibility = View.VISIBLE
                    deleteCommentButton.setOnClickListener {
                        listener.onClickDeleteCommentButton(comment)
                    }
                }
                    else{
                    deleteCommentButton.visibility = View.GONE
                    }







            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(commentList[position], idAuthor, listener)
    }

    fun addComments(comments: ArrayList<Comment>, idAuthor: String) {

        commentList.clear()
        this.idAuthor = idAuthor
        commentList.addAll(comments)
        notifyDataSetChanged()
    }


    fun deleteItem(id : String)
    {
        val com = commentList.find { it.id == id }
        val pos = commentList.indexOf(com)
        commentList.removeAt(pos)
        notifyItemRemoved(pos)

    }


    fun addComment(comment: Comment, idAuthor: String) {

        this.idAuthor = idAuthor
        commentList.add(comment)
        println("ari " + commentList.size)
        notifyItemInserted(itemCount-1)
    }

    interface Listener {
        fun onClickComment(comment: Comment)
        fun onClickDeleteCommentButton(comment: Comment)
    }


}