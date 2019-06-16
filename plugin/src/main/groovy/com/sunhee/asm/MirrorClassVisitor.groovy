package com.sunhee.asm

import com.sunhee.utils.MLogger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 类访问器
 * @author sunhee
 * @intro
 * @date 2019/6/15
 */
class MirrorClassVisitor extends ClassVisitor {

    private ClassVisitor classVisitor;

    private String mClassName
    private String mSuperName
    private String[] mInterfaces

    MirrorClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.classVisitor = cv;
    }


    /**
     * 访问类头部
     * @param version 表示类版本：51，表示 “.class” 文件的版本是 JDK 1.7
     * @param access 类的修饰符：修饰符在 ASM 中是以 “ACC_” 开头的常量进行定义。
     *                          可以作用到类级别上的修饰符有：ACC_PUBLIC（public）、ACC_PRIVATE（private）、ACC_PROTECTED（protected）、
     *                          ACC_FINAL（final）、ACC_SUPER（extends）、ACC_INTERFACE（接口）、ACC_ABSTRACT（抽象类）、
     *                          ACC_ANNOTATION（注解类型）、ACC_ENUM（枚举类型）、ACC_DEPRECATED（标记了@Deprecated注解的类）、ACC_SYNTHETIC
     * @param name 类的名称：通常我们的类完整类名使用 “org.test.mypackage.MyClass” 来表示，但是到了字节码中会以路径形式表示它们 “org/test/mypackage/MyClass” 。
     *                      值得注意的是虽然是路径表示法但是不需要写明类的 “.class” 扩展名。
     * @param signature 表示泛型信息，如果类并未定义任何泛型该参数为空
     * @param superName 表示所继承的父类：由于 Java 的类是单根结构，即所有类都继承自 java.lang.Object。 因此可以简单的理解为任何类都会具有一个父类。
     *                  虽然在编写 Java 程序时我们没有去写 extends 关键字去明确继承的父类，但是 JDK在编译时 总会为我们加上 “ extends Object”。
     * @param interfaces 表示类实现的接口，在 Java 中类是可以实现多个不同的接口因此此处是一个数组。
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name
        mInterfaces = interfaces
        mSuperName = superName
        MLogger.info("\n======================访问${mClassName}类头部======================");
        MLogger.info("==  version=${version};\taccess=${MLogger.accCode2String(access)};\tname=${name};\tsignature=${signature};\tsuperName=${superName};\tinterfaces=${interfaces.toArrayString()}  ==");
        super.visit(version, access, name, signature, superName, interfaces);
    }


    /** 内部类
     * @param name
     * @param outerName
     * @param innerName
     * @param access
     */
    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        MLogger.info("==                    访问类内部类${name}                    ==");
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MLogger.info("==                    访问类方法${name}                    ==");
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        if (methodVisitor != null){
            return new MirrorMethodTimerAdapter(methodVisitor,access,mClassName,name,desc);
        } else
            return methodVisitor

    }


    /**
     * 访问结束
     */
    @Override
    void visitEnd() {
        super.visitEnd();
        MLogger.info("======================访问${mClassName}类结束======================\n");
    }
}
