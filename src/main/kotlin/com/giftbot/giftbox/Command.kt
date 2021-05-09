package com.giftbot.giftbox

import com.giftbot.giftbox.cmds.*
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle
import sun.java2d.pipe.SpanShapeRenderer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

suspend fun command(prefix: String, event: MessageCreateEvent, message: Message, channel: MessageChannel, guild: Guild, time: Long) {
    if (message.content == prefix + "ping"){
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        val timeInDate = Date(System.currentTimeMillis())
        val timeInFormat = sdf.format(timeInDate).substringAfter(".").toInt()
        val tIf = sdf.format(time).substringAfter(".").toInt()
        val res: String = if(tIf > timeInFormat) "Respond Time: " + "0." + (tIf-timeInFormat)
        else "Respond Time: " + "0." + (timeInFormat - tIf)
        channel.createMessage(res).awaitSingle()
    }
    if (message.content == prefix +"info") Info().callinfo(guild, channel)
    if (message.content == prefix + "help")Callhelp().showhelp(prefix, channel)
    if (message.content.substringBefore(" ") == prefix + "setprefix") SetPrfx().setPrefix(channel, message, guild)
    if (message.content.substringBefore(" ") == prefix + "join") Song().play(event, null)
    if(message.content.substringBefore(" ") == prefix + "play"){
        if(message.content.substringAfter(" ") != " "){
            Song().play(event, message.content.substringAfter(" "))
        }
        else channel.createMessage("Nothing was given").awaitSingle()
    }
    if(message.content == prefix + "stop") Song().leave(event)
}