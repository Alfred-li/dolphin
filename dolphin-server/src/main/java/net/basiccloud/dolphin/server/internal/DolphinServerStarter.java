package net.basiccloud.dolphin.server.internal;

import com.google.common.collect.Maps;

import net.basiccloud.dolphin.DolphinService;
import net.basiccloud.etcd.registry.ServiceRegistryFactoryEtcdImpl;
import net.basiccloud.registry.ServiceRegistryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;

/**
 * server starter
 */
@Component
@SuppressWarnings("unused")
class DolphinServerStarter implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(DolphinServerStarter.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... strings) throws Exception {
        Collection<Object> dolphinServices = applicationContext.getBeansWithAnnotation(DolphinService.class).values();


        ServiceRegistryFactory serviceRegistryFactory = new ServiceRegistryFactoryEtcdImpl();
        HashMap<String, String> params = Maps.newHashMap();
        params.put("registryUrl", "etcd://127.0.0.1:2379");
        serviceRegistryFactory.connect(params);
    }
}
