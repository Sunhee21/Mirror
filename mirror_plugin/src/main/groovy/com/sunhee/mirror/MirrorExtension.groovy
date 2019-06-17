package com.sunhee.mirror

/**
 * build.gradle可配置参数
 */
class MirrorExtension {


  boolean annotation = false

  /**
   * 日志开关
   */
  boolean isDebug = true

  /**
   * 要显示的类或包
   */
  List<String> filter = []

}
