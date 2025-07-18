/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.systemtest.resources.types;

import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.skodjob.testframe.interfaces.ResourceType;
import io.skodjob.testframe.resources.KubeResourceManager;
import io.strimzi.api.kafka.Crds;
import io.strimzi.api.kafka.model.connector.KafkaConnector;
import io.strimzi.api.kafka.model.connector.KafkaConnectorList;
import io.strimzi.systemtest.resources.ResourceConditions;
import io.strimzi.systemtest.resources.ResourceOperation;

import java.util.function.Consumer;

public class KafkaConnectorType implements ResourceType<KafkaConnector> {
    private final MixedOperation<KafkaConnector, KafkaConnectorList, Resource<KafkaConnector>> client;

    public KafkaConnectorType() {
        client = Crds.kafkaConnectorOperation(KubeResourceManager.get().kubeClient().getClient());
    }

    @Override
    public Long getTimeoutForResourceReadiness() {
        return ResourceOperation.getTimeoutForResourceReadiness(KafkaConnector.RESOURCE_KIND);
    }

    @Override
    public NonNamespaceOperation<?, ?, ?> getClient() {
        return client;
    }

    @Override
    public String getKind() {
        return KafkaConnector.RESOURCE_KIND;
    }

    @Override
    public void create(KafkaConnector kafkaConnector) {
        client.inNamespace(kafkaConnector.getMetadata().getNamespace()).resource(kafkaConnector).create();
    }

    @Override
    public void update(KafkaConnector kafkaConnector) {
        client.inNamespace(kafkaConnector.getMetadata().getNamespace()).resource(kafkaConnector).update();
    }

    @Override
    public void delete(KafkaConnector kafkaConnector) {
        client.inNamespace(kafkaConnector.getMetadata().getNamespace()).resource(kafkaConnector).delete();
    }

    @Override
    public void replace(KafkaConnector kafkaConnector, Consumer<KafkaConnector> consumer) {
        KafkaConnector toBeReplaced = client.inNamespace(kafkaConnector.getMetadata().getNamespace()).withName(kafkaConnector.getMetadata().getName()).get();
        consumer.accept(toBeReplaced);
        update(toBeReplaced);
    }

    @Override
    public boolean isReady(KafkaConnector kafkaConnector) {
        return ResourceConditions.resourceIsReady().predicate().test(kafkaConnector);
    }

    @Override
    public boolean isDeleted(KafkaConnector kafkaConnector) {
        return kafkaConnector == null;
    }
}