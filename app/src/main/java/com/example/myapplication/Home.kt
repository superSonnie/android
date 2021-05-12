package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_item_click.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_recycler.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        val School = intent.getStringExtra("School").toString()
        val userID=intent.getStringExtra("userID").toString()

        var retrofit= Retrofit.Builder()
            .baseUrl("http://ad26ab60425c.ngrok.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var itemService=retrofit.create(ItemService::class.java)

        itemService.itemRequest(School).enqueue(object:Callback<MutableList<Item>> {
            override fun onResponse(call: Call<MutableList<Item>>, response: Response<MutableList<Item>>) {


                val data:MutableList<Item>? = response.body()
                var adapter=Adapter(){ item ->
                    val intent = Intent(this@Home,ItemClick::class.java)
                    intent.putExtra("photo", item.photo)
                    intent.putExtra("content", item.content)
                    intent.putExtra("name", item.userID)
                    intent.putExtra("date", item.date)
                    intent.putExtra("title", item.title)
                    startActivity(intent)
                }

                if (data != null) { adapter.listData=data }
                else{Log.d("아이템이 없음","아이템이 없음")}

                mRecyclerView?.adapter = adapter
                mRecyclerView?.layoutManager = LinearLayoutManager(this@Home)


            }
            override fun onFailure(call: Call<MutableList<Item>>, t: Throwable) {
                //실패시
                Log.d("통신결과:",t.message.toString())
                var dialog = AlertDialog.Builder(this@Home)
                dialog.setTitle("통신실패")
                dialog.show()
            }
        })

        item_Enroll.setOnClickListener{
            val intent = Intent(this,ItemEnroll::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("School",School)
            startActivity(intent)
        }
    }
}