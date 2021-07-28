package com.giftbot.giftbox

import arrow.core.Either
import com.giftbot.giftbox.dataClasses.CommandBox
import com.giftbot.giftbox.dataClasses.Key
import com.google.gson.Gson
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import discord4j.core.DiscordClient
import discord4j.core.event.domain.lifecycle.DisconnectEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.json.JSONObject
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap


lateinit var keys: Key

var PLAYER_MANAGER: AudioPlayerManager? = null
val queue: MutableList<Pair<String, AudioTrack>> = Collections.synchronizedList(LinkedList())
var prefixMap:HashMap<String, String>? = HashMap()

suspend fun main() {
    Giftbox.main()
}

sealed class BoxDamaged{
    object NoPrefixFound : Error()
}
object Giftbox {
    init {
        setPlayer()
        loadPrefix()
        loadTokens()
    }
    suspend fun main() {
        val client = DiscordClient.create(keys.discord)
        client.withGateway {
            mono {
                it.on(ReadyEvent::class.java)
                    .subscribe {
                        println("\nLogged in as " + it.self.tag)
                    }
                it.on(MessageCreateEvent::class.java)
                    .asFlow()
                    .collect {
                        val guildName = it.guild.awaitSingle().name
                        val prefix: String? = when (catchPrefix(guildName)) {
                            is Either.Left -> "?"
                            is Either.Right -> prefixMap?.get(guildName)
                        }
                        val cardboard = CommandBox(prefix!! ,it.message, it.message.channel.awaitSingle(), it.message.guild.awaitSingle())
                        if(cardboard.message.content.startsWith(prefix)) {
                            boxCommands(it, cardboard)
                        }
                    }
                it.on(DisconnectEvent::class.java)
                    .subscribe{
                        savePrefix()
                        println("Disconnecting")
                    }
            }
        }.awaitSingle()
    }

    private fun setPlayer(){
        PLAYER_MANAGER = DefaultAudioPlayerManager()
        (PLAYER_MANAGER as DefaultAudioPlayerManager).configuration.frameBufferFactory =
            AudioFrameBufferFactory { bufferDuration: Int, format: AudioDataFormat?, stopping: AtomicBoolean? ->
                NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping)
            }
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER)
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER)
    }

    private fun savePrefix(){
        val gson = Gson()
        val jsonOBJ = JSONObject(gson.toJson(prefixMap))
        val file = FileWriter(ClassLoader.getSystemResource("prefixes.json").path)
        file.write(jsonOBJ.toString())
    }

    private fun loadPrefix(){
        val gson = Gson()
        val file = FileReader(ClassLoader.getSystemResource("prefixes.json").path)
        prefixMap = (gson.fromJson<HashMap<String, String>>(file, Map::class.java))
    }

    private fun loadTokens(){
        val gson = Gson()
        val file = FileReader(ClassLoader.getSystemResource("keys.json").path)
        keys = gson.fromJson(file, Key::class.java)
    }

    private fun catchPrefix(s: String): Either<Error, String> =
        if(prefixMap != null) {
            if (prefixMap!!.containsKey(s)) Either.Right(prefixMap!![s].toString())
            else Either.Left(BoxDamaged.NoPrefixFound)
        } else Either.Left(BoxDamaged.NoPrefixFound)
}