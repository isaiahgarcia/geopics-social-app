package edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.models.User
import edu.ucsb.cs.cs184.isaiahgarcia.igarciageopics.R


class UserRecyclerAdapter(users: ArrayList<User>) :
    RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {
    private var mUsers: ArrayList<User> = ArrayList<User>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = mUsers[position].getUsername()
        holder.email.text = mUsers[position].getEmail()
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var email: TextView

        init {
            username = itemView.findViewById(R.id.username)
            email = itemView.findViewById(R.id.email)
        }
    }

    init {
        mUsers = users
    }
}