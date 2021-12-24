package com.kejaplus.application.ui.Map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.kejaplus.application.R
import com.kejaplus.application.databinding.FragmentMapsBinding
import com.kejaplus.application.ui.AddProperty.AddPropertyFragmentDirections
import java.io.IOException

class MapsFragment : Fragment() {
    private lateinit var mapsBinding: FragmentMapsBinding
    private lateinit var mMap:GoogleMap
    private lateinit var mContext: Context

    companion object{
        const val COORDINATE_KEY = "COORDINATE_TEXT"
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         */
        val kenya = LatLng(0.0236, 37.9062)
        googleMap.addMarker(MarkerOptions().position(kenya).title("Kenya"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(kenya))
        mapsBinding.searchLocation.isSubmitButtonEnabled = true
        mapsBinding.searchLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                var location: String = mapsBinding.searchLocation.query.toString()
                var addressList: List<android.location.Address>? = null

                if (location != null || location != "") {
                    val geocoder: Geocoder = Geocoder(activity?.applicationContext)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)// Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("Location","Location Not Found ....")
                        Toast.makeText(activity, "Location Not Found.....", Toast.LENGTH_SHORT)
                            .show();
                    }

                    if (addressList?.size!! > 0) {
                        val address: Address = addressList.get(0)
                        val latLng: LatLng = LatLng(address.latitude, address.longitude)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(location))
                        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                        //val coordinates: String = latLng.toString()
                        val coordinates = "$location $latLng"
                        val fab = mapsBinding.fab
                        fab.show()
                        fab.setOnClickListener { view ->
                            Snackbar.make(view, "$location $latLng", Snackbar.LENGTH_LONG)
                                .setAction("Submit", null)
                                .show()
                            navigateBack(coordinates)
                        }

                    }

                   }else{
                    mapsBinding.fab.hide()
                    Toast.makeText(activity, "Location Not Found.....", Toast.LENGTH_SHORT).show()
                    Log.d("Location","Location Not Found ....")

                    }
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                var location: String = mapsBinding.searchLocation.query.toString()
                if (location.isEmpty() || location.equals("")) {
                    mapsBinding.fab.hide()
                }
                return false
            }
        })
    }

    fun navigateBack(coordinate: String) = with(findNavController()){
        previousBackStackEntry?.savedStateHandle?.set(COORDINATE_KEY,coordinate)
        popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapsBinding= FragmentMapsBinding.inflate(inflater,container,false)

        mContext = container!!.context
        return mapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}