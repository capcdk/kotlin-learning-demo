package com.capc.kotlinlearningdemo.common

/**
 * 组合详细信息
 * @author chendk on 2021/8/9
 */
class PoDetailInfo {

    /**
     * 组合基本信息
     */
    var poInfo: PoInfo? = null

    /**
     * 用户持有的组合资产
     */
    var poAsset: PoAsset? = null

    /**
     * 组合组成的基金
     */
    var fundInfos: List<Fund>? = null

    override fun toString(): String {
        return "PoDetailInfo(poInfo=$poInfo, poAsset=$poAsset, fundInfos=$fundInfos)"
    }

}