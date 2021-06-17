package com.giftbot.giftbox.game

import com.giftbot.giftbox.r6
import de.jan.r6statsjava.R6Player
import de.jan.r6statsjava.R6Stats
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.rest.util.Color
import kotlinx.coroutines.reactive.awaitSingle


class RainbowSix(uname: String) {
    private val player: R6Player? = try{
        r6.getR6PlayerStats(uname, R6Stats.Platform.PC)
    } catch (e: Exception){
        null
    }
    suspend fun rainbow(channel: MessageChannel) {
        val zerolong: Long = 0
        if(player != null){
            channel.createEmbed{
                it.setAuthor(player.username, null, player.avatarURL146)
                    .setTitle(player.username + "'s Stats")
                    .setColor(Color.DEEP_SEA)
                    .addField("General K/D", player.generalStats.kd.toString(), true)
                    .addField("General Headshot", player.generalStats.headshots.toString() + " Times", true)
                    .addField("Level", player.level.toString(), false)
            }.awaitSingle()
        }
        else channel.createMessage("Player Not Found!").awaitSingle()
    }
}