package com.giftbot.giftbox.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import discord4j.core.`object`.entity.channel.MessageChannel
import java.util.*


class AudioTrackScheduler(val player: AudioPlayer) : AudioEventAdapter() {
    private val queue: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())
    fun getQueue(): List<AudioTrack> {
        return queue
    }

    @JvmOverloads
    fun play(track: AudioTrack, channel: MessageChannel?, force: Boolean = false): Boolean {
        val playing: Boolean = player.startTrack(track, !force)
        if (!playing) {
            channel?.createMessage("Added to Queue!")?.block()
            queue.add(track)
        }
        return playing
    }

    private fun skip(): Boolean {
        return queue.isNotEmpty() && play(queue.removeAt(0), null, true)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
        // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
        if (endReason.mayStartNext) {
            skip()
        }
    }

    init {
        // The queue may be modifed by different threads so guarantee memory safety
        // This does not, however, remove several race conditions currently present
    }
}