package eu.on.screen

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class DrawService : Service() {
    private var drawingView: DrawingView? = null
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    var height: Int? = null
    var weight: Int? = null
    var receiver: BroadcastReceiver? = null
    private var mImageButtonCurrentPaint: ImageButton? =
        null // A variable for current color is picked from color pallet.

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {

            val contentIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )


            val CHANNEL_ID = "channel1"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay notification",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Draw on Screen")
                .setContentText("Click to to back app")
                .setSmallIcon(eu.on.screen.R.drawable.ic_brush)
                .setContentIntent(contentIntent)
                .build()
            startForeground(1, notification)
        }

        var widthInPixels: Int? = applicationContext.resources.displayMetrics.widthPixels
        var heightInPixels: Int? = applicationContext.resources.displayMetrics.heightPixels
        mFloatingView = LayoutInflater.from(this)
            .inflate(eu.on.screen.R.layout.draw_overlay, null)
        drawingView = mFloatingView!!.findViewById(eu.on.screen.R.id.drawing_view)
        drawingView?.setSizeForBrush(10.toFloat())

        val params = WindowManager.LayoutParams(
            widthInPixels!!.toInt(),
            heightInPixels!!.toInt(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
         receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action.equals("action.hideDraw")) {
                    val message = intent?.getBooleanExtra("hideDraw", true)
                    if (message == true) {
                        mFloatingView?.visibility = View.INVISIBLE
                    } else {
                        mFloatingView?.visibility = View.VISIBLE
                    }
                } else if (intent?.action.equals("action.setSize")) {
                    val setSize = intent?.getIntExtra("setSize", 10)
                    drawingView?.setSizeForBrush(setSize!!.toFloat())

                } else if (intent?.action.equals("action.setSizeShape")) {
                    val setSize = intent?.getIntExtra("setSizeShape", 10)
                    drawingView?.setSizeForBrushShape(setSize!!.toFloat())

                } else if (intent?.action.equals("action.PickColor")) {
                    var setSize = intent?.getIntExtra("pickColor", 10)
                    if (setSize != null) {
                        drawingView?.setColor(setSize)
                    }
                } else if (intent?.action.equals("action.PickShape")) {
                    var setShape = intent?.getIntExtra("pickShape", 2)
                    if (setShape != null) {
                        drawingView?.setShapeType(setShape)
                    }
                }
                else if (intent?.action.equals("action.setSizeErase")){
                    var setShape = intent?.getIntExtra("setSizeErase", 20)
                    if (setShape != null) {
                        drawingView?.setSizeForBrushErase(setShape.toFloat())
                    }

                }
                else if (intent?.action.equals("action.undo")){
                        drawingView?.onClickUndo()
                }
                else if (intent?.action.equals("action.redo")){
                    drawingView?.onClickRedo()
                }
                else if (intent?.action.equals("action.delete")){
                    drawingView?.clearAllDrawings()
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("action.hideDraw") // Action1 to filter
        intentFilter.addAction("action.setSize") // Action2 to filter
        intentFilter.addAction("action.setSizeShape") // Action2 to filter

        intentFilter.addAction("action.PickColor") // Action2 to filter
        intentFilter.addAction("action.PickShape") // Action2 to filter
        intentFilter.addAction("action.setSizeErase") // Action2 to filter
        intentFilter.addAction("action.undo") // Action2 to filter
        intentFilter.addAction("action.redo") // Action2 to filter
        intentFilter.addAction("action.delete") // Action2 to filter
        registerReceiver(receiver, IntentFilter(intentFilter))
//        val ibBrush: ImageButton = mFloatingView!!.findViewById(eu.tutorials.kidsdrawingapp.R.id.ib_brush)
//        ibBrush.setOnClickListener {
//            showBrushSizeChooserDialog()
//        }
        //    val ibUndo: ImageButton = mFloatingView!!.findViewById(eu.tutorials.kidsdrawingapp.R.id.ib_undo)
//        ibUndo.setOnClickListener {
//            // This is for undo recent stroke.
//            drawingView?.visibility = View.INVISIBLE
//        //    drawingView?.onClickUndo()
//        }


        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mFloatingView, params)


    }

    fun paintClicked(view: View) {
        if (view !== mImageButtonCurrentPaint) {
            // Update the color
            val imageButton = view as ImageButton
            // Here the tag is used for swaping the current color with previous color.
            // The tag stores the selected view
            val colorTag = imageButton.tag.toString()
            // The color is set as per the selected tag here.
            //   drawingView?.setColor(colorTag)
            // Swap the backgrounds for last active and currently active image button.
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    eu.on.screen.R.drawable.pallet_pressed
                )
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    eu.on.screen.R.drawable.pallet_normal
                )
            )

            //Current view is updated with selected view in the form of ImageButton.
            mImageButtonCurrentPaint = view
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}