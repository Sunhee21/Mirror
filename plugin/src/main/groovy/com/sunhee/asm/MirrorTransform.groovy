package com.sunhee.asm

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sunhee.utils.MirrorCheckUtil
import com.sunhee.utils.MLogger
import com.sunhee.utils.MirrorClassNameUtil
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @intro 遍历所有文件更换字节码
 * @author sunhee
 * @date 2019/6/14
 */
class MirrorTransform extends Transform {

    @Override
    String getName() {
        return "MirrorTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() { // 指明你自定义的这个Transform处理的输入类型为class
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {//指明自定的Transform的输入文件所属的范围,仅当前工程代码 或其他。
        return TransformManager.SCOPE_FULL_PROJECT//这里包含 当前工程 子工程 和 EXTERNAL_LIBRARIES
    }

    @Override
    boolean isIncremental() {
        return false
    }

/**
 *  此核心方法简单理解: 每一个Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源。
 *   eg: input ->ATransform-> output>>input -> BTransform ->  output …一直下去
 */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Context context = transformInvocation.getContext()

        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->




                if (directoryInput.file.isDirectory()) {
                    MLogger.info("==========》开始遍历目录:${directoryInput.file.absolutePath}")
                    directoryInput.file.eachFileRecurse { File file ->
                        String relativePath = file.absolutePath.replace(directoryInput.file.absolutePath + File.separator, "")//得到的值-> com\sunhee\mirrordemo\A.class
                        String finalClassName = MirrorClassNameUtil.path2ClassName(relativePath)
                        //得到的值-> com.sunhee.mirrordemo.A
                        MLogger.info("==============》类或包:${finalClassName} 大小:${file.size()}")

                        def name = file.name
                        if (name.endsWith(".class") && MirrorCheckUtil.isShouldModifyClass(finalClassName)) {
                            System.out.println("目录")
                            ClassReader reader = new ClassReader(file.bytes)
                            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
                            ClassVisitor visitor = new MirrorClassVisitor(writer)
                            reader.accept(visitor, ClassReader.EXPAND_FRAMES)

                            byte[] code = writer.toByteArray() //修改后的文件覆盖原文件
                            def classPath = file.parentFile.absolutePath + File.separator + name
                            FileOutputStream fos = new FileOutputStream(classPath)
                            fos.write(code)
                            fos.close()


                        }
                    }
                }

                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput -> //这里没有对jar包做修改
                MLogger.info("==========》开始遍历Jar包:${jarInput.file.absolutePath}")

                String jarName = jarInput.file.name
                /** 截取文件路径的md5值重命名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                /** 获得输出文件*/
                File dest = transformInvocation.outputProvider.getContentLocation(jarName + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                MLogger.info("\n==》开始遍历特定jar ${dest.absolutePath}")
                def modifiedJar = modifyJarFile(jarInput.file, context.getTemporaryDir())
                MLogger.info("==》结束遍历特定jar ${dest.absolutePath}\n")
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)

//                def jarName = jarInput.name
//                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
//                if (jarName.endsWith(".jar")) {
//                    jarName = jarName.substring(0, jarName.length() - 4)
//                }
//
//                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
//                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
//
//                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

    /**
     * Jar文件中修改对应字节码
     * @jarFile 源文件
     * @tempDir 临时目录
     */
    private static File modifyJarFile(File jarFile, File tempDir) {
        if (jarFile) {
            return modifyJar(jarFile, tempDir, true)
        }
        return null
    }

    private static File modifyJar(File jarFile, File tempDir, boolean nameHex) {
        def file = new JarFile(jarFile)
        /** 设置输出到的jar */
        def hexName = ""
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        }
        def outputJar = new File(tempDir, hexName + jarFile.name)//新jar包，在临时目录里的唯一名字

        MLogger.info("----新Jar包名字----${outputJar.absolutePath}")
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))

        //遍历原jar中所有的元素
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            String className

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {//如果是类文件则进行判断是否为需要修改的类
                className = MirrorClassNameUtil.path2ClassName(entryName)
                if (MirrorCheckUtil.isShouldModifyClass(className)) {
                    MLogger.info("Jar:className:" + className)
                    modifiedClassBytes = MirrorClassModifier.modifyClass(sourceClassBytes)
                }
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return outputJar
    }



    /**
     *  jar包和class目录，打印出来用于调试
     */
    private static void printlnJarAndDir(Collection<TransformInput> inputs) {

        def classPaths = []
        String buildTypes
        String productFlavors
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPaths.add(directoryInput.file.absolutePath)
                buildTypes = directoryInput.file.name
                productFlavors = directoryInput.file.parentFile.name
                MLogger.info("||项目class目录：${directoryInput.file.absolutePath}")
            }
            input.jarInputs.each { JarInput jarInput ->
                classPaths.add(jarInput.file.absolutePath)
                MLogger.info("||项目jar包：${jarInput.file.absolutePath}")
            }
        }
    }
}