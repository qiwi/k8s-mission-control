package ru.qiwi.devops.mission.control.service.service

import ru.qiwi.devops.mission.control.model.ServiceInfo

interface ServiceService {
    fun getServicesByDeployment(namespace: String, deploymentName: String): List<ServiceInfo>
}