package com.example.investup.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.CommentAdapter
import com.example.investup.adapter.TagInPostAdapter
import com.example.investup.dataModels.DataModeLPost
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentPostDetailsBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.retrofit.dataClass.Comment
import com.example.investup.retrofit.requestModel.UploadCommentRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class PostDetailsFragment : Fragment(), CommentAdapter.Listener {
    lateinit var binding: FragmentPostDetailsBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModeLPost: DataModeLPost by activityViewModels()
    private val dataModeLUser: DataModelUser by activityViewModels()
    lateinit var player: ExoPlayer
    lateinit var coroutine: CoroutineScope
    var error = ""
    val commentAdapter = CommentAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {


            coroutine.launch {
                val responsePost = ApiInstance.getApi().requestPostById(
                    dataModeLPost.id.value!!, dataModelToken.accessToken.value!!
                )
                if (responsePost.code() == 200) {
                    val body = responsePost.body()
                    body?.let {

                        withContext(Dispatchers.Main) {


                            profileButton.setOnClickListener{
                                if (dataModelToken.myId.value == body.user.id)
                                    navigator().navToProfile()

                                else {
                                    dataModeLUser.id.value = body.user.id
                                    navigator().navToUserProfile()
                                }
                            }

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

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                fullDescriptionLabel.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                                shortDescriptionLabel.justificationMode = JUSTIFICATION_MODE_INTER_WORD
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
                        onClickFavoriteButton()
                    }
                }
            }
        }
    }

    fun uploadComment() {

        coroutine.launch {
            val response = ApiInstance.getApi().uploadComment(
                dataModelToken.accessToken.value!!, UploadCommentRequest(
                    dataModeLPost.id.value!!, binding.commentInput.text.toString()
                )
            )

            if (response.code() == 200) {
                val responseRefresh = ApiInstance.getApi().requestPostById(
                    dataModeLPost.id.value!!, dataModelToken.accessToken.value!!
                )
                responseRefresh.body()?.let {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.Comment_upload_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        commentAdapter.addComments(
                            it.comments, dataModelToken.myId.value!!
                        )


                        binding.commentInput.setText("")
                        val view: View? = requireActivity().currentFocus
                        if (view != null) {
                            val context = getContext()
                            val inputMethodManager =
                                context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(
                                view.getWindowToken(), 0
                            )
                        }

                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        JSONObject(response.errorBody()?.string()).getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }




    }



    private fun onClickFavoriteButton() = with(binding){
        if (favoriteButton.text.toString() == getString(R.string.Add_to_favorite)) {

            coroutine.launch {
                val response = ApiInstance.getApi().addToFavoritePostById(
                    dataModeLPost.id.value!!, dataModelToken.accessToken.value!!
                )
                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {


                        Toast.makeText(
                            requireContext(), getString(R.string.Toast_success_add_to_favorite), Toast.LENGTH_SHORT
                        ).show()
                        favoriteButton.setText(R.string.Delete_from_favorite)


                    } else {
                        Toast.makeText(
                            requireContext(), JSONObject(
                                response.errorBody()?.string()
                            ).getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        } else {

            coroutine.launch {
                val response = ApiInstance.getApi().deleteFromFavoritePostById(
                    dataModeLPost.id.value!!, dataModelToken.accessToken.value!!
                )


                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {


                        Toast.makeText(
                            requireContext(), getString(R.string.Toast_success_delete_from_favorite), Toast.LENGTH_SHORT
                        ).show()
                        favoriteButton.setText(R.string.Add_to_favorite)


                    } else {
                        Toast.makeText(
                            requireContext(), JSONObject(
                                response.errorBody()?.string()
                            ).getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }
    override fun onClickComment(comment: Comment) {
        TODO("Not yet implemented")
    }

    override fun onClickDeleteCommentButton(comment: Comment) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(getString(R.string.Are_you_sure_about_delete_comment))
        alertDialogBuilder.setPositiveButton(getString(R.string.Yes)) { dialog, which ->
            coroutine.launch {
                val response = ApiInstance.getApi()
                    .deleteCommentById(comment.id, dataModelToken.accessToken.value!!)
                if (response.code() == 200) {
                    val responseRefresh = ApiInstance.getApi().requestPostById(
                        dataModeLPost.id.value!!, dataModelToken.accessToken.value!!
                    )


                    if (responseRefresh.code() == 200) {

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), getString(R.string.Comment_delete_success), Toast.LENGTH_SHORT)
                                .show()
                            commentAdapter.addComments(
                                responseRefresh.body()!!.comments, dataModelToken.myId.value!!
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                JSONObject(response.errorBody()?.string()).getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
//
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

    override fun onClickProfileButton(id: String) {
        if (id == dataModelToken.myId.value) navigator().navToProfile()
        else {
            dataModeLUser.id.value = id
            navigator().navToUserProfile()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = PostDetailsFragment()
    }
}