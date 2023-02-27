package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models

class User {
    private var userID: String? = null
    private var email: String? = null
    private var username: String? = null
    private var avatar: String? = null

    constructor(userID: String?, email: String?, username: String?, avatar: String?) {
        this.userID = userID
        this.email = email
        this.username = username
        this.avatar = avatar
    }

    constructor() {}

    fun getUserID(): String? {
        return userID
    }

    fun getEmail(): String? {
        return email
    }

    fun getUsername(): String? {
        return username
    }

    fun getAvatar(): String? {
        return avatar
    }

    fun setUserID(userID: String?) {
        this.userID = userID
    }

    fun setUsername(username: String?) {
        this.username = username
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun setAvatar(avatar: String?) {
        this.avatar = avatar
    }

    override fun toString(): String {
        return "User{" +
                ", userID=" + userID +
                ", email=" + email +
                ", username=" + username +
                ", avatar==" + avatar +
                "}"
    }
}