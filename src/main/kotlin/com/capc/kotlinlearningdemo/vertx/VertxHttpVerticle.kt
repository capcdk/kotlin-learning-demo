package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import org.springframework.util.MimeTypeUtils

/**
 * @author chendk on 2022/2/19
 */
class VertxHttpVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        startHttpServer()
    }
}

fun startHttpServer() {
    val router = generateHttpRouter()
    serverVertx
        .createHttpServer(
            HttpServerOptions()
                .setTcpFastOpen(true)
                .setTcpNoDelay(true)
                .setTcpQuickAck(true)
        )
        .requestHandler(router)
        .listen(mockHttpPort) { listenResult ->
            if (listenResult.succeeded()) {
                println("vertx http server started on $mockHttpPort")
            } else {
                System.err.println("failed to start vertx http server on $mockHttpPort")
            }
        }
}

fun generateHttpRouter(): Router {
    val router = Router.router(serverVertx)

    router.route(HttpMethod.GET, "/mock/:cost").handler(mockCost)

    return router
}

val mockCost: (RoutingContext) -> Unit = { context ->
    val cost = context.request().getParam("cost").toLongOrNull()
//    println("mock接收参数cost = $cost")
    val doResponse = {
        val res = (cost ?: 0) + 1
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(res.toString())
    }
    if (cost != null) {
        serverVertx.setTimer(cost) {
            doResponse()
        }
    } else doResponse()
}