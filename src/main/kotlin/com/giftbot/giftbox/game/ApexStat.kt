package com.giftbot.giftbox.game

import com.giftbot.giftbox.doubleCut
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.rest.util.Color
import org.jsoup.Jsoup


class ApexStat {
    val apikey = try {
        ClassLoader.getSystemResource("apexAPI.txt").readText().trim()
    } catch (error: Exception) {
        throw RuntimeException(
            "Failed to load apikey", error
        )
    }
    fun GetApex(name: String, channel: MessageChannel) {
        //https://api.mozambiquehe.re/bridge?version=5&platform=PC&player=$name&auth=$apikey
        val datUrl = "https://api.mozambiquehe.re/bridge?version=5&platform=PC&player=$name&auth=$apikey"
        var contin = true
        val apxDa = try{
            Jsoup.connect(datUrl).get().toString()
        }catch(e: Exception){
            channel.createMessage("Something Went Wrong...").block()
            contin=false
        }
        /*if(apxDat.contains("Error")){
            if(apxDat.contains("Player not found.")) channel.createMessage("Could not find player.").block()
            else if(apxDat.contains("but has never played Apex Legends")) channel.createMessage("Player exists but has never played.").block()
            contin = false
        }*/
        //"\"avatar\": \"https:\\/\\/secure.download.dm.origin.com\\/production\\/avatar\\/prod\\/1\\/599\\/416x416.JPEG\""
        if(contin){
            val apxDat = apxDa.toString()
            val apexUser = doubleCut(apxDat, "\"name\": \"", "\",")
            val avatar = doubleCut(apxDat, "\"avatar\": \"", "\",").replace("\\","")
            val lvl = doubleCut(apxDat, "\"level\": ", ",")
            val rkImg = doubleCut(apxDat, "\"rankImg\": \"", "\",").replace("\\","")
            val rkName = doubleCut(apxDat, "\"rankName\": \"", "\",")
            val rkSeason = doubleCut(apxDat, "\"rankedSeason\": \"season", "_")
            channel.createEmbed{
                it.setTitle("Stat of $apexUser")
                    .setColor(Color.RED)
                    .setAuthor(apexUser, null, avatar)
                    .setThumbnail(rkImg)
                    .addField("Level", lvl, false)
                    .addField("Rank", rkName, true)
                    .addField("Season", rkSeason, true)
            }.block()
        }
    }

}