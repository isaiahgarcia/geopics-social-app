package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.clustering.ClusterManager
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.Constants.MAPVIEW_BUNDLE_KEY
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.adapters.UserRecyclerAdapter
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.ClusterMarker
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.UserLocation
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.util.MyClusterManagerRenderer
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import javax.annotation.Nullable


class UserListFragment : Fragment(), OnMapReadyCallback {
    //widgets
    private var mUserListRecyclerView: RecyclerView? = null
    private var mMapView: MapView? = null

    //vars
    private lateinit var mDb: FirebaseFirestore
    private val mUserList = ArrayList<User>()
    private val mUserLocations = ArrayList<UserLocation>()
    private var mUserRecyclerAdapter: UserRecyclerAdapter? = null
    private var mGoogleMap: GoogleMap? = null
    private var mUserPosition: UserLocation? = null
    private var mMapBoundary: LatLngBounds? = null
    private var mClusterManager: ClusterManager<ClusterMarker?>? = null
    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private val mClusterMarkers = ArrayList<ClusterMarker>()
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mUserLocations.size == 0) { // make sure the list doesn't duplicate by navigating back
            if (arguments != null) {
            }
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_user_list, container, false)
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view)
        mMapView = view.findViewById(R.id.user_list_map)
        initUserListRecyclerView()
        initGoogleMap(savedInstanceState)
        setUserPosition()
        return view
    }

    private fun addMapMarkers() {
        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager = ClusterManager(activity?.applicationContext, mGoogleMap)
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = activity?.applicationContext?.let {
                    MyClusterManagerRenderer(
                        it,
                        mGoogleMap,
                        mClusterManager
                    )
                }
                mClusterManager!!.renderer = mClusterManagerRenderer
            }
            for (userLocation in mUserLocations) {
                Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString())
                try {
                    var snippet = ""
                    snippet = if (userLocation.getUser()?.getUserID()
                            .equals(FirebaseAuth.getInstance().uid)
                    ) {
                        "This is you"
                    } else {
                        "Determine route to " + userLocation.getUser()!!.getUsername() + "?"
                    }
                    var avatar: Int = R.drawable.firebase_logo // set the default avatar
                    try {
                        avatar = userLocation.getUser()?.getAvatar()?.toInt()!!
                    } catch (e: NumberFormatException) {
                        Log.d(
                            TAG, "addMapMarkers: no avatar for " + userLocation.getUser()!!
                                .getUsername() + ", setting default."
                        )
                    }
                    val newClusterMarker = ClusterMarker(
                        LatLng(
                            userLocation.getGeo_point()!!.latitude, userLocation.getGeo_point()!!
                                .longitude
                        ),
                        userLocation.getUser()!!.getUsername()!!,
                        snippet,
                        avatar,
                        userLocation.getUser()
                    )
                    mClusterManager!!.addItem(newClusterMarker)
                    mClusterMarkers.add(newClusterMarker)
                } catch (e: NullPointerException) {
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.message)
                }
            }
            mClusterManager!!.cluster()
            setCameraView()
        }
    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
    private fun setCameraView() {

        // Set a boundary to start
        val bottomBoundary = mUserPosition!!.getGeo_point()!!.latitude - .1
        val leftBoundary = mUserPosition!!.getGeo_point()!!.longitude - .1
        val topBoundary = mUserPosition!!.getGeo_point()!!.latitude + .1
        val rightBoundary = mUserPosition!!.getGeo_point()!!.longitude + .1
        mMapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
    }

    private fun setUserPosition() {
        for (userLocation in mUserLocations) {
            if (userLocation.getUser()?.getUserID().equals(FirebaseAuth.getInstance().uid)) {
                mUserPosition = userLocation
            }
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)
    }

    private fun initUserListRecyclerView() {
        mUserRecyclerAdapter = UserRecyclerAdapter(mUserList)
        mUserListRecyclerView!!.adapter = mUserRecyclerAdapter
        mUserListRecyclerView!!.layoutManager = LinearLayoutManager(getActivity())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();
        mGoogleMap = map
        addMapMarkers()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    companion object {
        private const val TAG = "UserListFragment"
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }
}
