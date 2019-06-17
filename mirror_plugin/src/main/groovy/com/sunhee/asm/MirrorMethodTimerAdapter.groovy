package com.sunhee.asm;

import com.sunhee.utils.MLogger;

import org.gradle.internal.impldep.aQute.bnd.build.Container;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 方法访问
 * @author sunhee
 * @intro
 * @date 2019/6/15
 */
public class MirrorMethodTimerAdapter extends AdviceAdapter {


    private String mMethodName;
    private String mClassName;

    private int timerId;

    private boolean isInsert = true

    //desc 参数及返回值: 格式:(Ljava/lang/String;Ljava/lang/String;)I
    protected MirrorMethodTimerAdapter(MethodVisitor mv, int access, String className, String methodName, String desc) {
        super(Opcodes.ASM4, mv, access, methodName, desc);
        mMethodName = methodName;
        mClassName = className
        isInsert = !MirrorConfig.getAnnotation()
        MLogger.info("==                    开始扫描方法：" + MLogger.accCode2String(access) + mMethodName + desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        isInsert = !MirrorConfig.getAnnotation() || (MirrorConfig.getAnnotation() && "Lcom/sunhee/hugo_annotations/MirrorLog;".equals(desc))
        return super.visitAnnotation(desc, visible);
    }

    @Override
    protected void onMethodEnter() {
        if (isInsert) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            timerId = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, timerId);
        }
    }


    @Override
    protected void onMethodExit(int opcode) {
        if (isInsert) {
            mv.visitLdcInsn(mClassName + "\$" + mMethodName)//用方法名表示Log 的TAG
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, timerId)
            mv.visitInsn(LSUB)//表示（System.currentTimeMillis - 方法开始时间）的值

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false)
            //表示String.valueOf(前面的时间差)
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            //表示Log.d()
            pop()//编译一直不过教训：防止因为方法直接 return另一个方法 导致编译时报错
        }
    }
}
