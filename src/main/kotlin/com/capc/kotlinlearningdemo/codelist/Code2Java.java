package com.capc.kotlinlearningdemo.codelist;

import com.capc.kotlinlearningdemo.common.DataBean;
import com.capc.kotlinlearningdemo.common.Fund;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * 示例2-java：模拟调用栈很深的时候，判空代码的冗余。
 * p.s. 调用层级：controller -> service -> dao -> dbClient
 *
 * @author chendk on 2021/8/9
 */
public class Code2Java {

    public static void main(String[] args) {
        FundControllerJ controller = new FundControllerJ();

        String code = null;
        if (RandomUtils.nextBoolean()) {
            code = "000001";
        }

        DataBean<Fund> dataBean = controller.getFund(code);
        System.out.println(dataBean);
    }
}

class FundControllerJ {

    private final FundServiceJ fundService = new FundServiceJ();

    DataBean<Fund> getFund(String code) {
        // 有必要的参数判空
        if (StringUtils.isBlank(code)) {
            return DataBean.fail("基金代码不可为空");
        }

        Fund fund = fundService.getFundDetail(code);

        // 有必要的结果判空
        if (fund != null) {
            return DataBean.ok(fund);
        } else {
            return DataBean.fail("基金不存在");
        }
    }
}

class FundServiceJ {

    private final FundDaoJ fundDao = new FundDaoJ();

    Fund getFundDetail(String code) {
        // 重复的参数判空
        if (StringUtils.isBlank(code)) {
            return null;
        }

        Fund fund = fundDao.findFundByCode(code);

        // 有必要的结果判空
        if (fund != null) {
            // ... 假装做一些业务操作
        }
        return fund;
    }
}

class FundDaoJ {

    private final MockDBClientJ dbClient = new MockDBClientJ();

    Fund findFundByCode(String code) {
        // 重复的参数判空
        if (StringUtils.isBlank(code)) {
            return null;
        }

        // 模拟sql查询
        String sql = MessageFormat.format("select * from t_fund where code = '{0}'", code);
        Fund fund = dbClient.queryFundTable(sql);

        // 有必要的结果判空
        if (fund != null) {
            fund.setScaleTotal(BigDecimal.ONE);
            fund.setShareTotal(BigDecimal.ONE);
        }
        return fund;
    }
}

class MockDBClientJ {

    Fund queryFundTable(String sql) {
        // 模拟查询，可能查到或查不到
        if (RandomUtils.nextBoolean()) {
            return new Fund("某基金", "000001");
        } else {
            return null;
        }
    }
}





