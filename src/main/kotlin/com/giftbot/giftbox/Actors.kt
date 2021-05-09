package com.giftbot.giftbox

import org.jetbrains.exposed.sql.Table

object Actors: Table() {
    val serverID = integer("serverId").autoIncrement().primaryKey()
    val name = varchar("servername", 255)
    val prfxes = varchar("prefix", 255)
}