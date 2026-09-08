package com.gx.etl.scheme.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SchemeProcessService.matchByType 是 private 方法，且当前没有更浅的测试接缝
 * （mapToFieldValues 同样 private），因此通过反射调用验证其纯逻辑。
 */
class SchemeProcessServiceMatchByTypeTest {

    private Object matchByType(List<?> nested, String targetType) throws Exception {
        Method m = SchemeProcessService.class.getDeclaredMethod("matchByType", List.class, String.class);
        m.setAccessible(true);
        return m.invoke(new SchemeProcessService(), nested, targetType);
    }

    @Test
    void doubleTypeSkipsNonNumberElements() throws Exception {
        // 嵌套数组形如 msgpack 的 [1,2,3,['平台id','平台类型']]，
        // 靠前的位置可能是标签/字符串；double 目标应跳过 String 取到数值 5.5。
        Object result = matchByType(Arrays.asList("平台id", 5.5), "double");
        assertEquals(5.5, (Double) result, 0.0001);
    }

    @Test
    void doubleTypeTakesNumberWhenFirst() throws Exception {
        Object result = matchByType(Arrays.asList(2.5, "标签"), "double");
        assertEquals(2.5, (Double) result, 0.0001);
    }

    @Test
    void longTypeSkipsNonNumberElements() throws Exception {
        Object result = matchByType(Arrays.asList("平台id", 42L), "long");
        assertEquals(42L, result);
    }
}
