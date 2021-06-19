package com.giftbot.giftbox.music

import com.giftbot.giftbox.queue
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import discord4j.core.`object`.entity.channel.MessageChannel


class AudioTrackScheduler(val player: AudioPlayer) : AudioEventAdapter() {

    fun getQueue(): List<AudioTrack> {
        val rT: MutableList<AudioTrack> = mutableListOf()
        for(i in 0..synchronized(queue, queue::lastIndex)){
            if(synchronized(queue, queue[i]::first) == player.toString()){
                rT.add(synchronized(queue, queue[i]::second))
            }
        }
        return rT
    }

    @JvmOverloads
    fun play(track: AudioTrack, channel: MessageChannel?, force: Boolean = false): Boolean {
        val playing: Boolean = player.startTrack(track, !force)
        if (!playing) {
            channel?.createMessage("Added to Queue!")?.block()
            synchronized(queue){
                queue.add(Pair(player.toString(), track))
            }
        }
        println(player)
        return playing
    }

    fun skip() {
        if(synchronized(queue, queue::isNotEmpty)){
            player.stopTrack()
            for(i in 0..synchronized(queue, queue::lastIndex)){
                if(synchronized(queue, queue[i]::first) == player.toString()){
                    synchronized(queue){
                        play(queue[i].second,null, true)
                        queue.removeAt(i)
                    }
                    break
                }
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
        // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
        if (endReason.mayStartNext) {
            skip()
        }
    }
}