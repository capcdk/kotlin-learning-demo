package com.capc.kotlinlearningdemo.vertx.server.pressure

import com.capc.kotlinlearningdemo.vertx.webClient
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import org.springframework.util.MimeTypeUtils

/**
 * @author chendk on 2022/2/24
 */

val querySomething: suspend (RoutingContext) -> Unit = { context ->
    val cost = context.request().getParam("cost").toLongOrNull()
    val response = webClient.getAbs("http://mock-server:9011/mock/$cost").send().await()
    val resBody = response.bodyAsString()
    val resStatus = response.statusCode()
    if (resStatus == 200) {
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(resBody.toString())
    } else {
        context.response().setStatusCode(resStatus).putHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN_VALUE).end(resBody.toString())
    }
}