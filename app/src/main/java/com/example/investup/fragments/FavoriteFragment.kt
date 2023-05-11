package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.coroutines.withContext
import org.json.JSONObject


class FavoriteFragment : Fragment(), PostAdapter.Listener, TagAdapter.Listener {


    lateinit var binding: FragmentFavoriteBinding
    private val tagAdapter = TagAdapter(this)
    private val postAdapter = PostAdapter(this)
    private var allTags = ArrayList<Tag>()
    private val activeTags = ArrayList<Tag>()
    lateinit var coroutine: CoroutineScope
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
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {
            emptyLabel.visibility = View.GONE
            postRecyclerView.visibility = View.VISIBLE
            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter
            coroutine.launch {
                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                if (response.code() == 200) {
                    allTags = response.body()!!
                    withContext(Dispatchers.Main) {
                        tagAdapter.addTags(allTags)
                    }
                }


            }



            postRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            postRecyclerView.adapter = postAdapter
            coroutine.launch {
                val response = ApiInstance.getApi()
                    .requestFavoritePosts(dataModelToken.accessToken.value!!)
                if (response.code() == 200) {
                    withContext(Dispatchers.Main) {
                        if (response.body()!!.size > 0) {
                            postRecyclerView.setHasFixedSize(false)
                            postAdapter.addPosts(response.body()!!, dataModelToken.myId.value!!)
                            emptyLabel.visibility = View.GONE
                        } else {
                            postRecyclerView.visibility = View.GONE
                            emptyLabel.visibility = View.VISIBLE
                        }
                    }
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


    override fun onClickAddToFavoriteButton(post: Post, flag: Boolean) {
        binding.let {

            coroutine.launch {
                var message: String = getString(R.string.Unexpected_error)
                if (flag) {
                    val response = ApiInstance.getApi()
                        .addToFavoritePostById(
                            post.id,
                            dataModelToken.accessToken.value!!
                        )
                    if (response.code() == 200)
                        message = JSONObject(response.body()!!.string()).getString("message")
                } else {
                    val response = ApiInstance.getApi()
                        .deleteFromFavoritePostById(
                            post.id,
                            dataModelToken.accessToken.value!!
                        )
                    if (response.code() == 200)
                        message = JSONObject(response.body()!!.string()).getString("message")

                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

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