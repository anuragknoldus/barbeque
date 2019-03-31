package com.knoldus.barbeque.exception

import com.lightbend.lagom.scaladsl.api.transport.{TransportErrorCode, TransportException}

final case class InternalServerError (override val errorCode: TransportErrorCode, message: String)
  extends TransportException(errorCode, message)
