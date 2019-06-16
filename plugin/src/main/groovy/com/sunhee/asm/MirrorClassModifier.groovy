package com.sunhee.asm


import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * 类修改封装
 * @author sunhee
 * @intro
 * @date 2019/6/15
 */
class MirrorClassModifier {


    static byte[] modifyClass(byte[] srcByteCode) {

        byte[] classBytesCode = null;
        try {
            classBytesCode = modifyAsmClass(srcByteCode);
//            if (MLogger.isDebug()) {
//                seeModifyMethod(classBytesCode);
//            }
            return classBytesCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return srcByteCode;

    }

    /**
     * 真正修改类中方法字节码
     */
    static byte[] modifyAsmClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor adapter = new MirrorClassVisitor(classWriter);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(adapter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}
