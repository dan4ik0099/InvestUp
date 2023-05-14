package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.investup.publicObject.ApiInstance

import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentRegisterBinding
import com.example.investup.navigationInterface.navigator
import com.example.investup.retrofit.requestModel.UserRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        if (response.code() == 200) {

                            val userToken = response.body()
                            println("userToken = $userToken")
                            withContext(Dispatchers.Main) {
                                userToken?.apply {
                                    dataModelToken.accessToken.value = accessToken
                                    dataModelToken.refreshToken.value = refreshToken
                                    navigator().navToHome()
                                    navigator().navOn()
                                    navigator().navAfterLoginRegister()
                                }
                            }

                        }
                        else
                        {

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