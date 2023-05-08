package com.example.investup.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.TagAdapter
import com.example.investup.adapter.TagInPostAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentEditPostBinding
import com.example.investup.databinding.FragmentEditProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ToastHelper
import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class EditPostFragment : Fragment(), TagAdapter.Listener {
    lateinit var binding: FragmentEditPostBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    lateinit var player: ExoPlayer
    var uri: Uri? = null
    var launcher: ActivityResultLauncher<Intent>? = null
    var havePermission: Boolean = false
    lateinit var post: Post
    private var allTags = ArrayList<Tag>()
    private var activeTags = ArrayList<Tag>()
    val tagAdapter = TagAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.apply {
            addDeleteVideoButton.setText(R.string.Delete_video)


            launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val res: Intent? = it.data
                        uri = res?.data!!
                        player = ExoPlayer.Builder(requireContext()).build()
                        videosView.visibility = View.VISIBLE
                        videosView.player = player
                        val mediaItem = MediaItem.fromUri(uri.toString());

                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()
                        addDeleteVideoButton.setText(R.string.Delete_video)
                    }
                }


            val loadPostJob = CoroutineScope(Dispatchers.IO)
            loadPostJob.launch {
                val responsePost =
                    ApiInstance.getApi().requestPostById(
                        dataModeLPost.id.value!!,
                        "Bearer " + dataModelToken.accessToken
                    )
                val response =
                    ApiInstance.getApi().requestTags("Bearer ${dataModelToken.accessToken.value}")
                allTags = response.body()!!
                post = responsePost.body()!!
                post.let {


                    requireActivity().runOnUiThread {

                        videosView.player = player

                        uri = Uri.parse(it.videoUrl)
                        val mediaItem = MediaItem.fromUri(uri!!);

                        player.setMediaItem(mediaItem)

                        player.prepare()

                        player.play()
                        titleInput.setText(it.title)
                        val formatedDate = it.createdAt.substringBefore("T")
                        shortDescriptionInput.setText(it.shortDescription)
                        fullDescriptionInput.setText(it.description)




                        println("url " + it.videoUrl)
                        tagsRcView.layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        activeTags.addAll(it.tags)
                        for (index in activeTags.indices) {
                            allTags[allTags.indexOf(activeTags[index])].isActive = true
                        }

                        tagsRcView.adapter = tagAdapter
                        tagAdapter.addTags(allTags)
                        println("tag count + = " + activeTags.size)

                    }
                }
            }
            addDeleteVideoButton.setOnClickListener {
                val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                    )
                } else {
                    havePermission = true

                }
                if (havePermission) {
                    if (uri == null) {
                        addDeleteVideoButton.setText(R.string.Delete_video)
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.type = "video/*"
                        launcher!!.launch(i)

                    } else {
                        addDeleteVideoButton.setText(R.string.Add_video)
                        uri = null
                        player.release()
                        videosView.visibility = View.GONE

                    }
                }
            }

            deletePostButton.setOnClickListener {
                val deletePostJob = CoroutineScope(Dispatchers.IO)
                deletePostJob.launch {
                    val response = ApiInstance.getApi()
                        .deletePostById(post.id, "Bearer ${dataModelToken.accessToken.value}")

                    requireActivity().runOnUiThread {
                        ToastHelper.toast(
                            requireActivity(),
                            R.string.Success_delete,
                            R.string.Unexpected_error,
                            response.message()
                        )
                        navigator().navToProfile()

                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = EditPostFragment()

    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)
        println("tag count + = " + activeTags.size)
    }
}