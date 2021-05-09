package com.giftbot.giftbox.music

import com.giftbot.giftbox.PLAYER_MANAGER
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import discord4j.common.util.Snowflake

import java.util.concurrent.ConcurrentHashMap


class GuildAudioManager private constructor() {
    val player: AudioPlayer = PLAYER_MANAGER!!.createPlayer()
    val scheduler: AudioTrackScheduler = AudioTrackScheduler(player)
    val provider: LavaPlayerAudioProvider = LavaPlayerAudioProvider(player)

    companion object {
        private val MANAGERS: MutableMap<Snowflake, GuildAudioManager> = ConcurrentHashMap()
        fun of(id: Snowflake): GuildAudioManager {
            return MANAGERS.computeIfAbsent(
                id
            ) { GuildAudioManager() }
        }
    }

    init {
        player.addListener(scheduler)
    } // getters
}