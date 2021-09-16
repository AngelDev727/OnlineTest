package com.example.onlineTest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class UserAdapter (private val users: ArrayList<UserModel>, private val context : Context) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(users[position], context)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: UserModel, context: Context) {
            val txtUserName      = itemView.findViewById(R.id.txtUserName) as TextView
            val txtType     = itemView.findViewById(R.id.txtType) as TextView
            val imageView = itemView.findViewById(R.id.imgAvatar) as ImageView

            txtUserName.text = "Name: ${user.userName}"
            txtType.text = "Type: ${user.type}"
            Picasso.get().load(user.avatarUrl).fit().centerCrop()
                .placeholder(R.drawable.no_profile)
                .error(R.drawable.no_profile)
                .into(imageView);
        }
    }
}
