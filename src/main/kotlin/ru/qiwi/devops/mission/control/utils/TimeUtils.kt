package ru.qiwi.devops.mission.control.utils

import org.joda.time.DateTime
import java.time.Instant

fun DateTime.toJavaInstant() = Instant.ofEpochMilli(this.millis)

fun Instant?.orZeroTime() = this ?: Instant.MIN