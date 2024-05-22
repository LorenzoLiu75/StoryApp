package com.mobile.storyapp.view.add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mobile.storyapp.R
import com.mobile.storyapp.data.response.FileUploadResponse
import com.mobile.storyapp.data.pref.UserPreference
import com.mobile.storyapp.databinding.ActivityAddStoryBinding
import com.mobile.storyapp.getImageUri
import com.mobile.storyapp.reduceFileImage
import com.mobile.storyapp.uriToFile
import com.mobile.storyapp.view.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null

    private lateinit var locationManager: LocationManager

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, getString(R.string.permission_request_granted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.permission_request_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_story)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        binding.checkBoxLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    getCurrentLocation()
                }
            } else {
                currentLocation = null
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d(PHOTO_PICKER, getString(R.string.no_media_selected))
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                currentLocation = it
            }
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val userPreference = UserPreference.getInstance(this@AddStoryActivity)
                    val token = userPreference.getSession().first().token
                    val lat = currentLocation?.latitude
                    val lon = currentLocation?.longitude

                    viewModel.uploadStoryImage(token, multipartBody, requestBody, lat, lon)

                    viewModel.response.observe(this@AddStoryActivity){ res ->
                        if(!res.error){
                            AlertDialog.Builder(this@AddStoryActivity).apply {
                                setTitle(getString(R.string.success))
                                setMessage(getString(R.string.image_upload_successfully))
                                setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    dialog.dismiss()
                                    clearInputFields()
                                    showLoading(false)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                    AlertDialog.Builder(this@AddStoryActivity).apply {
                        setTitle(getString(R.string.success))
                        setMessage(getString(R.string.failed_to_upload_image) + ": ${errorResponse.message}")
                        setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                            showLoading(false)
                        }
                        create()
                        show()
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearInputFields() {
        binding.edAddDescription.text = null
        binding.previewImageView.setImageURI(null)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val PHOTO_PICKER = "Photo Picker"
    }
}