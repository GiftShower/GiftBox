package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.PLAYER_MANAGER
import com.giftbot.giftbox.Search
import com.giftbot.giftbox.music.AudioTrackScheduler
import com.giftbot.giftbox.music.GuildAudioManager
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.voice.AudioProvider
import discord4j.voice.VoiceConnection


class Song{
    fun play(event: MessageCreateEvent, song: String?, mch: MessageChannel){
        var songArg = song
        val member: Member = event.member.orElse(null)
        val voiceState: VoiceState = member.voiceState.block()
        val channel: VoiceChannel = voiceState.channel.block()
        val manager: GuildAudioManager = GuildAudioManager.of(channel.guildId)
        val provider: AudioProvider = manager.provider
        val connection: VoiceConnection? =
                channel.join { spec: VoiceChannelJoinSpec -> spec.setProvider(provider) }
                    .block()
        if(song?.contains("https://") == false){
            val (id, title, thumbnail) = Search.main("searcher", song)
            songArg = "https://www.youtube.com/watch?v=$id"
        }

        if(song != null)
        {
            mch.createMessage("**Searching...**").block()
            PLAYER_MANAGER!!.loadItemOrdered(manager, songArg, object:
                AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack?) {
                    if (track != null) {
                        AudioTrackScheduler(GuildAudioManager.of(channel.guildId).player).play(track)
                    }
                }

                override fun playlistLoaded(playlist: AudioPlaylist?) {
                }

                override fun noMatches() {

                }

                override fun loadFailed(exception: FriendlyException?) {

                }
            })
        }
    }

    fun leave(event: MessageCreateEvent,channel: VoiceChannel? = event.member.orElse(null).voiceState.block().channel.block()){
        if (channel != null) {
            AudioTrackScheduler(GuildAudioManager.of(channel.guildId).player).player.stopTrack()
        }
    }
}