package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.PoDetailInfo

/**
 * 示例11：kotlin形式的函数式
 *
 * @author chendk on 2021/8/12
 */
fun main() {
    val poDetailList = listOf<PoDetailInfo>()

    val codeFundMap = poDetailList
        .filter { it.fundInfos != null }
        .flatMap { it.fundInfos ?: emptyList() }
        .associateBy { it.code }
}