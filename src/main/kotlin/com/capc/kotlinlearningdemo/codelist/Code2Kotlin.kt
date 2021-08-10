package com.capc.kotlinlearningdemo.codelist

import com.capc.kotlinlearningdemo.common.DataBean
import com.capc.kotlinlearningdemo.common.Fund
import org.apache.commons.lang3.RandomUtils
import java.math.BigDecimal
import java.text.MessageFormat

/**
 * 示例2-kotlin：模拟调用栈很深的时候，kotlin的判空。
 * p.s. 调用层级：controller -> service -> dao -> dbClient
 *
 * @author chendk on 2021/8/9
 */
fun main() {
    val controller = FundControllerK()

    var code: String? = null
    if (RandomUtils.nextBoolean()) {
        code = "000001"
    }

    val dataBean = controller.getFund(code)
    println(dataBean)
}

internal class FundControllerK {
    private val fundService = FundServiceK()

    fun getFund(code: String?): DataBean<Fund> {
        // 有必要的参数判空
        if (code.isNullOrBlank()) {
            return DataBean.fail("基金代码不可为空")
        }

        val fund = fundService.getFundDetail(code)

        // 有必要的结果判空
        return if (fund != null) {
            DataBean.ok(fund)
        } else {
            DataBean.fail("基金不存在")
        }
    }
}

internal class FundServiceK {
    private val fundDao = FundDaoK()

    fun getFundDetail(code: String): Fund? {
//        // 重复的参数判空
//        if (code.isNullOrBlank()) {
//            return null
//        }

        val fund = fundDao.findFundByCode(code)

        // 有必要的结果判空
        if (fund != null) {
            // ... 假装做一些业务操作
        }
        return fund
    }
}

internal class FundDaoK {
    private val dbClient = MockDBClientK()

    fun findFundByCode(code: String): Fund? {
//        // 重复的参数判空
//        if (code.isNullOrBlank()) {
//            return null
//        }

        // 模拟sql查询
        val sql = MessageFormat.format("select * from t_fund where code = '{0}'", code)
        val fund = dbClient.queryFundTable(sql)

        // 有必要的结果判空
        if (fund != null) {
            fund.scaleTotal = BigDecimal.ONE
            fund.shareTotal = BigDecimal.ONE
        }
        return fund
    }
}

internal class MockDBClientK {
    fun queryFundTable(sql: String): Fund? {
        // 模拟查询，可能查到或查不到
        return if (RandomUtils.nextBoolean()) {
            Fund("某基金", "000001")
        } else {
            null
        }
    }
}