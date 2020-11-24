package com.ajayu.newproyect.otros

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.ajayu.newproyect.ExoActivity
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import java.text.NumberFormat


class Export(val inputPath: String, val outputPath: String, val fps: String, val context: Context) {

    val animateView=AnimateView()

    val TAG = "ffmpeg"

    fun execute2(){

        val start=System.currentTimeMillis()

        when (val rc = FFmpeg.execute("-framerate $fps -i $inputPath -c:v mpeg4 -crf 0 -b:v 10M $outputPath")) {

            RETURN_CODE_SUCCESS -> {
                Log.e(TAG, "onFinish: Command execution completed successfully.")
                Toast.makeText(
                    context,
                    "Puedes encontrar el video en : $outputPath",
                    Toast.LENGTH_LONG
                ).show()
                Log.e(TAG, "${Toast.LENGTH_SHORT} ")
                val end = System.currentTimeMillis()
                val format = NumberFormat.getNumberInstance()
                format.minimumFractionDigits = 0
                Toast.makeText(
                    context,
                    "Finalizado en : ${format.format((end - start) / 1000)} segundos",
                    Toast.LENGTH_LONG
                ).show()
                //Log.e("Tiempo de carga","${format.format((end-start)/1000)}")
                animateView.run {
                    format.minimumFractionDigits = 0
                    Toast.makeText(
                        context,
                        "Finalizado en : ${format.format((end - start) / 1000)} segundos",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Tiempo de carga", "${format.format((end - start) / 1000)}")
                }
                Handler().postDelayed({
                    val intent = Intent(context, ExoActivity::class.java)
                    intent.putExtra("videoPath", outputPath)
                    Log.e("videoPath_export", outputPath)
                    context.startActivity(intent)
                }, 200)
            }
            RETURN_CODE_CANCEL -> {
                Log.i(Config.TAG, "Command execution cancelled by user.")
            }
            else -> {
                Log.i(
                    Config.TAG, String.format(
                        "Command execution failed with rc=%d and the output below.",
                        rc
                    )
                )
                Config.printLastCommandOutput(Log.INFO)
            }
        }

    }



}