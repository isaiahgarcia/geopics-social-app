package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var fabC: FloatingActionButton
    private lateinit var fabG: FloatingActionButton
    private lateinit var fabM: FloatingActionButton
    private lateinit var fabL: FloatingActionButton
    private lateinit var imageView: ImageView
    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fabC = findViewById(R.id.createImgFab)
        fabG = findViewById(R.id.createGalFab)
        fabM = findViewById(R.id.backToMap)
        fabL = findViewById(R.id.createListFab)
        imageView = findViewById(R.id.image)

        fabC.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent()
        })

        fabG.setOnClickListener(View.OnClickListener {
            dispatchAccessGalleryIntent()
        })

        fabM.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
        })

        fabL.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@HomeActivity, ImageListActivity::class.java))
        })

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.fileprovider",
                        photoFile!!
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                displayMessage(baseContext, ex.message.toString())
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchAccessGalleryIntent() {
        val accessGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (accessGalleryIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile()
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.fileprovider",
                        photoFile!!
                    )
                    accessGalleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(accessGalleryIntent, CAPTURE_IMAGE_REQUEST)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                displayMessage(baseContext, ex.message.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            imageView.setImageBitmap(myBitmap)
        } else {
            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}