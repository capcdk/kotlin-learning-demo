package com.capc.kotlinlearningdemo.vertx.server.mock

import com.capc.kotlinlearningdemo.vertx.server.VertxHttpVerticle
import io.vertx.core.http.HttpMethod

/**
 * @author chendk on 2022/2/24
 */
class MockHttpVerticle : VertxHttpVerticle({
    route(HttpMethod.GET, "/mock/:cost").handler(mockCost)
    route(HttpMethod.GET, "/redis/get").coroutineHandler(redisGet)
    route(HttpMethod.GET, "/redis/set").coroutineHandler(redisSet)
}) {
}