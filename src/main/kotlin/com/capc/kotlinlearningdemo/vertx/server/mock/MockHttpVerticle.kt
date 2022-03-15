package com.capc.kotlinlearningdemo.vertx.server.mock

import com.capc.kotlinlearningdemo.vertx.server.VertxHttpVerticle
import io.vertx.core.http.HttpMethod
import org.springframework.util.MimeTypeUtils

/**
 * @author chendk on 2022/2/24
 */
class MockHttpVerticle : VertxHttpVerticle({
    route(HttpMethod.GET, "/mock/:cost").handler(mockCost)
    route(HttpMethod.GET, "/redis/get").coroutineHandler(redisGet)
    route(HttpMethod.GET, "/redis/set").coroutineHandler(redisSet)

    route(HttpMethod.GET, "/admin/brokerinfoByApiKey/:key").handler { getMockData(it, "brokerinfo", MimeTypeUtils.APPLICATION_JSON_VALUE) }
    route(HttpMethod.POST, "/fundFee/queryBuyFundFee").handler { getMockData(it, "fundFee", MimeTypeUtils.APPLICATION_JSON_VALUE) }
    route(HttpMethod.GET, "/enterprise/brokerBuyStatus/:broker").handler { getMockData(it, "buyStatus", MimeTypeUtils.TEXT_PLAIN_VALUE) }
    route(HttpMethod.GET, "/brokerUser/:brokerUserId/account").handler { getMockData(it, "accountInfo", MimeTypeUtils.APPLICATION_JSON_VALUE) }
    route(HttpMethod.GET, "/account/:accountId/payment-method/:paymentMethodId").handler { getMockData(it, "paymentMethod", MimeTypeUtils.APPLICATION_JSON_VALUE) }
    route(HttpMethod.GET, "/x1/antimoney/ready").handler { getMockData(it, "antiMoney", MimeTypeUtils.APPLICATION_JSON_VALUE) }
    route(HttpMethod.GET, "/enterprise/investWhiteLists/:broker/:brokerUserId").handler { getMockData(it, "investWhite", MimeTypeUtils.TEXT_PLAIN_VALUE) }
    route(HttpMethod.POST, "/wallet/recharge").handler { getMockData(it, "recharge", MimeTypeUtils.APPLICATION_JSON_VALUE) }
}) {
}