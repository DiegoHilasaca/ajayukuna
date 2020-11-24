package com.ajayu.newproyect.otros

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.ajayu.newproyect.R
import kotlinx.android.synthetic.main.activity_camera.*

class AnimateView {
    fun scaleView(
        v: View,
        startScale: Float,
        endScale: Float
    ) {
        val anim: Animation = ScaleAnimation(
            startScale, endScale,  // Start and end values for the X axis scaling
            startScale, endScale,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling

        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 250
        v.startAnimation(anim)
    }
    fun disableView( view:View, isDisabled:Boolean){

        if(isDisabled){
            view.alpha=0.5f
            view.isEnabled=false
            Log.e("disableView","view esta deshabilitado")
        }else{
            view.alpha=1f
            view.isEnabled=true
            Log.e("disableView","view esta habilitado")
        }
    }

    fun animateCamera(context: Context,view: ImageView,color: String,isDisabled: Boolean){
        var rAjayu=0
        when(color){
            "white"->rAjayu=ContextCompat.getColor(context,R.color.white)
            "red"->rAjayu=ContextCompat.getColor(context,R.color.colorRojoAjayu)
            "green"->rAjayu=ContextCompat.getColor(context,R.color.colorVerdeAjayu)
        }
        when(isDisabled){
            true->scaleView(view,1f,1.25f)
            false->scaleView(view,1.25f,1f)
        }
        disableView(view,isDisabled)
        ImageViewCompat.setImageTintList(view , ColorStateList.valueOf(rAjayu))
    }

    fun showHideViews(view: View,float: Float,translation:String){
        ObjectAnimator.ofFloat(view,translation,float).apply {
            if (float!=0f){
                view.animate().apply {
                    scaleX(0f)
                    duration=200
                    start()
                }
            }else{
                view.animate().apply {
                    scaleX(1f)
                    duration=200
                    start()
                }
            }
            duration=200
            start()
        }
    }
    fun showAlphaViews(view: View,alphaA: Float,alphaB: Float){
        ObjectAnimator.ofFloat(view,"alpha",alphaA,alphaB).apply {
            duration=3000
            start()
        }
    }

}