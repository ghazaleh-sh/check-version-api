package ir.sadad.co.checkversionapi.configs;

import com.hazelcast.config.*;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * using Embedded Hazelcast Config
 */
//@Configuration
//@EnableCaching
public class HazelcastConfig {


    @Bean
    public Config createConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-version-instance");
        config.setManagementCenterConfig(new ManagementCenterConfig());
        config.getNetworkConfig().setPortAutoIncrement(true);
//        config.getNetworkConfig().getJoin().setMulticastConfig(new MulticastConfig().setEnabled(false));
//        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
//        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
//        config.getSerializationConfig()
//                .addSerializerConfig(serializerConfig());
        config.addMapConfig(mapConfig());

        return config;
    }

    private MapConfig mapConfig() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("checkVersionCache");
        mapConfig.setStatisticsEnabled(true);

        /*
        Valid values are:
        NONE (no eviction),
        LRU (Least Recently Used),
        LFU (Least Frequently Used).
        NONE is the default.
        */
        mapConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);

        /*
        Maximum size of the map. When max size is reached,
        map is evicted based on the policy defined.
        Any integer between 0 and Integer.MAX_VALUE. 0 means
        Integer.MAX_VALUE. Default is 0.
        */
        mapConfig.getEvictionConfig().setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE);
        mapConfig.setTimeToLiveSeconds(300); // how long an entry stays in the cache. After 300 seconds, the entry will be evicted. If the entry is updated, the eviction time will reset to 0 again.
        mapConfig.setMaxIdleSeconds(20);    // how long the entry stays in the cache without being touched. An entry is “touched” with each read operation. If an entry is not touched for 20 seconds, it will be evicted.
        return mapConfig;
    }

//    @Bean
//    public KeyGenerator carKeyGenerator() {
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object o, Method method, Object... objects) {
//                return null;
//            }
//        };
//    }


}
