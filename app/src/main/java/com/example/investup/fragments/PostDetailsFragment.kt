package com.example.investup.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.PostAdapter
import com.example.investup.adapter.TagInPostAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentPostDetailsBinding
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.publicObject.ApiInstance
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class PostDetailsFragment : Fragment() {
    var isDragging: Boolean = false
    lateinit var binding: FragmentPostDetailsBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    lateinit var player: ExoPlayer



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }


    private fun init() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.apply {

            val loadPostJob = CoroutineScope(Dispatchers.IO)
            loadPostJob.launch {
                val responsePost =
                    ApiInstance.getApi().requestPostById(
                        dataModeLPost.id.value!!,
                        "Bearer " + dataModelToken.accessToken
                    )
                val body = responsePost.body()
                body?.let {





                    requireActivity().runOnUiThread {

                        videosView.player = player


                        val mediaItem = MediaItem.fromUri(Uri.parse(it.videoUrl));

                        player.setMediaItem(mediaItem)

                        player.prepare()

                        player.play()




                        Picasso.get().load(it.user.avatar).into(userProfileImageView)
                        nameSurnameLabel.text = ("${it.user.firstName} ${it.user.lastName}")
                        val formatedDate = it.createdAt.substringBefore("T")
                        dateLabel.text = formatedDate
                        titleLabel.text = it.title
                        shortDescriptionLabel.text = it.shortDescription
                        fullDescriptionLabel.text = it.description



                        println("url " + it.videoUrl)
                        tagsRecyclerView.layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        val tagInPostAdapter = TagInPostAdapter()
                        tagsRecyclerView.adapter = tagInPostAdapter
                        tagInPostAdapter.addTags(it.tags)
                    }
                }
            }

        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = PostDetailsFragment()
    }
}