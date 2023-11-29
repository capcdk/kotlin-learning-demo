package com.capc.kotlinlearningdemo.codelist

import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

/**
 * @author chendk on 2022/6/15
 */
fun main() {
    // 线程竞争测试
//    testThreadParallel()

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

fun testThreadParallel() {
    val total = 1_000_0000L // 1000w

    val t1 = testIncreaseParallel(total, 1)
    val t4 = testIncreaseParallel(total, 4)
    val t8 = testIncreaseParallel(total, 8)
    val t12 = testIncreaseParallel(total, 12)
    val t16 = testIncreaseParallel(total, 16)
    val t20 = testIncreaseParallel(total, 20)
    val t100 = testIncreaseParallel(total, 100)
    val t200 = testIncreaseParallel(total, 200)
    val t500 = testIncreaseParallel(total, 500)

    println(
        """
        核心数 ${Runtime.getRuntime().availableProcessors()}，递增千万，
            无竞争    单线程耗时: ${t1.toMillis()}ms
            无竞争    4线程耗时: ${t4.toMillis()}ms
            无竞争    8线程耗时: ${t8.toMillis()}ms
            无竞争    12线程耗时: ${t12.toMillis()}ms
            无竞争    16线程耗时: ${t16.toMillis()}ms
            5争1     20线程耗时: ${t20.toMillis()}ms
            85争1    100线程耗时: ${t100.toMillis()}ms
            185争1   200线程耗时: ${t200.toMillis()}ms
            485争1   500线程耗时: ${t500.toMillis()}ms
    """.trimIndent()
    )
}

fun testIncreaseParallel(max: Long, threads: Int): Duration {
    val barrier = CyclicBarrier(threads)
    val latch = CountDownLatch(threads)

    val maxInners = mutableListOf<Long>()
    var remaining = max
    val range = (max / threads)..(max / threads + max % threads)
    maxInners.addAll(List(threads - 1) {
        val part = range.random()
        remaining -= part
        part
    })
    maxInners.add(remaining)

    val threadList = mutableListOf<Thread>()
    maxInners.forEach { maxInner ->
        val thread = Thread {
            try {
                barrier.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            testIncrease(maxInner)
            latch.countDown()
        }
        threadList.add(thread)
    }
    val start = System.currentTimeMillis()
    println("🐔start test: $threads threads, $max increase, start on $start")
    threadList.forEach { it.start() }
    latch.await()
    val cost = Duration.ofMillis(System.currentTimeMillis() - start)
    println("✔️end test: $threads threads, $max increase, cost ${cost.toMillis()}ms")
    return cost
}

fun testIncrease(max: Long): Duration {
    var num = 0L
    val start = System.currentTimeMillis()
    while (num < max) {
        num++
        Thread.sleep(0)
    }
    return Duration.ofMillis(System.currentTimeMillis() - start)
}

suspend fun testIncreaseInCoroutine(max: Long): Long {
    var num = 0L
    val start = System.currentTimeMillis()
    while (num < max) {
        num++
        yield()
    }
    return num
}
