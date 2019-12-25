package com.april.develop.helper

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.BigDecimal

/**
 * 获取缓存大小
 *
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun Context.getTotalCacheSize(): Double {
    var cacheSize = cacheDir.getFolderSize()
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        cacheSize += externalCacheDir?.getFolderSize() ?: 0
    }
    return cacheSize.toDouble()
}

/**
 * 清空缓存
 */
fun Context.clearAllCache(): Boolean {
    var b = false
    deleteDir(cacheDir)
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        b = deleteDir(externalCacheDir)
    }
    return b
}

private fun deleteDir(dir: File?): Boolean {
    if (dir == null) {
        return true
    }
    if (dir.isDirectory) {
        val children = dir.list() ?: return true
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
    }
    return true
}

/**
 *  获取文件大小
 *
 *  Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，
 *  一般放一些长时间保存的数据
 *  Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，
 *  一般存放临时缓存数据
 */
@Throws(Exception::class)
fun File.getFolderSize(): Long {
    var size: Long = 0
    try {
        val fileList = listFiles() ?: return size
        for (i in fileList.indices) {
            // 如果下面还有文件
            size = if (fileList[i].isDirectory) {
                size + fileList[i].getFolderSize()
            } else {
                size + fileList[i].length()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 * 格式化单位
 *
 * @param size
 * @return
 */
fun formatCacheSize(size: Double): String {
    //            return size + "Byte";
    val kiloByte = size / 1024
    if (kiloByte < 1) {
        return "0.00M"
    }
    val megaByte = kiloByte / 1024
    if (megaByte < 1) {
        return "${BigDecimal(kiloByte.toString()).setScale(
            2,
            BigDecimal.ROUND_HALF_UP
        ).toPlainString()}KB"
    }
    val gigaByte = megaByte / 1024
    if (gigaByte < 1) {
        return "${BigDecimal(megaByte.toString()).setScale(
            2,
            BigDecimal.ROUND_HALF_UP
        ).toPlainString()}M"
    }
    val teraBytes = gigaByte / 1024
    if (teraBytes < 1) {
        return "${BigDecimal(gigaByte.toString()).setScale(
            2,
            BigDecimal.ROUND_HALF_UP
        ).toPlainString()}GB"
    }
    return "${BigDecimal(teraBytes).setScale(
        2,
        BigDecimal.ROUND_HALF_UP
    ).toPlainString()}TB"
}