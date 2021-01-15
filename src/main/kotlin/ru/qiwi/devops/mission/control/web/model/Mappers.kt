package ru.qiwi.devops.mission.control.web.model

import ru.qiwi.devops.mission.control.model.KubeCluster
import java.time.Instant
import java.time.ZonedDateTime

fun KubeCluster.toDTO() = KubeClusterDTO(this.name, this.displayName, this.host, this.dataCenter)

fun Instant.toZoned(): ZonedDateTime = this.atZone(java.time.ZoneId.systemDefault())