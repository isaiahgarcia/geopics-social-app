package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class UserLocation {
    var userLoc: User? = null
    private var geoPoint: GeoPoint? = null

    @ServerTimestamp
    var timestamp: Date? = null

    constructor(user: User?, geo_point: GeoPoint?, timestamp: Date?) {
        this.userLoc = user
        this.geoPoint = geo_point
        this.timestamp = timestamp
    }

    constructor() {}

    fun getUser(): User? {
        return userLoc
    }

    fun getGeo_point(): GeoPoint? {
        return geoPoint
    }

    fun setGeo_point(geo_point: GeoPoint) {
        this.geoPoint = geo_point
    }

    @JvmName("getTimestamp1")
    fun getTimestamp(): Date? {
        return timestamp
    }

    @JvmName("setTimestamp1")
    fun setTimestamp(timestamp: Date?) {
        this.timestamp = timestamp
    }

    override fun toString(): String {
        return "UserLocation{" +
                ", geo_point=" + geoPoint +
                ", timestamp=" + timestamp +
                '}'
    }
}