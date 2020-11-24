package com.ajayu.newproyect

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.bumptech.glide.Glide
import com.ajayu.newproyect.otros.AnimateView
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animate= AnimateView()
        Glide.with(this)
            .asGif()
            .load(R.drawable.out2)
            .into(image_view_splash)
        animate.showAlphaViews(layout_splash,0f,1f)

        var estado=false
        Handler().postDelayed({

            finish()
            if(estado)Log.e("handler","espere bastante")
            else{
                val i = Intent(baseContext, DrawerActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_left,R.animator.slide_right)
                Log.e("tiempo","recien me ejecuto")
                //Toast.makeText(this,"recien me ejecuto",Toast.LENGTH_SHORT).show()
            }
        }, 4400)

        skip.setOnClickListener{
            estado=true
            animate.scaleView(skip,1f,1.15f)
            animate.scaleView(skip,1.15f,1f)
            deleteCache(this)
            Handler().postDelayed({
                val i = Intent(baseContext,DrawerActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.animator.slide_left,R.animator.slide_right)
            },500)
        }
    }


    private fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}
