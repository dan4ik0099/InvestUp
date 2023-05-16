package com.example.investup.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

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
import com.example.investup.UriHelper
import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.FragmentEditProfileBinding
import com.example.investup.retrofit.requestModel.UserChangeNameRequest
import com.example.investup.retrofit.requestModel.UserChangePasswordRequest
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileFragment : Fragment() {
    lateinit var binding: FragmentEditProfileBinding
    var launcher: ActivityResultLauncher<Intent>? = null
    var havePermission = false
    lateinit var coroutine: CoroutineScope
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

        init()


    }

    fun init() {
        coroutine = CoroutineScope(Dispatchers.IO)
        binding.apply {

            coroutine.launch {

                println(ApiInstance)
                val response =
                    ApiInstance.getApi().requestInfoMe(dataModelToken.accessToken.value!!)
                val message = response.body()
                message?.let {
                    withContext(Dispatchers.Main) {
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

                        filePart = buildMultipart(
                            res?.data!!,
                            requireContext(),
                            "image/jpeg",
                            "photo"
                        )
                        coroutine.launch {
                            val response = ApiInstance.getApi()
                                .uploadFile(
                                    dataModelToken.accessToken.value!!,
                                    filePart

                                )
                            withContext(Dispatchers.Main){
                                if (response.code()==200) avatarChangeView.setImageURI(res?.data)
                            }
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

                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                    )
                } else {

                    havePermission = true

                }
                if (havePermission) {

                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    launcher!!.launch(i)

                }


            }
            saveDataButton.setOnClickListener {
                coroutine.launch {
                    val response = ApiInstance.getApi().requestChangeNameAndLastName(
                        dataModelToken.accessToken.value!!,
                        UserChangeNameRequest(
                            nameInput.text.toString(), surnameInput.text.toString()
                        )
                    )
                    if (response.code() == 200) {

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.Toast_saved_data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            savePasswordButton.setOnClickListener {
                if (newPasswordInput.text.toString() == repeatPasswordInput.text.toString()) {

                    coroutine.launch {
                        val response = ApiInstance.getApi().requestChangePassword(
                            dataModelToken.accessToken.value!!,
                            UserChangePasswordRequest(
                                oldPasswordInput.text.toString(),
                                newPasswordInput.text.toString()
                            )
                        )
                        withContext(Dispatchers.Main) {

                            if (response.code() == 200) {

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.Toast_saved_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.Unexpected_error),
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

