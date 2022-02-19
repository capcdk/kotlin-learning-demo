package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions


/**
 * 全局唯一vertx实例
 */
val serverVertx = Vertx.vertx(
    VertxOptions()
        .setEventLoopPoolSize(mockInstance * 2)
        .setPreferNativeTransport(true)
        .setInternalBlockingPoolSize(Runtime.getRuntime().availableProcessors() * 8)
)

val clientVertx = Vertx.vertx(
    VertxOptions()
        .setEventLoopPoolSize(Runtime.getRuntime().availableProcessors())
        .setPreferNativeTransport(true)
        .setInternalBlockingPoolSize(Runtime.getRuntime().availableProcessors() * 8)
)

// Https支持
val webClient = WebClient.create(
    clientVertx, WebClientOptions()
        .setTrustAll(true)
        .setVerifyHost(false)
        .setMaxPoolSize(1000)
)
