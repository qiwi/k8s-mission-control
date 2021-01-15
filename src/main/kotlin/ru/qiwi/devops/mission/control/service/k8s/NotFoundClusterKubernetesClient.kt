package ru.qiwi.devops.mission.control.service.k8s

import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.openapi.auth.Authentication
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.openapi.models.V1ReplicaSet
import io.kubernetes.client.openapi.models.V1Service
import ru.qiwi.devops.mission.control.utils.clusterNotFoundException
import java.io.InputStream

// See AbstractClusterDependentServiceFactory.kt for details
class NotFoundClusterKubernetesClient(
    override val clusterName: String
) : KubernetesClient {
    override val authentications: MutableMap<String, Authentication>
        get() = throw clusterNotFoundException(clusterName)
    override val basePath: String
        get() = throw clusterNotFoundException(clusterName)

    override fun createDeploymentInformer(namespace: String): SharedInformer<V1Deployment> {
        throw clusterNotFoundException(clusterName)
    }

    override fun createIngressInformer(namespace: String): SharedInformer<ExtensionsV1beta1Ingress> {
        throw clusterNotFoundException(clusterName)
    }

    override fun listDeployments(): Iterable<V1Deployment> {
        throw clusterNotFoundException(clusterName)
    }

    override fun listDeployments(namespace: String): Iterable<V1Deployment> {
        throw clusterNotFoundException(clusterName)
    }

    override fun listNamespaces(): Iterable<V1Namespace> {
        throw clusterNotFoundException(clusterName)
    }

    override fun checkNamespaceIsAccessible(namespace: String): Boolean {
        throw clusterNotFoundException(clusterName)
    }

    override fun findDeployment(namespace: String, deploymentName: String): V1Deployment? {
        throw clusterNotFoundException(clusterName)
    }

    override fun listDeploymentPods(namespace: String, deploymentName: String): Iterable<V1Pod> {
        throw clusterNotFoundException(clusterName)
    }

    override fun listPodsBySelector(namespace: String, labelSelector: String): Iterable<V1Pod> {
        throw clusterNotFoundException(clusterName)
    }

    override fun findPod(namespace: String, podName: String): V1Pod? {
        throw clusterNotFoundException(clusterName)
    }

    override fun listReplicaSets(namespace: String): Iterable<V1ReplicaSet> {
        throw clusterNotFoundException(clusterName)
    }

    override fun findReplicaSet(namespace: String, replicaSetName: String): V1ReplicaSet? {
        throw clusterNotFoundException(clusterName)
    }

    override fun findService(namespace: String, serviceName: String): V1Service? {
        throw clusterNotFoundException(clusterName)
    }

    override fun getNamespacedPodLog(namespace: String, pod: String, containerName: String, sinceSecond: Int, follow: Boolean): String? {
        throw clusterNotFoundException(clusterName)
    }

    override fun streamNamespacedPodLog(namespace: String, podName: String, container: String, sinceSeconds: Int, follow: Boolean): InputStream {
        throw clusterNotFoundException(clusterName)
    }

    override fun close() { }
}