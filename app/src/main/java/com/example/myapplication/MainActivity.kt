package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.dcloud.common.adapter.util.Logger
import io.dcloud.feature.barcode2.BarcodeProxy.context
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val (name, path) = getWgt("__UNI__FC58B10.wgt")


        findViewById<Button>(R.id.button).setOnClickListener {

            val uniMPReleaseConfiguration = UniMPReleaseConfiguration()
            uniMPReleaseConfiguration.wgtPath = path
            uniMPReleaseConfiguration.password =null

            DCUniMPSDK.getInstance().releaseWgtToRunPath("__UNI__FC58B10", uniMPReleaseConfiguration) { code, pArgs ->
                if (code == 1) {

                    try {
                        DCUniMPSDK.getInstance().openUniMP(this@MainActivity, "__UNI__FC58B10")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    println("释放完成")
                } else {
                    //释放wgt失败
                    println("释放失败")
                }
            }


        }


        findViewById<Button>(R.id.button2).setOnClickListener {

            try {
                val uniMPOpenConfiguration = UniMPOpenConfiguration()
                uniMPOpenConfiguration.splashClass = MySplashView::class.java
                val unimp = DCUniMPSDK.getInstance().openUniMP(context, "__UNI__FC58B10",uniMPOpenConfiguration)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        }

        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack { appid, id ->
            when (id) {
                "gy" -> {
                    println(appid + "用户点击了关于")
                }
            }
        }
    }


    /**
     * Get wgt
     *  assets 文件转本地文件
     * @param name
     * @return
     */
    private fun getWgt(name: String): Wat {

        val open = applicationContext.assets.open("wgt/$name")

        val readBytes = open.readBytes()
        val path = getExternalFilesDir("wgt")?.path
        val file = File("$path/$name")
        val fileOutputStream = FileOutputStream(file)

        fileOutputStream.write(readBytes)

        readBytes.clone()
        fileOutputStream.close()


        return Wat(name = name, path = file.path)
    }


}
