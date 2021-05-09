package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.doubleCut
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.rest.util.Image
import kotlinx.coroutines.reactive.awaitSingle
import java.time.Instant

class Info {
    suspend fun callinfo(guild: Guild, channel: MessageChannel){
        println("이거 출력 되면 플원 아님")
        val imgUrl: String = doubleCut(guild.getIconUrl(Image.Format.PNG).toString(), "[", "]")
        val svName: String = guild.name
        val usrCount: String = guild.memberCount.toString()
        val rlCount = guild.roles.count().block().toString()
        val svServ: String = doubleCut(guild.region.block().toString(), "name=", ",")
        val svOwn: String = doubleCut(guild.owner.block().toString(), "username=", ",") +
                "#" +
                doubleCut(guild.owner.block().toString(), "discriminator=", ",")

        channel.createEmbed() {
            it.setColor(discord4j.rest.util.Color.CYAN)
                .setThumbnail(imgUrl)
                .setDescription("Information of this server")
                .setTitle(svName)
                .addField("Owner", svOwn, true)
                .addField("Server Region", svServ, false)
                .addField("User Count", usrCount, true)
                .addField("Role Count", rlCount, true)
                .addField("Server Icon", imgUrl, false)
                .setTimestamp(Instant.now())
        }.awaitSingle()
    }
}