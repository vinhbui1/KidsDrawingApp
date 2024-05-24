package eu.on.screen

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import eu.on.screen.model.EraseData
import kotlin.math.abs


class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? =
        null // An variable of CustomPath inner class to use it further.
    private var mCanvasBitmap: Bitmap? = null // An instance of the Bitmap.

    private var mDrawPaint: Paint? =
        null // The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private var mCanvasPaint: Paint? = null // Instance of canvas paint view.

    private var mBrushSize: Float =
        0.toFloat() // A variable for stroke/brush size to draw on the canvas.
    private var mBrushSizeShape: Float =
        10.toFloat() // A variable for stroke/brush size to draw on the canvas.

    private var mBrushSizeErase: Float =
        20f
    private val erasePaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 20f
         xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    // A variable to hold a color of the stroke.
    private var color = Color.WHITE
    private var typeDrawing = 4
    private var initialCircleRadius = 0f
    private var distance = 0f
    private  var listEarse = mutableListOf<EraseData>()
    private  var listEraseRedo = mutableListOf<EraseData>()

    var startX = 0.0F// Touch event of X coordinate
    var startY = 0.0F // touch event of Y coordinate


    private var canvas: Canvas? = null

    private val mPaths = ArrayList<CustomPath>() // ArrayList for Paths

    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE // This is to draw a STROKE style
        mDrawPaint?.strokeJoin = Paint.Join.ROUND // This is for store join
        mDrawPaint?.strokeCap = Paint.Cap.ROUND // This is for stroke Cap

        mCanvasPaint = Paint(Paint.DITHER_FLAG) // Paint flag that enables dithering when blitting.

    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        // Measure the width and height of the view
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = MeasureSpec.getSize(heightMeasureSpec)
//        Log.e("231", width.toString() + "width")
//        Log.e("231", height.toString())
//
//        // Set the measured dimensions to maintain aspect ratio or customize as needed
//        setMeasuredDimension(width, height)
//    }

