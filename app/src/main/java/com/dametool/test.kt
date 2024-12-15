package com.dametool

import java.net.HttpURLConnection
import java.net.URL


fun main() {
    val url = URL("https://www.facebook.com/100069297840128_868641808789073")
    val httpConn = url.openConnection() as HttpURLConnection
    httpConn.requestMethod = "GET"

    val responseStream = if (httpConn.responseCode / 100 == 2)
        httpConn.inputStream
    else
        httpConn.errorStream
    println(httpConn.url.toString())
}
