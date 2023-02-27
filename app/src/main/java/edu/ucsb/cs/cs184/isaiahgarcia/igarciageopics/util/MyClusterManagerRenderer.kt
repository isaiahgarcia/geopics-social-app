package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.util

import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.ClusterMarker


open class MyClusterManagerRenderer(
    context: Context, googleMap: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker?>?
) :
    DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {
    private val iconGenerator: IconGenerator = IconGenerator(context.applicationContext)
    private val imageView: ImageView = ImageView(context.applicationContext)
    private val markerWidth: Int = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    private val markerHeight: Int = context.resources.getDimension(R.dimen.custom_marker_image).toInt()

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {
        imageView.setImageResource(item.iconPictureIG)
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }

    init {

        // initialize cluster item icon generator
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        val padding =
            context.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }
}