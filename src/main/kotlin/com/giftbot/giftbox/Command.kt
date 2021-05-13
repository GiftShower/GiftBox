package com.giftbot.giftbox

import com.giftbot.giftbox.cmds.Callhelp
import com.giftbot.giftbox.cmds.Info
import com.giftbot.giftbox.cmds.SetPrfx
import com.giftbot.giftbox.cmds.Song
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

suspend fun command(prefix: String, event: MessageCreateEvent, message: Message, channel: MessageChannel, guild: Guild) {
    if (message.content == prefix + "ping"){
        channel.createMessage("Pong!")
    }
    if (message.content == prefix +"info") Info().callinfo(guild, channel)
    if (message.content == prefix + "help")Callhelp().showhelp(prefix, channel)
    if (message.content.substringBefore(" ") == prefix + "setprefix") SetPrfx().setPrefix(channel, message, guild)
    if (message.content.substringBefore(" ") == prefix + "join") Song().play(event, null, channel)
    if(message.content.substringBefore(" ") == prefix + "play"){
        if(message.content.substringAfter(" ") != " "){
            Song().play(event, message.content.substringAfter(" "), channel)
        }
        else channel.createMessage("Nothing was given").awaitSingle()
    }
    if(message.content == prefix + "stop") Song().leave(event)
    if(message.content == prefix + "shutdown") {
        channel.createMessage("Shutting Down...").awaitSingle()
        exitProcess(0)
    }
    /*if(message.content == prefix + "react") {
        val msg = channel.createMessage("Testing").awaitSingle()
        msg.addReaction(ReactionEmoji.unicode("🛑")).block()
    }*/
}