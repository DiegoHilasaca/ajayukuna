package com.ajayu.newproyect.otros

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View


class DrawLineCanvas : View {
    private var c: Canvas=Canvas()
    private var pLine: Paint = Paint()
    private var touchPath: Path

    constructor(context: Context?) : super(context) {
        pLine.color = Color.RED
        pLine.isAntiAlias = true
        pLine.style = Paint.Style.STROKE
        pLine.strokeWidth = 8f
        touchPath = Path()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchPath.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> touchPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                touchPath.lineTo(touchX, touchY)
                c.drawPath(touchPath, pLine)
                //touchPath = Path()
            }
            else -> return false
        }
        invalidate()
        return true
    }
    fun limpiar(){
        touchPath.reset()
        c.drawPath(touchPath, pLine)
        touchPath = Path()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(touchPath, pLine)
    }
}