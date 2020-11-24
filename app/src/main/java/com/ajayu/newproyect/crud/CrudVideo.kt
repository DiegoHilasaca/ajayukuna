package com.ajayu.newproyect.crud

import android.os.Environment
import android.util.Log
import com.ajayu.newproyect.model.VideoModel
import java.io.File
import java.io.FileFilter

@Suppress("DEPRECATION")
class CrudVideo {
    private val APP_NAME="Ajayukuna"
    private val proyectPath= "Animaciones"
    private val VIDEO_PATH = "Videos"
    private val APP_PATH = Environment.getExternalStorageDirectory().toString()

    fun addVideo(videoPath:String, videoList : ArrayList<VideoModel>){
        //agrega carpeta al recycleAdapter
        var num = 0
        var num2 = 0
        File(videoPath).walkTopDown().sortedWith(reverseOrder()).forEach {
            val dir = it.isFile
            val itMp4=it.absolutePath.substringAfter(".")
            when{dir->{
                num++
                if (itMp4 == "mp4"){
                    num2++
                    videoList.add(
                        VideoModel(it.toString().substringAfterLast("/").substring(2),
                            it.toString()
                        )
                    )
                    Log.e("videoList",videoList[num2-1].path)
                }

                }

            }
        }
        //addPhotoFromVideo(videoPath,videoList)
        Log.e("lista de videos",videoList.toString())

    }

    fun deleteEmptyVideo(animationsPath:String){
        var noProyect=0
        File(animationsPath).walkTopDown().forEach {
            val dir=it.isDirectory
            if (dir){
                //Log.e("dir",it.toString())
                var count=0
                it.walkTopDown().forEach { files->
                    val file=files.isFile
                    when{file->{
                        //Log.e("count",count.toString())
                        count++

                        //if (count==0)it.delete()
                    }}
                }
                if (count==0)it.delete()
                //Log.e("countfinal",count.toString())
            }
            when {dir->{
                noProyect++
            }}
        }
    }

}