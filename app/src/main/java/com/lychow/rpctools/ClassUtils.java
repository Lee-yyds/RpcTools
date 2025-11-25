package com.lychow.rpctools;
import android.util.Log;

import dalvik.system.BaseDexClassLoader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import java.util.HashSet;
import java.util.Set;
public class ClassUtils {
    static final Set<String> SYSTEM_PACKAGE_PREFIXES = new HashSet<>();
    static {
        SYSTEM_PACKAGE_PREFIXES.add("java.");
        SYSTEM_PACKAGE_PREFIXES.add("javax.");
        SYSTEM_PACKAGE_PREFIXES.add("android.");
        SYSTEM_PACKAGE_PREFIXES.add("androidx."); // androidx 库通常也被认为是平台的一部分
        SYSTEM_PACKAGE_PREFIXES.add("dalvik.");
        SYSTEM_PACKAGE_PREFIXES.add("kotlin.");
        SYSTEM_PACKAGE_PREFIXES.add("kotlinx.");
        SYSTEM_PACKAGE_PREFIXES.add("com.android.");
        SYSTEM_PACKAGE_PREFIXES.add("org.json.");
        SYSTEM_PACKAGE_PREFIXES.add("com.google.");
        SYSTEM_PACKAGE_PREFIXES.add("org.intellij");
        SYSTEM_PACKAGE_PREFIXES.add("org.jetbrains");

    }
    public static String[] getAllClassNames(ClassLoader classLoader) {
        Log.d("LychowHook", "getAllClassNames: "+classLoader.toString());

        List<String> classNames = new ArrayList<>();
        if (classLoader.toString().contains("java.lang.BootClassLoader") ){
            return classNames.toArray(new String[0]);
        }
        try {
            // BaseDexClassLoader
            Field pathListField = findField(classLoader.getClass(), "pathList");
            Object pathList = pathListField.get(classLoader);

            // pathList -> DexPathList
            Field dexElementsField = findField(pathList.getClass(), "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            // Element[]
            for (Object element : dexElements) {

                Field dexFileField = findField(element.getClass(), "dexFile");
                // DexFile
                dalvik.system.DexFile dexFile = (dalvik.system.DexFile) dexFileField.get(element);
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    // 检查是否是系统类
                    if (!isSystemClass(className)) {
                        classNames.add(className);
                    }
                }
                // Enumerate class names
//                classNames.addAll(Collections.list(entries));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNames.toArray(new String[0]);
    }

    // 递归查找字段，以处理继承关系
    private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // Not in this class, try superclass
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + clazz);
    }
    /**
     * 检查一个类名是否属于系统包
     * @param className 类的完全限定名
     * @return 如果是系统类则返回 true
     */
    private static boolean isSystemClass(String className) {
        for (String prefix : SYSTEM_PACKAGE_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
