package com.giftbot.giftbox.dataClasses

import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel

data class CommandBox(val prefix: String, val message: Message, val channel: MessageChannel, val guild: Guild)