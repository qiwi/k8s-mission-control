package ru.qiwi.devops.mission.control.utils

import java.util.Random

fun Random.nextHex(length: Int): String {
    val sb = StringBuffer()
    while (sb.length < length) {
        sb.append(Integer.toHexString(this.nextInt()))
    }
    return sb.toString().substring(0, length)
}
