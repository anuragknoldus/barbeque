package com.knoldus.barbeque.util

import java.time.LocalDateTime

object DateTimeUtils {

  def currentDateTime(): String = LocalDateTime.now().toString
}
