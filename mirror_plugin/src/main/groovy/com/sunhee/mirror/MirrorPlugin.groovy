package com.sunhee.mirror

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.sunhee.asm.MirrorConfig
import com.sunhee.asm.MirrorTransform
import com.sunhee.utils.MLogger
import org.gradle.api.Plugin
import org.gradle.api.Project

class MirrorPlugin implements Plugin<Project> {

    void apply(Project project) {

//        project.dependencies {
//            compile 'com.sunhee:mirror_annotations:1.0.0'
//        }

        project.extensions.create("mirror", MirrorExtension)
        System.out.println("========================");
        System.out.println("Hello MirrorPlugin!");
        System.out.println("========================");

        //使用Transform实行遍历
        def android = project.extensions.getByType(AppExtension)//AppExtension 就是build.gradle里的android{} 这一块
        registerTransform(android) //Transform 在afterEvaluate 之后执行


        project.afterEvaluate {

            //解析mirror配置的参数
            MLogger.setDebug(project.mirror.isDebug)
            MirrorConfig.setAnnotation(project.mirror.annotation)

            List<String> filterPackges = project.mirror.filter
            HashSet<String> filterPackgesSet = new HashSet<>()
            if (filterPackges != null) {
                filterPackgesSet.addAll(filterPackges)
            }
            MLogger.info("==                 Mirror 过滤设置: " + filterPackgesSet)
            MirrorConfig.setFilter(filterPackgesSet)

        }


    }


    def static registerTransform(BaseExtension android) {
        MirrorTransform transform = new MirrorTransform()
        android.registerTransform(transform)
    }
}
