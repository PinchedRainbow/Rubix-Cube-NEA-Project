package com.jawaadianinc.rubixcubesolver

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.hardware.Camera
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Created by development on 8/16/17.
 */
class CaptureCubeActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    var preview: CameraPreview? = null

    //int values used to store where the grid is on-screen
    //passed onto the preview frame for processing cube's colors
    private var centerX = 0
    private var centerY = 0
    private var startX = 0
    private var startY = 0
    private var cubeSideLength = 0
    private var cubieSideLength = 0

    //These views are saved for animation purposes
    var instructions: TextView? = null
    var toggleInstructions: ImageView? = null
    private var camera: Camera? = null
    private var hasCamera = false
    private var transparentView: SurfaceView? = null
    private var focusHolder: SurfaceHolder? = null

    //Value used to store which side's picture is being taken
    private var side = 0
    private var showingInstructions = false
    private var animTime = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_capture_cube)
        hasCamera = checkCameraHardware(this)
        val sideOptions: Spinner = findViewById(R.id.side_options)
        val spinnerAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.cube_capture_sides, R.layout.spinner_item
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        sideOptions.adapter = spinnerAdapter
        sideOptions.onItemSelectedListener = this
        transparentView = findViewById(R.id.grid_view)
        transparentView!!.setZOrderOnTop(true)
        focusHolder = transparentView!!.holder
        focusHolder!!.setFormat(PixelFormat.TRANSPARENT)
        focusHolder!!.addCallback(this)
        focusHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        instructions = findViewById<TextView>(R.id.instructions)
        toggleInstructions = findViewById<ImageView>(R.id.toggle_instructions)
        showingInstructions = false
        animTime = resources.getInteger(
            android.R.integer.config_shortAnimTime
        )
        instructions!!.setOnClickListener(this)
        toggleInstructions!!.setOnClickListener(this)
//        findViewById(R.id.instructions_layout).setOnClickListener(this)
//        findViewById(R.id.take_picture).setOnClickListener(this)
//        findViewById(R.id.solve_cube).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (hasCamera && camera == null) {
            camera = getCameraInstance()
            if (camera != null) {
                preview = CameraPreview(this, camera!!)
                val container: FrameLayout =
                    findViewById<FrameLayout>(R.id.camera_preview_container)
                container.addView(preview, 0)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (camera != null) {
            //Will prevent the surfaceCreated() callback from taking place
            //and prevent trying to use the released camera
            preview!!.holder.removeCallback(preview)
            //prevent onPreviewFrame() from being called after camera released
            preview!!.camera.setPreviewCallback(null)
            camera!!.stopPreview()
            camera!!.release()
            camera = null
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.instructions_layout || id == R.id.instructions || id == R.id.toggle_instructions) {
            showingInstructions = if (showingInstructions) {
                instructions?.animate()
                    ?.alpha(0f) //.translationY(-instructions.getHeight())
                    ?.setDuration(animTime.toLong())
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animator: Animator) {
                            instructions!!.visibility = View.INVISIBLE
                            transparentView?.visibility = View.VISIBLE
                        }
                    })
                toggleInstructions!!.animate()
                    .rotation(0f)
                    .setDuration(animTime.toLong())
                    .setListener(null)
                false
            } else {
                instructions?.alpha = 0f
                instructions?.visibility = View.VISIBLE
                instructions?.animate()
                    ?.alpha(1f) //.translationY(0)
                    ?.setDuration(animTime.toLong())
                    ?.setListener(null)
                toggleInstructions!!.animate()
                    .rotation(180f)
                    .setDuration(animTime.toLong())
                    .setListener(null)
                transparentView?.visibility = View.INVISIBLE
                true
            }
            return
        }
        when (id) {
            R.id.take_picture -> preview!!.saveCurrentBitmap(side)
            R.id.solve_cube -> {
                val colors = preview!!.resolveColors()
                val colorInputIntent = Intent(
                    applicationContext,
                    MainActivity::class.java
                )
                val args = Bundle()
                args.putString(MainActivity.INITIAL_INPUT_TYPE, MainActivity.CAMERA_INPUT)
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_LEFT,
                    packageSide(colors[0])
                )
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_RIGHT,
                    packageSide(colors[4])
                )
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_UP,
                    packageSide(colors[1])
                )
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_DOWN,
                    packageSide(colors[5])
                )
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_FRONT,
                    packageSide(colors[2])
                )
                args.putCharArray(
                    MainActivity.COLORS_INPUTTED_BACK,
                    packageSide(colors[3])
                )
                colorInputIntent.putExtra(MainActivity.ALL_COLORS_INPUTTED, args)
                colorInputIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                startActivity(colorInputIntent)
                finish()
            }
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, id: Long) {
        val side: Char = adapterView.getItemAtPosition(pos).toString()
            .trim { it <= ' ' }[0]
        when (side) {
            'R' -> this.side = 0
            'Y' -> this.side = 1
            'G' -> this.side = 2
            'B' -> this.side = 3
            'O' -> this.side = 4
            else -> this.side = 5 //case 'W'
        }
        if (preview != null) {
            preview!!.setSide(this.side)
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {}
    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        val canvas: Canvas = surfaceHolder.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = ContextCompat.getColor(this, R.color.colorAccent)
        strokePaint.strokeWidth = 3f
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        centerX = metrics.widthPixels / 2
        centerY = metrics.heightPixels / 2
        cubeSideLength = metrics.widthPixels * 7 / 10
        cubieSideLength = cubeSideLength / 3
        startX = centerX - cubeSideLength / 2
        startY = centerY - cubeSideLength / 2
        var x = startX
        while (x < startX + cubeSideLength) {
            var y = startY
            while (y < startY + cubeSideLength) {
                canvas.drawRect(
                    x.toFloat(), y.toFloat(), (
                            x + cubieSideLength).toFloat(), (
                            y + cubieSideLength).toFloat(),
                    strokePaint
                )
                y += cubieSideLength
            }
            x += cubieSideLength
        }
        preview!!.setGridPositions(centerX, centerY, startX, startY, cubieSideLength)
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}

    /**
     * Check if this device has a camera
     */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun packageSide(colors: Array<CharArray>): CharArray {
        val packageArray = CharArray(9)
        for (i in 0..2) {
            for (j in 0..2) {
                packageArray[i * 3 + j] = colors[i][j]
            }
        }
        return packageArray
    }

    companion object {
        fun getCameraInstance(): Camera? {
            var c: Camera? = null
            try {
                c = Camera.open()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return c
        }
    }
}