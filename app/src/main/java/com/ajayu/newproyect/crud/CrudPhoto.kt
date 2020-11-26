package com.ajayu.newproyect.crud

import android.util.Log
import java.io.File

class CrudPhoto {

    fun outputPath(proyectName: String, videoPath: String): String {
        Log.e("VideoFile","Generando nombre de video")
        var file=File(videoPath, "$proyectName.mp4")
        val isFileCreated : Boolean = file.createNewFile()
        if (isFileCreated)Log.e("isFileCreated","Se ha creado el file")
        else Log.e("isFileCreated","No se ha creado el file")
        var count=1
        File(videoPath).walkTopDown().forEach {
            val dir=it.isFile
            when {
                dir->{
                    if (file.exists()){
                        val nombreVideoDuplicado="${proyectName}_$count.mp4"
                        file=File(videoPath,nombreVideoDuplicado)
                        count++
                        Log.e("fileVideo",file.toString())
                    }else{
                        Log.e("fileVideo","fileVideo no existe")
                    }
                }

            }
        }
        Log.e("outputpath",file.absolutePath)
        return file.absolutePath
    }
}