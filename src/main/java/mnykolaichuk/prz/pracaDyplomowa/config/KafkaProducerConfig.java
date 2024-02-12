package mnykolaichuk.prz.pracaDyplomowa.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
* Zwraca obiekt typu ProducerFactory<String, Object>.
* Ten obiekt jest używany do konfiguracji i utworzenia instancji producenta Kafka.
* Tworząc kolejne obiekty producentów korzystamy z ProducerFactory
* Klucz('String') - określa do której partycji w danym temacie zostanie przekazana wiadomość
* Wartość('Object') - zawartość wiadomości w postaci słownika (obecnie Object w praktyce Map)

* Obiekt KafkaTemplate jest gotową klasą Springa ułatwiającą wysyłanie wiadomości do brokera Kafka
* przy użyciu producenta Kafka skonfigurowanego wcześniej przez producerFactory()
*/

@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "redpanda-0:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
