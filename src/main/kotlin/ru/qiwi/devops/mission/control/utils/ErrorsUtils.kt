package ru.qiwi.devops.mission.control.utils

import ru.qiwi.devops.mission.control.web.errors.ApiErrors
import ru.qiwi.devops.mission.control.web.errors.ApiException

fun apiUnavailableException(reason: String, e: Throwable): ApiException {
    return ApiException(
        ApiErrors.CLUSTER_UNAVAILABLE,
        "Cluster api unavailable: $reason",
        e
    )
}

fun clusterNotFoundException(clusterName: String): ApiException {
    return ApiException(
        ApiErrors.CLUSTER_NOT_FOUND,
        "Cluster '$clusterName' don't exist"
    )
}