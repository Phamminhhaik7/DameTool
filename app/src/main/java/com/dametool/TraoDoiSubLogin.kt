package com.dametool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dametool.databinding.TraodoisubLoginBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

class TraoDoiSubLogin : Fragment() {

    private var _binding: TraodoisubLoginBinding? = null // Thay đổi kiểu dữ liệu
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TraodoisubLoginBinding.inflate(inflater, container, false) // Thay đổi inflate
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolinuse = TraoDoiSubLogin()
        binding.included.GolikeHome.setOnClickListener {
            toolinuse = GolikeHome()
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
        binding.TraoDoiSubLogin.setOnClickListener {
            val username = binding.TraoDoiSubUsername.editText?.text.toString()
            val password = binding.TraoDoiSubPassword.editText?.text.toString()
            println("Username: $username")
            println("Password: $password")
            if (username.isEmpty() || password.isEmpty()) {
                println("Vui lòng nhập đầy đủ thông tin đăng nhập!")
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val gson = Gson()
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
                        return@launch
                    }

                    // Lấy cookie từ header và đóng kết nối
                    val cookie = connection.getHeaderField("Set-Cookie")
                    connection.disconnect()

                    // Gửi yêu cầu GET để lấy token
                    val url2 = URL("https://traodoisub.com/view/setting/load.php")
                    val connection2 = (url2.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        setRequestProperty("Cookie", cookie)
                    }
                    val response2 = connection2.inputStream.bufferedReader().use { it.readText() }
                    connection2.disconnect()
                    // Kiểm tra xem có trường "tokentds" không
                    if (!response2.contains("tokentds")) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Có lỗi xảy ra, hãy thử đăng nhập lại!", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    // Lấy token từ phản hồi JSON

                    val jsonObject = gson.fromJson(response2, JsonObject::class.java)
                    val token = jsonObject.get("tokentds").asString

                    // Lưu dữ liệu vào file JSON
                    val jsonData = JSONObject().apply {
                        put("username", username)
                        put("password", password)
                        put("cookies", cookie)
                        put("token", token)
                    }
                    val hiddenDir = File(requireContext().getExternalFilesDir(null), ".DameToolData")
                    val jsonFile = File(hiddenDir, "traodoisublogindata.json")
                    FileWriter(jsonFile).use { writer -> writer.write(jsonData.toString()) }

                    // Chuyển đến fragment tiếp theo
                    toolinuse = TraoDoiSubHome()
                    (activity as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, toolinuse)
                        .commit()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        val message = when (e) {
                            is UnknownHostException -> "Không có kết nối mạng. Vui lòng kiểm tra lại."
                            is SocketTimeoutException -> "Kết nối quá chậm, vui lòng thử lại sau."
                            else -> "Có lỗi xảy ra, vui lòng thử lại."
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}