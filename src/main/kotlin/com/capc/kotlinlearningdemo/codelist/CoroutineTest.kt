package com.capc.kotlinlearningdemo.codelist

import kotlinx.coroutines.*
import java.time.Duration

/**
 * @author chendk on 2022/6/15
 */
fun main() {
    // 协程并行测试
    testCoroutineParallel()
}

fun testCoroutineParallel() {
    val total = 1_000_0000L // 1000w

    val c1 = testIncreaseByCoroutine(total, 1)
    val c4 = testIncreaseByCoroutine(total, 4)
    val c8 = testIncreaseByCoroutine(total, 8)
    val c12 = testIncreaseByCoroutine(total, 12)
    val c16 = testIncreaseByCoroutine(total, 16)
    val c20 = testIncreaseByCoroutine(total, 20)
    val c100 = testIncreaseByCoroutine(total, 100)
    val c200 = testIncreaseByCoroutine(total, 200)
    val c500 = testIncreaseByCoroutine(total, 500)

    println(
        """
            核心数 ${Dispatchers.Default.asExecutor()}，递增千万，
                无竞争    单协程耗时: ${c1.toMillis()}ms
                无竞争    4协程耗时: ${c4.toMillis()}ms
                无竞争    8协程耗时: ${c8.toMillis()}ms
                无竞争    12协程耗时: ${c12.toMillis()}ms
                无竞争    16协程耗时: ${c16.toMillis()}ms
                竞争?     20协程耗时: ${c20.toMillis()}ms
                竞争?     100协程耗时: ${c100.toMillis()}ms
                竞争?     200协程耗时: ${c200.toMillis()}ms
                竞争?     500协程耗时: ${c500.toMillis()}ms
        """.trimIndent()
    )
}

fun testIncreaseByCoroutine(max: Long, coroutines: Int): Duration {
    return runBlocking {
        val dispatcher = Dispatchers.Default

        // 按协程数拆解任务
        val maxInners = mutableListOf<Long>()
        var remaining = max
        val range = (max / coroutines)..(max / coroutines + max % coroutines)
        maxInners.addAll(List(coroutines - 1) {
            val part = range.random()
            remaining -= part
            part
        })
        maxInners.add(remaining)

        // 创建协程
        val jobs = maxInners.map { maxInner ->
            async(dispatcher, CoroutineStart.LAZY) {
                testIncreaseInCoroutine(maxInner)
            }
        }

        // 一齐启动协程
        val start = System.currentTimeMillis()
        println("🐔start test: $coroutines coroutines, $max increase, start on $start")
        jobs.forEach { it.start() }

        // 等待所有协程执行完成
        val results = awaitAll(*jobs.toTypedArray())
        println("execute result: ${results.sum()}")
        val cost = Duration.ofMillis(System.currentTimeMillis() - start)
        println("✔️end test: $coroutines coroutines, $max increase, cost ${cost.toMillis()}ms")
        cost
    }
}

suspend fun testIncreaseInCoroutine(max: Long): Long {
    var num = 0L
    while (num < max) {
        num++
        yield()
    }
    return num
}
