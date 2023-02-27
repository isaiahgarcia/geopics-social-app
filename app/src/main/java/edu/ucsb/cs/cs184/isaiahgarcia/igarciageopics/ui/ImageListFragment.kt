package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.adapters.ImageListRecyclerAdapter
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R


/**
 * A simple [Fragment] subclass.
 * Use the [ImageListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageListFragment : Fragment(), ImageListRecyclerAdapter.ImageListRecyclerClickListener {
    //widgets
    private var mRecyclerView: RecyclerView? = null

    //vars
    private val mImageResources = ArrayList<Int>()
    private var mIProfile: IProfile? = null
    private lateinit var fabB: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_image_list, container, false)
        mRecyclerView = view.findViewById(R.id.image_list_recyclerview)
        imageResources
        initRecyclerview()
        return view
    }

    private val imageResources: Unit
        private get() {
            mImageResources.add(R.drawable.ucsb)
            mImageResources.add(R.drawable.woodstocks)
            mImageResources.add(R.drawable.deltopia)
            mImageResources.add(R.drawable.firebase_logo)
            mImageResources.add(R.drawable.home)
            mImageResources.add(R.drawable.firebase_logo)
            mImageResources.add(R.drawable.home)
            mImageResources.add(R.drawable.firebase_logo)
        }

    private fun initRecyclerview() {
        val mAdapter = activity?.let { ImageListRecyclerAdapter(it, mImageResources, this) }
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL)
        mRecyclerView!!.layoutManager = staggeredGridLayoutManager
        mRecyclerView!!.adapter = mAdapter
    }

    fun onAttachIt(context: Context?) {
        mIProfile = activity as IProfile?
    }

    override fun onImageSelected(position: Int) {
        mIProfile?.onImageSelected(mImageResources[position])
    }

    companion object {
        private const val TAG = "ImageListFragment"
        private const val NUM_COLUMNS = 2
        fun newInstance(): ImageListFragment {
            return ImageListFragment()
        }
    }
}