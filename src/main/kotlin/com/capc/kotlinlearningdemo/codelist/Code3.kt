package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.Fund
import java.math.BigDecimal

/**
 * 示例3：空安全相关语法糖示例：elvis、as?、?.
 * @author chendk on 2021/8/9
 */
fun main() {
    /*
    * 空安全语法糖1：elvis
    * 前面对象为空时，会执行?:之后的代码，时常用作默认值的赋值，或为空时的快速返回
    * */
    val notNullFund = mockNullableFund() ?: Fund("默认", "")
    val notNullFund2 = mockNullableFund() ?: mockNullableFund() ?: Fund("默认", "")
    val notNullFund3 = mockNullableFund() ?: throw RuntimeException("fund is null")
    val notNullFund4 = mockNullableFund() ?: return


    /*
    * 空安全语法糖2：as?
    * as为强转关键字，同java里的(Fund) obj形式。而as?的意思为，类型不符无法强转时，引用会被赋值为null，而不是抛异常
    * */
    // 类型不符时，会抛异常的强转
    val number: Int = 123
    val fund1 = number as Fund
    // 类型不符时，不会抛异常的强转
    val fund2 = number as? Fund
    val fund3 = null as? Fund


    /*
    * 空安全语法糖3：?.
    * 在kotlin中当我们想调用可空对象的方法时，要么我们传统的提前判空，要么也可以通过?.来做便捷的非空时调用
    * */
    val someNullableFund = mockNullableFund()
    // 传统的判空后调用
    val scaleTotal1: BigDecimal
    if (someNullableFund != null && someNullableFund.scaleTotal != null) {
        scaleTotal1 = someNullableFund.scaleTotal!!
    } else {
        scaleTotal1 = BigDecimal.ZERO
    }
    println(scaleTotal1)

    // kotlin式的非空时调用语法糖
    val scaleTotal2 = someNullableFund?.scaleTotal ?: BigDecimal.ZERO
    println(scaleTotal2)
}
