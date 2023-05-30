package com.example.investup.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.R
import com.example.investup.adapter.MessageAdapter
import com.example.investup.adapter.PostAdapter
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentDialogUserBinding
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.other.OnMessageListener
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.SocketEvents
import com.example.investup.publicObject.SocketSingleton
import com.example.investup.retrofit.dataClass.Dialog
import com.example.investup.retrofit.dataClass.Message
import com.example.investup.retrofit.dataClass.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class DialogUserFragment : Fragment(), MessageAdapter.Listener, OnMessageListener {
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModelUser: DataModelUser by activityViewModels()
    lateinit var coroutine: CoroutineScope
    private val messageAdapter = MessageAdapter(this)
    lateinit var binding: FragmentDialogUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDialogUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        SocketSingleton.setCallback(this)
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {

            messageRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, true)
            messageRecyclerView.adapter = messageAdapter
            messageRecyclerView.setHasFixedSize(false)


            coroutine.launch {
                val response = ApiInstance.getApi().requestDialogById(
                    dataModelUser.dialogId.value!!,
                    dataModelToken.accessToken.value!!
                )
                if (response.code() == 200) {
                    response.body()?.let {
                        it.messages.reverse()
                        withContext(Dispatchers.Main) {
                            val user = it.users.find { x -> x.id == dataModelUser.id.value }
                            println(dataModelUser.id.value)
                            println(user!!.id)

                            Picasso.get().load(user!!.avatar).into(avatarImage)
                            nameSurnameLabel.text = "${user.firstName} ${user.lastName}"
                            messageAdapter.addMessages(
                                response.body()!!.messages,
                                dataModelUser.id.value!!
                            )
                            messageRecyclerView.scrollToPosition(0)


                        }
                    }
                }
            }
            sendMessageButton.setOnClickListener {
                coroutine.launch {
                    SocketSingleton.sendMessage(
                        dataModelToken.accessToken.value!!.replace(
                            "Bearer ",
                            ""
                        ),
                        dataModelUser.dialogId.value!!,
                        messageInput.text.toString(),
                        SocketEvents.MESSAGE.s
                    )
                }
                messageInput.setText("")
            }
            profileCard.setOnClickListener {

                navigator().navToUserProfile()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DialogUserFragment()
    }


    override fun onMessage(message: String, event: String) {
        if (event == SocketEvents.MESSAGE.s) {
            var msg: Message?

            val messageJson = JSONObject(message)
            val userJson = JSONObject(messageJson.getString("user"))
            requireActivity().runOnUiThread {

                msg = Message(
                    messageJson.getString("id"),
                    messageJson.getString("updatedAt"),
                    messageJson.getString("createdAt"),
                    messageJson.getString("dialogId"),
                    messageJson.getString("text"),
                    true,
                    User(
                        userJson.getString("email"),
                        ArrayList(),
                        1.toString(),
                        1.toString(),
                        userJson.getString("id"),
                        true,
                        userJson.getString("firstName"),
                        userJson.getString("lastName"),
                        userJson.getString("avatar")


                    )

                )
                if (msg!!.user.id == dataModelUser.id.value) {


                    SocketSingleton.sendReadMessage(
                        dataModelToken.accessToken.value!!.replace(
                            "Bearer ",
                            ""
                        ),
                        msg!!.id,
                        SocketEvents.READ_MESSAGE.s
                    )
                    println(msg!!.id)

                }
                if (msg!!.dialogId == dataModelUser.dialogId.value!!) {
                    messageAdapter.addMessage(msg!!)
                    binding.messageRecyclerView.scrollToPosition(0)
                }


            }


        }

    }

    override fun onLongClickMessage(message: Message) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(getString(R.string.Are_you_sure_about_delete_message))
        alertDialogBuilder.setPositiveButton(getString(R.string.Yes)) { dialog, which ->
            coroutine.launch {
                SocketSingleton.sendDeleteMessage(
                    dataModelToken.accessToken.value!!.replace(
                        "Bearer ",
                        ""
                    ),
                    message.id,
                    SocketEvents.DELETE_MESSAGE.s
                )
                requireActivity().runOnUiThread {
                    println(message.id)
                    messageAdapter.removeMessage(message)
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