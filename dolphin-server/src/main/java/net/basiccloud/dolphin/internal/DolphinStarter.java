package net.basiccloud.dolphin.internal;

import com.google.common.collect.Lists;

import net.basiccloud.dolphin.DolphinService;
import net.basiccloud.dolphin.utils.IpUtil;
import net.basiccloud.registry.RegisterId;
import net.basiccloud.registry.ServiceInstance;
import net.basiccloud.registry.ServiceInstanceData;
import net.basiccloud.registry.ServiceInstanceStatus;
import net.basiccloud.registry.ServiceRegistryServer;
import net.basiccloud.registry.Version;
import net.basiccloud.registry.WorkMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.internal.ServerImpl;
import io.grpc.netty.NettyServerBuilder;

/**
 * this class will scan @dolphinService and publish service
 */
@Component
class DolphinStarter implements CommandLineRunner, DisposableBean {


    private static Logger logger = LoggerFactory.getLogger(DolphinStarter.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ServiceRegistryServer<RegisterId> serviceRegistryServer;

    private static List<RegisterId> registerIds = Lists.newArrayList();
    private static List<Server> servers = Lists.newArrayList();

    @Override
    public void run(String... strings) throws Exception {
        logger.info("start run");
        Optional<Object> dolphinServiceOptional = applicationContext.getBeansWithAnnotation(
                DolphinService.class).values().stream().findFirst();
        if (dolphinServiceOptional.isPresent()) {
            BindableService bindableService = (BindableService) dolphinServiceOptional.get();
            ServerImpl server = NettyServerBuilder.forPort(1080).addService(bindableService).build();
            server.start();
            servers.add(server);
            logger.info("service start");

            RegisterId registry = registry("testGroup", "testService", "0.1.0");
            registerIds.add(registry);
            server.awaitTermination();
        }
    }

    private RegisterId registry(String group, String name, String version) {
        ServiceInstanceData serviceInstanceData = ServiceInstanceData.newBuilder().setServiceType
                (ServiceInstanceData.DOLPHIN_SERVICE_TYPE).setServiceVersion(Version.valueOf(version))
                .setFrameworkVersion("0.1.0").setWorkMode(WorkMode.NORMAL).build();

        ServiceInstanceStatus serviceInstanceStatus = ServiceInstanceStatus.newBuilder().setStatus(
                ServiceInstanceStatus.Status.ONLINE).setComment("registry service").setLastUpdateTime(System
                .currentTimeMillis()).build();

        Optional<String> ipFromSocket = IpUtil.getIpFromSocket();
        if (ipFromSocket.isPresent()) {
            ServiceInstance serviceInstance = ServiceInstance.newBuilder().setData(serviceInstanceData).setGroup(
                    group).setService(name).setIp(ipFromSocket.get()).setPort(1080).setStatus(serviceInstanceStatus)
                    .build();
            return serviceRegistryServer.register(serviceInstance);
        }
        throw new RuntimeException("can not get local ip");
    }

    @Override
    public void destroy() throws Exception {
        servers.forEach(Server::shutdown);
        registerIds.forEach(registerId -> serviceRegistryServer.deregister(registerId));
    }
}
