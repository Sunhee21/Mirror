package com.sunhee.utils

import com.sunhee.asm.MirrorConfig


/**
 * 类名检查过滤
 */
class MirrorCheckUtil {

    /**
     * 是否对扫描类进行修改
     *
     * @param className 扫描到的类名 格式 ：com.sunhee.mirrordemo.MirrorCheckUtil
     * @param exclude 过滤掉的类
     */
    static boolean isShouldModifyClass(String className) {
        if (className.contains('R$') ||
                className.contains('R2$') ||
                className.endsWith('R') ||
                className.endsWith('R2') ||
                className.endsWith('BuildConfig')) {
            return false
        }

        Iterator<String> filter = MirrorConfig.getFilter().iterator()
        int size = MirrorConfig.getFilter().size()
        while (filter.hasNext()){
            String fn = filter.next()
            MLogger.info("此次的过滤设置2: " + fn + className.startsWith(fn))
            if (className.startsWith(fn))return true //设置了根据设置的返回true
        }
        MLogger.info("此次的过滤设置3: " +filter.size() == 0)
        return size == 0 //没设置默认显示所有

    }


}
