package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.database.Actors
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import kotlinx.coroutines.reactive.awaitSingle
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


class SetPrfx {
    suspend fun setPrefix (channel: MessageChannel, message: Message, guild: Guild) {
        val setTo = message.content.substringAfter(" ")

        transaction {
            Actors.update ({ Actors.name eq guild.name }) {
                it[prfxes] = setTo
            }
        }

        channel.createMessage("Prefix is now $setTo").awaitSingle()
    }
}