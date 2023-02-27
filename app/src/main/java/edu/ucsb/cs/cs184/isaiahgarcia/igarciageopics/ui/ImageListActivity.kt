package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R

class ImageListActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        fab = findViewById(R.id.recycleBack)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        fab.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ImageListActivity, HomeActivity::class.java))
        })

        button1.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button2.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button3.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button4.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button5.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button6.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button7.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })

        button8.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ImageListActivity,
                "Liked",
                Toast.LENGTH_SHORT
            ).show()
        })
    }


}