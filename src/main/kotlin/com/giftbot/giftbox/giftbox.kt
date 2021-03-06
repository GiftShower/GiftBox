package com.giftbot.giftbox

import com.giftbot.giftbox.database.Actors
import com.giftbot.giftbox.database.Modules
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import de.jan.r6statsjava.R6Stats
import discord4j.core.DiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.event.domain.VoiceServerUpdateEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


//Getting bot token
private val botTok = try {
    ClassLoader.getSystemResource("bot-token.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load bot token. Make sure to create a file named bot-token.txt in" +
                " src/main/resources and paste the bot token into that file.", error
    )
}

//help list
val helps = try {
    ClassLoader.getSystemResource("help.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load help list", error
    )
}

val r6key = try {
    ClassLoader.getSystemResource("r6apikey.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load apikey", error
    )
}

var PLAYER_MANAGER: AudioPlayerManager? = null

val r6 by lazy { R6Stats(r6key) }

val queue: MutableList<Pair<String, AudioTrack>> = Collections.synchronizedList(LinkedList())

suspend fun main() {
    Giftbox.main()
}
object Giftbox {
    init {
        PLAYER_MANAGER = DefaultAudioPlayerManager()
        (PLAYER_MANAGER as DefaultAudioPlayerManager).configuration.frameBufferFactory =
            AudioFrameBufferFactory { bufferDuration: Int, format: AudioDataFormat?, stopping: AtomicBoolean? ->
                NonAllocatingAudioFrameBuffer(
                    bufferDuration,
                    format,
                    stopping
                )
            }
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER)
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER)
    }
    suspend fun main() {
        readLine()?.let {
            try {
                Database.connect("jdbc:mysql://localhost:3306/bot?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true",
                    driver = "com.mysql.jdbc.Driver",
                    user = "root",
                    password = it)
            }catch (error: Exception){
                throw RuntimeException(
                    "Failed to log in to the server. Check if the password is correct."
                )
            }
        }

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(Actors)
            SchemaUtils.createMissingTablesAndColumns(Modules)
        }

        val client = DiscordClient.create(botTok)
        client.withGateway {
            mono {
                it.on(ReadyEvent::class.java)
                    .subscribe {
                        println("\nLogged in as " + it.self.tag)
                    }
                it.on(MessageCreateEvent::class.java)
                    .asFlow()
                    .collect { it ->
                        val message = it.message
                        val channel = message.channel.awaitSingle()
                        val guild = message.guild.awaitSingle()
                        var prefix =
                            transaction {
                                Actors.slice(Actors.prfxes).
                                select { Actors.name eq guild.name }.
                                withDistinct().map {
                                    it[Actors.prfxes]
                                }
                            }
                        if (prefix.isEmpty()){
                            transaction {
                                Actors.insert {
                                    it[name] = guild.name
                                    it[prfxes] = "!"
                                }
                            }
                            prefix =
                                transaction {
                                    Actors.slice(Actors.prfxes).
                                    select { Actors.name eq guild.name }.
                                    withDistinct().map { 
                                        it[Actors.prfxes]
                                    }
                                }
                        }
                        if(message.content.contains(prefix[0]))
                            command(prefix[0], it, message, channel, guild)
                    }
            }
        }.awaitSingle()
    }
}
