package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.PLAYER_MANAGER
import com.giftbot.giftbox.client
import com.giftbot.giftbox.google.Search
import com.giftbot.giftbox.music.AudioTrackScheduler
import com.giftbot.giftbox.music.GuildAudioManager
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.Channel
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.rest.request.Router
import discord4j.rest.service.GuildService
import discord4j.voice.AudioProvider
import discord4j.voice.VoiceConnection
import kotlinx.coroutines.reactive.awaitSingle
import okhttp3.internal.applyConnectionSpec


class Song{
    suspend fun play(event: MessageCreateEvent, song: String?, mch: MessageChannel, guild: Guild){
        var songArg = song
        val member: Member = event.member.orElse(null)
        val voiceState: VoiceState = member.voiceState.awaitSingle()
        val channel: VoiceChannel = voiceState.channel.awaitSingle()
        val manager: GuildAudioManager = GuildAudioManager.of(channel.guildId)
        val provider: AudioProvider = manager.provider
        val connection: VoiceConnection? =  channel.join { spec: VoiceChannelJoinSpec ->
            spec.setProvider(provider)
        }.awaitSingle()

        if(channel.type == Channel.Type.GUILD_STAGE_VOICE) {
            TODO("Not Yet.")
        }

        if(song?.contains("https://") == false){
            val (id, title, thumbnail) = Search.main("searcher", song)
            songArg = "https://www.youtube.com/watch?v=$id"
            mch.createEmbed {
                it.setTitle("Song found!")
                    .setThumbnail(thumbnail)
                    .setDescription(title)
            }.awaitSingle()
        }
        else if(song != null){
            mch.createMessage("Song found!").awaitSingle()
        }


        if(song != null)
        {
            PLAYER_MANAGER!!.loadItemOrdered(manager, songArg, object:
                AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack?) {
                    if (track != null) {
                        AudioTrackScheduler(GuildAudioManager.of(channel.guildId).player).play(track, mch)
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

    suspend fun leave(event: MessageCreateEvent, member: Member = event.member.orElse(null)){
        val voiceState = member.voiceState.awaitSingle()
        val channel = voiceState.channel.awaitSingle()
        if (channel != null) {
            AudioTrackScheduler(GuildAudioManager.of(channel.guildId).player).player.stopTrack()
        }
    }
}