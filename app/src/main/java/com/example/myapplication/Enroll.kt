package com.example.myapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_enroll.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class Enroll : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll)

        var list_of_items = arrayListOf("중앙대", "숭실대", "서울대", "광운대")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)
        var email =""
        //아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                when(position){
                    0->{email="@cau.ac.kr"}
                    1->{email="@ssu.ac.kr"}
                    2->{email="@snu.ac.kr"}
                    3->{email="@kw.ac.kr"}
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        enroll.setOnClickListener {

            fun sendEmailVerification(){
                if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                    var dialog= AlertDialog.Builder(this@Enroll)
                    dialog.setTitle("이미 메일 발송 완료")
                    dialog.show()
                    return
                }

                FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var dialog= AlertDialog.Builder(this@Enroll)
                        dialog.setTitle(" 메일 발송 완료")
                        dialog.show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }else{
                        var dialog= AlertDialog.Builder(this@Enroll)
                        dialog.setTitle("메일 발송 실패")
                        dialog.show()
                    }
                }
            }

            FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(id_enroll.text.toString()+email,password_enroll.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    sendEmailVerification()

                                    var retrofit=Retrofit.Builder()
                                        .baseUrl("http://ad26ab60425c.ngrok.io")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                    var registerService=retrofit.create(RegisterService::class.java)

                                    var School=email.substring(1,3)
                                    var userID=id_enroll.text.toString()

                                     registerService.requestRegister(userID,School).enqueue(object : Callback<Register>{
                                         override fun onResponse(call: Call<Register>, response: Response<Register>) {
                                            //성공시
                                            var register=response.body()//code, msg
                                            var dialog= AlertDialog.Builder(this@Enroll)
                                            dialog.setTitle("code="+register?.code+"msg="+register?.msg)
                                            dialog.show()
                                            Log.d("회원가입결과",register?.code+"+"+ register?.msg)
                                        }
                                         override fun onFailure(call: Call<Register>, t: Throwable) {
                                            //실패시
                                            var dialog = AlertDialog.Builder(this@Enroll)
                                            dialog.setTitle("통신실패")
                                            dialog.show()
                                        }
                                    })
                                } else {
                                    var dialog= AlertDialog.Builder(this@Enroll)
                                    dialog.setTitle("실패")
                                    dialog.show()
                                }
                            }
                    }
    }
}