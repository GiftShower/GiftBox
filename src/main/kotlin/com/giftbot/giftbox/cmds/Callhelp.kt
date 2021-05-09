package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.helps
import discord4j.core.`object`.entity.channel.MessageChannel
import kotlinx.coroutines.reactive.awaitSingle

class Callhelp {
    suspend fun showhelp(prefix: String, channel: MessageChannel){
        var hlps = helps
        hlps.replace("<prefix>", prefix).also { hlps = it }
        channel.createEmbed() {
            it.setColor(discord4j.rest.util.Color.RUBY)
                .setTitle("Help List")
                .setDescription(hlps)
        }.awaitSingle()
    }
}