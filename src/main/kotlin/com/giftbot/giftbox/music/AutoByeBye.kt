package com.giftbot.giftbox.music

import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.event.domain.VoiceStateUpdateEvent
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import java.lang.Void
import java.time.Duration

class AutoByeBye {
    val channel: VoiceChannel? = null
    val onDisconnect: Mono<Void> = channel!!.join { }
        .flatMap { connection ->
            // The bot itself has a VoiceState; 1 VoiceState signals bot is alone
            val voiceStateCounter: Publisher<Boolean> = channel.voiceStates
                .count()
                .map { count -> 1L == count }

            // After 10 seconds, check if the bot is alone. This is useful if
            // the bot joined alone, but no one else joined since connecting
            val onDelay: Mono<Void> = Mono.delay(Duration.ofSeconds(10L))
                .filterWhen { ignored -> voiceStateCounter }
                .switchIfEmpty(Mono.never())
                .then()

            // As people join and leave `channel`, check if the bot is alone.
            // Note the first filter is not strictly necessary, but it does prevent many unnecessary cache calls
            val onEvent: Mono<Void> = channel.client.eventDispatcher.on(VoiceStateUpdateEvent::class.java)
                .filter { event ->
                    event.getOld().flatMap(VoiceState::getChannelId).map(channel.id::equals).orElse(false)
                }
                .filterWhen { voiceStateCounter }
                .next()
                .then()
            Mono.first(onDelay, onEvent).then(connection.disconnect())
        }
}