package com.capc.kotlinlearningdemo.vertx.server

import io.vertx.core.http.HttpServerOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * @author chendk on 2022/2/19
 */
open class VertxHttpVerticle(
    val registerRouter: ExtendRouter.() -> Unit
) : CoroutineVerticle() {

    private val port by lazy { config.getInteger("port") ?: 8080 }
    private val coScope: CoroutineScope by lazy { CoroutineScope(vertx.dispatcher()) }

    override suspend fun start() {
        startListen()
    }

    private suspend fun startListen() {
        val router = ExtendRouter(vertx, coScope)
        router.registerRouter()

        val httpServer = vertx
            .createHttpServer(
                HttpServerOptions()
                    .setTcpFastOpen(true)
                    .setTcpNoDelay(true)
                    .setTcpQuickAck(true)
            )
            .requestHandler(router)
        println("${Thread.currentThread().name} listen $port")
        try {
            httpServer.listen(port).await()
            println("vertx http server started on $port, server=$httpServer")
        } catch (e: Exception) {
            System.err.println("failed to start vertx http server on $port")
        }
    }
}