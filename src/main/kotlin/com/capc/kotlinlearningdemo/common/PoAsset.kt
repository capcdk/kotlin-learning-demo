package com.capc.kotlinlearningdemo.common

import java.math.BigDecimal

/**
 * 用户持有组合的资产
 * @author chendk on 2021/8/9
 */
class PoAsset(

    /**
     * 持有份额
     */
    var holdingShare: BigDecimal? = null,

    /**
     * 持有金额
     */
    var holdingScale: BigDecimal? = null
)