package com.example.investup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.investup.publicObject.ApiInstance
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentProfileBinding
import com.example.investup.navigationInterface.navigator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val dataModelToken: DataModelToken by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val jobInfoInit = CoroutineScope(Dispatchers.IO)
            jobInfoInit.launch {
                val response =
                    ApiInstance.getApi().requestInfoMe("Bearer ${dataModelToken.accessToken.value}")
                val message = response.body()

                message?.apply {

                    activity?.runOnUiThread {


                        nameSurNameText.text = ("${firstName} ${lastName}")
                        loginText.text = email

                        Picasso.get().load(avatar).into(imageView)


                        cardView2.setOnClickListener {
                            navigator().navToEditProfile()
                        }
                    }
                }
            }



            exitButton.setOnClickListener {
                dataModelToken.accessToken.value = "-1"
                navigator().goToLogin()
            }
            addPostButton.setOnClickListener{
                navigator().navToAddPost()
            }
        }

    }


    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()

    }
}