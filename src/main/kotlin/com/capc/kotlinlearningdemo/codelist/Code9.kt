package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.PoDetailInfo
import com.capc.kotlinlearningdemo.vertx.startHttpServer
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.apache.commons.lang3.RandomStringUtils

/**
 * 示例9：响应式框架+coroutine优化后的批量查询
 *
 * p.s. 耗时 4.6s
 * @author chendk on 2021/8/9
 */
@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    // 模拟被调用系统接口
    startHttpServer()

    // 模拟查询参数
    val token = "123"
    val poNameList = mutableListOf<String>()
    repeat(1000) {
        poNameList.add(RandomStringUtils.randomAlphanumeric(6))
    }

    // 调用响应式的批量查询
    val start = System.currentTimeMillis()
    val poDetailInfos = batchGetPoDetailInfoReactive3(token, poNameList)
    println(poDetailInfos)
    println("cost ${System.currentTimeMillis() - start} ms")
}


@OptIn(DelicateCoroutinesApi::class)
suspend fun batchGetPoDetailInfoReactive3(token: String, poNameList: List<String>): List<PoDetailInfo> {
    // 1.校验账户信息
    val userInfo = checkAccountReactive2(token).await()
    val userId = userInfo?.userId ?: throw RuntimeException("401 用户未授权")

    val deferredList = poNameList.map { poName ->
        // 可以类比于CompletableFuture.supplyAsync，只不过利用了协程作为异步任务的载体
        GlobalScope.async {
            // 2.查询组合信息
            val poInfo = findPoInfoReactive2(userId, poName).await()
            val poCode = poInfo?.poCode ?: return@async null

            val poDetailInfo = PoDetailInfo()
            poDetailInfo.poInfo = poInfo

            // 3.查询组合的成分基金信息
            val fundInfoListFuture = getFundInfosReactive2(userId, poInfo.fundCodes)
            // 4.查询组合资产信息
            val poAssetFuture = getPoAssetReactive2(userId, poCode)
            poDetailInfo.fundInfos = fundInfoListFuture.await()
            poDetailInfo.poAsset = poAssetFuture.await()

            poDetailInfo
        }
    }
    // Deferred.await()可类比于java线程的Future.get()，不同的是一个挂起协程，一个挂起阻塞线程
    return deferredList.mapNotNull { it.await() }
}