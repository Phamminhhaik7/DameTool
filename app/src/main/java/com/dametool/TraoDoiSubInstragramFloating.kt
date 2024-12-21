package com.dametool
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException


class TraoDoiSubInstragramFloating : Service() {
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
            }
            try {
                FileWriter(jsonFile).use { writer ->
                    writer.write(saveconfig.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stopSelf()
        }
        windowManager.addView(floatingView, params)

        //code
        val autoFollow = floatingView.findViewById<Switch>(R.id.autofollow)
        val autoLike = floatingView.findViewById<Switch>(R.id.autolike)

        autoFollow.setOnCheckedChangeListener { _, isChecked ->
            autofollow = isChecked
        }
        autoLike.setOnCheckedChangeListener { _, isChecked ->
            autolike = isChecked
        }


        val nickchay = floatingView.findViewById<TextView>(R.id.nickdangchay)
        nickchay.text = "Nick đang chạy: $namenickrun"
        val run = floatingView.findViewById<Switch>(R.id.run)
        run.setOnCheckedChangeListener { _, isChecked ->
            runstatus = isChecked
            if(runstatus){
                if(loaijobins == "Follow"){
                    TraoDoiSubInstragramFollow()
                } else {
                    TraoDoiSubInstragramLike()
                }
            }
        }
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
    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    private fun TraoDoiSubInstragramFollow() {
        serviceScope.launch {
            val countdowntext = floatingView.findViewById<TextView>(R.id.countdown)
            val messagetext = floatingView.findViewById<TextView>(R.id.messenger)
            val dachay = floatingView.findViewById<TextView>(R.id.dachay)
            val thanhcong = floatingView.findViewById<TextView>(R.id.thanhcong)
            try{
                requests(URL("https://traodoisub.com/api/coin/?type=INS_FOLLOW&id=INS_FOLLOW_API&access_token=$token"))
                while (runstatus) {
                    if (!runstatus) { return@launch}
                    var url = URL("https://traodoisub.com/api/?fields=instagram_follow&access_token=$token")
                    var response = requests(url)
                    println(response.toString())
                    val gson = Gson()
                    var jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                    println(jsonObject)
                    if (jsonObject.has("error")) {
                        withContext(Dispatchers.Main){ messagetext.text = "Thao tác quá nhanh vui lòng đợi" }
                        val countdown = jsonObject.get("countdown").asInt
                        for (i in countdown downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000L)
                            if (!runstatus) { return@launch }
                        }
                        continue
                    }
                    if (!jsonObject.has("data")|| jsonObject.get("data") == null) {
                        withContext(Dispatchers.Main){ messagetext.text = "Không tìm thấy job, Đang lấy lại" }
                        for (i in 2 downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000L)
                            if (!runstatus) { return@launch }
                        }
                        continue

                    }
                    if (!runstatus) { return@launch }
                    val data = jsonObject.get("data")
                    val dataArray = gson.fromJson(data.toString(), JsonArray::class.java)
                    for (element in dataArray) {
                        val elementObject = gson.fromJson(element.toString(), JsonObject::class.java)
                        val id = elementObject.get("id").asString
                        val link = elementObject.get("link").asString
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        withContext(Dispatchers.Main){ messagetext.text = id }
                        for (i in countdown downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000)
                            if (!runstatus) { return@launch }
                        }
                        if (!runstatus) { return@launch }
                        if(autofollow){
                            val click = Intent("FIND_THEODOIINSTAGRAM")
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(click)
                            withContext(Dispatchers.Main){ messagetext.text = "Thực hiện click!" }
                            sleep(2000)
                        }
                        url = URL("https://traodoisub.com/api/coin/?type=INS_FOLLOW_CACHE&id=$id&access_token=$token")
                        response = requests(url)
                        jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                        if(jsonObject.get("cache").asInt >=8) {
                            url = URL("https://traodoisub.com/api/coin/?type=INS_FOLLOW&id=INS_FOLLOW_API&access_token=$token")
                            response = requests(url)
                            jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                            val data = jsonObject.get("data")
                            val dataObject = gson.fromJson(data.toString(), JsonObject::class.java)
                            val xu = dataObject.get("xu").asString
                            val xuthem = dataObject.get("xu_them").asInt
                            val msg = dataObject.get("msg").asString
                            tongxuthem += xuthem
                            withContext(Dispatchers.Main) { messagetext.text = msg }
                            withContext(Dispatchers.Main) {
                                thanhcong.text = "Thành công: +$tongxuthem xu"
                            }
                        }
                        demjob += 1
                        withContext(Dispatchers.Main){ dachay.text = "Đã chạy: $demjob" }
                        if(demjob >= maxjob){
                            runstatus = false
                            val run = floatingView.findViewById<Switch>(R.id.run)
                            withContext(Dispatchers.Main){
                                run.isChecked = false
                                messagetext.text = "Đã đạt max job, Dừng tool!"
                                demjob = 0
                            }
                            return@launch
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is UnknownHostException -> "Không có kết nối mạng. Vui lòng kiểm tra lại."
                        is SocketTimeoutException -> "Kết nối quá chậm, vui lòng thử lại sau."
                        else -> "Có lỗi xảy ra, vui lòng thử lại."
                    }
                   messagetext.text = message
                    e.printStackTrace()
                }
                return@launch
            }
        }

    }
    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    private fun TraoDoiSubInstragramLike() {
        serviceScope.launch {
            val countdowntext = floatingView.findViewById<TextView>(R.id.countdown)
            val messagetext = floatingView.findViewById<TextView>(R.id.messenger)
            val dachay = floatingView.findViewById<TextView>(R.id.dachay)
            val thanhcong = floatingView.findViewById<TextView>(R.id.thanhcong)
            try{

                while (runstatus) {
                    if (!runstatus) { return@launch}
                    var url = URL("https://traodoisub.com/api/?fields=instagram_like&access_token=$token")
                    var response = requests(url)
                    println(response.toString())
                    val gson = Gson()
                    var jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                    println(jsonObject)
                    if (jsonObject.has("error")) {
                        withContext(Dispatchers.Main){ messagetext.text = "Thao tác quá nhanh vui lòng đợi" }
                        val countdown = jsonObject.get("countdown").asInt
                        for (i in countdown downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000L)
                            if (!runstatus) { return@launch }
                        }
                        continue
                    }
                    if (!jsonObject.has("data")|| jsonObject.get("data") == null) {
                        withContext(Dispatchers.Main){ messagetext.text = "Không tìm thấy job, Đang lấy lại" }
                        for (i in 2 downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000L)
                            if (!runstatus) { return@launch }
                        }
                        continue

                    }
                    if (!runstatus) { return@launch }
                    val data = jsonObject.get("data")
                    val dataArray = gson.fromJson(data.toString(), JsonArray::class.java)
                    for (element in dataArray) {
                        val elementObject = gson.fromJson(element.toString(), JsonObject::class.java)
                        val id = elementObject.get("id").asString
                        val link = elementObject.get("link").asString
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        withContext(Dispatchers.Main){ messagetext.text = id }
                        for (i in countdown downTo 0 step 1) {
                            withContext(Dispatchers.Main){ countdowntext.text = "Countdown: $i" }
                            sleep(1000)
                            if (!runstatus) { return@launch }
                        }
                        if (!runstatus) { return@launch }
                        if(autolike){
                            val click = Intent("FIND_THICHINSTAGRAM")
                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(click)
                            withContext(Dispatchers.Main){ messagetext.text = "Thực hiện click!" }
                            sleep(2000)
                        }
                        println("Đã làm đủ 8 nv")
                        url = URL("https://traodoisub.com/api/coin/?type=INS_LIKE&id=$id&access_token=$token")
                        response = requests(url)
                        jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                        println(jsonObject)
                        if(jsonObject.has("error")) {
                            continue
                        }
                        val data = jsonObject.get("data")
                        println(data.toString())
                        val dataObject = gson.fromJson(data.toString(), JsonObject::class.java)
                        val xu = dataObject.get("xu").asString
                        val msg = dataObject.get("msg").asString
                        tongxuthem += 500
                        withContext(Dispatchers.Main){ messagetext.text = msg }
                        withContext(Dispatchers.Main){ thanhcong.text = "Thành công: +$tongxuthem xu" }
                        demjob += 1
                        withContext(Dispatchers.Main){ dachay.text = "Đã chạy: $demjob" }
                        if(demjob >= maxjob){
                            runstatus = false
                            val run = floatingView.findViewById<Switch>(R.id.run)
                            withContext(Dispatchers.Main){
                                run.isChecked = false
                                messagetext.text = "Đã đạt max job, Dừng tool!"
                                demjob = 0
                            }
                            return@launch
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is UnknownHostException -> "Không có kết nối mạng. Vui lòng kiểm tra lại."
                        is SocketTimeoutException -> "Kết nối quá chậm, vui lòng thử lại sau."
                        else -> "Có lỗi xảy ra, vui lòng thử lại."
                    }
                   messagetext.text = message
                    e.printStackTrace()
                }
                return@launch
            }
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


}