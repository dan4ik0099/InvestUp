package com.example.investup.fragments

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels


import com.example.investup.R
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ToastHelper
import com.example.investup.UriHelper
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentEditProfileBinding
import com.example.investup.retrofit.requestModel.UserChangeNameRequest
import com.example.investup.retrofit.requestModel.UserChangePasswordRequest
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    var havePermission = false
    private lateinit var filePart: MultipartBody.Part
    private val dataModelToken: DataModelToken by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val jobInitInfo = CoroutineScope(Dispatchers.IO)
            jobInitInfo.launch {

                println(ApiInstance)
                val response =
                    ApiInstance.getApi().requestInfoMe("Bearer ${dataModelToken.accessToken.value}")
                val message = response.body()
                message?.let {
                    requireActivity().runOnUiThread {
                        nameInput.setText(it.firstName)
                        surnameInput.setText(it.lastName)
                        Picasso.get().load(it.avatar).into(avatarChangeView)
                    }
                }
            }

            launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == RESULT_OK) {
                        val res: Intent? = it.data

                        avatarChangeView.setImageURI(res?.data)
                        filePart = buildMultipart(
                            res?.data!!,
                            requireContext(),
                            "image/jpeg",
                            "photo"
                        )
                        val uploadImageJob = CoroutineScope(Dispatchers.IO)
                        uploadImageJob.launch {
                            val response = ApiInstance.getApi()
                                .uploadFile(
                                    "Bearer ${dataModelToken.accessToken.value}",
                                    filePart

                                )
                            println(response.message())

                        }


                    }


                }



            avatarChangeView.setOnClickListener {
                val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1




                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    println(1)
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                    )
                } else {
                    println(2)
                    havePermission = true

                }
                if (havePermission) {
                    println(3)
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    launcher!!.launch(i)

                }


            }
            saveDataButton.setOnClickListener {
                val jobSaveData = CoroutineScope(Dispatchers.IO)
                jobSaveData.launch {
                    val response = ApiInstance.getApi().requestChangeNameAndLastName(
                        "Bearer ${dataModelToken.accessToken.value}",
                        UserChangeNameRequest(
                            nameInput.text.toString(), surnameInput.text.toString()
                        )
                    )
                    requireActivity().runOnUiThread {


                        if (response.message() == "OK") {
                            ToastHelper.toast(
                                requireActivity(),
                                R.string.Toast_saved_data
                            )
                        } else {
                            ToastHelper.toast(
                                requireActivity(),
                                R.string.Toast_unexpected_error
                            )
                        }
                    }
                }
            }
            savePasswordButton.setOnClickListener {
                if (newPasswordInput.text == repeatPasswordInput.text) {
                    val jobSavePassword = CoroutineScope(Dispatchers.IO)
                    jobSavePassword.launch {
                        val response = ApiInstance.getApi().requestChangePassword(
                            "Bearer ${dataModelToken.accessToken.value}",
                            UserChangePasswordRequest(
                                oldPasswordInput.text.toString(),
                                newPasswordInput.text.toString()
                            )
                        )
                        val message = response.message()
                        requireActivity().runOnUiThread {
                            if (message == "OK") {
                                Toast.makeText(
                                    activity,
                                    R.string.Toast_saved_data,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                println(message)
                                ToastHelper.toast(
                                    requireActivity(),
                                    R.string.Toast_unexpected_error
                                )

                                Toast.makeText(
                                    activity,
                                    R.string.Toast_unexpected_error,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                    }

                }

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
        fun newInstance() = EditProfileFragment()
    }

}

