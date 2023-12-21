package com.paw.ecocycle.view.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.paw.ecocycle.R
import com.paw.ecocycle.databinding.ActivityMainBinding
import com.paw.ecocycle.utils.ResultState
import com.paw.ecocycle.utils.getImageUri
import com.paw.ecocycle.utils.reduceFileImage
import com.paw.ecocycle.utils.showToast
import com.paw.ecocycle.utils.uriToFile
import com.paw.ecocycle.view.viewmodel.MainViewModel
import com.paw.ecocycle.view.viewmodel.ViewModelFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { factory }

    private var selectedImageUri: Uri? = null

    private var isExpanded = false
    private val fromBottomfabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab)
    }
    private val toBottomfabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab)
    }
    private val rotateClockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_clock_wise)
    }
    private val rotateAntiClockWiseAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_anti_clock_wise)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "My Store"
        //setupView()

        viewModel.getSession().observe(this) { user ->
            binding.tvHello.text = "Hello, ${user.name}"
        }

        binding.fabMenu.setOnClickListener {
            if (isExpanded) {
                shrinkFab()
            } else {
                expandFab()
            }
        }
        binding.btnStartRecycle.setOnClickListener {
            binding.fabMenu.performClick()
        }
        setupViewModel()
        setupAction()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupAction() {
        binding.fabCamera.setOnClickListener {
            startCamera()
        }
        binding.fabGallery.setOnClickListener {
            startGallery()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startCamera() {
        selectedImageUri = getImageUri(this)
        launcherIntentCamera.launch(selectedImageUri)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.getSession().observe(this) { user ->
                postImage(user.token)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.getSession().observe(this) {user ->
                postImage(user.token)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun postImage(token: String) {
        selectedImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            viewModel.postImage(token, imageFile).observe(this) { response ->
                if (response != null) {
                    when (response) {
                        is ResultState.Loading -> {
                            binding.loadAnim.isVisible = true
                        }

                        is ResultState.Success -> {
                            binding.loadAnim.isVisible = false
                            showToast(response.data.message)
                            val toMain = Intent(this, MainActivity::class.java)
                            toMain.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(toMain)
                        }

                        is ResultState.Error -> {
                            binding.loadAnim.isVisible = false
                            showToast(response.error)
                        }
                    }
                }
            }
        } ?: showToast("Tidak ada gambar")
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun shrinkFab() {
        binding.fabMenu.startAnimation(rotateAntiClockWiseAnim)
        binding.fabCamera.startAnimation(toBottomfabAnim)
        binding.fabGallery.startAnimation(toBottomfabAnim)
        isExpanded = !isExpanded
    }

    private fun expandFab() {
        binding.fabMenu.startAnimation(rotateClockWiseAnim)
        binding.fabCamera.startAnimation(fromBottomfabAnim)
        binding.fabGallery.startAnimation(fromBottomfabAnim)
        isExpanded = !isExpanded
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_1 -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.menu_2 -> {
                viewModel.logout()
                val toStart = Intent(this, StartActivity::class.java)
                toStart.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(toStart)
            }
        }
        return true
    }
}