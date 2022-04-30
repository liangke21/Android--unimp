package com.example.myapplication


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import io.dcloud.feature.barcode2.BarcodeProxy.context
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var name = ""
        var path = ""

        findViewById<Button>(R.id.button3).setOnClickListener {//获取异步的小程序
            getYunWgt("/wgt/__UNI__58E29BC", "__UNI__58E29BC", object : OnDownloadListener {

                override fun onDownloadSuccess(file: File?, n: String) {
                    path = file!!.path
                    name = n
                }

                override fun onDownloading(progress: Int) {
                    findViewById<ProgressBar>(R.id.progressBar2).progress = progress
                    println(progress)
                }

                override fun onDownloadFailed() {

                }
            })

        }

        val (name1, path1) = getWgt("__UNI__FC58B10")


        fun releaseAndStart(name: String, path: String) {
            val uniMPReleaseConfiguration = UniMPReleaseConfiguration()
            uniMPReleaseConfiguration.wgtPath = path
            uniMPReleaseConfiguration.password = null

            DCUniMPSDK.getInstance().releaseWgtToRunPath(name, uniMPReleaseConfiguration) { code, pArgs ->
                if (code == 1) {

                    try {
                        DCUniMPSDK.getInstance().openUniMP(this@MainActivity, name)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    println("释放完成")
                } else {
                    println("释放失败")
                }
            }
        }
        findViewById<Button>(R.id.button).setOnClickListener {//释放wgt并启动

            releaseAndStart(name1, path1)
        }

        findViewById<Button>(R.id.button4).setOnClickListener {//释放
            releaseAndStart(name, path)
        }


        findViewById<Button>(R.id.button2).setOnClickListener {//启动小程序

            try {
                val uniMPOpenConfiguration = UniMPOpenConfiguration()
                uniMPOpenConfiguration.splashClass = MySplashView::class.java
                DCUniMPSDK.getInstance().openUniMP(context, name, uniMPOpenConfiguration)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }







        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack { aphid, id ->
            when (id) {
                "gy" -> {
                    println(aphid + "aphid")
                }
            }
        }


    }


    /**
     * Get yun wgt
     *  云上获取
     * @param uri
     */
    private fun getYunWgt(urlM: String, fileName: String, listener: OnDownloadListener) {


        val url = "https://55-1251889734.cos.ap-beijing-1.myqcloud.com$urlM.wgt"

        println(url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                listener.onDownloadFailed()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response?) {
                var `is`: InputStream? = null
                val buf = ByteArray(2048)
                var len: Int
                var fos: FileOutputStream? = null
                // 储存下载文件的目录
                val savePath: String = isExistDir()
                Log.w(TAG, "存储下载目录：$savePath")
                try {
                    `is` = response?.body()?.byteStream()
                    val total: Long? = response?.body()?.contentLength()
                    val file = File(savePath, "$fileName.wgt")
                    Log.w(TAG, "最终路径：$file")
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (`is`!!.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total!! * 100).toInt()
                        // 下载中
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    // 下载完成
                    listener.onDownloadSuccess(file, fileName)
                } catch (e: java.lang.Exception) {
                    listener.onDownloadFailed()
                } finally {
                    try {
                        `is`?.close()
                    } catch (e: IOException) {
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        })
    }


    /**
     * Get wgt
     *  assets 文件转本地文件
     * @param name
     * @return
     */
    private fun getWgt(name: String): Wat {

        val open = applicationContext.assets.open("wgt/$name.wgt")

        val readBytes = open.readBytes()
        val path = getExternalFilesDir("wgt")?.path
        val file = File("$path/$name.wgt")
        val fileOutputStream = FileOutputStream(file)

        fileOutputStream.write(readBytes)

        readBytes.clone()
        fileOutputStream.close()


        return Wat(name = name, path = file.path)
    }


    @Throws(IOException::class)
    private fun isExistDir(): String {
        val path = getExternalFilesDir("download")?.path

        // 下载位置
        val downloadFile = File(path, "wgt")
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        val savePath = downloadFile.absolutePath
        Log.w(TAG, "下载目录：$savePath")
        return savePath
    }


    interface OnDownloadListener {
        /**
         * 下载成功
         */
        fun onDownloadSuccess(file: File?, n: String)

        /**
         * @param progress
         * 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }
}
