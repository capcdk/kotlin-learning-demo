package com.capc.kotlinlearningdemo.common

/**
 * 组合基本信息
 * @author chendk on 2021/8/9
 */
class PoInfo(

    /**
     * 组合名称
     */
    var poName: String,

    /**
     * 组合代码
     */
    var poCode: String,

    /**
     * 组合组成的基金代码
     */
    var fundCodes: List<String>
)