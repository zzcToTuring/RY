package com.ruoyi;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Enumeration;

@RestController
@RequestMapping("/NoAuth/file")
public class FileShareController {

    /**
     * 生成文件下载链接
     */
    @PostMapping("/generate")
    public AjaxResult generateFileUrl(@RequestParam String filePath, HttpServletRequest request) {
        try {
            // 基础校验
            File file = new File(filePath);
            if (!file.exists()) return AjaxResult.error("文件不存在");
            if (!file.isFile()) return AjaxResult.error("路径不是文件");
            if (!file.canRead()) return AjaxResult.error("文件不可读");

            // 获取服务器信息
            String ip = getLocalIP();
            int port = request.getServerPort();
            String encodedPath = URLEncoder.encode(filePath, "UTF-8");

            // 构建下载链接
            String downloadUrl = String.format("http://%s:%d/NoAuth/file/download?path=%s", ip, port, encodedPath);
            return AjaxResult.success("链接生成成功", downloadUrl);
        } catch (Exception e) {
            return AjaxResult.error("链接生成失败: " + e.getMessage());
        }
    }

    /**
     * 文件下载接口
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path) throws UnsupportedEncodingException {
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File file = new File(decodedPath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        System.out.println("即将下载此文件");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodeFilename(file.getName()) + "\"")
                .body(resource);
    }

    /**
     * 获取本机局域网IP地址
     */
    private static String getLocalIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {  // 优先返回IPv4地址
                    return addr.getHostAddress();
                }
            }
        }
        return "127.0.0.1";
    }

    /**
     * 处理中文文件名编码问题
     */
    private String encodeFilename(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return fileName;
        }
    }
}