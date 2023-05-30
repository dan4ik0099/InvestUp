package com.example.investup.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentEditPostBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.retrofit.dataClass.Post
import com.example.investup.retrofit.dataClass.Tag
import com.example.investup.retrofit.requestModel.PostEditRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.*
import org.json.JSONObject


class EditPostFragment : Fragment(), TagAdapter.Listener {
    lateinit var binding: FragmentEditPostBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    lateinit var player: ExoPlayer
    var uri: Uri? = null
    private lateinit var coroutine: CoroutineScope
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
        coroutine = CoroutineScope(Dispatchers.IO)
        player = ExoPlayer.Builder(requireContext()).build()
        binding.apply {


            coroutine.launch {
                val responsePost =
                    ApiInstance.getApi().requestPostById(
                        dataModeLPost.id.value!!,
                        dataModelToken.accessToken.value!!
                    )
                val response =
                    ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                if (responsePost.code() == 200 && response.code() == 200) {
                    if (response.body() != null) allTags = response.body()!!
                    if (responsePost.body() != null) post = responsePost.body()!!

                    post.let {
                        withContext(Dispatchers.Main) {
                            videosView.player = player
                            uri = Uri.parse(it.videoUrl)
                            val mediaItem = MediaItem.fromUri(uri!!);
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            titleInput.setText(it.title)
                            val formatedDate = it.createdAt.substringBefore("T")
                            shortDescriptionInput.setText(it.shortDescription)
                            fullDescriptionInput.setText(it.description)
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
            }

            saveChangeButton.setOnClickListener {
                if (
                    titleInput.text.isNotEmpty()
                    && shortDescriptionInput.text.isNotEmpty()
                    && fullDescriptionInput.text.isNotEmpty()
                    && activeTags.isNotEmpty()
                    && activeTags.size < 4

                ) {
                    val idTagList = ArrayList<String>()
                    activeTags.mapTo(idTagList)
                    {
                        it.id
                    }

                    coroutine.launch {

                        val response = ApiInstance.getApi().changePostById(
                            post.id, PostEditRequest(
                                titleInput.text.toString(),
                                shortDescriptionInput.text.toString(),
                                fullDescriptionInput.text.toString(),
                                idTagList
                            ),
                            dataModelToken.accessToken.value!!
                        )
                        withContext(Dispatchers.Main){
                            if (response.code() == 200){
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.Post_success_edit),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigator().navBack()
                            }
                        }
                    }
                }
            }




            deletePostButton.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setMessage(getString(R.string.Are_you_sure_about_delete_post))
                alertDialogBuilder.setPositiveButton(getString(R.string.Yes)) { dialog, which ->
                    coroutine.launch {
                        val response = ApiInstance.getApi()
                            .deletePostById(post.id, dataModelToken.accessToken.value!!)

                        if (response.code() == 200) {

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.Success_delete), Toast.LENGTH_SHORT
                                ).show()

                                navigator().navToProfile()
                            }
                        }
                    }


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
        fun newInstance() = EditPostFragment()

    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)
        println("tag count + = " + activeTags.size)
    }
}