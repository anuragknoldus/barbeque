package com.knoldus.barbeque.util

import java.util.UUID.randomUUID

object BarbequeUtils {

  def getRandomUUID(): String = randomUUID().toString
}
