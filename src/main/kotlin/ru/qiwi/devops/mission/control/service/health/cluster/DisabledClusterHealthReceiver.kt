package ru.qiwi.devops.mission.control.service.health.cluster

import java.time.Duration

class DisabledClusterHealthReceiver : ClusterHealthReceiver {
    override fun onSuccess(duration: Duration) { }

    override fun onError(duration: Duration) { }
}