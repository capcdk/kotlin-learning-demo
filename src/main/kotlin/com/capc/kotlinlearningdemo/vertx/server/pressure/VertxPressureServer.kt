package com.capc.kotlinlearningdemo.vertx.server.pressure

import com.capc.kotlinlearningdemo.vertx.instance
import com.capc.kotlinlearningdemo.vertx.serverVertx
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await

/**
 * @author chendk on 2021/8/10
 */
val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080

suspend fun main() {
    val result = serverVertx.deployVerticle("com.capc.kotlinlearningdemo.vertx.server.pressure.PressureHttpVerticle", depOps).await()
    println("verticle 部署完成，result=$result")
}

private val depOps by lazy {
    val ops = DeploymentOptions()
    ops.instances = instance
    ops.config = JsonObject().put("port", serverPort)
    ops
}