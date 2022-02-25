package com.capc.kotlinlearningdemo.vertx.server.pressure

import com.capc.kotlinlearningdemo.vertx.server.VertxHttpVerticle
import io.vertx.core.http.HttpMethod

/**
 * @author chendk on 2022/2/24
 */
class PressureHttpVerticle : VertxHttpVerticle({
    route(HttpMethod.GET, "/query/:cost").coroutineHandler(querySomething)
}) {
}