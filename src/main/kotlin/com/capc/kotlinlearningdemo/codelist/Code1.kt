package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.Fund
import kotlin.random.Random

/**
 * 示例1：可空类型方法调用，编译报错示例
 * @author chendk on 2021/8/6
 */
//fun main() {
//    val fund = mockNullableFund()
//    fund.scaleTotal
//}


fun mockNullableFund(): Fund? = if (Random.nextBoolean()) Fund("", "") else null