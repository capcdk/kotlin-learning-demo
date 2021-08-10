package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.*
import org.apache.commons.lang3.RandomStringUtils
import java.math.BigDecimal
import kotlin.random.Random

/**
 * 示例4：同步协作
 *
 * p.s. 每个依赖的外部接口模拟耗时为1s，总耗时为4s。
 * @author chendk on 2021/8/9
 */
fun main() {
    val token = "123"
    val poName = "一个组合"

    val poDetailInfo = getPoDetailInfo(token, poName)
    println(poDetailInfo)
}

fun getPoDetailInfo(token: String, poName: String): PoDetailInfo? {
    val poDetailInfo = PoDetailInfo()

    // 1.校验账户信息
    val userInfo = checkAccount(token)
    val userId = userInfo?.userId ?: throw RuntimeException("401 用户未授权")

    // 2.查询组合信息
    val poInfo = findPoInfo(userId, poName)
    val poCode = poInfo?.poCode ?: return null
    poDetailInfo.poInfo = poInfo

    // 3.查询组合的成分基金信息
    val fundInfoList = getFundInfos(userId, poInfo.fundCodes)
    poDetailInfo.fundInfos = fundInfoList

    // 4.查询组合资产信息
    val poAsset = getPoAsset(userId, poCode)
    poDetailInfo.poAsset = poAsset

    return poDetailInfo
}

/**
 * 模拟从账户中心校验查询用户信息
 */
fun checkAccount(token: String): UserInfo? {
    // mock blockingRpcClient.doGet()
    Thread.sleep(1000)
    return if (token.isBlank()) null else UserInfo("123", "用户名")
}

/**
 * 模拟从组合交易系统查询组合信息
 */
fun findPoInfo(userId: String, poName: String): PoInfo? {
    // mock blockingRpcClient.doGet()
    Thread.sleep(1000)
    return if (userId.isBlank() || poName.isBlank()) null else PoInfo(poName, "ZH123", listOf("000001", "000002"))
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getFundInfos(userId: String, fundCodes: List<String>): List<Fund> {
    // mock blockingRpcClient.doGet()
    Thread.sleep(1000)
    return if (userId.isBlank() || fundCodes.isEmpty())
        emptyList()
    else {
        fundCodes.map { Fund("基金${RandomStringUtils.randomAlphanumeric(3)}", it) }
    }
}

/**
 * 模拟从基金交易系统查询基金信息
 */
fun getPoAsset(userId: String, poCode: String): PoAsset? {
    // mock blockingRpcClient.doGet()
    Thread.sleep(1000)
    return if (userId.isBlank() || poCode.isBlank())
        null
    else {
        PoAsset(BigDecimal.valueOf(Random.nextLong(0, 10000)), BigDecimal.valueOf(Random.nextLong(0, 10000)))
    }
}