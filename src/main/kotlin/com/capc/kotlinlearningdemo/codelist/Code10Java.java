package com.capc.kotlinlearningdemo.codelist;

import com.capc.kotlinlearningdemo.common.Fund;
import com.capc.kotlinlearningdemo.common.PoDetailInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 示例10：java形式的函数式
 *
 * @author chendk on 2021/8/12
 */
public class Code10Java {

    public static void main(String[] args) {
        List<PoDetailInfo> poDetailList = Collections.emptyList();

        Map<String, Fund> codeFundMap = poDetailList.stream()
                .filter(poDetail -> poDetail.getFundInfos() != null)
                .flatMap(poDetail -> poDetail.getFundInfos().stream())
                .collect(Collectors.toMap(Fund::getCode, Function.identity()));
    }

}
