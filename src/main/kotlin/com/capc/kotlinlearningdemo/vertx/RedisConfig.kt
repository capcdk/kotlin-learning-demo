package com.capc.kotlinlearningdemo.vertx

import io.vertx.core.net.NetClientOptions
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisClientType
import io.vertx.redis.client.RedisOptions

/**
 * @author chendk on 2022/2/21
 */
object RedisConfig {

    private var redisApi: RedisAPI? = null

    fun getRedisApi(): RedisAPI {
        return redisApi ?: throw RuntimeException("redisApi not initialized")
    }

    fun init(host: String, port: Int, password: String) {
        val options = RedisOptions()
            .setType(RedisClientType.STANDALONE)
            .setConnectionString("redis://:$password@$host:$port/0")
            .setMaxPoolSize(2)
            .setMaxPoolWaiting(256)
            .setMaxWaitingHandlers(512)
            .setNetClientOptions(
                NetClientOptions()
                    .setTcpKeepAlive(true)
                    .setTrustAll(true)
                    .setTcpFastOpen(true)
                    .setTcpNoDelay(true)
            )
        val redisClient = Redis.createClient(serverVertx, options)
        redisApi = RedisAPI.api(redisClient)
    }
}