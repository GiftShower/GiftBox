package com.giftbot.giftbox.commandClass

import com.giftbot.giftbox.dataClasses.CommandBox
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import kotlinx.coroutines.reactive.awaitSingle

class BasicCmd {
    suspend fun ping(event: MessageCreateEvent, box: CommandBox){
        val boxmessage = box.channel.createEmbed(EmbedCreateSpec.create()
                   .withDescription("Finishing...")).awaitSingle()
        val boxTime = boxmessage.timestamp
        boxmessage.delete().block()
        box.channel.createEmbed(EmbedCreateSpec.create()
            .withTitle("Response Time")
            .withDescription("Ping: ${boxTime.toEpochMilli() - box.message.timestamp.toEpochMilli()}ms"))
            .awaitSingle()

    }
    fun info(event: MessageCreateEvent, box: CommandBox){
        box.channel.createEmbed(EmbedCreateSpec.create()
            .withTitle(box.guild.name)
        )
    }
    /*fun help()
    fun toggleF()
    fun sPfx()*/
}