package com.example.investup.fragments

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
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentFavoriteBinding

import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.SortingObject
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
    lateinit var coroutine: CoroutineScope
    private val dataModeLUser: DataModelUser by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModelSearch: DataModelSearch by activityViewModels()
    private var isFirstTime = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onStop() {
        binding.apply {
            dataModelSearch.searchFavorite.value = searchView.query.toString()
            dataModelSearch.tagFavorite.value = ArrayList(allTags.filter { it.isActive })
            dataModelSearch.sortFavorite.value = spinner.selectedItemPosition


        }
        super.onStop()
    }
    override fun onResume() {
        super.onResume()
        binding.postRecyclerView.removeAllViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        val sortList = listOf(
            getString(R.string.Sort_date_new),
            getString(R.string.Sort_date_old),
            getString(R.string.Sort_view_max),
            getString(R.string.Sort_view_min)
        )
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {
            emptyLabel.visibility = View.GONE
            postRecyclerView.visibility = View.VISIBLE


            postRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            postRecyclerView.adapter = postAdapter
            postRecyclerView.setHasFixedSize(false)


            tagsRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagsRecyclerView.adapter = tagAdapter

            val adapterSortedList = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sortList
            )

            searchView.setQuery(dataModelSearch.searchFavorite.value, false)


            spinner.adapter = adapterSortedList


            coroutine.launch {

                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                if (response.code() == 200) {


                    val filteredTags = ArrayList<String>()
                    dataModelSearch.tagFavorite.value!!.filter { it.isActive }.mapTo(filteredTags){
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

            spinner.setSelection(dataModelSearch.sortFavorite.value!!)



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

        }
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
                val searchResponse = ApiInstance.getApi().requestFavoritePostsBySearch(
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
                            postRecyclerView.visibility = View.VISIBLE
                            postAdapter.addPosts(postList!!, dataModelToken.myId.value!!)
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

    override fun onClickProfileButton(id: String) {

        dataModeLUser.id.value = id
        navigator().navToUserProfile()

    }


    override fun onClickTag(tag: Tag) {

        search()
    }
}