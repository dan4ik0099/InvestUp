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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
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
import com.example.investup.retrofit.dataClass.Tag
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddPostFragment : Fragment(), TagAdapter.Listener {
    lateinit var binding: FragmentAddPostBinding
    private val dataModelAddPost: DataModelAddPost by activityViewModels()
    private val dataModelToken: DataModelToken by activityViewModels()
    var launcher: ActivityResultLauncher<Intent>? = null
    private var havePermission = false
    private var allTags = ArrayList<Tag>()
    private var activeTags = ArrayList<Tag>()

    private var isDragging = false
    private val adapter = TagAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {


            tagsRcView.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            tagsRcView.adapter = adapter


            val loadTags = CoroutineScope(Dispatchers.IO)
            loadTags.launch {
                val response =
                    ApiInstance.getApi().requestTags("Bearer ${dataModelToken.accessToken.value}")
                allTags = response.body()!!



                println("cuja " + response.message())
                allTags?.let {
                    requireActivity().runOnUiThread {
                        adapter.addTags(it)
                    }
                }

            }




            titleInput.setText(dataModelAddPost.title.value)
            shortDescriptionInput.setText(dataModelAddPost.shortDescription.value)
            fullDescriptionInput.setText(dataModelAddPost.fullDescription.value)
            if (dataModelAddPost.video.value != null) {
                println("videos " + dataModelAddPost.video.value.toString())
                videoView.setVideoURI(dataModelAddPost.video.value)
                videoView.seekTo(10)
                addDeleteVideoButton.setText(R.string.Delete_video)
                relativeLayout.visibility = View.VISIBLE
            } else {
                addDeleteVideoButton.setText(R.string.Add_video)
                relativeLayout.visibility = View.GONE
            }

            launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val res: Intent? = it.data
                        dataModelAddPost.video.value = res?.data
                        videoView.setVideoURI(res?.data)
                        videoView.start()
                        playButton.visibility = View.INVISIBLE
                        relativeLayout.visibility = View.VISIBLE
                        addDeleteVideoButton.setText(R.string.Delete_video)
                    }
                }



            videoView.setOnClickListener {
                println(videoView.isPlaying)
                if (videoView.isPlaying) {
                    playButton.visibility = View.VISIBLE

                    videoView.pause()
                } else {
                    videoView.start()
                    playButton.visibility = View.INVISIBLE
                }
            }


            videoView.setOnPreparedListener { mediaPlayer ->

                val duration = mediaPlayer.duration
                seekBar.max = duration


            }

            videoView.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    if (!isDragging) {

                        seekBar.post(object : Runnable {
                            override fun run() {
                                val currentPosition = videoView.currentPosition
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
                        videoView.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    isDragging = true
                    videoView.pause()
                    playButton.visibility = View.VISIBLE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    isDragging = false
                    println("gg " + seekBar.progress)
                    videoView.start()
                    playButton.visibility = View.INVISIBLE
                }
            })




            titleInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    dataModelAddPost.title.value = titleInput.text.toString()
                }


            })

            shortDescriptionInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    dataModelAddPost.shortDescription.value = shortDescriptionInput.text.toString()
                }


            })
            fullDescriptionInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    dataModelAddPost.fullDescription.value = fullDescriptionInput.text.toString()
                }


            })


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
                    if (dataModelAddPost.video.value == null) {
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.type = "video/*"
                        launcher!!.launch(i)
                    } else {
                        addDeleteVideoButton.setText(R.string.Add_video)
                        dataModelAddPost.video.value = null
                        videoView.stopPlayback()

                        videoView.setVideoURI(null)
                        relativeLayout.visibility = View.GONE


                    }

                }

            }

            uploadPostButton.setOnClickListener {
                if (true) {


                    val title =
                        dataModelAddPost.title.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                    val description =
                        dataModelAddPost.fullDescription.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                    val shortDescription =
                        dataModelAddPost.shortDescription.value?.toRequestBody("text/plain".toMediaTypeOrNull())

                    val text = ArrayList<String>()
                    text.add("6442dbdca63dcfbd0f9e644a")

                    var helper = UriHelper.URIPathHelper()

                    val videoFile = File(
                        helper.getPath(requireContext(), dataModelAddPost.video.value!!)
                    )


                    val tags = ArrayList<String>()

                    activeTags.mapTo(tags){
                        it.id
                    }
                    val json = Gson().toJson(tags)
                    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())


                    val videoRequestBody =
                        videoFile?.asRequestBody("video/mp4".toMediaTypeOrNull())
                    val video = MultipartBody.Part.createFormData(
                        "video",
                        videoFile.name,
                        videoRequestBody!!
                    )
                    println(videoFile.name)

                    val uploadPostJob = CoroutineScope(Dispatchers.IO)
                    uploadPostJob.launch {
                        val response = ApiInstance.getApi().uploadPost(
                            "Bearer ${dataModelToken.accessToken.value}",
                            title!!,
                            description!!,
                            shortDescription!!,
                            requestBody,
                            video
                        )
                        println("resp " + response)
                        val body = response.body()
                        body?.let {
                            println("gg " + response.message())
                        }
                    }


                }
                navigator().navToProfile()
            }

        }

    }

    fun buildMultipart(
        uri: Uri,
        context: Context,
        type: String,
        name: String
    ): MultipartBody.Part {

        val helper = UriHelper.URIPathHelper()
        val file = File(helper.getPath(requireContext(), uri)!!)
        val requestFile = file.asRequestBody(type.toMediaTypeOrNull())
        val filePart =
            MultipartBody.Part.createFormData("photo", file.name, requestFile)
        return filePart
    }

    companion object {

        @JvmStatic
        fun newInstance() = AddPostFragment()
    }

    override fun onClickTag(tag: Tag) {
        if (activeTags.contains(tag)) activeTags.remove(tag)
        else activeTags.add(tag)

    }
}