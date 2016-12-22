package net.basiccloud.dolphin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * IP地址工具类。
 *
 * Created by sky on 16-3-7.
 */
public class IpUtil {

    private static Logger logger = LoggerFactory.getLogger(IpUtil.class);

    /**
     * 从网络接口中获取IP地址列表。
     * <pre>
     *     注意：
     *
     *     1. 只返回 IPV4 地址
     *     2. 不返回 localhost/127.0.0.1 地址
     *     3. 只返回可用的地址 (见networkInterface.isUp())
     * </pre>
     * @return IP地址列表,如果没有有效IP地址，返回为空。
     */
    public static List<String> getIpFromNetworkInterfaces() {
        List<String> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            logger.error("can't getNetworkInterfaces()", e);
            return ipList;
        }

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            try {
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
            } catch (SocketException e) {
                logger.error("fail to check networkInterface status", e);
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (isValidIP(address)) {
                    ipList.add(address.getHostAddress());
                }
            }
        }

        return ipList;
    }

    /**
     * 通过连接socket到指定host/port来获取本地IP地址。
     * <pre>
     *      这个方法等同于调用getIpFromSocket(String host, int port)，
     *      但是默认是使用三个公网地址，分别是baidu.com/163.com/sina.com.cn，只要有一个能连接
     *      就可以获取到IP地址。
     * </pre>
     * @return IP地址，如果获取失败则Optional为空
     */
    public static Optional<String> getIpFromSocket() {
        Optional<String> result = getIpFromSocket("www.baidu.com", 80);
        if (result.isPresent()) {
            return result;
        }

        result = getIpFromSocket("www.163.com", 80);
        if (result.isPresent()) {
            return result;
        }

        result = getIpFromSocket("www.sina.com.cn", 80);
        return result;
    }

    /**
     * 通过连接socket到指定host/port来获取本地IP地址。
     * @param host 需要连接的host
     * @param port 需要连接的port
     * @return IP地址，如果获取失败则Optional为空
     */
    public static Optional<String> getIpFromSocket(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            InetAddress localAddress = socket.getLocalAddress();
            if (!isValidIP(localAddress)) {
                return Optional.empty();
            }

            String ip= localAddress.getHostAddress();
            return Optional.of(ip);
        } catch (Exception e) {
            logger.error("fail to get IP because of network failure", e);
            return Optional.empty();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // no nothing
                }
            }
        }
    }

    private static boolean isValidIP(InetAddress address) {
        return !address.isLoopbackAddress() && address instanceof Inet4Address;
    }
}
