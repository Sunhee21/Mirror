Mirror

简介：埋点计时Gradle插件,利用ASM插入字节码，对指定包名内的类或指定注解的方法，打印其方法的耗时时间。

用法：
项目的build.gradle配置
```
buildscript {
    repositories {
      maven {
        url  "https://dl.bintray.com/sunhee/maven"
      }//因为还没有添加到Jcenter 要加这一句
    }
    dependencies {
        classpath 'com.sunhee.mirror:mirror_plugin:1.0.3'
    }
}
allprojects {
    repositories {
        maven {
            url  "https://dl.bintray.com/sunhee/maven"
        }//因为还没有添加到Jcenter 要加这一句
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
    annotation = false //开启注解后只有注解标记的才会输出耗时，要依赖不然所有方法都不显示耗时             //implementation'com.sunhee:mirror_annotations:1.0.0'

}
```
注：要注解得到指定方法耗时需要添加依赖
```
implementation'com.sunhee:mirror_annotations:1.0.0'
//之后即可使用@MirrorLog注解标记打印该方法耗时
```
日志输出：
```
2019-06-17 17:42:36.902 30281-30281/? D/com/csh/application/main/mvp/ui/activity/DBTestActivity<Mirror>onCreate: 142

142就代表耗时142毫秒
```

