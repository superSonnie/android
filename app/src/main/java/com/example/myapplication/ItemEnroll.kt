package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_item__enroll.*
import kotlinx.android.synthetic.main.activity_item_click.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.System.out
import java.text.SimpleDateFormat
import java.util.*


class ItemEnroll : AppCompatActivity(){
    val CAMERA_PERMISSION=arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION=arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val FLAG_PERM_CAMERA=98
    val FLAG_PERM_STORAGE=99
    val FLAG_REQ_CAMERA=101
    val FLAG_REQ_STORAGE=102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item__enroll)

        if(checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
            setViews()
        }


        button.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

    }

    fun setViews(){
        imageButton.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type=MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, FLAG_REQ_STORAGE)
        }
        cameraButton.setOnClickListener {
            openCamera()
        }
    }

    fun openCamera() {
        if (checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, FLAG_REQ_CAMERA)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        var School =intent.getStringExtra("School").toString()
                        var userID=intent.getStringExtra("userID").toString()

                        val bitmap = data.extras?.get("data") as Bitmap
                        val uri = saveImageFile(newFileName(), "image/jpg", bitmap)
                        imagePreview.setImageURI(uri)

                        var timeStamep=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        var imageFile="Image_"+timeStamep+"_"+userID+"_"+School+".jpg"
                        val storage=Firebase.storage
                        var storageRef=storage.reference.child("images").child(imageFile)

                        storageRef.putFile(uri!!).addOnSuccessListener {
                        }


                        val title_enroll=title_itemEnroll.text.toString()
                        val content_enroll=content_itemEnroll.text.toString()


                        val time=System.currentTimeMillis().toString()

                        var gson : Gson =  GsonBuilder()
                            .setLenient()
                            .create()

                        var retrofit=Retrofit.Builder()
                            .baseUrl(" http://ad26ab60425c.ngrok.io")
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build()

                        var itemEnrollService=retrofit.create(ItemEnrollService::class.java)

                        itemEnrollService.itemEnroll(userID, School, title_enroll, content_enroll, time,imageFile).enqueue(object : Callback<Register> {
                            override fun onFailure(call: Call<Register>, t: Throwable) {
                                t.message?.let { it1 -> Log.d("레트로피 결과0", it1) }
                                Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
                            }
                            override fun onResponse(call: Call<Register>, response: retrofit2.Response<Register>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(getApplicationContext(),
                                        "File Uploaded Successfully...", Toast.LENGTH_LONG).show()
                                    var res=response.body()
                                    Log.d("레트로핏 결과1", "" + res?.msg.toString())
                                    var dialog= AlertDialog.Builder(this@ItemEnroll)
                                    dialog.setTitle("code="+res?.code+"msg="+res?.msg)
                                    dialog.show()
                                } else {
                                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show()
                                    var res=response.body()
                                    Log.d("레트로핏 결과2", "" + res?.msg.toString())
                                    var dialog= AlertDialog.Builder(this@ItemEnroll)
                                    dialog.setTitle("code="+res?.code+"msg="+res?.msg)
                                    dialog.show()
                                }
                            }
                        })

                    }
                }
                FLAG_PERM_STORAGE -> {
                    val uri=data?.data
                    var School =intent.getStringExtra("School").toString()
                    var userID=intent.getStringExtra("userID").toString()
                    imagePreview.setImageURI(uri)
                    var timeStamep=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    var imageFile="Image_"+timeStamep+"_"+userID+"_"+School+".jpg"
                    val storage=Firebase.storage
                    var storageRef=storage.reference.child("images").child(imageFile)

                    storageRef.putFile(uri!!).addOnSuccessListener {
                    }
                    val title_enroll=title_itemEnroll.text.toString()
                    val content_enroll=content_itemEnroll.text.toString()
                    val time=System.currentTimeMillis().toString()

                    var gson : Gson =  GsonBuilder()
                        .setLenient()
                        .create()
                    var retrofit=Retrofit.Builder()
                        .baseUrl(" http://ad26ab60425c.ngrok.io")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                    var itemEnrollService=retrofit.create(ItemEnrollService::class.java)
                    itemEnrollService.itemEnroll(userID, School, title_enroll, content_enroll, time,imageFile).enqueue(object : Callback<Register> {
                        override fun onFailure(call: Call<Register>, t: Throwable) {
                            t.message?.let { it1 -> Log.d("레트로피 결과0", it1) }
                            Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
                        }
                        override fun onResponse(call: Call<Register>, response: retrofit2.Response<Register>) {
                            if (response.isSuccessful) {
                                Toast.makeText(getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show()
                                var res=response.body()
                                Log.d("레트로핏 결과1", "" + res?.msg.toString())
                            } else {
                                Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show()
                                var res=response.body()
                                Log.d("레트로핏 결과2", "" + res?.msg.toString())
                                var dialog= AlertDialog.Builder(this@ItemEnroll)
                                dialog.setTitle("code="+res?.code+"msg="+res?.msg)
                                dialog.show()
                            }
                        }
                    })
                }
            }
        }
    }

    fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap):Uri?{
        var values =ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try{
            if(uri!=null){
                var descriptor=contentResolver.openFileDescriptor(uri, "w")
                if(descriptor!=null){


                    val fos=FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        }catch (e: java.lang.Exception){
            Log.e("File", "error=${e.localizedMessage}")
        }
        return uri
    }

    private fun newFileName():String{
        val sdf=SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename=sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    private fun checkPermission(permissions: Array<out String>, flag: Int):Boolean{
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            for(permission in permissions){
                if(ContextCompat.checkSelfPermission(
                        this, permission
                    )!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        when(requestCode){
            FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해야합니다", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                }
                setViews()
            }
            FLAG_PERM_CAMERA -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해야합니다", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                }
                openCamera()
            }
        }
    }
}