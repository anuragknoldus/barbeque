package com.knoldus.barbeque.util

object Constants {

  val Empty: String = ""
  val CassandraKeyspaceName: String = "cassandra.store.data.keyspace"
  val CassandraTableName: String = "cassandra.store.data.table"
  val Zero: Long = 0
  val EmptyTimeStampInput: Long =0L
  val OrderIdToReplace = "ORDER_ID_TO_REPLACE"
  val AcceptedStatus = "accepted"
  val ReplaceJson = "Replace_JSON"

  object Cassandra {
    val KeyspaceName: String = ConfigManager.getValue(CassandraKeyspaceName)
    val TableName: String = ConfigManager.getValue(CassandraTableName)
  }

  object Error {
    val UnprocessableErrorCode: Int = 422
    val UnprocessableEntityErrorMessage: String = "Request Body contains invalid parameters"
    val InternalServerErrorCode: Int = 500
    val InternalServerErrorMessage: String = "Request Body contains invalid parameters"
    val DataNotFoundErrorCode: Int = 404
    val DataNotFoundErrorMessage: String = "Data is not available"
  }

  object Lagom {
    val NumberOfShards: Int = 4
  }
}
