package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions


/**
 * 全局唯一vertx实例
 */
val vertx = Vertx.vertx(
    VertxOptions()
        .setEventLoopPoolSize(Runtime.getRuntime().availableProcessors())
        .setPreferNativeTransport(true)
        .setInternalBlockingPoolSize(Runtime.getRuntime().availableProcessors() * 8)
)

// Https支持
val webClient = WebClient.create(
    vertx, WebClientOptions()
        .setTrustAll(true)
        .setVerifyHost(false)
        .setMaxPoolSize(500)
)
