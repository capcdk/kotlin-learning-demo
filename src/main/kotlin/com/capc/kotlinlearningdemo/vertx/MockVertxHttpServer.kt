package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE

/**
 * @author chendk on 2021/8/10
 */
val mockHttpHost = "127.0.0.1"
val mockHttpPort = 8080

fun main() {
    val router = generateHttpRouter()

    vertx.createHttpServer(
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
    val router = Router.router(vertx)

    router.route(HttpMethod.GET, "/mock/:cost").apply {
        this.handler { context ->
            val cost = context.request().getParam("cost").toLongOrNull()
            println("mock接收参数cost = $cost")
            val doResponse = {
                val res = (cost ?: 0) + 1
                context.response().putHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE).end(res.toString())
            }
            if (cost != null) {
                vertx.setTimer(cost) {
                    doResponse()
                }
            } else doResponse()
        }
    }

    return router
}