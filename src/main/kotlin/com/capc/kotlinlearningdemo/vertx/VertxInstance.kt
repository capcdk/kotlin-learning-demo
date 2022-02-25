package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions


val instance = System.getenv("INSTANCES")?.toIntOrNull() ?: Runtime.getRuntime().availableProcessors()

/**
 * 全局唯一vertx实例
 */
val serverVertx: Vertx by lazy {
    Vertx.vertx(
        VertxOptions()
            .setEventLoopPoolSize(instance * 2)
            .setPreferNativeTransport(true)
            .setInternalBlockingPoolSize(instance * 8)
    )
}

val clientVertx: Vertx by lazy {
    Vertx.vertx(
        VertxOptions()
            .setEventLoopPoolSize(instance * 2)
            .setPreferNativeTransport(true)
            .setInternalBlockingPoolSize(instance * 8)
    )
}

// Https支持
val webClient: WebClient by lazy {
    val options = WebClientOptions().apply {
        isTrustAll = true
        isVerifyHost = false
        isTcpFastOpen = true
        isTcpQuickAck = true
        isTcpNoDelay = true
        maxPoolSize = 1000
//        if (httpKeepAliveSec > 0) {
//            options.setTcpKeepAlive(true).keepAliveTimeout = httpKeepAliveSec
//        }
    }
    WebClient.create(clientVertx, options)
}