package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.database.Modules
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.channel.MessageChannel
import kotlinx.coroutines.reactive.awaitSingle
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class Toggle {
    suspend fun tgl(workvl: List<Boolean>, guild: Guild, channel: MessageChannel,whatFc: String, sqlFc: Column<Boolean>) {
        if(workvl[0]){
            transaction {
                Modules.update({ Modules.sname eq guild.name }) {
                    it[sqlFc] = false
                }
            }
            channel.createEmbed {
                it.setTitle("$whatFc function is now OFF!")
                    .setDescription("The command related to ${whatFc.lowercase()} will not work.\n" +
                            "To turn this on, use this command again.")
            }.awaitSingle()
        }
        else{
            transaction {
                Modules.update({ Modules.sname eq guild.name }) {
                    it[sqlFc] = true
                }
            }
            channel.createEmbed {
                it.setTitle("$whatFc function is now ON!")
                    .setDescription("The command related to ${whatFc.lowercase()} will work.\n" +
                            "To turn this off, use this command again.")
            }.awaitSingle()
        }
    }
}