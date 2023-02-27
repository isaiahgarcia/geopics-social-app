package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User


class ClusterMarker(// required field
    private var position: LatLng, // required field
    private var title: String, // required field
    private var snippet: String,
    var iconPictureIG: Int,
    private var user: User?
) :
    ClusterItem {

    fun setPosition(position: LatLng) {
        this.position = position
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setSnippet(snippet: String) {
        this.snippet = snippet
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun getIconPicture(): Int {
        return iconPictureIG
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun getUser(): User? {
        return user
    }

    fun setIconPicture(iconPicture: Int) {
        this.iconPictureIG = iconPicture
    }
}