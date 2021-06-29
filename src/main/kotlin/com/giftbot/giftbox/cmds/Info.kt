package com.giftbot.giftbox.cmds

import com.giftbot.giftbox.doubleCut
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.rest.http.client.ClientException
import discord4j.rest.util.Image
import kotlinx.coroutines.reactive.awaitSingle
import java.net.URL
import java.time.Instant

class Info {
    suspend fun callinfo(guild: Guild, channel: MessageChannel){
        println("이거 출력 되면 플원 아님")
        val imgUrl: String = doubleCut(try{
            guild.getIconUrl(Image.Format.PNG)
        }catch(e: ClientException){
            "[NaN]"
        }.toString(), "[", "]")
        val svName: String = guild.name
        val usrCount: String = guild.memberCount.toString()
        val rlCount = guild.roles.count().awaitSingle()
        val svServ: String = doubleCut(guild.region.awaitSingle().toString(), "name=", ",")
        val svOwn: String = doubleCut(guild.owner.awaitSingle().toString(), "username=", ",") +
                "#" +
                doubleCut(guild.owner.awaitSingle().toString(), "discriminator=", ",")

        channel.createEmbed() {
            it.setColor(discord4j.rest.util.Color.CYAN)
                .setThumbnail(
                    when (imgUrl) {
                        "NaN" -> "https://cdn.logojoy.com/wp-content/uploads/20210422095037/discord-mascot.png"
                        else -> imgUrl
                    }
                )
                .setDescription("Information of this server")
                .setTitle(svName)
                .addField("Owner", svOwn, true)
                .addField("Server Region", svServ, false)
                .addField("User Count", usrCount, true)
                .addField("Role Count", rlCount.toString(), true)
                .addField("Server Icon", imgUrl, false)
                .setTimestamp(Instant.now())
        }.awaitSingle()
    }
}