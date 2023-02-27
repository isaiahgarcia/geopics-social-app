package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.UserClient
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.adapters.ImageListRecyclerAdapter
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.databinding.ActivityMapsBinding
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.UserLocation


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var mDb: FirebaseFirestore
    private lateinit var binding: ActivityMapsBinding

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Location vars
    private val defaultLocation = LatLng(34.412936, -119.847863)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null

    private var mUserLocation: UserLocation? = null

    private var mImageListRecyclerAdapter: ImageListRecyclerAdapter? = null
    private var mImageRecyclerView: RecyclerView? = null
    private var mImages: ArrayList<Int> = ArrayList()

    private lateinit var fab: FloatingActionButton

    private val permissionAll = 1
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!hasPermissions(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, permissionAll)
        }

        if (!hasPermissions(this, permissions[1])) {
            ActivityCompat.requestPermissions(this, permissions, permissionAll)
        }

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        mImageRecyclerView = findViewById(R.id.image_list_recyclerview);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fab = findViewById(R.id.mapFab)
        fab.setOnClickListener {
            startActivity(Intent(this@MapsActivity, HomeActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MapsActivity, LoginActivity::class.java))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMap?.let { map ->
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        updateLocationUI()
        getDeviceLocation()
        addCustomMarker(LatLng(34.412936, -119.847863), "I love UCSB!", R.drawable.ucsb)
        addCustomMarker(LatLng(34.412646,-119.855377), "This pizza looks bomb!", R.drawable.woodstocks)
        addCustomMarker(LatLng(34.409357, -119.859860), "Deltopia", R.drawable.deltopia)
        addCustomMarker(LatLng(34.408810, -119.852817), "Moving out of dorms :(", R.drawable.manzi)
        addCustomMarker(LatLng(34.413303, -119.856406), "Just stopped at blenders.", R.drawable.blenders)
        addCustomMarker(LatLng(34.413404, -119.845522), "Last study session before quarter ends!", R.drawable.library)
        addCustomMarker(LatLng(34.410771, -119.864860), "Hanging out with friends.", R.drawable.sabado)
        addCustomMarker(LatLng(34.413781, -119.863788), "At the park.", R.drawable.park)
    }

    private fun getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = UserLocation()
            val userRef = mDb.collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().uid!!)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: successfully set the user client.")
                    val user = task.result.toObject(User::class.java)
                    mUserLocation!!.userLoc = user;
                    (applicationContext as UserClient).user = user
                    getDeviceLocation()
                }
            }
        } else {
            getDeviceLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            val title = "I am on the verge of tears with my finals and COVID :("
                            addCustomMarker(LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude), title, R.drawable.home)
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                        val geoPoint = GeoPoint(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                        mUserLocation!!.setGeo_point(geoPoint)
                        mUserLocation!!.timestamp = null
                        saveUserLocation()
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun saveUserLocation() {
        if (mUserLocation != null) {
            val locationRef = mDb
                .collection(getString(R.string.collection_user_locations))
                .document(FirebaseAuth.getInstance().uid!!)
            locationRef.set(mUserLocation!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        TAG, """saveUserLocation: inserted user location into database.
                             latitude: ${mUserLocation!!.getGeo_point()!!.latitude}
                             longitude: ${mUserLocation!!.getGeo_point()!!.longitude}"""
                    )
                }
            }
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            getUserDetails()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap?.isMyLocationEnabled = false
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getMarkerBitmapFromView(@DrawableRes resId: Int): Bitmap? {
        val customMarkerView: View =
            (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_marker,
                null
            )
        val markerImageView: ImageView =
            customMarkerView.findViewById(R.id.profile_image) as ImageView
        markerImageView.setImageResource(resId)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable: Drawable = customMarkerView.background
        drawable.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    private fun addCustomMarker(position: LatLng, title: String, picture: Int) {
        Log.d(TAG, "addCustomMarker()")
        if (mMap == null) {
            return
        }

        // adding a marker on map with image from  drawable
        mMap!!.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
                .icon(getMarkerBitmapFromView(picture)?.let { BitmapDescriptorFactory.fromBitmap(it) })
        )
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
