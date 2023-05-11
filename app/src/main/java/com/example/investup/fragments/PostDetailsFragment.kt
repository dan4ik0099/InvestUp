package com.example.investup.fragments

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.CommentAdapter
import com.example.investup.adapter.TagInPostAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentPostDetailsBinding
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ToastHelper
import com.example.investup.retrofit.dataClass.Comment
import com.example.investup.retrofit.requestModel.UploadCommentRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostDetailsFragment : Fragment(), CommentAdapter.Listener {
    lateinit var binding: FragmentPostDetailsBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    lateinit var player: ExoPlayer

    var error = ""
    val commentAdapter = CommentAdapter(this)

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

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
                        dataModelToken.accessToken.value!!
                    )
                val body = responsePost.body()
                body?.let {

                   withContext(Dispatchers.Main) {

                        videosView.player = player


                        val mediaItem = MediaItem.fromUri(Uri.parse(it.videoUrl));

                        player.setMediaItem(mediaItem)

                        player.prepare()


                        Picasso.get().load(it.user.avatar).into(userProfileImageView)
                        nameSurnameLabel.text = ("${it.user.firstName} ${it.user.lastName}")
                        val formatedDate = it.createdAt.substringBefore("T")
                        dateLabel.text = formatedDate
                        titleLabel.text = it.title
                        shortDescriptionLabel.text = it.shortDescription
                        fullDescriptionLabel.text = it.description
                        viewCountLabel.text = it.views.toString()
                        commentCountLabel.text = it.commentsCount.toString()
                        favoriteCountLabel.text = it.favoriteCount.toString()
                        println(it.id + " +=+ " + dataModelToken.myId.value)
                        println(it.isFavorite)
                        if (it.user.id == dataModelToken.myId.value) {
                            favoriteButton.visibility = View.GONE
                            contactButton.visibility = View.GONE
                        } else if (it.isFavorite) {
                            favoriteButton.setText(R.string.Delete_from_favorite)

                        } else {
                            favoriteButton.setText(R.string.Add_to_favorite)
                        }



                        tagsRecyclerView.layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        val tagInPostAdapter = TagInPostAdapter()
                        tagsRecyclerView.adapter = tagInPostAdapter
                        tagInPostAdapter.addTags(it.tags)

                        commentsRecyclerView.layoutManager =
                            LinearLayoutManager(context, RecyclerView.VERTICAL, true)

                        commentsRecyclerView.adapter = commentAdapter
                        commentAdapter.addComments(it.comments, dataModelToken.myId.value!!)
                    }
                }
                fun uploadComment() {
                    val uploadComment = CoroutineScope(Dispatchers.IO)
                    uploadComment.launch {
                        val response = ApiInstance.getApi().uploadComment(
                            dataModelToken.accessToken.value!!,
                            UploadCommentRequest(dataModeLPost.id.value!!, commentInput.text.toString())
                        )

                        if (response.message() == "OK") {
                            val responseRefresh = ApiInstance.getApi().requestPostById(
                                dataModeLPost.id.value!!,
                                dataModelToken.accessToken.value!!
                            )
                            responseRefresh.body()?.let {
                               withContext(Dispatchers.Main) {
                                    ToastHelper.toast(
                                        requireActivity(),
                                        getString(R.string.Comment_upload_success),
                                        response.message(),
                                        response.message()
                                    )
                                    commentAdapter.addComments(
                                        it.comments,
                                        dataModelToken.myId.value!!
                                    )

                        //                              commentAdapter.addComment(
                        //                                    response.body()!!,
                        //                                    dataModelToken.myId.value.toString()
                        //                                )
                                    commentInput.setText("")
                                    val view: View? = requireActivity().currentFocus
                                    if (view != null) {
                                        val context = getContext()
                                        val inputMethodManager =
                                            context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                        inputMethodManager.hideSoftInputFromWindow(
                                            view.getWindowToken(),
                                            0
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
                commentInput.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        uploadComment()
                        return@OnEditorActionListener true
                    }
                    false
                })
                uploadCommentButton.setOnClickListener {
                    uploadComment()
                }


                favoriteButton.setOnClickListener {
                    if (favoriteButton.text == getString(R.string.Add_to_favorite)) {
                        val addToFavoriteJob = CoroutineScope(Dispatchers.IO)
                        addToFavoriteJob.launch {
                            val response = ApiInstance.getApi().addToFavoritePostById(
                                dataModeLPost.id.value!!,
                                dataModelToken.accessToken.value!!
                            )
                            response.message().let {
                               withContext(Dispatchers.Main) {
                                    if (it == "OK") {

                                        ToastHelper.toast(
                                            requireActivity(),
                                            getString(R.string.Success),
                                            it,
                                            it
                                        )
                                        favoriteButton.setText(R.string.Delete_from_favorite)

                                    }
                                }
                            }

                        }
                    } else {
                        val addToFavoriteJob = CoroutineScope(Dispatchers.IO)
                        addToFavoriteJob.launch {
                            val response = ApiInstance.getApi().deleteFromFavoritePostById(
                                dataModeLPost.id.value!!,
                                dataModelToken.accessToken.value!!
                            )


                            response.message().let {

                               withContext(Dispatchers.Main) {
                                    if (it == "OK") {

                                        ToastHelper.toast(
                                            requireActivity(),
                                            getString(R.string.Success),
                                            it,
                                            it
                                        )
                                        favoriteButton.setText(R.string.Add_to_favorite)
                                    }
                                }
                            }
                        }
                    }

                }


            }
        }
    }


    override fun onClickComment(comment: Comment) {
        TODO("Not yet implemented")
    }

    override fun onClickDeleteCommentButton(comment: Comment) {
        val deleteCommentJob = CoroutineScope(Dispatchers.IO)
        deleteCommentJob.launch {
            val response = ApiInstance.getApi()
                .deleteCommentById(comment.id, dataModelToken.accessToken.value!!)
            if (response.message() == "OK") {
                val responseRefresh = ApiInstance.getApi().requestPostById(
                    dataModeLPost.id.value!!,
                    dataModelToken.accessToken.value!!
                )




               withContext(Dispatchers.Main) {
                    ToastHelper.toast(
                        requireActivity(),
                        getString(R.string.Comment_delete_success),
                        response.message(),
                        response.message()
                    )
                    commentAdapter.addComments(
                        responseRefresh.body()!!.comments,
                        dataModelToken.myId.value!!
                    )

//
                }
            } else {
               withContext(Dispatchers.Main) {
                    ToastHelper.toast(
                        requireActivity(),
                        getString(R.string.Comment_upload_success),
                        response.errorBody()!!.string(),
                        response.message()
                    )

                }
//
            }


        }


    }

    companion object {

        @JvmStatic
        fun newInstance() = PostDetailsFragment()
    }
}