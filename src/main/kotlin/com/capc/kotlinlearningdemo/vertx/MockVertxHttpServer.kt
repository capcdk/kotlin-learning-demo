package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.DeploymentOptions

/**
 * @author chendk on 2021/8/10
 */
val mockHttpHost = "127.0.0.1"
val mockHttpPort = System.getenv("MOCK_PORT")?.toIntOrNull() ?: 8080
val mockInstance = System.getenv("INSTANCES")?.toIntOrNull() ?: 2
val getMockPath: (Long) -> String = { "http://$mockHttpHost:$mockHttpPort/mock/$it" }

fun main() {
//    startHttpServer()

    val depOps = DeploymentOptions().apply {
        this.instances = mockInstance
    }
    serverVertx.deployVerticle("com.capc.kotlinlearningdemo.vertx.VertxHttpVerticle", depOps) { ar ->
        if (ar.succeeded()) {
            println("verticle 部署完成")
        } else {
            println("verticle 部署异常")
        }
    }

    println("启动 vert.x http server：$mockHttpPort")
}