package com.example.investup

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.investup.dataModels.DataModelAddPost

import com.example.investup.dataModels.DataModelToken
import com.example.investup.databinding.ActivityMainBinding
import com.example.investup.fragments.*
import com.example.investup.navigationInterface.Navigator
import com.example.investup.publicObject.ConstNavigation


class MainActivity : AppCompatActivity(), Navigator {
    var pref: SharedPreferences? = null
    private val dataModelToken: DataModelToken by viewModels()
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private var idString: Int = -1


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
        println("its ${dataModelToken.accessToken.value}")
        if (dataModelToken.accessToken.value == "-1") {
            openFragment(
                LoginFragment.newInstance(),
                binding.mainPlaceholder.id,
                ConstNavigation.LOGIN,
                R.string.Authorize,
                false,
                false
            )
            binding.bottomNavigationView.visibility = View.GONE
        } else {
            navToHome()


            navOn()
        }
        dataModelToken.accessToken.observe(this) {
            val editor = pref?.edit()
            editor?.apply {
                putString("accessToken", it)
                apply()
            }

            println("its save = = " + pref?.getString("accessToken", "feee"))

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
        idStringPrevTitle: Int = -1
    ) {


        ConstNavigation.currentFragment = navigation
        supportActionBar?.title = getString(idStringTitle)
        if (isAddBackStack)

            supportFragmentManager.beginTransaction().replace(idHolder, f).addToBackStack(null)
                .commit()
        else
            supportFragmentManager.beginTransaction().replace(idHolder, f)
                .commit()

       supportActionBar?.setDisplayHomeAsUpEnabled(isArrowButtonOn)


        idString = idStringPrevTitle


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

    override fun navToPostDetails() {

        openFragment(
            PostDetailsFragment.newInstance(),
            binding.mainPlaceholder.id,
            ConstNavigation.POST_DETAILS,
            R.string.Post,
            true,
            true,
            R.string.Home
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
            false,
            R.string.Profile
        )

    }

    override fun onBackPressed() {
        if(idString!=-1){
            supportActionBar?.title = getString(idString)
        }
        when (ConstNavigation.currentFragment) {
            ConstNavigation.HOME -> {
                finish()
            }
            ConstNavigation.EDIT_PROFILE -> {
                navToProfile()
            }
            ConstNavigation.POST_DETAILS -> {
                backTo(ConstNavigation.HOME, false, R.string.Home)
            }
            ConstNavigation.ADD_POST -> {
                navToProfile()
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
            false,
            R.string.Profile
        )
    }

    fun backTo(navigation: Int,isArrowButtonOn: Boolean, idStringTitle: Int){
        ConstNavigation.currentFragment = navigation
        supportActionBar?.title = getString(idStringTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(isArrowButtonOn)
        supportFragmentManager.popBackStack()
    }



    override fun onSupportNavigateUp(): Boolean {
        if(idString!=-1){
            supportActionBar?.title = getString(idString)
        }
        when (ConstNavigation.currentFragment) {
            ConstNavigation.EDIT_PROFILE -> {
                navToProfile()
            }
            ConstNavigation.ADD_POST -> {
                navToProfile()
            }
            ConstNavigation.POST_DETAILS -> {

             backTo(ConstNavigation.HOME, false, idString)

            }
        }
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



