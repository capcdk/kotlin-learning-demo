package com.capc.kotlinlearningdemo.codelist

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

/**
 * @author chendk on 2022/6/15
 */
fun main() {
    val total = 1_000_0000L // 1000万

    val duration1 = testIncrease(total)
    val duration2 = testIncreaseParallel(total, 6)
    val duration3 = testIncreaseParallel(total, 12)
    val duration4 = testIncreaseParallel(total, 16)
    val duration5 = testIncreaseParallel(total, 50)
    val duration6 = testIncreaseParallel(total, 100)
    val duration7 = testIncreaseParallel(total, 200)
    val duration8 = testIncreaseParallel(total, 500)

    println(
        """
        核心数 ${Runtime.getRuntime().availableProcessors()}，递增千万，
            无竞争    单线程耗时: ${duration1.toMillis()}ms
            无竞争    6线程耗时: ${duration2.toMillis()}ms
            无竞争    12线程耗时: ${duration3.toMillis()}ms
            5争1     16线程耗时: ${duration4.toMillis()}ms
            39争1    50线程耗时: ${duration5.toMillis()}ms
            89争1    100线程耗时: ${duration6.toMillis()}ms
            189争1   200线程耗时: ${duration7.toMillis()}ms
            489争1   500线程耗时: ${duration8.toMillis()}ms
    """.trimIndent()
    )
}

fun testIncreaseParallel(max: Long, threads: Int): Duration {
    val barrier = CyclicBarrier(threads)
    val latch = CountDownLatch(threads)
    val maxInner = max / threads
    val threadList = mutableListOf<Thread>()
    repeat(threads) {
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
    threadList.forEach { it.start() }
    latch.await()
    return Duration.ofMillis(System.currentTimeMillis() - start)
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
