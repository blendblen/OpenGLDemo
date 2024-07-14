package com.blendspace.example.myglsurfaceview

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author xiang.cheng
 * @date 2024/7/13
 */
class MyGLSurfaceView: GLSurfaceView {
    constructor(context: Context) : super(context) {
        setEGLContextClientVersion(3)
        setRenderer(MyGLRender())
    }

    class MyGLRender: Renderer {

        private lateinit var mTriangle: Triangle
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            mTriangle = Triangle()

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            mTriangle.draw()
            Log.d(TAG, "onDrawFrame: ")
        }

    }

    companion object {
        private const val TAG = "MyGLSurfaceView"
    }
}