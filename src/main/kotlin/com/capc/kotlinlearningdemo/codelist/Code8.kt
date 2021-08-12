package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.*
import com.capc.kotlinlearningdemo.vertx.getMockPath
import com.capc.kotlinlearningdemo.vertx.startHttpServer
import com.capc.kotlinlearningdemo.vertx.webClient
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import org.apache.commons.lang3.RandomStringUtils
import java.math.BigDecimal
import kotlin.random.Random

/**
 * 示例8：响应式框架封装优化后的批量查询
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
    batchGetPoDetailInfoReactive2(token, poNameList)
        // 成功回调
        .onSuccess { poDetailInfo ->
            println(poDetailInfo)
            println("cost ${System.currentTimeMillis() - start} ms")
        }
        // 失败回调
        .onFailure { System.err.println(it) }
}

fun batchGetPoDetailInfoReactive2(token: String, poNameList: List<String>): Future<List<PoDetailInfo>> {
    // 1.校验账户信息
    return checkAccountReactive2(token)
        .compose { userInfo ->
            val userId = userInfo?.userId ?: return@compose Future.failedFuture(RuntimeException("401 用户未授权"))
            val poDetailFutureList = poNameList.map { poName ->
                // 2.查询组合信息
                findPoInfoReactive2(userId, poName).compose poInfoCompose@{ poInfo ->
                    val poCode = poInfo?.poCode ?: return@poInfoCompose Future.succeededFuture(null)
                    val poDetailInfo = PoDetailInfo()
                    poDetailInfo.poInfo = poInfo

                    // 3.查询组合的成分基金信息
                    val phase1 = getFundInfosReactive2(userId, poInfo.fundCodes).onSuccess { fundInfoList ->
                        poDetailInfo.fundInfos = fundInfoList
                    }

                    // 4.查询组合资产信息
                    val phase2 = getPoAssetReactive2(userId, poCode).onSuccess { poAsset ->
                        poDetailInfo.poAsset = poAsset
                    }
                    CompositeFuture.join(phase1, phase2).map(poDetailInfo)
                }
            }
            CompositeFuture.join(poDetailFutureList).map { it.result().list() }
        }
}


/**
 * 模拟从账户中心校验查询用户信息
 */
fun checkAccountReactive2(token: String): Future<UserInfo?> {
    // mock blockingRpcClient.doGet()
    return webClient.getAbs(getMockPath(1000)).send()
        .map {
            if (token.isBlank()) {
                null
            } else {
                UserInfo("123", "用户名")
            }
        }
}

/**
 * 模拟从组合交易系统查询组合信息
 */
fun findPoInfoReactive2(userId: String, poName: String): Future<PoInfo?> {
    // mock blockingRpcClient.doGet()
    return webClient.getAbs(getMockPath(1000)).send()
        .map {
            if (userId.isBlank() || poName.isBlank()) {
                null
            } else {
                PoInfo(poName, "ZH123", listOf("000001", "000002"))
            }
        }
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getFundInfosReactive2(userId: String, fundCodes: List<String>): Future<List<Fund>> {
    // mock blockingRpcClient.doGet()
    return webClient.getAbs(getMockPath(1000)).send()
        .map {
            if (userId.isBlank() || fundCodes.isEmpty()) {
                emptyList()
            } else {
                fundCodes.map { Fund("基金${RandomStringUtils.randomAlphanumeric(3)}", it) }
            }
        }
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getPoAssetReactive2(userId: String, poCode: String): Future<PoAsset?> {
    // mock blockingRpcClient.doGet()
    return webClient.getAbs(getMockPath(1000)).send()
        .map {
            if (userId.isBlank() || poCode.isBlank()) {
                null
            } else {
                PoAsset(BigDecimal.valueOf(Random.nextLong(0, 10000)), BigDecimal.valueOf(Random.nextLong(0, 10000)))
            }
        }
}