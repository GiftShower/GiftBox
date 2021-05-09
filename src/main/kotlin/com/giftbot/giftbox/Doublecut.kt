package com.giftbot.giftbox

fun doubleCut(toCut: String, dlA: String, dlB: String) = toCut.substringAfter(dlA).substringBefore(dlB)
