package com.example.investup

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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


class MainActivity : AppCompatActivity(), Navigator{
    var pref: SharedPreferences? = null
    private val dataModelToken : DataModelToken by viewModels()
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private var isHomeFragment: Boolean = false
    private var currentFragment: Int = 0


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
        dataModelToken.accessToken.value = pref?.getString("accessToken","-1")
        println("its ${dataModelToken.accessToken.value}")
        if (dataModelToken.accessToken.value=="-1") {
            openFragment(LoginFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.LOGIN)
            binding.bottomNavigationView.visibility = View.GONE
        }
        else{
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
    private fun openFragment(f: Fragment, idHolder: Int, navigation: Int) {


        currentFragment = navigation
        if (currentFragment == ConstNavigation.EDIT_PROFILE || currentFragment == ConstNavigation.ADD_POST){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)


        }else {

            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        supportFragmentManager.beginTransaction().replace(idHolder, f).commit()

    }

    override fun goToRegister() {

        openFragment(RegisterFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.REGISTER)
    }


    override fun goToLogin() {
        openFragment(LoginFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.LOGIN)

        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun navOn() {
        binding.bottomNavigationView.visibility = View.VISIBLE

    }
    override fun navAfterLoginRegister(){
        binding.bottomNavigationView.selectedItemId = R.id.home

    }

    override fun navToHome() {
        supportActionBar?.title = getString(R.string.Home)

        openFragment(HomeFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.HOME)
        isHomeFragment = true;
    }

    override fun navToChat() {
        supportActionBar?.title = getString(R.string.Chat)
        openFragment(ChatFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.CHAT)
    }
    override fun navToFavorite() {
        supportActionBar?.title = getString(R.string.Favorite)

    }

    override fun navToProfile() {
        supportActionBar?.title = getString(R.string.Profile)
        openFragment(ProfileFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.PROFILE)
    }

    override fun navToEditProfile() {
        supportActionBar?.title = getString(R.string.Edit_profile)

        openFragment(EditProfileFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.EDIT_PROFILE)

    }

    override fun onBackPressed() {
        if (currentFragment== ConstNavigation.HOME){
            finish()
        }
        else{
            navAfterLoginRegister()

        }
    }
    override fun navToAddPost(){
        supportActionBar?.title = getString(R.string.Creating_post)
        openFragment(AddPostFragment.newInstance(), binding.mainPlaceholder.id, ConstNavigation.ADD_POST)
    }


    override fun onSupportNavigateUp(): Boolean {
        when(currentFragment){
            ConstNavigation.EDIT_PROFILE -> {
                navToProfile()
            }
            ConstNavigation.ADD_POST -> {
                navToProfile()
            }
        }
        return true
    }

    private fun checkCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ->{
                Toast.makeText(this,"Camera run", Toast.LENGTH_LONG).show()
            }

            else -> {
                pLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
            if(it[READ_EXTERNAL_STORAGE] == true){
                Toast.makeText(this,"Camera run", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

}



