package com.example.investup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.investup.publicObject.ApiInstance

import com.example.investup.dataModels.DataModelToken

import com.example.investup.databinding.FragmentLoginBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.requestModel.UserAuthRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val dataModelToken: DataModelToken by activityViewModels()


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




        binding.apply {
            enterButton.setOnClickListener() {
                val jobLogin = CoroutineScope(Dispatchers.IO)
                jobLogin.launch {
                    val response = ApiInstance.getApi().requestAuth(
                        UserAuthRequest(
                            loginInput.text.toString(),
                            passwordInput.text.toString()
                        )
                    )
                    val message =
                        response.errorBody()?.string()?.let { JSONObject(it).getString("message") }

                    val userToken = response.body()


                    requireActivity().runOnUiThread {
                        userToken?.apply {

                            dataModelToken.accessToken.value = accessToken
                            dataModelToken.refreshToken.value = refreshToken

                            navigator().navOn()
                            navigator().navAfterLoginRegister()
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