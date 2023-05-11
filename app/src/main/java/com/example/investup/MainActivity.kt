package com.example.investup

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.ActivityMainBinding
import com.example.investup.fragments.*
import com.example.investup.navigationInterface.Navigator
import com.example.investup.publicObject.ApiInstance
import com.example.investup.publicObject.ConstNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), Navigator {
    var pref: SharedPreferences? = null
    private val dataModelToken: DataModelToken by viewModels()

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        registerPermissionListener()
        checkCameraPermission()
    }


    private fun init() {

        pref = getSharedPreferences("base", Context.MODE_PRIVATE)
        dataModelToken.accessToken.value = pref?.getString("accessToken", "-1")

        dataModelToken.accessToken.observe(this) {
            val editor = pref?.edit()
            editor?.apply {
                putString("accessToken", it)
                apply()
            }

            println("its save = = " + pref?.getString("accessToken", "feee"))

        }

        val initIdJob = CoroutineScope(Dispatchers.IO)
        initIdJob.launch {
            val response = ApiInstance.getApi()
                .requestInfoMe(dataModelToken.accessToken.value!!)

            runOnUiThread {
                if (response.message() == "OK") {
                    val body = response.body()
                    body?.let {
                        dataModelToken.myId.value = it.id
                        navToHome()
                        navOn()
                    }
                } else if (response.message() != "OK") {

                    openFragment(
                        LoginFragment.newInstance(),
                        binding.mainPlaceholder.id,
                        ConstNavigation.LOGIN,
                        R.string.Authorize,
                        false,
                        false
                    )
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }






        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navToHome()
                }
                R.id.chat -> {
                    navToChat()
                }
                R.id.favorite -> {
                    navToFavorite()
                }
                R.id.profile -> {
                    navToProfile()
                }
            }
            true
        }

    }

    private fun openFragment(
        f: Fragment,
        idHolder: Int,
        navigation: Int,
        idStringTitle: Int,
        isArrowButtonOn: Boolean,
        isAddBackStack: Boolean,

        ) {


        ConstNavigation.currentFragmentStack.push(navigation)


        if (isAddBackStack) {
            ConstNavigation.titleStack.push(supportActionBar?.title.toString())

            println("kkkk  " + ConstNavigation.titleStack.peek())

            supportFragmentManager.beginTransaction().replace(idHolder, f).addToBackStack(null)
                .commit()

        } else {
            supportFragmentManager.beginTransaction().replace(idHolder, f)
                .commit()

            ConstNavigation.titleStack.clear()

        }


        supportActionBar?.title = getString(idStringTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(isArrowButtonOn)


    }

    override fun goToRegister() {

        openFragment(
            RegisterFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.REGISTER,
            R.string.Register,
            false,
            false
        )
    }


    override fun goToLogin() {
        openFragment(
            LoginFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.LOGIN,
            R.string.Authorize,
            false,
            false
        )

        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun navOn() {
        binding.bottomNavigationView.visibility = View.VISIBLE

    }

    override fun scrollSave(y: Int) {
        val editor = pref?.edit()
        editor?.apply {
            putInt("scrollY", y)
            apply()
        }
    }

    override fun scrollTo(): Int {
        return pref?.getInt("scrollY", 0)!!
    }

    override fun navToEditPost() {
        openFragment(
            EditPostFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.EDIT_POST,
            R.string.Edit_post,
            true,
            true,
        )
    }

    override fun navToPostDetails() {

        openFragment(
            PostDetailsFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.POST_DETAILS,
            R.string.Post,
            true,
            true,
        )
    }

    override fun navAfterLoginRegister() {
        binding.bottomNavigationView.selectedItemId = R.id.home

    }

    override fun navToHome() {
        openFragment(
            HomeFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.HOME,
            R.string.Home,
            false,
            true
        )


    }

    override fun navToChat() {
        openFragment(
            ChatFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.CHAT,
            R.string.Chat,
            false,
            false
        )
    }

    override fun navToFavorite() {
        openFragment(
            FavoriteFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.FAVORITE,
            R.string.Favorite,
            false,
            false
        )

    }

    override fun navToProfile() {

        openFragment(
            ProfileFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.PROFILE,
            R.string.Profile,
            false,
            false
        )
    }

    override fun navToEditProfile() {


        openFragment(
            EditProfileFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.EDIT_PROFILE,
            R.string.Edit_profile,
            true,
            true,

            )

    }

    override fun onBackPressed() {
        if (!ConstNavigation.titleStack.empty()) {
            supportActionBar?.title = ConstNavigation.titleStack.pop()
        }
        when (ConstNavigation.currentFragmentStack.peek()) {
            ConstNavigation.HOME -> {
                finish()
            }
            ConstNavigation.EDIT_PROFILE -> {
                backTo(false)
            }
            ConstNavigation.POST_DETAILS -> {
                backTo(false)
            }
            ConstNavigation.ADD_POST -> {
                backTo(false)
            }
            ConstNavigation.EDIT_POST -> {
                backTo(false)
            }


        }


    }

    override fun navToAddPost() {
        openFragment(
            AddPostFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.ADD_POST,
            R.string.Creating_post,
            true,
            true,
        )
    }



    fun backTo(isArrowButtonOn: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isArrowButtonOn)
        supportFragmentManager.popBackStack()
        ConstNavigation.currentFragmentStack.pop()

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            }

            else -> {
                pLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun registerPermissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[READ_EXTERNAL_STORAGE] == true) {
                Toast.makeText(this, "Camera run", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

}



