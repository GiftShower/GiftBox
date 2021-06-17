package com.giftbot.giftbox.database

import com.giftbot.giftbox.database.Actors.autoIncrement
import com.giftbot.giftbox.database.Actors.primaryKey
import org.jetbrains.exposed.sql.Table

object Modules: Table() {
    val serverID = integer("serverId").autoIncrement().primaryKey()
    val sname = varchar("servername", 255)
    val museMod = bool("music")
    val gameMod = bool("game")
}