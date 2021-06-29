package com.giftbot.giftbox

import com.giftbot.giftbox.cmds.*
import com.giftbot.giftbox.database.Modules
import com.giftbot.giftbox.game.ApexStat
import com.giftbot.giftbox.game.RainbowSix
import com.giftbot.giftbox.music.AudioTrackScheduler
import com.giftbot.giftbox.music.GuildAudioManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URL

suspend fun command(prefix: String, event: MessageCreateEvent, message: Message, channel: MessageChannel, guild: Guild) {
    var musicOn = transaction {
        Modules.slice(Modules.museMod).
        select { Modules.sname eq guild.name }.
        withDistinct().map {
            it[Modules.museMod]
        }
    }
    var gameOn = transaction {
        Modules.slice(Modules.gameMod).
        select { Modules.sname eq guild.name }.
        withDistinct().map {
            it[Modules.gameMod]
        }
    }

    if(musicOn.isEmpty()){
        transaction {
            Modules.insert {
                it[sname] = guild.name
                it[museMod] = true
                it[gameMod] = true
            }
        }
        musicOn = listOf(true)
        gameOn = listOf(true)
    }

    if (message.content == prefix + "ping"){
        channel.createMessage("Pong!").awaitSingle()
    }
    if (message.content == prefix +"info") Info().callinfo(guild, channel)
    if (message.content == prefix + "help")Callhelp().showhelp(prefix, channel)

    if(message.content.substringBefore(" ") == prefix + "toggle"){
        when {
            message.content.substringAfter(" ") == "music" -> {
                Toggle().tgl(musicOn, guild, channel, "Music", Modules.museMod)
            }
            message.content.substringAfter(" ") == "status" -> {
                Toggle().tgl(gameOn, guild, channel, "Game status", Modules.gameMod)
            }
            else -> {
                channel.createMessage("No function found.").awaitSingle()
            }
        }
    }
    if (message.content.substringBefore(" ") == prefix + "setprefix") SetPrfx().setPrefix(channel, message, guild)
    if (message.content == prefix + "hanriver") {
        val a = URL("https://api.hangang.msub.kr/").readText()
        channel.createMessage("현재 한강 수온은 " + doubleCut(a, "temp\":\"", "\"") + "ºC 입니다.").awaitSingle()
    }

    if(musicOn[0]){
        if (message.content.substringBefore(" ") == prefix + "join") Song().play(event, null, channel, guild)
        if(message.content.substringBefore(" ") == prefix + "play"){
            if(message.content.substringAfter(" ") != " "){
                Song().play(event, message.content.substringAfter(" "), channel, guild)
            }
            else channel.createMessage("Nothing was given").awaitSingle()
        }
        if(message.content == prefix + "stop") Song().leave(event)
        if(message.content == prefix + "skip") {
            val member: Member = event.member.orElse(null)
            val voiceState: VoiceState = member.voiceState.awaitSingle()
            val ch: VoiceChannel = voiceState.channel.awaitSingle()
            val manager: GuildAudioManager = GuildAudioManager.of(ch.guildId)
            AudioTrackScheduler(manager.player).skip()
        }
        if(message.content == prefix + "queue"){
            val member: Member = event.member.orElse(null)
            val voiceState: VoiceState = member.voiceState.awaitSingle()
            val ch: VoiceChannel = voiceState.channel.awaitSingle()
            val manager: GuildAudioManager = GuildAudioManager.of(ch.guildId)
            val rT: List<AudioTrack> = AudioTrackScheduler(manager.player).getQueue()
            var mes = ""
            for(i in 0..rT.lastIndex){
                mes += "${i+1}: ${rT[i].info.title}\n"
            }
            channel.createEmbed {
                it.setTitle("Queue")
                    .setDescription(mes)
            }.awaitSingle()
        }
    }
    //music

    if(gameOn[0])
    {
        if(message.content.substringBefore(" ") == prefix + "apexstat") {
            val apexUsername = message.content.substringAfter(" ")
            if(message.content != prefix + "apexstat") ApexStat().GetApex(apexUsername, channel)
        }
        if(message.content.substringBefore(" ") == prefix + "r6") {
            if(message.content != prefix + "rsixstat") RainbowSix(message.content.substringAfter(" ")).rainbow(channel)
        }
    }



    /*if(message.content == prefix + "react") {
        val msg = channel.createMessage("Testing").awaitSingle()
        msg.addReaction(ReactionEmoji.unicode("🛑")).block()
    }*/
}