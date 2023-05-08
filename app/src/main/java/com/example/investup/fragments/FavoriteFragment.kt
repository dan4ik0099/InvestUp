package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.PostAdapter
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentFavoriteBinding

import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ToastHelper
import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), PostAdapter.Listener, TagAdapter.Listener {


    lateinit var binding: FragmentFavoriteBinding
    private val tagAdapter = TagAdapter(this)
    private val postAdapter = PostAdapter(this)
    private var allTags = ArrayList<Tag>()
    private val activeTags = ArrayList<Tag>()

    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val dataModelToken: DataModelToken by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.postRecyclerView.removeAllViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter

            val loadTags = CoroutineScope(Dispatchers.IO)
            loadTags.launch {
                val response =
                    ApiInstance.getApi().requestTags("Bearer ${dataModelToken.accessToken.value}")
                allTags = response.body()!!
                allTags.let {
                    requireActivity().runOnUiThread {
                        tagAdapter.addTags(it)
                    }
                }

            }
            postRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            postRecyclerView.adapter = postAdapter


            val loadPostsJob = CoroutineScope(Dispatchers.IO)
            loadPostsJob.launch {
                val response = ApiInstance.getApi()
                    .requestFavoritePosts("Bearer ${dataModelToken.accessToken.value}")
                val body = response.body()
                body?.let {
                    println("whayt " + body.size)
                    requireActivity().runOnUiThread {

                        postRecyclerView.setHasFixedSize(false)
                        postAdapter.addPosts(it)
                    }
                }
            }

            hideSearchButton.setOnClickListener {
                if (tagsRecyclerView.visibility == View.GONE) {
                    hideSearchImage.setImageResource(R.drawable.baseline_remove_red_eye_24)
                    spinner.visibility = View.VISIBLE
                    tagsRecyclerView.visibility = View.VISIBLE
                } else {
                    hideSearchImage.setImageResource(R.drawable.baseline_visibility_off_24_unchecked)
                    spinner.visibility = View.GONE
                    tagsRecyclerView.visibility = View.GONE
                }
            }


        }
    }

    private fun init() {

    }

    companion object {

        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }

    override fun onClickPost(post: Post) {
        dataModeLPost.id.value = post.id
        navigator().navToPostDetails()
    }

    override fun onClickDontShowButton(post: Post) {
        TODO("Not yet implemented")
    }


    override fun onClickAddToFavoriteButton(post: Post) {
        binding.let {
            val addToFavoriteJob = CoroutineScope(Dispatchers.IO)
            addToFavoriteJob.launch {
                val response = ApiInstance.getApi()
                    .addToFavoritePostById(post.id, "Bearer ${dataModelToken.accessToken.value}")
                response.let {
                    requireActivity().runOnUiThread {
                        ToastHelper.toast(
                            requireActivity(),
                            R.string.Toast_success_add_to_favorite,
                            R.string.Unexpected_error,
                            response.message()
                        )

                    }
                }
            }
        }
    }

    override fun onClickDeleteButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickEditButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)

    }
}