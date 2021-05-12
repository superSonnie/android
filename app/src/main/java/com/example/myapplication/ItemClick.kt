package com.example.myapplication

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_item__enroll.*
import kotlinx.android.synthetic.main.activity_item_click.*
import kotlinx.android.synthetic.main.item_recycler.view.*


class ItemClick : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_click)

        val photo=intent.getStringExtra("photo")
        val storageReference = FirebaseStorage.getInstance().reference.child("images").child("${photo}")
        Glide.with(this).load(storageReference).into(click_photo)

        content.text=intent.getStringExtra("content")
        userID.text=intent.getStringExtra("name")

        val date=intent.getStringExtra("date")
        var sdf= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy/MM/dd")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val click_date=sdf.format(date?.toLong())

        time.text="${click_date}"

        title_itemClick.text=intent.getStringExtra("title")


    }
}