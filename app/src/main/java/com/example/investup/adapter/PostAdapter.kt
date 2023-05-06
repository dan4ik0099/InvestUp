package com.example.investup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.databinding.PostItemBinding
import com.example.investup.publicObject.ConstNavigation

import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.squareup.picasso.Picasso


class PostAdapter(val listener: Listener) : RecyclerView.Adapter<PostAdapter.PostHolder>() {
    private var postList = ArrayList<Post>()


    class PostHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = PostItemBinding.bind(item)
        fun bind(post: Post, listener : Listener) = with(binding) {
            post.apply {

                Picasso.get().load(user.avatar).into(userProfileImageView)
                nameSurnameLabel.text = ("${user.firstName} ${user.lastName}")
                val formatedDate = createdAt.substringBefore("T")
                dateLabel.text = formatedDate
                titleLabel.text = title
                shortDescriptionLabel.text = shortDescription
                tagsRecyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                val tagInPostAdapter = TagInPostAdapter()
                tagsRecyclerView.adapter = tagInPostAdapter
                tagInPostAdapter.addTags(post.tags)
                if (ConstNavigation.currentFragment == ConstNavigation.PROFILE){
                    favoriteButton.visibility = View.GONE
                    dontShowButton.visibility = View.GONE
                }


            }
            Height.heightPostProfile += itemView.height
            itemView.setOnClickListener{
                listener.onClickPost(post)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(postList[position], listener)
    }
    fun addPosts(posts: ArrayList<Post>){
        postList.addAll(posts)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClickPost(post: Post)
    }



}