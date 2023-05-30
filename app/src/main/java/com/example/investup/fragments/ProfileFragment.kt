package com.example.investup.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R

import com.example.investup.adapter.PostAdapter
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelSearch
import com.example.investup.publicObject.ApiInstance
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.SocketSingleton
import com.example.investup.publicObject.SortingObject
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
    private val dataModelSearch: DataModelSearch by activityViewModels()
    private var isFirstTime = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }
    override fun onStop() {
        binding.apply {
            dataModelSearch.searchProfile.value = searchView.query.toString()
            dataModelSearch.tagProfile.value = ArrayList(allTags.filter { it.isActive })
            dataModelSearch.sortProfile.value = spinner.selectedItemPosition


        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()

        binding.myPostsRecyclerView.removeAllViews()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        coroutine = CoroutineScope(Dispatchers.IO)
        val sortList = listOf(
            getString(R.string.Sort_date_new),
            getString(R.string.Sort_date_old),
            getString(R.string.Sort_view_max),
            getString(R.string.Sort_view_min)
        )
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






            emptyLabel.visibility = View.GONE
            myPostsRecyclerView.visibility = View.VISIBLE


            myPostsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            myPostsRecyclerView.adapter = postAdapter
            myPostsRecyclerView.setHasFixedSize(false)


            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter

            val adapterSortedList = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sortList
            )

            searchView.setQuery(dataModelSearch.searchProfile.value, false)


            spinner.adapter = adapterSortedList


            coroutine.launch {

                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                if (response.code() == 200) {


                    val filteredTags = ArrayList<String>()
                    dataModelSearch.tagProfile.value!!.filter { it.isActive }.mapTo(filteredTags){
                        it.id
                    }

                    allTags = response.body()!!

                    filteredTags.forEach{ tag->
                        allTags.find { tag == it.id}!!.isActive = true
                    }
                    println(111111111)
                    println(allTags.size)


                    withContext(Dispatchers.Main) {
                        tagAdapter.addTags(allTags)
                        search()

                    }
                }
            }




            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                    if (isFirstTime) isFirstTime = false
                    else search()

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Обработка отсутствия выбранного элемента

                }
            }

            spinner.setSelection(dataModelSearch.sortProfile.value!!)



            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    search()
                    return true
                }


                override fun onQueryTextChange(newText: String?): Boolean {
                    if (searchView.query.isEmpty()) {search()

                    }
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
                    coroutine.launch {

                        val response = ApiInstance.getApi().session(dataModelToken.accessToken.value!!)
                        SocketSingleton.closeConnection()

                    }

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
            var searchTags: ArrayList<String>?

            searchTags = ArrayList()

            allTags.filter { it.isActive }.mapTo(searchTags) {
                it.id
            }

            if (searchTags.isEmpty()) searchTags = null

            search = if (searchView.query == "") null
            else searchView.query.toString()
            val sort: String
            val sortValue: String
            when (spinner.selectedItem.toString()) {
                getString(R.string.Sort_date_new) -> {
                    sortValue = SortingObject.SortValue.DESC.s
                    sort = SortingObject.PostsSort.CREATED_AT.s

                }
                getString(R.string.Sort_date_old) -> {
                    sortValue = SortingObject.SortValue.ASC.s
                    sort = SortingObject.PostsSort.CREATED_AT.s

                }
                getString(R.string.Sort_view_max) -> {

                    sortValue = SortingObject.SortValue.DESC.s
                    sort = SortingObject.PostsSort.VIEWS.s
                }
                getString(R.string.Sort_view_min) -> {

                    sortValue = SortingObject.SortValue.ASC.s
                    sort = SortingObject.PostsSort.VIEWS.s
                }
                else -> {
                    sort = ""
                    sortValue = ""
                }
            }

            coroutine.launch {
                println()
                val searchResponse = ApiInstance.getApi().requestMyPostsBySearch(
                    search,
                    searchTags,
                    sort,
                    sortValue,
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
        search()
    }
}