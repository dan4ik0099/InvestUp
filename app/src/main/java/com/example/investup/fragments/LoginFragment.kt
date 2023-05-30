package com.example.investup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.investup.R
import com.example.investup.publicObject.ApiInstance

import com.example.investup.dataModels.DataModelToken

import com.example.investup.databinding.FragmentLoginBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.publicObject.SocketEvents
import com.example.investup.publicObject.SocketSingleton
import com.example.investup.retrofit.requestModel.UserAuthRequest
import kotlinx.coroutines.*
import okhttp3.internal.wait
import org.json.JSONObject


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    lateinit var coroutine: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coroutine = CoroutineScope(Dispatchers.IO)

        binding.apply {
            enterButton.setOnClickListener() {

                coroutine.launch {
                    val response = ApiInstance.getApi().requestAuth(
                        UserAuthRequest(
                            loginInput.text.toString(),
                            passwordInput.text.toString()
                        )
                    )

                    if (response.code() == 200) {
                        val responseUser =
                            ApiInstance.getApi()
                                .requestInfoMe("Bearer ${response.body()!!.accessToken}")
                        SocketSingleton.connectSocket("Bearer ${response.body()!!.accessToken}")
                        SocketSingleton.sendConnection(response.body()!!.accessToken, SocketEvents.CONNECTION.s)
                        withContext(Dispatchers.Main) {
                            if (responseUser.code() == 200) {
                                dataModelToken.accessToken.value =
                                    "Bearer ${response.body()!!.accessToken}"
                                dataModelToken.refreshToken.value = response.body()!!.refreshToken

                                dataModelToken.myId.value = responseUser.body()!!.id

                                SocketSingleton.sendConnection(response.body()!!.accessToken, SocketEvents.CONNECTION.s)
                                navigator().navOn()
                                navigator().navAfterLoginRegister()

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.Authorize_error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

                }

            }



            toRegisterButton.setOnClickListener() {

                navigator().goToRegister()
            }
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment()
    }

}