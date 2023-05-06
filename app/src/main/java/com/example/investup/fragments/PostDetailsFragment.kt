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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostDetailsFragment : Fragment() {
    var isDragging: Boolean = false
    lateinit var binding: FragmentPostDetailsBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()


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


    private fun init() {
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


                        Picasso.get().load(it.user.avatar).into(userProfileImageView)
                        nameSurnameLabel.text = ("${it.user.firstName} ${it.user.lastName}")
                        val formatedDate = it.createdAt.substringBefore("T")
                        dateLabel.text = formatedDate
                        titleLabel.text = it.title
                        shortDescriptionLabel.text = it.shortDescription
                        fullDescriptionLabel.text = it.description
                        val uri = Uri.parse(it.videoUrl)
                        videosView.setVideoURI(uri)
                        videosView.start()
                        videosView.seekTo(10)
                        videosView.pause()
                        videosView.setOnClickListener {
                            println(videosView.isPlaying)
                            if (videosView.isPlaying) {
                                playButton.visibility = View.VISIBLE

                                videosView.pause()
                            } else {
                                videosView.start()
                                playButton.visibility = View.INVISIBLE
                            }
                        }
                        videosView.setOnPreparedListener { mediaPlayer ->

                            val duration = mediaPlayer.duration
                            seekBar.max = duration


                        }

                        videosView.setOnInfoListener { _, what, _ ->
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                if (!isDragging) {

                                    seekBar.post(object : Runnable {
                                        override fun run() {
                                            val currentPosition = videosView.currentPosition
                                            seekBar.progress = currentPosition
                                            seekBar.postDelayed(this, 100)
                                        }
                                    })
                                }
                            }
                            true
                        }

                        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                                if (fromUser) {
                                    println("прогресс " + progress)
                                    videosView.seekTo(progress)
                                }
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar) {
                                isDragging = true
                                videosView.pause()
                                playButton.visibility = View.VISIBLE
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar) {
                                isDragging = false
                                println("gg " + seekBar.progress)
                                videosView.start()
                                playButton.visibility = View.INVISIBLE
                            }
                        })



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