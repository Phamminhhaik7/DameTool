package com.dametool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dametool.databinding.TraodoisubTiktokBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

class TraoDoiSubTikTok : Fragment() {

    private var _binding: TraodoisubTiktokBinding? = null
    private val binding get() = _binding!!
    var nickdangchay :String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TraodoisubTiktokBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolinuse = TraoDoiSubTikTok()
        binding.included.GolikeHome.setOnClickListener {
            toolinuse = GolikeHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.included.TraoDoiSubHome.setOnClickListener {
            toolinuse = TraoDoiSubHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.included.TuongTacCheoHome.setOnClickListener {
            toolinuse = TuongTacCheoHome()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                val hiddenDir = File(requireContext().getExternalFilesDir(null), ".DameToolData")
                val jsonFile = File(hiddenDir, "traodoisublogindata.json")
                val jsonString = jsonFile.readText()
                val jsonObject = JSONObject(jsonString)
                var cookie = jsonObject.getString("cookies")
                token = jsonObject.getString("token")
                println("Cookie: $cookie")
                //    // Tách cookie thành Map
                var cookiesMap = cookie.split(";").associate {
                    val (key, value) = it.split("=", limit = 2)
                    key.trim() to value.trim()
                }
                println("Cookies Map: $cookiesMap")
                val getBadges = getBadges(cookiesMap)
                if (!getBadges) {
                    println("Reset cookie")
                    cookie = resetToken(jsonFile, jsonObject).toString()
                    cookiesMap = cookie.split(";").associate {
                        val (key, value) = it.split("=", limit = 2)
                        key.trim() to value.trim()
                    }
                    getBadges(cookiesMap)
                }
            } catch (e: UnknownHostException) {
                // Xử lý trường hợp không có kết nối mạng
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Không có kết nối mạng. Vui lòng kiểm tra lại.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: SocketTimeoutException) {
                // Xử lý trường hợp hết thời gian chờ
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Kết nối quá chậm, vui lòng thử lại sau.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Xử lý các lỗi khác
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val spinner2 = binding.materialSpinner2
        val badgeContents2 = listOf("Follow", "Like")
        spinner2.setItems(badgeContents2)
        spinner2.setOnItemSelectedListener { _, _, _, item ->
            loaijob = item.toString()
            println(loaijob)
        }
        binding.TraoDoiSubTikTokStart.setOnClickListener {
            maxjob = binding.TraoDoiSubTikTokMaxJob.editText?.text.toString().toIntOrNull() ?: 0
            countdown = binding.TraoDoiSubTikTokCountdown.editText?.text.toString().toIntOrNull() ?: 0
            lifecycleScope.launch(Dispatchers.IO){
                try {
                    var url = URL("https://traodoisub.com/api/?fields=tiktok_run&id=$nickdangchay&access_token=$token")
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "GET"
                        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                        val gson = Gson()
                        // Đọc toàn bộ dữ liệu từ inputStream
                        val reader: BufferedReader = inputStream.bufferedReader()
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()
                    }
                } catch (e: UnknownHostException) {
                    // Xử lý trường hợp không có kết nối mạng
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Không có kết nối mạng. Vui lòng kiểm tra lại.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: SocketTimeoutException) {
                    // Xử lý trường hợp hết thời gian chờ
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Kết nối quá chậm, vui lòng thử lại sau.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Xử lý các lỗi khác
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            startFloatingService(requireContext())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private suspend fun getBadges(cookiesMap: Map<String, String>): Boolean {
        val document: Document = Jsoup.connect("https://traodoisub.com/view/chtiktok/")
            .cookies(cookiesMap) // Sử dụng cookies từ Map
            .get()
        // Lấy tất cả các thẻ <span> với class "badge badge-soft-success"
        val badges: List<Element> = document.select("span.badge.badge-soft-success")

        // In ra nội dung
        println("Badges:$badges")
        if (badges.isEmpty()) {
            // Kiểm tra nếu không tìm thấy thẻ có class "badge badge-soft-success"
            // Kiểm tra sự xuất hiện của từ "đăng nhập" trong toàn bộ nội dung văn bản của trang
            val pageText = document.text()
            if (pageText.contains("Đăng nhập", ignoreCase = true)) {
                println("Tìm thấy từ 'đăng nhập' trên trang.")
                return false
            } else {
                println("Không tìm thấy thẻ badge cũng như từ 'đăng nhập'.")
                withContext(Dispatchers.Main) {
                    val err = listOf("Hãy thêm tài khoản Tiktok trước","")
                    binding.materialSpinner.setItems(err)
                }
                return true
            }
        }
        val badgeContents = badges.map { it.text() }
        withContext(Dispatchers.Main) {
            nickdangchay = badgeContents[0]
            namenickrun = badgeContents[0]
            binding.materialSpinner.setItems(badgeContents)
            binding.materialSpinner.setOnItemSelectedListener { _, _, _, item ->
                nickdangchay = item.toString()
                namenickrun = item.toString()
                println("id nick đang chạy: $nickdangchay")
            }
        }
        return true
    }
    private suspend fun resetToken(jsonFile: File, jsonObject: JSONObject): String? {
        val username = jsonObject.getString("username")
        val password = jsonObject.getString("password")
        val token = jsonObject.getString("token")
        // Gửi yêu cầu POST để đăng nhập và lấy cookie
        val url = URL("https://traodoisub.com/scr/login.php")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }
        val postData = "username=$username&password=$password"
        connection.outputStream.use {
            it.write(postData.toByteArray())
            it.flush()
        }
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        if (!response.contains("success")) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Sai tài khoản hoặc mật khẩu, hãy đăng nhập lại!", Toast.LENGTH_SHORT).show()
            }
            return ""
        }

        // Lấy cookie từ header và đóng kết nối
        val cookie = connection.getHeaderField("Set-Cookie")
        val jsonData = JSONObject().apply {
            put("username", username)
            put("password", password)
            put("cookies", cookie)
            put("token", token)
        }
        FileWriter(jsonFile).use { writer -> writer.write(jsonData.toString()) }
        println("Đã cập nhập cookie vào file JSON.")
        connection.disconnect()
        return cookie
    }
    fun startFloatingService(context: Context) {
        val intent = Intent(context, TraoDoiSubTikTokFloating::class.java)
        context.startService(intent)
    }
}