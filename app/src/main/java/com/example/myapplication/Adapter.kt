package com.example.myapplication

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.*
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_recycler.view.*
import com.example.myapplication.MyAppGlideModule

class Adapter(val itemClick: (Item) -> Unit): RecyclerView.Adapter<Holder>() {

    var listData = mutableListOf<Item>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item= listData[position]
        holder.setItem(item)
    }


}

class Holder(itemView: View, val itemClick: (Item) -> Unit?):RecyclerView.ViewHolder(itemView){
    fun setItem(item: Item){

        val storageReference = FirebaseStorage.getInstance().reference.child("images").child("${item.photo}")

        Glide.with(itemView.context).load(storageReference).into(itemView.item_Photo)

        itemView.item_Title.text="${item.title}"
        itemView.item_Name.text="${item.userID}"


        var sdf= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy/MM/dd")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val item_date=sdf.format(item.date.toLong())
        itemView.item_Time.text="${item_date}"



        itemView.setOnClickListener{
            itemClick(item)
        }
    }
}


