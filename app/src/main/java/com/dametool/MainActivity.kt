package com.dametool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private val firstFragment = FirstFragment()
    private val M_REQUEST_CODE = 100
    var count = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        val hiddenDir = File(this.getExternalFilesDir(null), ".DameToolData")
        if (!hiddenDir.exists()) {
            hiddenDir.mkdirs()
        }
        val jsonFile = File(hiddenDir, "config.json")
        if (jsonFile.exists()) {
            val jsonString = jsonFile.readText()
            val jsonObject = JSONObject(jsonString)
            followtiktokx = jsonObject.optInt("followtiktokx").toFloat()
            followtiktoky = jsonObject.optInt("followtiktoky").toFloat()

            toadohandfollowtiktokx = jsonObject.optInt("toadohandx").toFloat()
            toadohandfollowtiktoky = jsonObject.optInt("toadohandy").toFloat()

            liketiktokx = jsonObject.optInt("liketiktokx").toFloat()
            liketiktoky = jsonObject.optInt("liketiktoky").toFloat()
            toadohandliketiktokx = jsonObject.optInt("toadohandliketiktokx").toFloat()
            toadohandliketiktoky = jsonObject.optInt("toadohandliketiktoky").toFloat()

            theodoiinstagramx = jsonObject.optInt("theodoiinstagramx").toFloat()
            theodoiinstagramy = jsonObject.optInt("theodoiinstagramy").toFloat()
            toadohandtheodoiinstagramx = jsonObject.optInt("toadohandtheodoiinstagramx").toFloat()
            toadohandtheodoiinstagramy = jsonObject.optInt("toadohandtheodoiinstagramy").toFloat()

            nhanjobngayx = jsonObject.optInt("nhanjobngayx").toFloat()
            nhanjobngayy = jsonObject.optInt("nhanjobngayy").toFloat()

            dahieucheckboxx = jsonObject.optInt("dahieucheckboxx").toFloat()
            dahieucheckboxy = jsonObject.optInt("dahieucheckboxy").toFloat()

            dongydahieux = jsonObject.optInt("dongydahieux").toFloat()
            dongydahieuy = jsonObject.optInt("dongydahieuy").toFloat()

            getjobtiktokx = jsonObject.optInt("getjobtiktokx").toFloat()
            getjobtiktoky = jsonObject.optInt("getjobtiktoky").toFloat()

            hoanthanhjobngayx = jsonObject.optInt("hoanthanhjobngayx").toFloat()
            hoanthanhjobngayy = jsonObject.optInt("hoanthanhjobngayy").toFloat()

            dongyhoanthanhjobx = jsonObject.optInt("dongyhoanthanhjobx").toFloat()
            dongyhoanthanhjoby = jsonObject.optInt("dongyhoanthanhjoby").toFloat()

            hoanhthanhloitiktokx = jsonObject.optInt("hoanhthanhloitiktokx").toFloat()
            hoanhthanhloitiktoky = jsonObject.optInt("hoanhthanhloitiktoky").toFloat()

            baoloitiktokx = jsonObject.optInt("baoloitiktokx").toFloat()
            baoloitiktoky = jsonObject.optInt("baoloitiktoky").toFloat()

            selectloix = jsonObject.optInt("selectloix").toFloat()
            selectloiy = jsonObject.optInt("selectloiy").toFloat()

            scrolltosendloix = jsonObject.optInt("scrolltosendloix").toFloat()
            scrolltosendloiy = jsonObject.optInt("scrolltosendloiy").toFloat()

            guibaocaoloix = jsonObject.optInt("guibaocaoloix").toFloat()
            guibaocaoloiy = jsonObject.optInt("guibaocaoloiy").toFloat()

            dongybaocaoloix = jsonObject.optInt("dongybaocaoloix").toFloat()
            dongybaocaoloiy = jsonObject.optInt("dongybaocaoloiy").toFloat()

            // Bổ sung các biến bị thiếu
            dahieux = jsonObject.optInt("dahieux").toFloat()
            dahieuy = jsonObject.optInt("dahieuy").toFloat()
            homex = jsonObject.optInt("homex").toFloat()
            homey = jsonObject.optInt("homey").toFloat()
            kiemthuongx = jsonObject.optInt("kiemthuongx").toFloat()
            kiemthuongy = jsonObject.optInt("kiemthuongy").toFloat()
            tiktokx = jsonObject.optInt("tiktokx").toFloat()
            tiktoky = jsonObject.optInt("tiktoky").toFloat()
            chontaikhoan = jsonObject.optInt("chontaikhoan").toFloat()
            chontaikhoany = jsonObject.optInt("chontaikhoany").toFloat()
            okthongbaox = jsonObject.optInt("okthongbaox").toFloat()
            okthongbaoy = jsonObject.optInt("okthongbaoy").toFloat()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, firstFragment).commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.intro -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, firstFragment).addToBackStack(null).commit()
                    true
                }
                R.id.tool -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, toolinuse).addToBackStack(null).commit()
                    true
                }
                else -> false
            }
        }

    }

    fun selectBottomNavigationItem(itemId: Int) {
        bottomNavigationView.selectedItemId = itemId
    }
    private fun checkPermission() {
        val listper : ArrayList<String> = ArrayList()
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            listper.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            listper.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        checkAndRequestOverlayPermission(this)
        requestAccessibilityService(this)

        if(listper.isNotEmpty()){
            ActivityCompat.requestPermissions(this, listper.toTypedArray(), M_REQUEST_CODE)
        }
    }
    fun checkAndRequestOverlayPermission(context: Context) {
        if (Settings.canDrawOverlays(context)) {
            // Quyền đã được cấp, có thể hiển thị cửa sổ nổi
        } else {

            // Mở màn hình cài đặt để người dùng cấp quyền
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    }
    fun checkAccessibilityPermission(): Boolean {
        var accessEnabled = 0
        try {
            accessEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // request permission via start activity for result
            startActivity(intent)
            return false
        } else {
            return true
        }
    }

    // Mở màn hình cài đặt để yêu cầu người dùng cấp quyền Accessibility
    fun requestAccessibilityService(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    override fun onBackPressed() {
        // Lấy fragment hiện tại từ FragmentManager
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Kiểm tra xem fragment hiện tại có phải là FirstFragment không
        if (fragment is FirstFragment) {
            count += 1
            if(count == 2){
                System.exit(0)
            }
            Toast.makeText(this, "Quay lại lần nữa để thoát", Toast.LENGTH_SHORT).show()

        } else {
            // Nếu không phải FirstFragment, thực hiện hành động mặc định của nút "Back"
            super.onBackPressed()
        }
    }


}

