package com.example.myapplication

import android.content.Context

class LoadLocalWGT(private val context: Context) {



    init {
        assetsTransferToLocal()
    }

    private fun getWgtFile() {

    }

    /**
     * Assets transfer to local
     * 获取文件名称
     */
   private fun assetsTransferToLocal(): Array<out String>? {
        val listS = ArrayList<String>()

        println("加载数据")
        val list = context.assets.list("wgt")
        if (list.isNullOrEmpty()) {
            println("没有这个文件夹或者文件为null")
        } else {
            list.forEach {
                val length = it.length
                val length2 = it.length - 3

                val toCharArray = it.toCharArray(length2, length)
                if (toCharArray.concatToString() == "wgt") {
                    listS.add(it)
                }

            }
        }

        return list
    }

}