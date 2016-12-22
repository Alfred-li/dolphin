package net.basiccloud.dolphin.internal;

import com.google.common.collect.Maps;

import net.basiccloud.etcd.registry.ServiceRegistryFactoryEtcdImpl;
import net.basiccloud.etcd.registry.internal.ServiceRegistryConnectionEtcdImpl;
import net.basiccloud.registry.RegisterId;
import net.basiccloud.registry.ServiceRegistryConnection;
import net.basiccloud.registry.ServiceRegistryServer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * dolphin configuration
 */
@Configuration
@ComponentScan(value = "net.basiccloud.dolphin.*")
public class DolphinConfiguration {

    @SuppressWarnings("unchecked")
    @Bean
    public ServiceRegistryServer<RegisterId> serviceRegistryServer() {
        HashMap<String, String> params = Maps.newHashMap();
        params.put("registryUrl", "etcd://127.0.0.1:2379");
        ServiceRegistryConnection connection = ServiceRegistryConnectionEtcdImpl.buildFromParameters(params);
        connection.connect();
        return new ServiceRegistryFactoryEtcdImpl().getServer(connection);
    }
}
