package com.dametool
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class GolikeTikTokFloating : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var closeButton: ImageView

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var runstatus : Boolean = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility", "UseSwitchCompatOrMaterialCode", "InflateParams",
        "InlinedApi", "SetTextI18n"
    )
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null)

        closeButton = floatingView.findViewById(R.id.mini)

        val floatingView2 = floatingView.findViewById<View>(R.id.floatingview2)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        floatingView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                }
            }
            true
        }
        closeButton.setOnClickListener {
            replaceWithSmallWindow()
        }
        floatingView2.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = Math.abs(event.rawX - initialTouchX)
                    val diffY = Math.abs(event.rawY - initialTouchY)
                    if (diffX < 10 && diffY < 10) {
                        replaceWithLargeWindow()
                    }
                }
            }
            true
        }
        val closefloating = floatingView.findViewById<ImageView>(R.id.close_floating)
        closefloating.setOnClickListener {
            // Lấy thư mục lưu trữ tệp
            val hiddenDir = File(applicationContext.getExternalFilesDir(null), ".DameToolData")
            val jsonFile = File(hiddenDir, "config.json")
            val saveconfig = JSONObject().apply {
                put("followtiktokx", followtiktokx ?: JSONObject.NULL)
                put("followtiktoky", followtiktoky ?: JSONObject.NULL)
                put("toadohandx", toadohandfollowtiktokx ?: JSONObject.NULL)
                put("toadohandy", toadohandfollowtiktoky ?: JSONObject.NULL)
                put("liketiktokx", liketiktokx ?: JSONObject.NULL)
                put("liketiktoky", liketiktoky ?: JSONObject.NULL)

                put("toadohandliketiktokx", toadohandliketiktokx ?: JSONObject.NULL)
                put("toadohandliketiktoky", toadohandliketiktoky ?: JSONObject.NULL)

                put("theodoiinstagramx", theodoiinstagramx ?: JSONObject.NULL)
                put("theodoiinstagramy", theodoiinstagramy ?: JSONObject.NULL)
                put("toadohandtheodoiinstagramx", toadohandtheodoiinstagramx ?: JSONObject.NULL)
                put("toadohandtheodoiinstagramy", toadohandtheodoiinstagramy ?: JSONObject.NULL)

                put("nhanjobngayx", nhanjobngayx ?: JSONObject.NULL)
                put("nhanjobngayy", nhanjobngayy ?: JSONObject.NULL)

                put("dahieucheckboxx", dahieucheckboxx ?: JSONObject.NULL)
                put("dahieucheckboxy", dahieucheckboxy ?: JSONObject.NULL)

                put("dongydahieux", dongydahieux ?: JSONObject.NULL)
                put("dongydahieuy", dongydahieuy ?: JSONObject.NULL)

                put("getjobtiktokx", getjobtiktokx ?: JSONObject.NULL)
                put("getjobtiktoky", getjobtiktoky ?: JSONObject.NULL)

                put("hoanthanhjobtiktokx", hoanthanhjobngayx ?: JSONObject.NULL)
                put("hoanthanhjobtiktoky", hoanthanhjobngayy ?: JSONObject.NULL)

                put("dongyhoanthanhjobx", dongyhoanthanhjobx ?: JSONObject.NULL)
                put("dongyhoanthanhjoby", dongyhoanthanhjoby ?: JSONObject.NULL)

                put("hoanhthanhloitiktokx", hoanhthanhloitiktokx ?: JSONObject.NULL)
                put("hoanhthanhloitiktoky", hoanhthanhloitiktoky ?: JSONObject.NULL)

                put("baoloitiktokx", baoloitiktokx ?: JSONObject.NULL)
                put("baoloitiktoky", baoloitiktoky ?: JSONObject.NULL)

                put("selectloix", selectloix ?: JSONObject.NULL)
                put("selectloiy", selectloiy ?: JSONObject.NULL)

                put("scrolltosendloix", scrolltosendloix ?: JSONObject.NULL)
                put("scrolltosendloiy", scrolltosendloiy ?: JSONObject.NULL)

                put("guibaocaoloix", guibaocaoloix ?: JSONObject.NULL)
                put("guibaocaoloiy", guibaocaoloiy ?: JSONObject.NULL)

                put("dongybaocaoloix", dongybaocaoloix ?: JSONObject.NULL)
                put("dongybaocaoloiy", dongybaocaoloiy ?: JSONObject.NULL)

                // Bổ sung các biến bị thiếu
                put("dahieux", dahieux ?: JSONObject.NULL)
                put("dahieuy", dahieuy ?: JSONObject.NULL)
                put("homex", homex ?: JSONObject.NULL)
                put("homey", homey ?: JSONObject.NULL)
                put("kiemthuongx", kiemthuongx ?: JSONObject.NULL)
                put("kiemthuongy", kiemthuongy ?: JSONObject.NULL)
                put("tiktokx", tiktokx ?: JSONObject.NULL)
                put("tiktoky", tiktoky ?: JSONObject.NULL)
                put("chontaikhoan", chontaikhoan ?: JSONObject.NULL)
                put("chontaikhoany", chontaikhoany ?: JSONObject.NULL)
                put("okthongbaox", okthongbaox ?: JSONObject.NULL)
                put("okthongbaoy", okthongbaoy ?: JSONObject.NULL)
            }

            try {
                FileWriter(jsonFile).use { writer ->
                    writer.write(saveconfig.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val intent = Intent(this, ToaDoFollowTikTok::class.java)
            stopService(intent)
            val intent2 = Intent(this, ToaDoLikeTikTok::class.java)
            stopService(intent2)
            stopSelf()
        }
        windowManager.addView(floatingView, params)

        //code
        val autoFollow = floatingView.findViewById<Switch>(R.id.autofollow)
        val autoLike = floatingView.findViewById<Switch>(R.id.autolike)

        autoFollow.setOnCheckedChangeListener { _, isChecked ->
            val intent = Intent("AUTO_CLICK_ACTION")
            println(liketiktokx)
            println(liketiktoky)
            println(followtiktokx)
            println(followtiktoky)
            intent.putExtra("x", followtiktokx)
            intent.putExtra("y", followtiktoky)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            autofollow = isChecked
            if (autofollow) {
                val intent = Intent(this, ToaDoFollowTikTok::class.java)
                startService(intent)
            }else{
                val intent = Intent(this, ToaDoFollowTikTok::class.java)
                stopService(intent)
            }
        }
        autoLike.setOnCheckedChangeListener { _, isChecked ->
            autolike = isChecked
            if (autolike) {
                val intent = Intent(this, ToaDoLikeTikTok::class.java)
                startService(intent)
            }else{
                val intent = Intent(this, ToaDoLikeTikTok::class.java)
                stopService(intent)
            }

        }


        val nickchay = floatingView.findViewById<TextView>(R.id.nickdangchay)
        nickchay.text = "Nick đang chạy: $namenickrun"

        val run = floatingView.findViewById<Switch>(R.id.run)
        run.setOnCheckedChangeListener { _, isChecked ->
            runstatus = isChecked
            if(runstatus){
                serviceScope.launch {
                    delay(2000)
                    daHieu()
                    delay(2000)
                    Home()
                    delay(2000)
                    Kiemthuong()
                    delay(2000)
                    Tiktok()
                    delay(2000)
                    Nhanjobngay()
                }
            }
        }
    }
    private fun daHieu(){
        if (dahieux == 0f ){
            val intent = Intent("FIND_Text")
            intent.putExtra("text", "Đã hiểu")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            val intent = Intent("AUTO_CLICK_ACTION")
            intent.putExtra("x", dahieux)
            intent.putExtra("y", dahieuy)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
    private fun Home(){
        if (homex == 0.0f ){
            val intent = Intent("FIND_Text")
            intent.putExtra("text", "Home")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            val intent = Intent("AUTO_CLICK_ACTION")
            intent.putExtra("x", homex)
            intent.putExtra("y", homey)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
    private fun Kiemthuong(){
        if (kiemthuongx == 0.0f ){
            val intent = Intent("FIND_Text")
            intent.putExtra("text", "Kiếm Thưởng")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            val intent = Intent("AUTO_CLICK_ACTION")
            intent.putExtra("x", kiemthuongx)
            intent.putExtra("y", kiemthuongy)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
    private fun Tiktok(){
        if (tiktokx == 0.0f ){
            val intent = Intent("FIND_Text")
            intent.putExtra("text", "Tiktok")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            val intent = Intent("AUTO_CLICK_ACTION")
            intent.putExtra("x", tiktokx)
            intent.putExtra("y", tiktoky)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
    private fun Nhanjobngay(){
        if (nhanjobngayx == 0.0f ){
            val intent = Intent("FIND_Text")
            intent.putExtra("text", "Nhận Job ngay")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            val intent = Intent("AUTO_CLICK_ACTION")
            intent.putExtra("x", nhanjobngayx)
            intent.putExtra("y", nhanjobngayy)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
    private suspend fun requests(url: URL): StringBuilder {
        val response = StringBuilder()
        withContext(Dispatchers.IO) {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                // Đọc toàn bộ dữ liệu từ inputStream
                val reader: BufferedReader = inputStream.bufferedReader()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
            }
        }
        return response
    }



    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
        serviceScope.cancel()
    }
    private fun replaceWithSmallWindow() {
        // Ẩn layout ban đầu (TextView, ImageView) và hiển thị "Cửa sổ đã thu nhỏ"
        val floatingView1 = floatingView.findViewById<View>(R.id.floatingview1)
        val floatingView2 = floatingView.findViewById<View>(R.id.floatingview2)
        floatingView1.visibility = View.GONE
        floatingView2.visibility = View.VISIBLE
    }

    private fun replaceWithLargeWindow() {
        // Ẩn layout ban đầu (TextView, ImageView) và hiển thị "Cửa sổ đã thu nhỏ"
        val floatingView1 = floatingView.findViewById<View>(R.id.floatingview1)
        val floatingView2 = floatingView.findViewById<View>(R.id.floatingview2)
        floatingView1.visibility = View.VISIBLE
        floatingView2.visibility = View.GONE
    }




}