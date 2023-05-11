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

    private lateinit var myId:String

    class PostHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = PostItemBinding.bind(item)
        fun bind(post: Post, myId: String, listener : Listener) = with(binding) {
            post.apply {

                Picasso.get().load(user.avatar).into(userProfileImageView)
                nameSurnameLabel.text = ("${user.firstName} ${user.lastName}")
                val formatedDate = createdAt.substringBefore("T")
                dateLabel.text = formatedDate
                titleLabel.text = title
                viewCountLabel.text = views.toString()
                commentCountLabel.text = commentsCount.toString()
                favoriteCountLabel.text = favoriteCount.toString()

                shortDescriptionLabel.text = shortDescription
                tagsRecyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                val tagInPostAdapter = TagInPostAdapter()
                tagsRecyclerView.adapter = tagInPostAdapter
                tagInPostAdapter.addTags(post.tags)
                if (isFavorite) favoriteButton.setText(R.string.Delete_from_favorite)

                if (user.id == myId){
                    editPostButton.setOnClickListener {
                        listener.onClickEditButton(post)
                    }

                    favoriteButton.visibility = View.GONE

                    editPostButton.visibility = View.VISIBLE
                }else{

                    favoriteButton.setOnClickListener {

                        if(favoriteButton.text  == itemView.resources.getString(R.string.Add_to_favorite)) {
                            favoriteButton.setText(R.string.Delete_from_favorite)
                            listener.onClickAddToFavoriteButton(post, true)
                        }else {
                            favoriteButton.setText(R.string.Add_to_favorite)
                            listener.onClickAddToFavoriteButton(post, false)
                        }


                    }



                    favoriteButton.visibility = View.VISIBLE


                    editPostButton.visibility = View.GONE

                }


            }

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
        holder.bind(postList[position], myId, listener)
    }
    fun addPosts(posts: ArrayList<Post>, myId: String){
        postList.clear()
        this.myId = myId
        postList.addAll(posts)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onClickPost(post: Post)
        fun onClickDontShowButton(post: Post)
        fun onClickAddToFavoriteButton(post: Post, flag: Boolean)
        fun onClickDeleteButton(post: Post)
        fun onClickEditButton(post: Post)
    }



}