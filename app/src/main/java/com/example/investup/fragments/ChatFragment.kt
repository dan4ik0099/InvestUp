package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investup.other.OnMessageListener
import com.example.investup.adapter.DialogAdapter
import com.example.investup.dataModels.DataModelToken
import com.example.investup.dataModels.DataModelUser
import com.example.investup.databinding.FragmentChatBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.SocketEvents
import com.example.investup.publicObject.SocketSingleton
import com.example.investup.retrofit.dataClass.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class ChatFragment : Fragment() , DialogAdapter.Listener, OnMessageListener {

    lateinit var coroutine: CoroutineScope
    private val dataModelToken: DataModelToken by activityViewModels()
    private val dataModelUser: DataModelUser by activityViewModels()
    lateinit var binding: FragmentChatBinding
    var dialogList = ArrayList<Dialog>()
    private val dialogAdapter = DialogAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.visibility = View.GONE
        init()

    }




    private fun init(){
        println("plssssss")
        SocketSingleton.setCallback(this)
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {
            dialogRecyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            dialogRecyclerView.adapter = dialogAdapter
            dialogRecyclerView.setHasFixedSize(false)

            coroutine.launch {
                val response = ApiInstance.getApi().requestAllDialogs(dataModelToken.accessToken.value!!)
                if (response.code() == 200)
                {
                    withContext(Dispatchers.Main){
                        dialogList.clear()
                        dialogList.addAll(response.body()!!)
                        dialogAdapter.addDialogs(dialogList)
                        requireView().visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ChatFragment()

    }

    override fun onClickDialog(dialog: Dialog) {
        dataModelUser.dialogId.value = dialog.id
        dataModelUser.id.value = dialog.user.id
        navigator().navToDialogUser()
    }



    override fun onMessage(message: String, event : String) {

        requireActivity().runOnUiThread {
            if (event == SocketEvents.MESSAGE.s) {
                val dialog = dialogList.find { it.id == JSONObject(message).getString("dialogId") }
                if (dialog == null)
                    dialogAdapter.bumpDialog(message, true)
                else dialogAdapter.bumpDialog(message, false)
            }
        }

    }
}