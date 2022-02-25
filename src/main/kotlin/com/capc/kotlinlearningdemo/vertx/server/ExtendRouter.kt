package com.capc.kotlinlearningdemo.vertx.server

import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.impl.RouterImpl
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author chendk on 2022/2/24
 */
class ExtendRouter(
    vertx: Vertx,
    private val coScope: CoroutineScope
) : RouterImpl(vertx) {

    fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit): Route =
        handler { ctx ->
            coScope.launch(ctx.vertx().dispatcher()) {
                try {
                    fn(ctx)
                } catch (e: Exception) {
                    ctx.fail(e)
                }
            }
        }
}