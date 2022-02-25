package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.*
import com.capc.kotlinlearningdemo.vertx.server.mock.getMockPath
import com.capc.kotlinlearningdemo.vertx.server.mock.startHttpServer
import com.capc.kotlinlearningdemo.vertx.webClient
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import org.apache.commons.lang3.RandomStringUtils
import java.math.BigDecimal
import kotlin.random.Random

/**
 * 示例7：响应式改造后的批量查询
 *
 * p.s. 耗时 4.6s
 * @author chendk on 2021/8/9
 */
fun main() {
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
    batchGetPoDetailInfoReactive(token, poNameList,
        // 成功回调
        { poDetailInfo ->
            println(poDetailInfo)
            println("cost ${System.currentTimeMillis() - start} ms")
        },
        // 异常回调
        {
            System.err.println(it)
        })
}

fun batchGetPoDetailInfoReactive(
    token: String,
    poNameList: List<String>,
    callback: (List<PoDetailInfo>) -> Unit,
    failCallback: (Throwable) -> Unit
) {
    // 1.校验账户信息
    checkAccountReactive(token) { userInfo ->
        val userId = userInfo?.userId ?: return@checkAccountReactive failCallback(RuntimeException("401 用户未授权"))

        val poDetailFutureList = poNameList.map { poName ->
            Future.future<PoDetailInfo?> { promise ->

                // 2.查询组合信息
                findPoInfoReactive(userId, poName) { poInfo ->
                    val poCode = poInfo?.poCode ?: return@findPoInfoReactive promise.complete(null)
                    val poDetailInfo = PoDetailInfo()
                    poDetailInfo.poInfo = poInfo

                    // 3.查询组合的成分基金信息
                    val phase1 = Future.future<Any?> { queryPromise ->
                        getFundInfosReactive(userId, poInfo.fundCodes) { fundInfoList ->
                            poDetailInfo.fundInfos = fundInfoList
                            queryPromise.complete()
                        }
                    }

                    // 4.查询组合资产信息
                    val phase2 = Future.future<Any?> { queryPromise ->
                        getPoAssetReactive(userId, poCode) { poAsset ->
                            poDetailInfo.poAsset = poAsset
                            queryPromise.complete()
                        }
                    }
                    CompositeFuture.join(phase1, phase2).onComplete {
                        if (it.succeeded()) {
                            promise.complete(poDetailInfo)
                        } else {
                            promise.complete(null)
                        }
                    }
                }
            }
        }

        CompositeFuture.join(poDetailFutureList).onComplete {
            if (it.succeeded()) {
                callback(it.result().list())
            } else {
                failCallback(it.cause())
            }
        }
    }
}


/**
 * 模拟从账户中心校验查询用户信息
 */
fun checkAccountReactive(token: String, callback: (UserInfo?) -> Unit) {
    // mock blockingRpcClient.doGet()
    webClient.getAbs(getMockPath(1000)).send {
        if (token.isBlank() || it.failed()) {
            callback(null)
        } else {
            callback(UserInfo("123", "用户名"))
        }
    }
}

/**
 * 模拟从组合交易系统查询组合信息
 */
fun findPoInfoReactive(userId: String, poName: String, callback: (PoInfo?) -> Unit) {
    // mock blockingRpcClient.doGet()
    webClient.getAbs(getMockPath(1000)).send {
        if (userId.isBlank() || poName.isBlank() || it.failed()) {
            callback(null)
        } else {
            callback(PoInfo(poName, "ZH123", listOf("000001", "000002")))
        }
    }
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getFundInfosReactive(userId: String, fundCodes: List<String>, callback: (List<Fund>) -> Unit) {
    // mock blockingRpcClient.doGet()
    webClient.getAbs(getMockPath(1000)).send {
        if (userId.isBlank() || fundCodes.isEmpty() || it.succeeded()) {
            callback(emptyList())
        } else {
            callback(fundCodes.map { Fund("基金${RandomStringUtils.randomAlphanumeric(3)}", it) })
        }
    }
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getPoAssetReactive(userId: String, poCode: String, callback: (PoAsset?) -> Unit) {
    // mock blockingRpcClient.doGet()
    webClient.getAbs(getMockPath(1000)).send {
        if (userId.isBlank() || poCode.isBlank() || it.succeeded()) {
            callback(null)
        } else {
            callback(PoAsset(BigDecimal.valueOf(Random.nextLong(0, 10000)), BigDecimal.valueOf(Random.nextLong(0, 10000))))
        }
    }
}