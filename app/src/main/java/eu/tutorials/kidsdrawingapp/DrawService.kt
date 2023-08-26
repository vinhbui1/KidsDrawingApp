package eu.tutorials.kidsdrawingapp

import android.R
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import android.widget.LinearLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get


class DrawService : Service() {
    private var drawingView: DrawingView? = null
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    var height: Int? = null
    var weight: Int? = null

    private var mImageButtonCurrentPaint: ImageButton? =
        null // A variable for current color is picked from color pallet.

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

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
                .setSmallIcon(R.drawable.ic_dialog_email)
                .build()
            startForeground(1, notification)
        }

        var widthInPixels: Int? = applicationContext.resources.displayMetrics.widthPixels
        var heightInPixels: Int? = applicationContext.resources.displayMetrics.heightPixels
        mFloatingView = LayoutInflater.from(this)
            .inflate(eu.tutorials.kidsdrawingapp.R.layout.draw_overlay, null)
        drawingView = mFloatingView!!.findViewById(eu.tutorials.kidsdrawingapp.R.id.drawing_view)
        drawingView?.setSizeForBrush(10.toFloat())

        val params = WindowManager.LayoutParams(
            widthInPixels!!.toInt(),
            heightInPixels!!.toInt(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val receiver = object : BroadcastReceiver() {
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

                }



            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("action.hideDraw") // Action1 to filter
        intentFilter.addAction("action.setSize") // Action2 to filter
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
        val linearLayoutPaintColors =
            mFloatingView!!.findViewById<LinearLayout>(eu.tutorials.kidsdrawingapp.R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint?.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                eu.tutorials.kidsdrawingapp.R.drawable.pallet_pressed
            )
        )

    }

    fun paintClicked(view: View) {
        if (view !== mImageButtonCurrentPaint) {
            // Update the color
            val imageButton = view as ImageButton
            // Here the tag is used for swaping the current color with previous color.
            // The tag stores the selected view
            val colorTag = imageButton.tag.toString()
            // The color is set as per the selected tag here.
            drawingView?.setColor(colorTag)
            // Swap the backgrounds for last active and currently active image button.
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    eu.tutorials.kidsdrawingapp.R.drawable.pallet_pressed
                )
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    eu.tutorials.kidsdrawingapp.R.drawable.pallet_normal
                )
            )

            //Current view is updated with selected view in the form of ImageButton.
            mImageButtonCurrentPaint = view
        }
    }


}