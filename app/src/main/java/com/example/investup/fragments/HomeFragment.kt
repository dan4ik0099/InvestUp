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
import com.example.investup.R
import com.example.investup.adapter.PostAdapter
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentHomeBinding
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.publicObject.ApiInstance
import com.example.investup.retrofit.dataClass.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), PostAdapter.Listener {


    lateinit var binding: FragmentHomeBinding
    val postAdapter = PostAdapter(this)
    private val dataModelToken: DataModelToken by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            postRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            postRecyclerView.adapter = postAdapter


           val loadPostsJob = CoroutineScope(Dispatchers.IO)
           loadPostsJob.launch {
               val response = ApiInstance.getApi().requestAllPosts("Bearer ${dataModelToken.accessToken.value}")
               val body = response.body()
               body?.let{
                   requireActivity().runOnUiThread{
                       postAdapter.addPosts(it)
                   }
               }
           }




        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onClickPost(post: Post) {
        TODO("Not yet implemented")
    }
}