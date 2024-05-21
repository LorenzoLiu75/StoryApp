package com.mobile.storyapp.view.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mobile.storyapp.R
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.ApiService
import com.mobile.storyapp.databinding.ActivityMapsBinding
import com.mobile.storyapp.view.ViewModelFactory
import com.mobile.storyapp.view.detail.DetailViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel.fetchStoriesWithLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mapsViewModel.stories.observe(this, Observer { response ->
            response.listStory.forEach { story ->
                val latLng = LatLng(story.lat!!, story.lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
            }

            response.listStory.firstOrNull()?.let {
                val firstLocation = LatLng(it.lat!!, it.lon!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 5f))
            }
        })
    }
}