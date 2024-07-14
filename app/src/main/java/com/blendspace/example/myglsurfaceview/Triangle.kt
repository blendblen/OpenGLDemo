package com.blendspace.example.myglsurfaceview

import android.opengl.GLES30
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author xiang.cheng
 * @date 2024/7/13
 */
class Triangle {
    private var mProgram: Int = 0
    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0
    private val vertexShaderCode = "" +
            "uniform mat4 mTMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "   gl_Position = mTMatrix * vPosition;" +
            "}"

    private val triangleCoords = floatArrayOf(
        0.0f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    private var triangleMatrix = FloatArray(16)

    private val fragmentShaderCode = "" +
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "   gl_FragColor = vColor;" +
            "}"
    private val color = floatArrayOf(0.5f, 0.5f, 0.5f, 1.0f)

    private lateinit var vertexBuffer: FloatBuffer
    private var mTMatrixHandle: Int = 0;

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        return shader
    }


    constructor() {
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
        Matrix.setIdentityM(triangleMatrix, 0)
//        Matrix.translateM(triangleMatrix, 0, 0.5f, 0f, 0f)
        Matrix.scaleM(triangleMatrix, 0, 0.5f, 0.5f, 1.0f)


        // 创建shader，并为其指定源码
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES30.glCreateProgram()
        GLES30.glAttachShader(mProgram, vertexShader)
        GLES30.glAttachShader(mProgram, fragmentShader)

        GLES30.glLinkProgram(mProgram)

    }

    fun draw() {
        // 使用program
        GLES30.glUseProgram(mProgram)


        // 将数据传递给shader
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")
        GLES30.glEnableVertexAttribArray(mPositionHandle)
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor")
        GLES30.glUniform4fv(mColorHandle, 1, color, 0)

        mTMatrixHandle = GLES30.glGetUniformLocation(mProgram, "mTMatrix")
        GLES30.glUniformMatrix4fv(mTMatrixHandle, 1, false, triangleMatrix, 0)

        // draw array， 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangleCoords.size / 3)
        GLES30.glDisableVertexAttribArray(mPositionHandle)



    }
}