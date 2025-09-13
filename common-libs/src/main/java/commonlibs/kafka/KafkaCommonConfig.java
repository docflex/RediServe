package commonlibs.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaCommonConfig
 * <p>
 * Centralized helper for creating Kafka consumers, producers, and listener container factories.
 * <p>
 * <ul>
 *     <li>Supports both JSON and String payloads.</li>
 *     <li>Fully configurable via application.yml (bootstrap servers and trusted packages).</li>
 *     <li>Meant to be extended or used as a base helper in microservices to reduce boilerplate.</li>
 * </ul>
 */
public class KafkaCommonConfig {

    /** Kafka bootstrap servers, injected from YAML */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /** Packages trusted for JSON deserialization */
    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackages;

    // --------------------------------------
    // JSON Consumer / Listener Helpers
    // --------------------------------------

    /**
     * Create a generic JSON consumer factory for a specific class type.
     *
     * @param clazz   Class of the message payload
     * @param groupId Kafka consumer group ID
     */
    public <T> ConsumerFactory<String, T> jsonConsumerFactory(Class<T> clazz, String groupId) {
        // Configure props
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(clazz));
    }

    /**
     * Create a listener container factory for JSON payloads.
     */
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> jsonListenerFactory(Class<T> clazz, String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory(clazz, groupId));
        return factory;
    }

    // --------------------------------------
    // String Consumer / Listener Helpers
    // --------------------------------------

    public ConsumerFactory<String, String> stringConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public ConcurrentKafkaListenerContainerFactory<String, String> stringListenerFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory(groupId));
        return factory;
    }

    // --------------------------------------
    // JSON Producer / KafkaTemplate Helpers
    // --------------------------------------

    public <T> ProducerFactory<String, T> jsonProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // optional: prevents type headers
        return new DefaultKafkaProducerFactory<>(props);
    }

    public <T> KafkaTemplate<String, T> jsonKafkaTemplate() {
        return new KafkaTemplate<>(jsonProducerFactory());
    }

    // --------------------------------------
    // String Producer / KafkaTemplate Helpers
    // --------------------------------------

    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }
}