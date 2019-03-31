package com.knoldus.barbeque.util

import com.knoldus.barbeque.util.Constants.Cassandra

object Queries {

  val createType: String = s"CREATE TYPE IF NOT EXISTS ${Cassandra.KeyspaceName}.orderType(foodName text, quantity int, price double);"

  val CreateTable: String = s"CREATE TABLE IF NOT" +
    s" EXISTS ${Cassandra.KeyspaceName}.${Cassandra.TableName}" +
    "(order_id TEXT, timestamp TEXT, orderList frozen<list<orderType>>, status Text, " +
    " PRIMARY KEY(order_id, timestamp));"

  val InsertOrder: String = s"INSERT INTO ${Cassandra.KeyspaceName}.${Cassandra.TableName} JSON '${Constants.ReplaceJson}';"

  val getOrderStatus: String =
    s"""SELECT status FROM ${Cassandra.KeyspaceName}.${Cassandra.TableName} WHERE
       |order_id = '${Constants.OrderIdToReplace}' ALLOW FILTERING;""".stripMargin
}
