package com.example.investup.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.investup.R
import com.example.investup.UriHelper
import com.example.investup.adapter.TagAdapter
import com.example.investup.dataModels.DataModelAddPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentAddPostBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ToastHelper
import com.example.investup.retrofit.dataClass.Tag
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File


class AddPostFragment : Fragment(), TagAdapter.Listener {
    lateinit var binding: FragmentAddPostBinding
    private val dataModelAddPost: DataModelAddPost by activityViewModels()
    private val dataModelToken: DataModelToken by activityViewModels()

    lateinit var player: ExoPlayer
    val tags = ArrayList<String>()
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var havePermission = false
    private var allTags = ArrayList<Tag>()
    private var activeTags = ArrayList<Tag>()
    private lateinit var coroutine: CoroutineScope

    private val adapter = TagAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    private fun init() {
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {


            tagsRcView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            tagsRcView.adapter = adapter



            coroutine.launch {
                val response = ApiInstance.getApi().requestTags(dataModelToken.accessToken.value!!)
                if (response.code() == 200) {
                    allTags = response.body()!!
                } else {
                    val msg =
                        response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }

                withContext(Dispatchers.Main) {
                    adapter.addTags(allTags)

                }

            }

            titleInput.setText(dataModelAddPost.title.value)
            shortDescriptionInput.setText(dataModelAddPost.shortDescription.value)
            fullDescriptionInput.setText(dataModelAddPost.fullDescription.value)
            if (dataModelAddPost.video.value != null) {
                player = ExoPlayer.Builder(requireContext()).build()
                println("videos " + dataModelAddPost.video.value.toString())
                videosView.visibility = View.VISIBLE
                videosView.player = player
                val mediaItem = MediaItem.fromUri(dataModelAddPost.video.value.toString());
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                addDeleteVideoButton.setText(R.string.Delete_video)
            } else {
                videosView.visibility = View.GONE
                addDeleteVideoButton.setText(R.string.Add_video)
            }

            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val res: Intent? = it.data
                    dataModelAddPost.video.value = res?.data
                    player = ExoPlayer.Builder(requireContext()).build()
                    videosView.visibility = View.VISIBLE
                    videosView.player = player
                    val mediaItem = MediaItem.fromUri(res?.data.toString());

                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    addDeleteVideoButton.setText(R.string.Delete_video)
                }
            }

            titleInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
                ) {
                    dataModelAddPost.title.value = titleInput.text.toString()
                }


            })
            shortDescriptionInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
                ) {
                    dataModelAddPost.shortDescription.value = shortDescriptionInput.text.toString()
                }


            })
            fullDescriptionInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int
                ) {
                    dataModelAddPost.fullDescription.value = fullDescriptionInput.text.toString()
                }


            })


            addDeleteVideoButton.setOnClickListener {
                val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1

                if (ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                    if (dataModelAddPost.video.value == null) {
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.type = "video/*"
                        launcher!!.launch(i)
                    } else {
                        addDeleteVideoButton.setText(R.string.Add_video)
                        dataModelAddPost.video.value = null
                        player.release()
                        videosView.visibility = View.GONE

                    }
                }
            }

            uploadPostButton.setOnClickListener {
                if (dataModelAddPost.video.value != null
                    && dataModelAddPost.title.value.toString().isNotEmpty()
                    && dataModelAddPost.shortDescription.value.toString().isNotEmpty()
                    && dataModelAddPost.fullDescription.value.toString().isNotEmpty()
                    && activeTags.isNotEmpty()
                ) {


                    val title =
                        dataModelAddPost.title.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                    val description =
                        dataModelAddPost.fullDescription.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                    val shortDescription =
                        dataModelAddPost.shortDescription.value?.toRequestBody("text/plain".toMediaTypeOrNull())


                    val helper = UriHelper.URIPathHelper()

                    val videoFile = File(
                        helper.getPath(requireContext(), dataModelAddPost.video.value!!)!!
                    )



                    activeTags.mapTo(tags) {
                        it.id
                    }
                    val json = Gson().toJson(tags)
                    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())


                    val videoRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                    val video = MultipartBody.Part.createFormData(
                        "video", videoFile.name, videoRequestBody
                    )



                    coroutine.launch {

                        val response = ApiInstance.getApi().uploadPost(
                            dataModelToken.accessToken.value!!,
                            title!!,
                            description!!,
                            shortDescription!!,
                            requestBody,
                            video
                        )

                            withContext(Dispatchers.Main) {


                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.Post_upload_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                dataModelAddPost.video.value = null
                                dataModelAddPost.title.value = null
                                dataModelAddPost.shortDescription.value = null
                                dataModelAddPost.fullDescription.value = null
                                activeTags.clear()
                                tags.clear()
                                navigator().navToProfile()


                        }

                    }
                }

            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = AddPostFragment()
    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)
        println("suka" + tag.isActive)


    }
}