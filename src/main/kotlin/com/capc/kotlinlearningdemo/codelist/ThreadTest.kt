package com.capc.kotlinlearningdemo.codelist

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

/**
 * @author chendk on 2022/6/15
 */
fun main() {
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
