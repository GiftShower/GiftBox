package com.giftbot.giftbox

import com.giftbot.giftbox.commandClass.BasicCmd
import com.giftbot.giftbox.dataClasses.CommandBox
import discord4j.core.event.domain.message.MessageCreateEvent

suspend fun boxCommands(event: MessageCreateEvent, box: CommandBox) {
    println(box.message.content)
    when(box.message.content.substringAfter(box.prefix)){
        "ping" -> BasicCmd().ping(event, box)
    }
}