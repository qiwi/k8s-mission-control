package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.openapi.auth.Authentication
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1ReplicaSet
import io.kubernetes.client.openapi.models.V1Service
import java.io.Closeable
import java.io.InputStream

interface KubernetesClient : Closeable {
    val clusterName: String
    val authentications: MutableMap<String, Authentication>
    val basePath: String

    fun createDeploymentInformer(namespace: String): SharedInformer<V1Deployment>

    fun createIngressInformer(namespace: String): SharedInformer<ExtensionsV1beta1Ingress>

    fun listDeployments(): Iterable<V1Deployment>

    fun listDeployments(namespace: String): Iterable<V1Deployment>

    fun listReplicaSets(namespace: String): Iterable<V1ReplicaSet>

    fun findReplicaSet(namespace: String, replicaSetName: String): V1ReplicaSet?

    fun listNamespaces(): Iterable<V1Namespace>

    fun checkNamespaceIsAccessible(namespace: String): Boolean

    fun findService(namespace: String, serviceName: String): V1Service?

    fun findDeployment(namespace: String, deploymentName: String): V1Deployment?

    fun listDeploymentPods(namespace: String, deploymentName: String): Iterable<V1Pod>

    fun listPodsBySelector(namespace: String, labelSelector: String): Iterable<V1Pod>

    fun findPod(namespace: String, podName: String): V1Pod?

    fun getNamespacedPodLog(namespace: String, pod: String, containerName: String, sinceSecond: Int, follow: Boolean): String?

    fun streamNamespacedPodLog(namespace: String, podName: String, container: String, sinceSeconds: Int, follow: Boolean): InputStream
}