//    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
//        super.onSizeChanged(w, h, wprev, hprev)
//
//        Log.e("231", "size change carll sercond")
//
//        // Recreate the bitmap and canvas with the new width and height
//        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//        canvas = Canvas(mCanvasBitmap!!)
//    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        val width =  Resources.getSystem().displayMetrics.widthPixels;
//        val height =  Resources.getSystem().displayMetrics.heightPixels;
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
////            Log.e("231", width.toString())
////            Log.e("231", height.toString())
////
////            Log.e("231", "ngang")
//
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//           // Log.e("231", "doc")
////            val width =  Resources.getSystem().displayMetrics.widthPixels;
////            val height =  Resources.getSystem().displayMetrics.heightPixels;
////            Log.e("231", width.toString())
////            Log.e("231", height.toString())
//        }
//        Log.e("231", width.toString() + "width")
//        Log.e("231", height.toString()+ "height")
//        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        canvas = Canvas(mCanvasBitmap!!)
//        // Request a layout pass to ensure that the view gets redrawn with the new dimensions
//        requestLayout()
//    }


    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }



        for ((index,p) in mPaths.withIndex()) {
            mDrawPaint?.strokeWidth = p.brushThickness
            mDrawPaint?.color = p.color
            var findErase : Boolean = false
            for (erase in listEarse){

                if (erase.customPath === p){

                    findErase  = true
                    erasePaint.strokeWidth  = erase.size
                    }
            }
                canvas.drawPath(p, if (findErase) erasePaint else mDrawPaint!!)
                //ve lai cac muc da ve
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            erasePaint!!.strokeWidth = mBrushSizeErase
            canvas.drawPath(mDrawPath!!, if (typeDrawing == 5) erasePaint else mDrawPaint!!)
        }
    }

    /**
     * This method acts as an event listener when a touch
     * event is detected on the device.
     */

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // Touch event of X coordinate
        val touchY = event.y // touch event of Y coordinate

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                mDrawPaint!!.strokeWidth = mBrushSizeShape
                mDrawPath!!.color = color
                startX = event.x
                startY = event.y
                when (typeDrawing) {
                    1 -> {
                        mDrawPath = CustomPath(
                            color,
                            mBrushSizeShape
                        ) // Create a new path for drawing lines
                        mDrawPath!!.reset()
                        mDrawPath!!.moveTo(startX, startY)
                    }

                    2 -> {
                        initialCircleRadius = event.x
                        mDrawPath!!.brushThickness = mBrushSizeShape

                    }

                    3 -> {
                        mDrawPath!!.brushThickness = mBrushSizeShape
                    }

                    4,5 -> {
                        mDrawPaint!!.strokeWidth = mBrushSize
                        mDrawPath!!.brushThickness = mBrushSize
                        mDrawPath!!.reset() // Clear any lines and curves from the path, making it empty.
                        mDrawPath!!.moveTo(
                            touchX,
                            touchY
                        ) // Set the beginning of the next contour to the point (x,y).
                    }
                }

            }

            MotionEvent.ACTION_MOVE -> {

                when (typeDrawing) {
                    1 -> {
                        // Clear the path and redraw the straight line from startX to touchX
                        mDrawPath!!.reset()
                        mDrawPath!!.moveTo(startX, startY)
                        mDrawPath!!.lineTo(touchX, touchY)
                        invalidate()
                    }

                    2 -> {
                        mDrawPath?.reset()
                        distance = abs(initialCircleRadius - event.x)
                        mDrawPath!!.addCircle(touchX, touchY, distance, Path.Direction.CW)
                    //    mPaths.add(mDrawPath!!)
                        invalidate()

                    }

                    3 -> {
                        mDrawPath?.reset()
                        mDrawPath!!.addRect(startX, startY, event.x, event.y, Path.Direction.CW)
                      //  mPaths.add(mDrawPath!!)
                        invalidate()

                    }

                    4,5 -> {

                        mDrawPath!!.lineTo(
                            touchX,
                            touchY
                        )
                    }
                }

            }

            MotionEvent.ACTION_UP -> {

                when (typeDrawing) {
                    1 -> {
                        mPaths.add(mDrawPath!!)
                        mDrawPath = CustomPath(color, mBrushSizeShape)
                    }

                    2 -> {
                        mPaths.add(mDrawPath!!)
                        mDrawPath = CustomPath(color, mBrushSizeShape)
                        invalidate()
                    }

                    3 -> {
                        mPaths.add(mDrawPath!!)
                        mDrawPath = CustomPath(color, mBrushSizeShape)
                        invalidate()
                    }

                    4,5 -> {
                        if(typeDrawing == 5){
                            listEarse?.add(
                                EraseData(
                                    mDrawPath!!,
                                    mBrushSizeErase
                                )
                            )
                        }
                        mPaths.add(mDrawPath!!) //Add when to stroke is drawn to canvas and added in the path arraylist
                        mDrawPath = CustomPath(color, mBrushSize)

                    }
                }
            }

            else -> return false
        }

        invalidate()
        return true
    }

    /**
     * This method is called when either the brush or the eraser
     * sizes are to be changed. This method sets the brush/eraser
     * sizes to the new values depending on user selection.
     */
    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setSizeForBrushShape(newSize: Float) {
        mBrushSizeShape = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth = mBrushSizeShape
    }
    fun setSizeForBrushErase(newSize: Float) {
        mBrushSizeErase = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        erasePaint!!.strokeWidth = mBrushSizeErase
    }
    /**
     * This function is called when the user desires a color change.
     * This functions sets the color of a store to selected color and able to draw on view using that color.
     *
     * @param newColor
     */
    fun setColor(newColor: Int) {
        val alpha = Color.alpha(newColor)
        val red = Color.red(newColor)
        val green = Color.green(newColor)
        val blue = Color.blue(newColor)

        color = Color.argb(alpha, red, green, blue)
        mDrawPaint!!.color = color
    }
    fun clearAllDrawings() {
        mPaths.clear() // Clear the main drawing paths
        listEarse.clear() // Clear the eraser data
        listEraseRedo.clear() // Clear the redo eraser data
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // Clear the canvas
        invalidate() // Refresh the view
    }
    fun setShapeType(typeShape: Int) {
        typeDrawing = typeShape
        // 1 is line
        // 2 is Circle
        // 3 is Rectangle
    }
    fun onClickUndo() {
        if (mPaths.size > 0) {
            val remove = listEarse
                .filter { it.customPath!! == mPaths[mPaths.size - 1]
                }.map {
                    listEraseRedo.add(it)
                    listEarse.remove(it)
                }
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }

    fun onClickRedo(){

        if(mUndoPaths.size > 0){

                val remove = listEraseRedo
                    .filter { it.customPath == mUndoPaths[mUndoPaths.size - 1]
                    }.map {
                        listEarse.add(it)
                        listEraseRedo.remove(it)
            }
            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible

        }
    }

    inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}