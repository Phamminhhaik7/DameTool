package com.dametool
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class ToaDoLikeTikTok : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var targetImage: ImageView
    private lateinit var coordinatesText: TextView

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inflate layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.get_coordinate, null)
        targetImage = floatingView.findViewById(R.id.targetImage)
        coordinatesText = floatingView.findViewById(R.id.coordinatesText)
        coordinatesText.text = "Like"
        // Lấy kích thước màn hình
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Thiết lập window params
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Đặt tọa độ ban đầu (có thể chọn giữa trung tâm màn hình hoặc tọa độ cố định)

        if (liketiktokx != 0f && liketiktoky != null) {
            initialX = toadohandliketiktokx!!.toInt()
            initialY = toadohandliketiktoky!!.toInt()
        }else{
            initialX = (screenWidth / 2) - (targetImage.width / 2)  // Giữa màn hình
            initialY = (screenHeight / 2) - (targetImage.height / 2)  // Giữa màn hình
        }
        // Thiết lập giá trị cho params
        params.x = initialX
        params.y = initialY


        params.gravity = Gravity.TOP or Gravity.START

        // Xử lý touch events
        targetImage.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    // Tính toán vị trí mới
                    val newX = initialX + (event.rawX - initialTouchX).toInt()
                    val newY = initialY + (event.rawY - initialTouchY).toInt()

                    // Giới hạn vị trí của ảnh trong phạm vi màn hình
                    params.x = newX.coerceIn(0, screenWidth - targetImage.width)
                    params.y = newY.coerceIn(0, screenHeight - targetImage.height)

                    // Cập nhật vị trí
                    windowManager.updateViewLayout(floatingView, params)

                    // Cập nhật vị trí của targetImage và in ra tọa độ
                    val location = IntArray(2)
                    targetImage.getLocationOnScreen(location)
                    val x = location[0] + targetImage.width / 2
                    val y = location[1]
                    println("X: $x, Y: $y")
                    updateCoordinates(x, y)
                }
            }
            true
        }

        // Thêm view vào window manager
        windowManager.addView(floatingView, params)
        Handler(Looper.getMainLooper()).postDelayed({
            updateCoordinates(params.x, params.y)
        }, 100)  // Delay 100ms để chắc chắn view đã được render



    }


    private fun updateCoordinates(px:Int, py:Int) {
        val location = IntArray(2)
        targetImage.getLocationOnScreen(location)
        val y = location[1]
        val x = location[0] + targetImage.width / 2
        toadohandliketiktokx = px.toFloat()
        toadohandliketiktoky = py.toFloat()
        liketiktokx = x.toFloat()
        liketiktoky = y.toFloat()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}