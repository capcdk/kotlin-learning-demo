package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.PoDetailInfo
import org.apache.commons.lang3.RandomStringUtils
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

/**
 * 示例6：传统多线程下的异步协作（批量查询）
 *
 * ① 该示例里使用java原生并发库里的Future，与后续的vert.x Future有所不同;
 * ② 发生异步任务嵌套时，不能提交至同一个线程池：因为线程的数量有限，
 *   大量的父子任务提交到同一个线程池时，会造成线程资源的竞争导致死锁。
 *
 * p.s. 耗时 22s
 * @author chendk on 2021/8/9
 */
val threadPool = Executors.newFixedThreadPool(100)
val threadPool2 = Executors.newFixedThreadPool(150)

fun main() {
    val token = "123"
    val poNameList = mutableListOf<String>()
    repeat(1000) {
        poNameList.add(RandomStringUtils.randomAlphanumeric(6))
    }

    measureTimeMillis {
        val poDetailInfo = batchGetPoDetailInfoAsync(token, poNameList)
        println(poDetailInfo)
    }.let { println("cost $it ms") }
}

fun batchGetPoDetailInfoAsync(token: String, poNameList: List<String>): List<PoDetailInfo> {
    // 1.校验账户信息
    val userInfo = checkAccount(token)
    val userId = userInfo?.userId ?: throw RuntimeException("401 用户未授权")

    val poDetailFutureList = poNameList.map { poName ->
        threadPool.submit<PoDetailInfo?> {
            // 2.查询组合信息
            val poInfo = findPoInfo(userId, poName)
            val poCode = poInfo?.poCode ?: return@submit null

            val poDetailInfo = PoDetailInfo()
            poDetailInfo.poInfo = poInfo

            // 3.查询组合的成分基金信息
            val phase1 = threadPool2.submit {
                val fundInfoList = getFundInfos(userId, poInfo.fundCodes)
                poDetailInfo.fundInfos = fundInfoList
            }

            // 4.查询组合资产信息
            val phase2 = threadPool2.submit {
                val poAsset = getPoAsset(userId, poCode)
                poDetailInfo.poAsset = poAsset
            }

            // 阻塞直到步骤3、4完成
            phase1.get()
            phase2.get()
            poDetailInfo
        }
    }

    return poDetailFutureList.mapNotNull { it.get() }
}