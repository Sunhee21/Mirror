package com.sunhee.mirror

/**
 * build.gradle可配置参数
 */
class MirrorExtension {



  /**
   * 日志开关
   */
  boolean isDebug = false

  /**
   * 要显示的类或包
   */
  List<String> filter = []

}
