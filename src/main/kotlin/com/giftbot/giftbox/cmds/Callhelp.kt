package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.helps
import discord4j.core.`object`.entity.channel.MessageChannel
import kotlinx.coroutines.reactive.awaitSingle

class Callhelp {
    suspend fun showhelp(prefix: String, channel: MessageChannel){
        val hlps = helps.replace("<prefix>", prefix)
        channel.createEmbed() {
            it.setColor(discord4j.rest.util.Color.RUBY)
                .setTitle("Help List")
                .setDescription(hlps)
        }.awaitSingle()
    }
}