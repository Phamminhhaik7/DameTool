package com.dametool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dametool.databinding.TraodoisubHomeBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

class TraoDoiSubHome : Fragment() {

    private var _binding: TraodoisubHomeBinding? = null // Thay đổi kiểu dữ liệu
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TraodoisubHomeBinding.inflate(inflater, container, false) // Thay đổi inflate
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolinuse = TraoDoiSubHome()
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
        binding.TraoDoiSubTikTok.setOnClickListener {
            toolinuse = TraoDoiSubTikTok()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.TraoDoiSubInstragram.setOnClickListener {
            toolinuse = TraoDoiSubInstragram()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.TraoDoiSubFacebook.setOnClickListener {
            toolinuse = TraoDoiSubFacebook()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .addToBackStack(null)
                .commit()
        }
        binding.TraoDoiSubLogout.setOnClickListener {
            val hiddenDir = File(requireContext().getExternalFilesDir(null), ".DameToolData")
            val jsonFile = File(hiddenDir, "traodoisublogindata.json")
            jsonFile.delete()
            toolinuse = TraoDoiSubLogin()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
        }
        val hiddenDir = File(requireContext().getExternalFilesDir(null), ".DameToolData")
        val jsonFile = File(hiddenDir, "traodoisublogindata.json")
        if (!jsonFile.exists()) {
            toolinuse = TraoDoiSubLogin()
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, toolinuse)
                .commit()
                return
        }
        val jsonString = jsonFile.readText()
        val jsonObject = JSONObject(jsonString)
        val token = jsonObject.getString("token")
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://traodoisub.com/api/?fields=profile&access_token=$token")
                with(url.openConnection() as HttpURLConnection) {
                    connectTimeout = 5000
                    readTimeout = 5000
                    requestMethod = "GET"
                    println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                    // Đọc dữ liệu từ inputStream
                    val reader: BufferedReader = inputStream.bufferedReader()
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    if(!response.contains("user")){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Có lỗi xảy ra, hãy thử đăng nhập lại!", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(response.toString(), JsonObject::class.java)
                    val data = jsonObject.get("data")
                    val dataObject = gson.fromJson(data.toString(), JsonObject::class.java)
                    val user = dataObject.get("user").asString
                    val xu = dataObject.get("xu").asString
                    withContext(Dispatchers.Main) {
                        binding.TraoDoiSubUsername.text = user
                        binding.TraoDoiSubCoin.text = "Xu: $xu"
                    }
                    reader.close()
                }
            } catch (e: UnknownHostException) {
                // Xử lý trường hợp không có kết nối mạng
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Không có kết nối mạng. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SocketTimeoutException) {
                // Xử lý trường hợp hết thời gian chờ
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Kết nối quá chậm, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                // Xử lý các lỗi khác
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Có lỗi xảy ra, vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}