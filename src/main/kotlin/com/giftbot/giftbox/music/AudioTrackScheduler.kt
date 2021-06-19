package com.giftbot.giftbox.music

import com.giftbot.giftbox.PLAYER_MANAGER
import com.giftbot.giftbox.queue
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import discord4j.core.`object`.entity.channel.MessageChannel
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.*


class AudioTrackScheduler(val player: AudioPlayer) : AudioEventAdapter() {
    private val musique: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())

    fun getQueue(): List<AudioTrack> {
        return musique
    }

    @JvmOverloads
    fun play(track: AudioTrack, channel: MessageChannel?, force: Boolean = false): Boolean {
        val playing: Boolean = player.startTrack(track, !force)
        if (!playing) {
            channel?.createMessage("Added to Queue!")?.block()
            queue.add("$player☆${track.info.uri.toHttpUrl()}")
        }
        println(player)
        return playing
    }

    fun skip() {
        if(musique.isNotEmpty()){
            player.stopTrack()
            for(i in 0..queue.lastIndex){
                if(queue[i].contains(player.toString())) {
                    queue.removeAt(i)
                    break
                }
            }
            play(musique.removeAt(0),null, true)
        }
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
        // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
        if (endReason.mayStartNext) {
            skip()
        }
    }

    init {
        lq()
    }

    private fun lq() {
        for (i in 0..queue.lastIndex){
            val a = queue[i]
            if(a.contains(player.toString())){
                PLAYER_MANAGER!!.loadItem(a.substringAfter("☆"), object :
                    AudioLoadResultHandler{
                    override fun trackLoaded(track: AudioTrack) {
                        synchronized(musique){
                            musique.add(track)
                            //queue not working!
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
    }
}