package com.paw.ecocycle.view.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.paw.ecocycle.R
import com.paw.ecocycle.databinding.ActivityMainBinding
import com.paw.ecocycle.utils.ResultState
import com.paw.ecocycle.utils.getImageUri
import com.paw.ecocycle.utils.reduceFileImage
import com.paw.ecocycle.utils.showToast
import com.paw.ecocycle.utils.uriToFile
import com.paw.ecocycle.view.adapter.ListProductAdapter
import com.paw.ecocycle.view.viewmodel.MainViewModel
import com.paw.ecocycle.view.viewmodel.ViewModelFactory

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
        supportActionBar?.title = getString(R.string.my_store)
        setupViewModel()
        binding.rvProduct.layoutManager = LinearLayoutManager(this)

        viewModel.getSession().observe(this) { user ->
            binding.tvHello.text = getString(R.string.Greeting, user.name)
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
        getImages()
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

    private fun getImages() {
        val adapter = ListProductAdapter()
        viewModel.getImages().observe(this) { response ->
            when (response) {
                ResultState.Loading -> {
                    binding.pbMain.isVisible = true
                }

                is ResultState.Error -> {
                    binding.pbMain.isVisible = false
                    showToast(response.error)
                }

                is ResultState.Success -> {
                    binding.pbMain.isVisible = false
                    adapter.submitList(response.data.data)
                    binding.rvProduct.adapter = adapter
                }
            }
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startCamera() {
        if (cameraPermissionGranted()) {
            selectedImageUri = getImageUri(this)
            launcherIntentCamera.launch(selectedImageUri)
        } else {
            requestCameraPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            postImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startGallery() {
        if (galleryPermissionGranted()) {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestGalleryPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            postImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun postImage() {
        selectedImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            viewModel.postImage(imageFile).observe(this) { response ->
                if (response != null) {
                    when (response) {
                        is ResultState.Loading -> {
                            binding.pbMain.isVisible = true
                        }


                        is ResultState.Error -> {
                            binding.pbMain.isVisible = false
                            showToast(response.error)
                        }

                        is ResultState.Success -> {
                            binding.pbMain.isVisible = false
                            showToast(response.data.message)
                            getImages()
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.no_image))
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

    private fun cameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun galleryPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Camera permission granted")
                startCamera()
            } else {
                showToast("Camera permission denied")
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestGalleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Gallery permission granted")
                startGallery()
            } else {
                showToast("Gallery permission denied")
            }
        }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestGalleryPermission() {
        requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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