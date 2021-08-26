package com.app.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.app.scanner.db.DaoManager
import com.app.scanner.db.DbOperationHelper
import com.app.scanner.util.LogUtils
import com.app.scanner.vo.AudioVo


/**********************************************
 * Filename： MainActivity
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 * ------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)



        val helper: DbOperationHelper<AudioVo> = DbOperationHelper<AudioVo>(
            AudioVo::class.java,
            DaoManager.getInstance().daoSession.audioVoDao
        )

        findViewById<TextView>(R.id.tv_usb1_in).setOnClickListener {
            LogUtils.debug("click tv_usb1_in")
            var intent = Intent()
            intent.action = ScannerService.MOCK_IN
            sendBroadcast(intent)
        }

        findViewById<TextView>(R.id.tv_usb1_out).setOnClickListener {
            var intent = Intent()
            intent.action = ScannerService.MOCK_OUT
            sendBroadcast(intent)
        }

        findViewById<TextView>(R.id.tv_insert).setOnClickListener {
            val albumVo = AudioVo()
            albumVo.name = "1111"
            albumVo.symbolName = "1"
            albumVo.duration = "1"
            albumVo.favFlag = "1"
            albumVo.size = "1"
            albumVo.folderId = 1L
            albumVo.albumId = 1L
            albumVo.path = "111111"
            albumVo.year = "1"
            albumVo.genreId = 1L
            helper.insert(albumVo)
            Toast.makeText(this, "已写入数据", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tv_query).setOnClickListener {
            val list: List<AudioVo> = helper.queryAll()
            Toast.makeText(this, "当前数据量：" + list.size, Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tv_get).setOnClickListener {
            val list: List<AudioVo> = helper.queryAll()
            if (list.size > 0) {
                Toast.makeText(this, "歌名：" + list.get(0).name, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_update).setOnClickListener {
            val list: List<AudioVo> = helper.queryAll()
            if (list.size > 0) {
                var teml = list.get(0)
                teml.name = "七里香"
                helper.update(teml)
                Toast.makeText(this, "数据已经更新", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_delete).setOnClickListener {
            val list: List<AudioVo> = helper.queryAll()
            if (list.size > 0) {
                var teml = list.get(0)
                helper.delete(teml)
                Toast.makeText(this, "数据已经删除", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_skip_jni).setOnClickListener {
            var intent = Intent()
            intent.setClass(this, TestJniMainAct::class.java)
            startActivity(intent)
        }

    }

}