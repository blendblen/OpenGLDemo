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
        -0.5f, 0.5f, 0.0f, // 左上角
        -0.5f, -0.5f, 0.0f, // 左下角
        0.5f, -0.5f, 0.0f, // 右下角
        0.5f, 0.5f, 0.0f // 右上角
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
    private val vboIds = IntArray(1) // vbo
    private val eboIds = IntArray(1) // ebo

    private var indics = intArrayOf(0, 1, 2, 2, 3, 0)

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

        // vbo使用 vbo需要在link之后使用
        GLES30.glGenBuffers(1, vboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, bb.capacity(), bb, GLES30.GL_STATIC_DRAW)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)


        var idxBuffer = ByteBuffer.allocateDirect(indics.size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
        idxBuffer.put(indics).position(0)
        // ebo使用
        GLES30.glGenBuffers(1, eboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIds[0])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, idxBuffer.capacity() * 4, idxBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)




    }

    fun draw() {
        // 使用program
        GLES30.glUseProgram(mProgram)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIds[0])

        // 将数据传递给shader
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")
        GLES30.glEnableVertexAttribArray(mPositionHandle)
//        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, 0)

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor")
        GLES30.glUniform4fv(mColorHandle, 1, color, 0)

        mTMatrixHandle = GLES30.glGetUniformLocation(mProgram, "mTMatrix")
        GLES30.glUniformMatrix4fv(mTMatrixHandle, 1, false, triangleMatrix, 0)

        // draw array， 绘制三角形
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangleCoords.size / 3)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indics.size, GLES30.GL_UNSIGNED_INT, 0)
        GLES30.glDisableVertexAttribArray(mPositionHandle)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)



    }
}