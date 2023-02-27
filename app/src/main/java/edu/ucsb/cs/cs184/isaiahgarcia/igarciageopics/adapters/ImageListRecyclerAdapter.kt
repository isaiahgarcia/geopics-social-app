package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R


class ImageListRecyclerAdapter(
    private val mContext: Context,
    images: ArrayList<Int>,
    imageListRecyclerClickListener: ImageListRecyclerClickListener
) :
    RecyclerView.Adapter<ImageListRecyclerAdapter.ViewHolder>() {
    private var mImages = ArrayList<Int>()
    private val mImageListRecyclerClickListener: ImageListRecyclerClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_image_list_item, parent, false)
        return ViewHolder(view, mImageListRecyclerClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestOptions: RequestOptions = RequestOptions()
            .placeholder(R.drawable.firebase_logo)
            .error(R.drawable.firebase_logo)
        Glide.with(mContext)
            .setDefaultRequestOptions(requestOptions)
            .load(mImages[position])
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return mImages.size
    }

    inner class ViewHolder(itemView: View, clickListener: ImageListRecyclerClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var image: ImageView
        var mClickListener: ImageListRecyclerClickListener
        override fun onClick(v: View) {
            mClickListener.onImageSelected(adapterPosition)
        }

        init {
            image = itemView.findViewById(R.id.image)
            mClickListener = clickListener
            itemView.setOnClickListener(this)
        }
    }

    interface ImageListRecyclerClickListener {
        fun onImageSelected(position: Int)
    }

    init {
        mImages = images
        mImageListRecyclerClickListener = imageListRecyclerClickListener
    }
}