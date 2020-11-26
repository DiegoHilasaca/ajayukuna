package com.ajayu.newproyect.crud

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.ajayu.newproyect.CameraActivity
import com.ajayu.newproyect.model.AnimationModel
import java.io.File
const val APP_NAME="Ajayukuna"

@Suppress("DEPRECATION")
class CrudAnimations {

    private val proyectPath= "Animaciones"
    private val videoPath = "Videos"
    private val appPath = Environment.getExternalStorageDirectory().toString()

    fun createAppPath(){
        val appPath = File(appPath,APP_NAME)
        if (!appPath.exists()){
            appPath.mkdirs()
            Log.e("create App path",appPath.toString())
        }
        if (appPath.exists()){
            val proyect = File(appPath,proyectPath)
            if (!proyect.exists()){
                proyect.mkdirs()
                Log.e("create proyect path",proyect.toString())
            }
            val video = File(appPath,videoPath)
            if (!video.exists()){
                video.mkdirs()
                Log.e("create video path",video.toString())
            }
        }
    }

    fun createAnimation(generalpath:String, nombreProyecto : String){
        val proyect = File(generalpath,nombreProyecto)
        if (!proyect.exists()){
            proyect.mkdirs()
            Log.e("create proyect",proyect.toString())
        }
    }

    fun addAnimation(animationsPath:String, listAnimations : ArrayList<AnimationModel>){
        //agrega carpeta al recycleAdapter
        File(animationsPath).walkTopDown().sorted().forEach {
            val dir = it.isDirectory
            val animationPathLength=it.toString().length
            when{dir->{
                Log.e("tamaño",it.toString().length.toString()+"/--/"+it.toString())
                if(animationPathLength>animationsPath.length) {
                    listAnimations.add(
                        AnimationModel(
                            it.toString().substringAfterLast("/").substring(2), it.toString(), "nada"

                        )
                    )
                    Log.e("tamaño_if",it.toString().length.toString()+"/--/"+it.toString())
                }}

            }
        }
        addPhotoAnimation(animationsPath,listAnimations)
        Log.e("listade proyectosss",listAnimations.toString())

    }


    private fun addPhotoAnimation(animationsPath:String, listAnimations : ArrayList<AnimationModel>){
        var parent2=""
        var count=0
        File(animationsPath).walkTopDown().maxDepth(2).sorted().forEach {
            val dir=it.isFile
            val parent=it.parent
            when {dir->{

                if (parent != parent2){
                    Log.e("if parent",it.toString())
                    listAnimations[count].imagePath=it.toString()
                    Log.e("agrega aqui",listAnimations[count].path+"////"+count.toString())
                    count=count.inc()
                }
            }
            }
            parent2=parent
        }
    }

    fun deleteEmptyAnimation(animationsPath:String){
        var noProyect=0
        File(animationsPath).walkTopDown().forEach {
            val dir=it.isDirectory
            if (dir){
                var count=0
                it.walkTopDown().forEach { files->
                    val file=files.isFile
                    when{file->{
                        count++
                    }}
                }
                if (count==0)it.delete()
            }
            when {dir->{
                noProyect++
            }}
        }
    }

    fun afterCreated(context:Context, nombreProyecto: String, animationsPath: String){
        val intent = Intent(context, CameraActivity::class.java)
        intent.putExtra("path", "$animationsPath/$nombreProyecto")
        Log.e("path",nombreProyecto)
        intent.putExtra("nombreProyecto",nombreProyecto)
        context.startActivity(intent)
    }

}