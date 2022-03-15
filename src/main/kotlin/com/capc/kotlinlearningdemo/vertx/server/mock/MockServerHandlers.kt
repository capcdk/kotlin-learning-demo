package com.capc.kotlinlearningdemo.vertx.server.mock

import com.capc.kotlinlearningdemo.vertx.RedisConfig
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import org.springframework.util.MimeTypeUtils

/**
 * @author chendk on 2022/2/24
 */
val mockCost: (RoutingContext) -> Unit = { context ->
    val cost = context.request().getParam("cost").toLongOrNull()
//    println("mock接收参数cost = $cost")
    val doResponse = {
        val res = (cost ?: 0) + 1
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(res.toString())
    }
    if (cost != null) {
        context.vertx().setTimer(cost) {
            doResponse()
        }
    } else doResponse()
}

val redisSet: suspend (RoutingContext) -> Unit = { context ->
    val key = context.request().getParam("key")
    val value = context.request().getParam("value")
    val redisApi = RedisConfig.getRedisApi()
    val response = redisApi.set(listOf(key, value)).await()
    context.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(response.toString())
}

val redisGet: suspend (RoutingContext) -> Unit = { context ->
    val key = context.request().getParam("key")
    val redisApi = RedisConfig.getRedisApi()
    val response = redisApi.get(key).await()
    context.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(response.toString())
}

val mockCostMillis = System.getenv("MOCK_COST_MILLIS")?.toLongOrNull()
fun getMockData(ctx: RoutingContext, mockDataKey: String, mockContentType: String) {
    if (mockCostMillis == null) {
        responseMockData(ctx, mockDataKey, mockContentType)
    } else {
        ctx.vertx().setTimer(mockCostMillis) {
            responseMockData(ctx, mockDataKey, mockContentType)
        }
    }
}

private fun responseMockData(ctx: RoutingContext, mockDataKey: String, mockContentType: String) {
    val mockData = mockDataMap[mockDataKey] ?: ""
    ctx.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, mockContentType)
        .end(mockData)
}