package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.investup.adapter.PostAdapter
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.publicObject.ApiInstance
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment(), PostAdapter.Listener,TagAdapter.Listener {
    lateinit var binding: FragmentProfileBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val postsAdapter = PostAdapter(this)
    private val tagAdapter = TagAdapter(this)
    private var allTags = ArrayList<Tag>()
    private val activeTags = ArrayList<Tag>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onResume() {
        super.onResume()
        binding.tipsRecyclerView.removeAllViews()
        binding.myPostsRecyclerView.removeAllViews()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    fun init() {

        binding.apply {
            val jobInfoInit = CoroutineScope(Dispatchers.IO)
            jobInfoInit.launch {
                val response =
                    ApiInstance.getApi().requestInfoMe(dataModelToken.accessToken.value!!)
                val message = response.body()

                message?.apply {


                   withContext(Dispatchers.Main) {


                        nameSurNameText.text = ("${firstName} ${lastName}")
                        loginText.text = email
                        Picasso.get().load(avatar).into(imageView)
                        cardView2.setOnClickListener {
                            navigator().navToEditProfile()

                        }
                    }
                }
            }


            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter

            val loadTags = CoroutineScope(Dispatchers.IO)
            loadTags.launch {

                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                response.body()?.let {
                    allTags = response.body()!!
                }
                allTags.let {
                   withContext(Dispatchers.Main) {
                        tagAdapter.addTags(it)
                    }
                }



            }


            myPostsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            myPostsRecyclerView.adapter = postsAdapter
            val jobPostInit = CoroutineScope(Dispatchers.IO)
            jobPostInit.launch {
                val responsePosts = ApiInstance.getApi()
                    .requestMyPosts(dataModelToken.accessToken.value!!)
                val bodyPosts = responsePosts.body()
                bodyPosts?.let {
                   withContext(Dispatchers.Main) {

                        postsAdapter.addPosts(it, dataModelToken.myId.value!!)
                        myPostsRecyclerView.setHasFixedSize(false)

                    }
                }

            }
            addPostButton.setOnClickListener {
                navigator().navToAddPost()
            }
            exitButton.setOnClickListener {
                dataModelToken.accessToken.value = "-1"
                navigator().goToLogin()
            }


        }

    }


    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()

    }

    override fun onClickPost(post: Post) {


        dataModeLPost.id.value = post.id
        navigator().navToPostDetails()
    }

    override fun onClickDontShowButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickAddToFavoriteButton(post: Post, flag: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onClickDeleteButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickEditButton(post: Post) {
        dataModeLPost.id.value = post.id
        navigator().navToEditPost()
    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)

    }
}