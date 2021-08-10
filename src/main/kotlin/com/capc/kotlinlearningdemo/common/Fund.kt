package com.capc.kotlinlearningdemo.common

import java.math.BigDecimal

/**
 * @author chendk on 2021/8/5
 */
class Fund(
    val name: String,
    val code: String
) {

    var shareTotal: BigDecimal? = null

    var scaleTotal: BigDecimal? = null

    companion object {
        const val TABLE_NAME = "t_fund"
    }

    override fun toString(): String {
        return "Fund(name='$name', code='$code', shareTotal=$shareTotal, scaleTotal=$scaleTotal)"
    }

}