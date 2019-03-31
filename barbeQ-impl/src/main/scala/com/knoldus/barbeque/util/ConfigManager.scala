package com.knoldus.barbeque.util

import com.typesafe.config.{Config, ConfigFactory}

object ConfigManager {

  val Config: Config = ConfigFactory.load()
  def getValue(path: String): String = Config.getString(path)
  def getLongValue(path: String): Long = Config.getLong(path)
  def getIntValue(path: String): Int = Config.getInt(path)

}
