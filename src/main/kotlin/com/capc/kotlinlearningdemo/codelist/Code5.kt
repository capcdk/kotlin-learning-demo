package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.PoDetailInfo
import kotlin.concurrent.thread

/**
 * 示例5：传统多线程下的异步协作
 *
 * p.s. 运行耗时3s
 * @author chendk on 2021/8/9
 */
fun main() {
    val token = "123"
    val poName = "一个组合"

    val poDetailInfo = getPoDetailInfoAsync(token, poName)
    println(poDetailInfo)
}

fun getPoDetailInfoAsync(token: String, poName: String): PoDetailInfo? {
    val poDetailInfo = PoDetailInfo()

    // 1.校验账户信息
    val userInfo = checkAccount(token)
    val userId = userInfo?.userId ?: throw RuntimeException("401 用户未授权")

    // 2.查询组合信息
    val poInfo = findPoInfo(userId, poName)
    val poCode = poInfo?.poCode ?: return null
    poDetailInfo.poInfo = poInfo

    val t1 = thread {
        // 3.查询组合的成分基金信息
        val fundInfoList = getFundInfos(userId, poInfo.fundCodes)
        poDetailInfo.fundInfos = fundInfoList
    }

    val t2 = thread {
        // 4.查询组合资产信息
        val poAsset = getPoAsset(userId, poCode)
        poDetailInfo.poAsset = poAsset
    }

    t1.join()
    t2.join()
    return poDetailInfo
}