Mirror
简介：埋点计时Gradle插件,利用ASM插入字节码，对指定包名内的类，打印所有方法的耗时时间。
用法：
项目的build.gradle配置
```
buildscript {
    repositories {
      maven {
        url  "https://dl.bintray.com/sunhee/maven"
      }
    }   
    dependencies {
        classpath 'com.sunhee.mirror:mirror_plugin:1.0.0'
    }
}
allprojects {
    repositories {
        maven {
            url  "https://dl.bintray.com/sunhee/maven"
        }
    }
}
```

app的build.gradle配置
```
apply plugin: 'com.sunhee.mirror.plugin'

mirror {
    filter = ["com.sunhee.mirrordemo"]
    //要打印的包或者类名"com.sunhee.mirrordemo.ui.MirrorActivity"
    isDebug = true //build日志开关
    annotation = false //开启注解后只有注解标记才会打印日志，要依赖              //implementation'com.sunhee:mirror_annotations:1.0.0'
    //@MirrorLog 对方法使用此注解标记打印该方法耗时
}
```