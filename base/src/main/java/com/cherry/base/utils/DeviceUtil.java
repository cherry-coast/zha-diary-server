package com.cherry.base.utils;

import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2025年01月07日 15:39:00
 * ClassName ComputerUtil
 * packageName com.cherry.animal.base.utils
 */
@Slf4j
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class DeviceUtil {

    /**
     * Windows
     */
    public static final String WINDOWS = "windows";
    /**
     * Linux
     */
    public static final String LINUX = "linux";
    /**
     * Unix
     */
    public static final String UNIX = "unix";
    /**
     * 正则表达式
     */
    public static final String REGEX = "\\b\\w+:\\w+:\\w+:\\w+:\\w+:\\w+\\b";

    /**
     * 获取 Windows 主板序列号
     *
     * @return String - 计算机主板序列号
     */
    private static String getWindowsMainBoardSerialNumber() {
        String vbs = """
                Set objWMIService = GetObject("winmgmts:\\\\.\\root\\cimv2")
                Set colItems = objWMIService.ExecQuery _\s
                   ("Select * from Win32_BaseBoard")\s
                For Each objItem in colItems\s
                    Wscript.Echo objItem.SerialNumber\s
                    exit for  ' do the first cpu only!\s
                Next\s
                """;
        try {
            return winExecCommand(vbs);
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "get windows board information error, write template file error"
            );
        }
    }

    /**
     * 获取 Linux 主板序列号
     *
     * @return String - 计算机主板序列号
     */
    private static String getLinuxMainBoardSerialNumber() {
        String maniBordCmd = "dmidecode | grep 'Serial Number' | awk '{print $3}' | tail -1";
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", maniBordCmd});
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting Linux board information error, exec command error"
            );
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            if (Objects.nonNull(bufferedReader.readLine())) {
                return bufferedReader.readLine();
            }
            return "";
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting Linux board information is incorrect"
            );
        }
    }

    /**
     * 从字节获取 MAC
     *
     * @param bytes - 字节
     * @return String - MAC
     */
    private static String getMacFromBytes(byte[] bytes) {
        StringBuilder mac = new StringBuilder();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

    /**
     * 获取 Windows 网卡的 MAC 地址
     *
     * @return String - MAC 地址
     */
    private static String getWindowsMACAddress() {
        InetAddress ip;
        NetworkInterface ni;
        List<String> macList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                ni = netInterfaces.nextElement();
                //  遍历所有 IP 特定情况，可以考虑用 ni.getName() 判断
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = ips.nextElement();
                    // 非127.0.0.1
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        macList.add(getMacFromBytes(ni.getHardwareAddress()));
                    }
                }
            }
        } catch (Exception e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting Windows mac info error"
            );
        }
        if (CherryCollectionUtil.listIsNotEmpty(macList)) {
            return macList.getFirst();
        } else {
            return "";
        }
    }

    /**
     * 获取 Linux 网卡的 MAC 地址 （如果 Linux 下有 eth0 这个网卡）
     *
     * @return String - MAC 地址
     */
    private static String getLinuxMACAddressForEth0() {
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"ifconfig eth0"});
        } catch (IOException e) {
            log.warn("getting linux mac info for eth0 error, exec command error");
            return "";
        }

        String mac = "";
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            // Linux下的命令，一般取eth0作为本地主网卡
            // 显示信息中包含有 MAC 地址信息
            String line;
            int index;
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            log.warn("getting linux mac info for eth0 error");
            return "";
        }
        return mac;
    }

    /**
     * 获取 Linux 网卡的 MAC 地址
     *
     * @return String - MAC 地址
     */
    private static String getLinuxMACAddress() {
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"ifconfig"});
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting linux mac info error, exec command error"
            );
        }

        String mac = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int index = -1;
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                Pattern pat = Pattern.compile(REGEX);
                Matcher mat = pat.matcher(line);
                if (mat.find()) {
                    mac = mat.group(0);
                }
            }
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting linux mac info error"
            );
        }
        return mac;
    }

    /**
     * 获取 Windows 的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    private static String getWindowsProcessorIdentification() {
        String vbs = """
                Set objWMIService = GetObject("winmgmts:\\\\.\\root\\cimv2")
                Set colItems = objWMIService.ExecQuery _\s
                   ("Select * from Win32_Processor")\s
                For Each objItem in colItems\s
                    Wscript.Echo objItem.ProcessorId\s
                    exit for  ' do the first cpu only!\s
                Next\s
                """;

        try {
            return winExecCommand(vbs);
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "get windows CUP information error, write template file error"
            );
        }
    }

    /**
     * 获取 Linux 的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    private static String getLinuxProcessorIdentification() {
        String result = "";
        Process process;
        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "dmidecode"});
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting linux mac info error, exec command error"
            );
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int index;
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                index = line.toLowerCase().indexOf("uuid");
                if (index >= 0) {
                    result = line.substring(index + "uuid".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "getting linux cpu info error"
            );
        }
        return result.trim();
    }

    /**
     * 获取当前计算机操作系统名称 例如:windows,Linux,Unix等.
     *
     * @return String - 计算机操作系统名称
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取当前计算机操作系统名称前缀 例如:windows,Linux,Unix等.
     *
     * @return String - 计算机操作系统名称
     */
    public static String getOSNamePrefix() {
        String name = getOSName();
        if (name.startsWith(WINDOWS)) {
            return WINDOWS;
        } else if (name.startsWith(LINUX)) {
            return LINUX;
        } else if (name.startsWith(UNIX)) {
            return UNIX;
        } else {
            return "";
        }
    }

    /**
     * 获取当前计算机主板序列号
     *
     * @return String - 计算机主板序列号
     */
    public static String getMainBoardSerialNumber() {
        return switch (getOSNamePrefix()) {
            case WINDOWS -> getWindowsMainBoardSerialNumber();
            case LINUX -> getLinuxMainBoardSerialNumber();
            default -> "";
        };
    }

    /**
     * 获取当前计算机网卡的 MAC 地址
     *
     * @return String - 网卡的 MAC 地址
     */
    public static String getMACAddress() {
        switch (getOSNamePrefix()) {
            case WINDOWS -> {
                return getWindowsMACAddress();
            }
            case LINUX -> {
                String macAddressForEth0 = getLinuxMACAddressForEth0();
                if (StringUtils.isEmpty(macAddressForEth0)) {
                    macAddressForEth0 = getLinuxMACAddress();
                }
                return macAddressForEth0;
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * 获取当前计算机的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    public static String getCPUIdentification() {
        return switch (getOSNamePrefix()) {
            case WINDOWS -> getWindowsProcessorIdentification();
            case LINUX -> getLinuxProcessorIdentification();
            default -> "";
        };
    }


    @SuppressWarnings("deprecation")
    private static String winExecCommand(String vbsCommand) throws IOException {
        StringBuilder result = new StringBuilder();
        Process process;
        File file = File.createTempFile("realhowto", ".vbs");

        file.deleteOnExit();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(vbsCommand);
        }
        process = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString().trim();
    }

}
