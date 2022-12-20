package com.geekbrain.android.withKotlin

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.geekbrain.android.withKotlin.databinding.ActivityMainWebviewBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWebviewBinding.inflate(layoutInflater)

        binding.ok.setOnClickListener(clickListener)
        setContentView(binding.root)
    }


    private val clickListener: View.OnClickListener = object : View.OnClickListener {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onClick(v: View?) {
            try {
                val uri = URL(binding.url.text.toString())
                val handler = Handler()
                Thread {

                    var urlConnection: HttpsURLConnection? = null
                    try {

                        urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                            requestMethod = "GET"
                            readTimeout = 10000
                        }

                        val responseStatusCode = urlConnection.responseCode
                        lateinit var reader: BufferedReader
                        if (responseStatusCode != HttpsURLConnection.HTTP_OK) {
                            reader = BufferedReader(InputStreamReader(urlConnection.errorStream))
                            Log.i("", "HttpsURLConnection: $responseStatusCode ")
                        } else{
                            reader = BufferedReader(InputStreamReader(urlConnection.inputStream))

                        }
                        var result = ""
                        result = reader.let { getLines(it) }



                        handler.post {

                            binding.webview.loadData(
                                result,
                                "text/html; charset=utf-8",
                                "utf-8"
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("", "Failed connection", e)
                        e.printStackTrace()

                    } finally {
                        urlConnection?.disconnect()

                    }
                }.start()
            }catch (e: MalformedURLException){
                Log.e("", "Fail URI ", e )
                e.printStackTrace()
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun getLines(reader: BufferedReader): String =
            reader.lines().collect(Collectors.joining("\n"))

    }

}