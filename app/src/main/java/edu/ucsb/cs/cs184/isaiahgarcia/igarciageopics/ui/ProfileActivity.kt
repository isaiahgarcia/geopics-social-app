package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.UserClient
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R


class ProfileActivity : AppCompatActivity(), View.OnClickListener,
    IProfile {
    //widgets
    private var mAvatarImage: CircleImageView? = null
    private lateinit var fabB: FloatingActionButton

    //vars
    private var mImageListFragment: ImageListFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setHomeButtonEnabled(true)
        mAvatarImage = findViewById(R.id.image_choose_avatar)
        findViewById<View>(R.id.image_choose_avatar).setOnClickListener(this)
        findViewById<View>(R.id.text_choose_avatar).setOnClickListener(this)
        retrieveProfileImage()
        fabB = findViewById(R.id.goBack)
        fabB.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ProfileActivity, MapsActivity::class.java))
        })
    }

    private fun retrieveProfileImage() {
        val requestOptions: RequestOptions = RequestOptions()
            .error(R.drawable.firebase_logo)
            .placeholder(R.drawable.firebase_logo)
        var avatar = 0
        try {
            //avatar = (applicationContext as UserClient).user!!.getAvatar()!!.toInt()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "retrieveProfileImage: no avatar image. Setting default. " + e.message)
        }
        mAvatarImage?.let {
            Glide.with(this@ProfileActivity)
                .setDefaultRequestOptions(requestOptions)
                .load(avatar)
                .into(it)
        }
    }

    override fun onClick(v: View) {
        mImageListFragment = ImageListFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_out_up
            )
            .replace(
                R.id.fragment_container,
                mImageListFragment!!,
                getString(R.string.fragment_image_list)
            )
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onImageSelected(resource: Int) {

        // remove the image selector fragment
        mImageListFragment?.let {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_in_down,
                    R.anim.slide_out_down,
                    R.anim.slide_out_up
                )
                .remove(it)
                .commit()
        }

        // display the image
        val requestOptions: RequestOptions = RequestOptions()
            .placeholder(R.drawable.firebase_logo)
            .error(R.drawable.firebase_logo)
        mAvatarImage?.let {
            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(resource)
                .into(it)
        }

        // update the client and database
        val user: User? = (applicationContext as UserClient).user
        user?.setAvatar(resource.toString())
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().uid!!)
                .set(user)
        }
    }

    companion object {
        private const val TAG = "ProfileActivity"
    }
}