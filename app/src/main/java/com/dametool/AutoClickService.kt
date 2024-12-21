package com.dametool

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoClickService : AccessibilityService() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val clickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try{
                if (intent?.action == "AUTO_CLICK_ACTION") {
                    var x = intent.getFloatExtra("x", 0f)
                    var y = intent.getFloatExtra("y", 0f)
                    println("Received click event at x=$x, y=$y")
                    performClick(x, y, 300)
                }
                if (intent?.action == "FIND_REACTION") {
                    println("Start searching")
                    val type = intent.getStringExtra("type")
                    findElementByContentDescription(rootInActiveWindow,"Thích. Nhấn đúp và giữ để bày tỏ cảm xúc.",type!!)
                    serviceScope.launch{
                        delay(2000)
                        when (type) {
                            "LOVE" -> findElementByContentDescription(rootInActiveWindow,"Yêu thích","Click")
                            "WOW" -> findElementByContentDescription(rootInActiveWindow,"Wow","Click")
                            "HAHA" -> findElementByContentDescription(rootInActiveWindow,"Haha","Click")
                            "ANGRY" -> findElementByContentDescription(rootInActiveWindow,"Phẫn nộ","Click")
                            "SAD" -> findElementByContentDescription(rootInActiveWindow,"Buồn","Click")
                        }

                    }

                }
                if (intent?.action == "FIND_FOLLOWFACEBOOK") {
                    println("Start searching")
                    val type = intent.getStringExtra("type")
                    findElementByContentDescription(rootInActiveWindow,"Theo dõi",type!!)
                }
                if (intent?.action == "FIND_THEODOIINSTAGRAM") {
                    println("Start searching")
                    findAndClickText(rootInActiveWindow,"Theo dõi")
                }
                if (intent?.action == "FIND_Text") {
                    println("Start searching")
                    var text = intent.getStringExtra("text")
                    findAndClickText(rootInActiveWindow,text!!)
                }
                if (intent?.action == "FIND_THICHINSTAGRAM") {
                    println("Start searching")
                    findElementByContentDescription(rootInActiveWindow,"Thích","ACTION_CLICK")
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onServiceConnected() {
        super.onServiceConnected()
        // Đăng ký nhận broadcast từ LocalBroadcastManager
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("AUTO_CLICK_ACTION"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_Follow"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_Like"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_REACTION"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_FOLLOWFACEBOOK"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_THEODOIINSTAGRAM"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_THICHINSTAGRAM"))
        localBroadcastManager.registerReceiver(clickReceiver, IntentFilter("FIND_Text"))

    }

    override fun onDestroy() {
        super.onDestroy()

        // Hủy đăng ký receiver khi không còn sử dụng
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(clickReceiver)

        // Hủy các coroutine đang chạy
        serviceScope.cancel()
    }

    private fun performClick(x: Float, y: Float, time: Long) {
        val clickPath = Path().apply {
            moveTo(x, y)
        }

        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(clickPath, 0L, time))
            .build()

        dispatchGesture(gestureDescription, null, null)
    }
    fun performSwipe(distance:Float) {
        // Lấy chiều cao và chiều rộng màn hình
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Tính toán tọa độ trung tâm của màn hình
        val startX = (screenWidth / 2).toFloat()
        val startY = (screenHeight / 2).toFloat()

        // Khoảng cách vuốt (500px) và hướng vuốt (ví dụ: vuốt sang trái)


        // Giới hạn khoảng cách vuốt để không vượt quá màn hình
        val validDistance = if (startX - distance < 0) startX else distance

        // Tạo Path cho gesture vuốt
        val swipePath = Path()

        // Vuốt sang trái (hoặc có thể điều chỉnh theo hướng khác)
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(startX , startY- validDistance) // Vuốt sang trái

        // Tạo GestureDescription để mô phỏng hành động vuốt
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(swipePath, 0L, 500L))  // 500ms để thực hiện vuốt
            .build()

        // Thực hiện gesture vuốt
        println("Thực hiện vuốt")
        dispatchGesture(gestureDescription, null, null)
    }

    private fun findAndClickText(node: AccessibilityNodeInfo, textcontent: String) {
        if(node == null){
            return
        }
        serviceScope.launch {
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)

                // Kiểm tra null và so sánh contentDescription
                childNode?.let {
                    val text = it.text

                    if (text != null && textcontent == text) {
                        val rect = android.graphics.Rect()

                        // Lấy tọa độ của childNode chứ không phải node cha
                        it.getBoundsInScreen(rect)
                        println("Position of node: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")
                        val x =rect.centerX().toFloat()
                        val y =rect.centerY().toFloat()
                        performClick(x, y, 300)
                        if(text == "Đã hiểu"){dahieux = x;dahieuy = y}
                        if(text == "Home"){homex = x;homey = y}
                        if(text == "Đồng ý"){dongydahieux = x;dongydahieuy = y}
                        if(text == "Nhận Job ngay"){nhanjobngayx = x;nhanjobngayy = y}
                        if(text == "TikTok"){getjobtiktokx = x;getjobtiktoky = y}
                        if(text == "Hoàn thành"){hoanthanhjobngayx = x;hoanthanhjobngayy = y}
                        if(text == "OK"){dongyhoanthanhjobx = x;dongyhoanthanhjoby = y}
                        if(text == "Tiktok"){tiktokx = x;tiktoky = y}
                        return@launch
                    } else {
                        println("Không tìm thấy node có textcontent là $textcontent")
                    }

                }
            }

            // Tiếp tục tìm kiếm trong các node con nếu chưa tìm thấy
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                childNode?.let {
                    findAndClickText(it, textcontent)
                }
            }
        }
    }
    private fun findTikTokLikeButton(rootNode: AccessibilityNodeInfo) {
        val nodeList = mutableListOf<AccessibilityNodeInfo>()
        val queue = mutableListOf(rootNode)

        while (queue.isNotEmpty()) {
            val currentNode = queue.removeAt(0)

            // Kiểm tra nếu element có contentDescription là "thích video"
            if (currentNode.contentDescription?.contains("Thích") == true) {
                // Thực hiện hành động khi tìm thấy
                val rect = android.graphics.Rect()
                currentNode.getBoundsInScreen(rect)
                println("Position of third node: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")
                println(currentNode)
                performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 300)
            }

            // Tiếp tục tìm kiếm các node con
            for (i in 0 until currentNode.childCount) {
                currentNode.getChild(i)?.let { queue.add(it) }
            }
        }
    }


    private fun findElementByContentDescription(node: AccessibilityNodeInfo, contentDesc: String, type: String) {
        if(node == null){
            return
        }
        serviceScope.launch {
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)

                // Kiểm tra null và so sánh contentDescription
                childNode?.let {
                    val description = it.contentDescription
                    if (description != null && type == "FOLLOWFACEBOOK" && (description.toString() == "Theo dõi" || description.toString() == "Thêm bạn bè" || description.toString() == "Thích")){
                        val rect = android.graphics.Rect()
                        it.getBoundsInScreen(rect)
                        println("Position of node: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")
                        performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 300)
                        return@launch
                    }
                    if (description != null && description.toString() == contentDesc) {
                        val rect = android.graphics.Rect()

                        // Lấy tọa độ của childNode chứ không phải node cha
                        it.getBoundsInScreen(rect)
                        println("Position of node: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")

                        print(it)
                        when (type) {
                            "LIKE" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 300)
                            "LOVE" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 1000L)
                            "WOW" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 1000)
                            "HAHA" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 1000)
                            "ANGRY" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 1000)
                            "SAD" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 1000)
                            "ACTION_CLICK" -> performClick(rect.centerX().toFloat(), rect.centerY().toFloat(), 300)
                            "Click" -> {
                                performClick((rect.centerX() + 50).toFloat(), (rect.centerY() + 50).toFloat(), 300L)
                                println("Position of node: left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom}")
                            }
                        }
                        return@launch
                    }
                    else{
                        println("Không tìm thấy node có contentDescription là $contentDesc")
                    }

                }
            }

            // Tiếp tục tìm kiếm trong các node con nếu chưa tìm thấy
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                childNode?.let {
                    findElementByContentDescription(it, contentDesc, type)
                }
            }
        }

    }

}
