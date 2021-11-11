package com.jawaadianinc.rubixcubesolver

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.renderscript.*
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import java.io.IOException

/**
 * Created by development on 8/15/17.
 */
class CameraPreview(context: Context?, camera: Camera) : SurfaceView(context),
    SurfaceHolder.Callback, PreviewCallback {
    var camera: Camera
    var previewSize: Camera.Size? = null

    //private boolean surfaceWasDestroyed;
    var supportedPreviewSizes: List<Camera.Size>?
    var camImageWidth = 0
    var camImageHeight = 0
    private val surfaceHolder: SurfaceHolder
    private var data: ByteArray? = null
    private val pixelHSVs: Array<Array<Array<FloatArray>>>
    private var previewBitmaps: Array<Bitmap?>?
    private var side = 0
    private var centerX = 0
    private var centerY = 0
    private var startX = 0
    private var startY = 0
    private var cubieSideLength = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("Surface Measured", "TRUE")
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        previewBitmaps = null
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val params = camera.parameters
        camImageWidth = params.previewSize.width
        camImageHeight = params.previewSize.height
        this.data = data
    }

    /* Callback that is called when the surface is created or orientation changes */
    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Log.d("Surface Created", "TRUE")
        try {
            camera.setPreviewDisplay(surfaceHolder)
            camera.setDisplayOrientation(90)
            camera.startPreview()
        } catch (e: IOException) {
            Log.d(ContentValues.TAG, "Error setting camera preview: " + e.message)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Took care of releasing the Camera preview in activity.
        Log.d("Surface Destroyed", "TRUE")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (surfaceHolder.surface == null) {
            // preview surface does not exist
            Log.d("Surface Changed", "FALSE")
            return
        } else {
            Log.d("Surface Changed", "TRUE")
        }

        // stop preview before making changes
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        val parameters = camera.parameters
        parameters.setPreviewSize(previewSize!!.width, previewSize!!.height)
        val focusModes = parameters.supportedFocusModes
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            Log.d("Supports contin focus", "TRUE")
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }
        Log.d(ContentValues.TAG, "width: " + previewSize!!.width)
        Log.d(ContentValues.TAG, "height: " + previewSize!!.height)
        camera.parameters = parameters

        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder)
            camera.setDisplayOrientation(90)
            camera.startPreview()
            camera.setPreviewCallback(this)
            invalidate()
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "Error starting camera preview: " + e.message)
        }
    }

    fun setSide(side: Int) {
        this.side = side
    }

    fun setGridPositions(vararg values: Int) {
        if (values.size == 5) {
            centerX = values[0]
            centerY = values[1]
            startX = values[3]
            startY = values[2]
            cubieSideLength = values[4]
        }
    }

    fun saveCurrentBitmap(side: Int) {
        // Log.d("Width", "" + camImageWidth);
        //Log.d("Height", "" + camImageHeight);

        //The RenderScript way
        val bitmap = Bitmap.createBitmap(camImageWidth, camImageHeight, Bitmap.Config.ARGB_8888)
        val bitmapData = renderScriptNV21ToRGBA888(
            context,
            camImageWidth,
            camImageHeight,
            data
        )
        Log.d("Data null", "" + (data == null))
        Log.d("Bitmap null", "" + (bitmap == null))
        Log.d("Side", "" + side)
        bitmapData.copyTo(bitmap)
        Log.d("Width", "" + bitmap!!.width)
        Log.d("Height", "" + bitmap.height)
        val y = startY
        val x = startX
        var j = 0
        while (j < 3) {
            var k = 0
            while (k < 3) {
                val colorHSV = FloatArray(3)
                Color.colorToHSV(
                    bitmap
                        .getPixel(
                            (startX + 0.5 * cubieSideLength).toInt(),
                            (startY + 0.5 * cubieSideLength).toInt()
                        ),
                    colorHSV
                )
                pixelHSVs[side][j][k][0] = colorHSV[0]
                pixelHSVs[side][j][k][1] = colorHSV[1]
                pixelHSVs[side][j][k][2] = colorHSV[2]
                k++
                startY += cubieSideLength
            }
            startY = y
            j++
            startX += cubieSideLength
        }
        startY = y
        startX = x
        Toast.makeText(
            context, "Picture captured! Select another side.",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun resolveColors(): Array<Array<CharArray>> {
        //First check if any of the Bitmaps are null, can't do comparison
        //TODO: Check if colors inputs complete
        val indexColors = charArrayOf('R', 'Y', 'G', 'B', 'O', 'W')
        val centerColors = arrayOfNulls<FloatArray>(6)
        for (i in centerColors.indices) {
            //Copy the appropriate center's color into the array
            centerColors[i] = pixelHSVs[i][1][1]
            Log.d("Center Hue " + indexColors[i], "" + centerColors[i]!![0])
            Log.d("Center Saturation " + indexColors[i], "" + centerColors[i]!![1])
            Log.d("Center Value " + indexColors[i], "" + centerColors[i]!![2])
        }
        val colors = Array(6) { Array(3) { CharArray(3) } }
        for (i in pixelHSVs.indices) {
            for (j in 0..2) {
                for (k in 0..2) {
                    val colorHSV = pixelHSVs[i][j][k]
                    val hue = colorHSV[0]
                    var color = indexColors[i]
                    //Do not change the color if it is a center color
                    if (j == 1 && k == 1) {
                        colors[i][k][j] = color
                        Log.d("$i, $j, $k", " $color")
                    } else if (colorHSV[1] < 0.3) {
                        //If saturation is very low, it's most likely white
                        colors[i][k][j] = 'W'
                        Log.d("$i, $j, $k", " " + 'W')
                    } else {
                        var minDiff = Math.abs(hue - centerColors[i]!![0]).toInt()
                        for (l in centerColors.indices) {
                            val diff = Math.abs(hue - centerColors[l]!![0]).toInt()
                            if (diff < minDiff) {
                                minDiff = diff
                                color = indexColors[l]
                            }
                        }
                        colors[i][k][j] = color
                        Log.d("$i, $j, $k", " $color")
                    }
                }
            }
        }
        return colors
    }

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w
        if (sizes == null) return null
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    private fun renderScriptNV21ToRGBA888(
        context: Context,
        width: Int,
        height: Int,
        nv21: ByteArray?
    ): Allocation {
        val rs = RenderScript.create(context)
        val yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
        val yuvType = Type.Builder(rs, Element.U8(rs)).setX(
            nv21!!.size
        )
        val `in` = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)
        val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height)
        val out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)
        `in`.copyFrom(nv21)
        yuvToRgbIntrinsic.setInput(`in`)
        yuvToRgbIntrinsic.forEach(out)
        return out
    }

    init {
        Log.d("Surface Constructed", "TRUE")
        this.camera = camera
        supportedPreviewSizes = camera.parameters.supportedPreviewSizes
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
        // deprecated setting, but required on Android versions < 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        previewBitmaps = arrayOfNulls(6)
        pixelHSVs = Array(6) { Array(3) { Array(3) { FloatArray(3) } } }
    }
}