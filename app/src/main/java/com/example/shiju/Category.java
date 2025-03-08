package com.example.shiju;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类别列表
 * @author dengchangxue
 */
public class Category {


    // 使用不可变列表保护数据
    private static final List<String> CATEGORY_LABELS = Arrays.asList(
            "黑斑病", "溃疡病", "黄龙病", "健康"
    );

    public static List<String> getCategoryLabels(){
        return new ArrayList<>(CATEGORY_LABELS);
    }
}
