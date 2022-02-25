package com.capc.kotlinlearningdemo.vertx.server.mock

import com.capc.kotlinlearningdemo.vertx.RedisConfig
import com.capc.kotlinlearningdemo.vertx.instance
import com.capc.kotlinlearningdemo.vertx.serverVertx
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await

/**
 * @author chendk on 2021/8/10
 */
val mockHttpHost = "127.0.0.1"
val mockHttpPort = System.getenv("MOCK_PORT")?.toIntOrNull() ?: 8080
val getMockPath: (Long) -> String = { "http://$mockHttpHost:$mockHttpPort/mock/$it" }

val redisHost = System.getenv("REDIS_HOST") ?: "localhost"
val redisPort = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379
val redisPassword = System.getenv("REDIS_PASSWORD") ?: "123456"

suspend fun main() {
    RedisConfig.init(redisHost, redisPort, redisPassword)
    val result = serverVertx.deployVerticle("com.capc.kotlinlearningdemo.vertx.server.mock.MockHttpVerticle", depOps).await()
    println("verticle 部署完成，result=$result")
}

fun startHttpServer() {
    serverVertx.deployVerticle("com.capc.kotlinlearningdemo.vertx.server.mock.MockHttpVerticle", depOps)
        .onSuccess {
            println("verticle 部署完成，result=$it")
        }
        .onFailure {
            System.err.println("verticle 部署异常，err=$it")
        }
}

private val depOps by lazy {
    DeploymentOptions().apply {
        this.instances = instance
        this.config = JsonObject()
            .put("port", mockHttpPort)
    }
}
