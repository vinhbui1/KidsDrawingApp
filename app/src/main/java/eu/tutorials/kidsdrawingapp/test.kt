package eu.tutorials.kidsdrawingapp

import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import top.defaults.colorpicker.ColorPickerPopup;


class DrawTestService : Service() {
    private var drawingView: DrawingView? = null
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    var height: Int? = null
    var hideDraw: Boolean = false
    var isAllFabsVisible: Boolean? = null
    var mPenFab: FloatingActionButton? = null
    var mPickColorFab: FloatingActionButton? = null
    var mPickSharpFab: FloatingActionButton? = null
    var mEarseFab: FloatingActionButton? = null
    var mUndoFab: FloatingActionButton? = null
    var mRedoFab: FloatingActionButton? = null
    var mDelete: FloatingActionButton? = null
    var mHide: FloatingActionButton? = null
    var mExits: FloatingActionButton? = null
    var isMoving = false

    var mAddFab: FloatingActionButton? = null
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var fab_clock: Animation? = null
    private var fab_anticlock: Animation? = null
    private var initialX: Float = 0.0f
    private var initialY: Float = 0.0f
    private var initialTouchX: Float = 0.0f
    private var initialTouchY: Float = 0.0f
    private var mImageButtonCurrentPaint: ImageButton? =
        null // A variable for current color is picked from color pallet.

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "channel1"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay notification",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("adsf")
                .setContentText("asdf1")
                .setSmallIcon(R.drawable.ic_brush)
                .build()
            startForeground(1, notification)
        }

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_button, null)
        drawingView = mFloatingView!!.findViewById(R.id.drawing_view)
        mAddFab = mFloatingView!!.findViewById(R.id.add_fab)


        // FAB button
        mPenFab = mFloatingView!!.findViewById(R.id.add_alarm_fab)
        mPickColorFab = mFloatingView!!.findViewById(R.id.add_person_fab)
        mPickSharpFab = mFloatingView!!.findViewById(R.id.btn_pick_sharp)
        mEarseFab = mFloatingView!!.findViewById(R.id.btn_erase)
        mUndoFab = mFloatingView!!.findViewById(R.id.btn_undo)
        mRedoFab = mFloatingView!!.findViewById(R.id.btn_redo)
        mDelete = mFloatingView!!.findViewById(R.id.btn_delete)
        mHide = mFloatingView!!.findViewById(R.id.btn_hide)
        mExits = mFloatingView!!.findViewById(R.id.btn_left)

        mPenFab?.visibility = View.GONE
        mPickColorFab?.visibility = View.GONE
        mPickSharpFab?.visibility = View.GONE
        mEarseFab?.visibility = View.GONE
        mUndoFab?.visibility = View.GONE
        mRedoFab?.visibility = View.GONE
        mDelete?.visibility = View.GONE
        mHide?.visibility = View.GONE
        mExits?.visibility = View.GONE
        isAllFabsVisible = false

        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_click);
        fab_anticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_antiblock);

        //   mAddFab!!.shrink()
        drawingView?.setSizeForBrush(20.toFloat())
        val desiredWidth = WindowManager.LayoutParams.WRAP_CONTENT
        val desiredHeight = WindowManager.LayoutParams.WRAP_CONTENT
        var window = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = window?.currentWindowMetrics?.bounds

        val fullWidth = displayMetrics?.width()
        val fullHeight = displayMetrics?.height()
        val params = WindowManager.LayoutParams(
            desiredWidth,
            desiredHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        Log.e("test", desiredHeight.toString())

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0 // Left offset
        params.y = 0 // Top offset
        mAddFab!!.setOnClickListener {
            isAllFabsVisible = if (!isAllFabsVisible!!) {

                mPickColorFab!!.show()
                mPenFab!!.show()
                mPickSharpFab!!.show()
                mEarseFab!!.show()
                mUndoFab!!.show()
                mRedoFab!!.show()
                mDelete!!.show()
                mHide!!.show()
                mExits!!.show()
                mPickColorFab?.startAnimation(fab_open);
                mPenFab?.startAnimation(fab_open);
                mAddFab?.startAnimation(fab_anticlock);
                mPickSharpFab?.startAnimation(fab_open);
                mEarseFab?.startAnimation(fab_open);
                mUndoFab?.startAnimation(fab_open);
                mRedoFab?.startAnimation(fab_open);
                mDelete?.startAnimation(fab_open);
                mHide?.startAnimation(fab_open);
                mExits?.startAnimation(fab_open);
                // Now extend the parent FAB, as
                // user clicks on the shrinked
                // parent FAB
                //       mAddFab!!.extend()

                // make the boolean variable true as
                // we have set the sub FABs
                // visibility to GONE
                true
            } else {
                
                mPenFab!!.hide()
                mPickColorFab!!.hide()
                mPickSharpFab!!.hide()
                mEarseFab!!.hide()
                mUndoFab!!.hide()
                mRedoFab!!.hide()
                mDelete!!.hide()
                mHide!!.hide()
                mExits!!.hide()
                mPenFab?.startAnimation(fab_close)
                mPickColorFab?.startAnimation(fab_close)
                mAddFab?.startAnimation(fab_clock)
                mPickSharpFab?.startAnimation(fab_close)
                mEarseFab?.startAnimation(fab_close)
                mUndoFab?.startAnimation(fab_close)
                mRedoFab?.startAnimation(fab_close)
                mDelete?.startAnimation(fab_close)
                mHide?.startAnimation(fab_close)
                mExits?.startAnimation(fab_close)

                false
            }
        }

        mPickColorFab!!.setOnClickListener { v ->
            ColorPickerPopup.Builder(this@DrawTestService).initialColor(
                Color.RED
            )
                .enableBrightness(
                    true
                ) // enable color brightness
                // slider or not
                .enableAlpha(
                    true
                ) // enable color alpha
                // changer on slider or
                // not
                .okTitle(
                    "Choose"
                ) // this is top right
                // Choose button
                .cancelTitle(
                    "Cancel"
                ) // this is top left
                // Cancel button which
                // closes the
                .showIndicator(
                    true
                ) // this is the small box
                .build()
                .show(
                    v,
                    object : ColorPickerPopup.ColorPickerObserver() {
                        override fun onColorPicked(color: Int) {

                            //    mDefaultColor = color
                            Log.e("color", color.toString())

                            //       mColorPreview.setBackgroundColor(mDefaultColor)
                        }
                    })
        }


        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mPenFab!!.setOnClickListener {
            Toast.makeText(
                this@DrawTestService,
                "Alarm Added",
                Toast.LENGTH_SHORT
            ).show()
        }

        mPenFab!!.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        mHide!!.setOnClickListener {
            // This is for undo recent stroke.
            val intent = Intent("action.hideDraw")
            hideDraw = !hideDraw
            intent.putExtra("hideDraw", hideDraw)
            sendBroadcast(intent)
            // mFloatingView?.visibility = View.INVISIBLE
            //    drawingView?.onClickUndo()
        }


        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        mAddFab!!.setOnLongClickListener {
            val gestureListener = View.OnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        if (!isMoving) {
                            initialX = view.x
                            initialY = view.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            isMoving = true
                            // Apply the animation for scale-up
                            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
                            mAddFab!!.startAnimation(scaleAnimation)
                        }
                        val offsetX = event.rawX - initialTouchX
                        val offsetY = event.rawY - initialTouchY

                        params.x = (initialX + offsetX).toInt()
                        params.y = (initialY + offsetY).toInt()
                        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_animation)
                        mFloatingView!!.startAnimation(animation)
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                    }

                    MotionEvent.ACTION_UP -> {
                        // Apply the animation for scale-down
                        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down)
                        mAddFab!!.startAnimation(scaleAnimation)
                        if (event.rawX < fullWidth / 2) {
                            params.x = 10
                        } else {
                            params.x = fullWidth
                        }
                        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_animation)
                        mFloatingView!!.startAnimation(animation)
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        mAddFab!!.setOnTouchListener(null)
                    }
                    else -> {
                        // Reset the flag for other touch events
                        isMoving = false
                    }
                }
                true
            }

            // Attach the gestureListener to handle touch movement
            mAddFab!!!!.setOnTouchListener(gestureListener)
            true
        }
        mWindowManager!!.addView(mFloatingView, params)
    }

    private fun showBrushSizeChooserDialog() {

        val brushDialog = Dialog(this@DrawTestService)
        brushDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        brushDialog.setContentView(R.layout.brush_size_dialog)
        val seekBarBrushSize = brushDialog.findViewById<SeekBar>(R.id.seekBarBrushSize)
        // Load saved SeekBar value from shared preferences
        // Load saved SeekBar value from shared preferences
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        var savedSeekBarValue: Int = sharedPreferences.getInt("seekBarValue", 10)
        seekBarBrushSize.progress = savedSeekBarValue
        val buttonApply = brushDialog.findViewById<Button>(R.id.buttonApply)
        val buttonCancel = brushDialog.findViewById<Button>(R.id.buttonCancel)
        val editor = sharedPreferences.edit()
        brushDialog.setTitle("Brush size :${seekBarBrushSize.progress}")

        seekBarBrushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                brushDialog.setTitle("Brush size :$p1")

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        buttonApply.setOnClickListener {
            val selectedBrushSize = seekBarBrushSize.progress
            drawingView?.setSizeForBrush(selectedBrushSize.toFloat())

            val intent = Intent("action.setSize")
            intent.putExtra("setSize", selectedBrushSize)
            sendBroadcast(intent)
            editor.putInt("seekBarValue", selectedBrushSize)
            editor.apply()
            brushDialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

}