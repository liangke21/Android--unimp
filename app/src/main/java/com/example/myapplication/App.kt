package com.example.myapplication

import android.app.Application
import android.util.Log
import io.dcloud.feature.sdk.DCSDKInitConfig
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.IDCUniMPPreInitCallback
import io.dcloud.feature.sdk.MenuActionSheetItem


class App :Application(){


    override fun onCreate() {
        super.onCreate()


        val item = MenuActionSheetItem("关于", "gy")
        val sheetItems: MutableList<MenuActionSheetItem> = ArrayList()
        sheetItems.add(item)
        val config = DCSDKInitConfig.Builder()
            .setCapsule(true)
            .setMenuDefFontSize("16px")
            .setMenuDefFontColor("#ff00ff")
            .setMenuDefFontWeight("normal")
            .setMenuActionSheetItems(sheetItems)
            .build()
        DCUniMPSDK.getInstance().initialize(this, config, IDCUniMPPreInitCallback(){

            Log.i("unimp","在初始化完成时----"+it);
        })


    }


}