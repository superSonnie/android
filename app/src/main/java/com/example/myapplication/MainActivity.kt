package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var items = arrayListOf("중앙대", "숭실대", "서울대", "광운대")
        spinner_main.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        var email =""
        //아이템 선택 리스너
        spinner_main.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                when(position){
                    0->{email="@cau.ac.kr"}
                    1->{email="@ssu.ac.kr"}
                    2->{email="@snu.ac.kr"}
                    3->{email="@kw.ac.kr"}
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println("학교를 선택해주세요")
            }
        }


               
        login_main.setOnClickListener {
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(
                    email_login.text.toString()+email,
                    password_login.text.toString()
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, Home::class.java)
                        intent.putExtra("userID", email_login.text.toString())
                        intent.putExtra("School",email)
                        startActivity(intent)
                    } else {
                        var dialog = AlertDialog.Builder(this@MainActivity)
                        dialog.setTitle("실패")
                        dialog.show()
                    }
                }
        }

        enroll_main.setOnClickListener {
            val intent = Intent(this, Enroll::class.java)
            startActivity(intent)
        }
    }
}

