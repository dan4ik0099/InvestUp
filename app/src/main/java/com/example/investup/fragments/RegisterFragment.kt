package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.investup.publicObject.ApiInstance

import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentRegisterBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.requestModel.UserRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    private val dataModelToken: DataModelToken by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            toLoginButton.setOnClickListener() {
                navigator().goToLogin()
            }
            confirmRegisterButton.setOnClickListener() {
                if (true) {
                    val jobRegister = CoroutineScope(Dispatchers.IO)
                    jobRegister.launch {
                        val response = ApiInstance.getApi().requestRegister(
                            UserRegisterRequest(
                                loginInput.text.toString(),
                                nameInput.text.toString(),
                                surnameInput.text.toString(),
                                passwordInput.text.toString()

                            )
                        )
                        val message = response.errorBody()?.string()
                            ?.let { JSONObject(it).getString("message") }
                        println(response)
                        val userToken = response.body()
                        println("userToken = $userToken")
                        requireActivity().runOnUiThread {
                            userToken?.apply {
                                dataModelToken.accessToken.value = accessToken
                                dataModelToken.refreshToken.value = refreshToken
                                navigator().navToHome()
                                navigator().navOn()
                                navigator().navAfterLoginRegister()
                            }


                        }
                    }
                }
            }
        }

    }


    companion object {

        @JvmStatic
        fun newInstance() = RegisterFragment()

    }
}