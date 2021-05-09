package com.giftbot.giftbox

import com.giftbot.giftbox.cmds.*
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle

suspend fun command(prefix: String, event: MessageCreateEvent, message: Message, channel: MessageChannel, guild: Guild) {
    if (message.content == prefix + "ping")channel.createMessage("Pong!").awaitSingle()
    if (message.content == prefix +"info") Info().callinfo(guild, channel)
    if (message.content == prefix + "help")Callhelp().showhelp(prefix, channel)
    if (message.content.substringBefore(" ") == prefix + "setprefix") SetPrfx().setPrefix(channel, message, guild)
    if (message.content.substringBefore(" ") == prefix + "join") Song().play(event, null)
    if(message.content.substringBefore(" ") == prefix + "play"){
        if(message.content.substringAfter(" ") != " "){
            Song().play(event, message.content.substringAfter(" "))
        }
        else channel.createMessage("No").awaitSingle()
    }
    if(message.content == prefix + "stop") Song().leave(event)
}