package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.adapter.Height
import com.example.investup.adapter.PostAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.publicObject.ApiInstance
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.dataClass.Post
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment(), PostAdapter.Listener {
    lateinit var binding: FragmentProfileBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val postsAdapter = PostAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val jobInfoInit = CoroutineScope(Dispatchers.IO)
            jobInfoInit.launch {
                val response =
                    ApiInstance.getApi().requestInfoMe("Bearer ${dataModelToken.accessToken.value}")
                val message = response.body()
                val responsePosts = ApiInstance.getApi().requestMyPosts("Bearer ${dataModelToken.accessToken.value}")
                val bodyPosts = responsePosts.body()
                message?.apply {



                    activity?.runOnUiThread {
                        bodyPosts?.let {
                            Height.heightPostProfile = 0
                            myPostsRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            myPostsRecyclerView.adapter = postsAdapter
                            postsAdapter.addPosts(bodyPosts)
                            myPostsRecyclerView.setHasFixedSize(false)

                        }


                        nameSurNameText.text = ("${firstName} ${lastName}")
                        loginText.text = email
                        Picasso.get().load(avatar).into(imageView)
                        cardView2.setOnClickListener {
                            navigator().navToEditProfile()
                        }
                    }
                }
            }



            exitButton.setOnClickListener {
                dataModelToken.accessToken.value = "-1"
                navigator().goToLogin()
            }
            addPostButton.setOnClickListener{
                navigator().navToAddPost()
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

    override fun onClickAddToFavoriteButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickDeleteButton(post: Post) {
        TODO("Not yet implemented")
    }

    override fun onClickEditButton(post: Post) {
        dataModeLPost.id.value = post.id
        navigator().navToEditPost()
    }
}