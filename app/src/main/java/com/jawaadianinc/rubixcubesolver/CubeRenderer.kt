package com.jawaadianinc.rubixcubesolver

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = TouchSurfaceView(this)
        setContentView(mGLSurfaceView)
        (mGLSurfaceView as TouchSurfaceView).requestFocus()
        (mGLSurfaceView as TouchSurfaceView).isFocusableInTouchMode = true

        Toast.makeText(this, "Press back button to exit", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView!!.onPause()
    }

    private var mGLSurfaceView: GLSurfaceView? = null
}

internal class TouchSurfaceView(context: Context?) : GLSurfaceView(context) {
    override fun onTrackballEvent(e: MotionEvent): Boolean {
        val TRACKBALL_SCALE_FACTOR = 36.0f
        mRenderer.mAngleX += e.x * TRACKBALL_SCALE_FACTOR
        mRenderer.mAngleY += e.y * TRACKBALL_SCALE_FACTOR
        requestRender()
        return true
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y
        if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = x - mPreviousX
            val dy = y - mPreviousY
            val TOUCH_SCALE_FACTOR = 180.0f / 320
            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR
            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR
            requestRender()
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    private class CubeRenderer : Renderer {
        override fun onDrawFrame(gl: GL10) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
            gl.glLoadIdentity()
            gl.glTranslatef(0f, 0f, -10.0f)
            gl.glRotatef(mAngleX, 0f, 1f, 0f)
            gl.glRotatef(mAngleY, 1f, 0f, 0f)
            mCube.draw(gl)
            gl.glLoadIdentity()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            gl.glViewport(0, 0, width, height)
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            GLU.gluPerspective(gl, 120.0f, width.toFloat() / height.toFloat(), 0.1f, 500.0f)
            gl.glViewport(0, 0, width, height)
            gl.glMatrixMode(GL10.GL_MODELVIEW)
            gl.glLoadIdentity()
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f)
            gl.glClearDepthf(1.0f)
            gl.glEnable(GL10.GL_DEPTH_TEST)
            gl.glDepthFunc(GL10.GL_LEQUAL)
            gl.glHint(
                GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST
            )
        }

        private val mCube: Cube2 = Cube2()
        var mAngleX = 0f
        var mAngleY = 0f

    }

    private val mRenderer: CubeRenderer = CubeRenderer()
    private var mPreviousX = 0f
    private var mPreviousY = 0f

    init {
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}