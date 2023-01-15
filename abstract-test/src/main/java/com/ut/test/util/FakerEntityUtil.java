package com.ut.test.util;

import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Locale;

public class FakerEntityUtil {
    private static Faker faker = new Faker(Locale.ENGLISH);

    private static Faker getFaker() {
        return faker;
    }

    /**
     * 根据 class 类自动为其中的一些数据类型注入虚假值, 支持的数据类型有：String 、Integer、 Boolean 、Double 、Long 、Float 、BigDecimal
     * Short，可以设置递归生成的层数，默认递归生成 2 层
     * @param c 传入的 class 类
     * @return
     */
    public static Object getFakerInstance(Class c) {
        int layer = 2;
        return getInstance(c, layer);
    }

    /**
     * 根据 class 类自动为其中的一些数据类型注入虚假值, 支持的数据类型有：String 、Integer、 Boolean 、Double 、Long 、Float 、BigDecimal
     * Short，可以设置递归生成的层数，默认递归生成 2 层
     * @param c 传入的 class 类
     * @param layer 递归生成虚假值的层数
     * @return
     */
    public static Object getFakerInstance(Class c,int layer) {
        return getInstance(c, layer);
    }

    private static Object getInstance(Class c, int layer) {
        Object o = null;
        try {
            o = c.getConstructor().newInstance();
            //获取这个类的所有方法
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    field.set(o, faker.letterify("????????"));
                } else if (field.getType() == Integer.class) {
                    field.set(o, faker.random().nextInt(1000000));
                } else if (field.getType() == Boolean.class) {
                    field.set(o, faker.random().nextBoolean());
                } else if (field.getType() == Double.class) {
                    field.set(o, faker.random().nextDouble());
                } else if (field.getType() == Long.class) {
                    field.set(o, faker.random().nextLong(100000000000l));
                } else if (field.getType() == Float.class) {
                    field.set(o, new Float(faker.random().nextDouble()));
                } else if (field.getType() == BigDecimal.class) {
                    field.set(o, new BigDecimal(faker.random().nextDouble()));
                } else if (field.getType() == Short.class) {
                    field.set(o, (short) faker.random().nextInt(1000));
                } else if (layer > 0) {
                    layer--;
                    field.set(o, getInstance(field.getType(), layer));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return o;
    }

}
