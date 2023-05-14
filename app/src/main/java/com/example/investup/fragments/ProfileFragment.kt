package com.example.investup.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R

import com.example.investup.adapter.PostAdapter
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.publicObject.ApiInstance
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class ProfileFragment : Fragment(), PostAdapter.Listener, TagAdapter.Listener {
    lateinit var binding: FragmentProfileBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val dataModelUser: DataModelUser by activityViewModels()
    private val postAdapter = PostAdapter(this)
    private val tagAdapter = TagAdapter(this)
    private var allTags = ArrayList<Tag>()
    lateinit var coroutine: CoroutineScope
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

    private fun init() {
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {

            coroutine.launch {
                val response =
                    ApiInstance.getApi().requestInfoMe(dataModelToken.accessToken.value!!)
                withContext(Dispatchers.Main) {

                    if (response.code() == 200) {
                        response.body()?.apply {


                            nameSurNameText.text = ("${firstName} ${lastName}")
                            loginText.text = email
                            Picasso.get().load(avatar).into(imageView)
                            cardView2.setOnClickListener {
                                navigator().navToEditProfile()

                            }
                        }

                    } else {
                        Toast.makeText(
                            requireContext(), JSONObject(
                                response.errorBody()?.string()
                            ).getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter


            coroutine.launch {

                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {

                        allTags = response.body()!!


                        tagAdapter.addTags(allTags)

                    } else {
                        Toast.makeText(
                            requireContext(), JSONObject(
                                response.errorBody()?.string()
                            ).getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }


            myPostsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            myPostsRecyclerView.adapter = postAdapter

            coroutine.launch {
                val responsePosts = ApiInstance.getApi()
                    .requestMyPosts(dataModelToken.accessToken.value!!)


                withContext(Dispatchers.Main) {
                    if (responsePosts.code() == 200) {
                        postAdapter.addPosts(
                            responsePosts.body()!!,
                            dataModelToken.myId.value!!
                        )
                        myPostsRecyclerView.setHasFixedSize(false)

                    } else {
                        Toast.makeText(
                            requireContext(), JSONObject(
                                responsePosts.errorBody()?.string()
                            ).getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    search()
                    return true
                }


                override fun onQueryTextChange(newText: String?): Boolean {
                    if (searchView.query.isEmpty()) search()
                    return true
                }

            })

            addPostButton.setOnClickListener {
                navigator().navToAddPost()
            }

            exitButton.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setMessage(getString(R.string.Are_you_sure_about_exit))
                alertDialogBuilder.setPositiveButton(getString(R.string.Yes)) { dialog, which ->
                    dataModelToken.accessToken.value = "-1"
                    navigator().goToLogin()
                }
                alertDialogBuilder.setNegativeButton(getString(R.string.Cancel)) { dialog, which ->
                    // Код отмены действия
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
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

    }

    override fun onClickAddToFavoriteButton(post: Post, flag: Boolean) {

    }

    override fun onClickDeleteButton(post: Post) {

    }

    override fun onClickEditButton(post: Post) {
        dataModeLPost.id.value = post.id
        navigator().navToEditPost()
    }

    override fun onClickProfileButton(id: String) {

    }

    private fun search() {
        var postList: ArrayList<Post>? = null
        binding.apply {

            val search: String?
            val searchTags: ArrayList<String>?
            if (activeTags.isNotEmpty()) {
                searchTags = ArrayList()
                activeTags.mapTo(searchTags) {
                    it.id

                }
            } else searchTags = null
            if (searchView.query == "") search = null
            else search = searchView.query.toString()


            coroutine.launch {

                val searchResponse = ApiInstance.getApi().requestMyPostsBySearch(
                    search,
                    searchTags,
                    dataModelToken.accessToken.value!!
                )

                if (searchResponse.code() == 200) {

                    postList = searchResponse.body()
                    withContext(Dispatchers.Main) {
                        if (postList!!.size > 0) {

                            myPostsRecyclerView.visibility = View.VISIBLE
                            postAdapter.addPosts(postList!!, dataModelToken.myId.value!!)
                            emptyLabel.visibility = View.GONE
                        } else {
                            myPostsRecyclerView.visibility = View.GONE
                            emptyLabel.visibility = View.VISIBLE
                        }
                    }


                }
            }
        }


    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)
        search()
    }
}