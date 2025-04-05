package com.ruoyi;

import com.ruoyi.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/NoAuth/file")
public class FileHandleController {

    private static final Logger log = LoggerFactory.getLogger(FileShareController.class);


    @PostMapping("/generateUrl")
    public AjaxResult generateFileUrl(@RequestParam String filePath, HttpServletRequest request) {
        log.info("收到生成下载链接请求，文件夹路径: {}", filePath);
        try {
            // 基础校验
            if (filePath == null || filePath.trim().isEmpty()) {
                log.error("文件路径不能为空！");
                return AjaxResult.error("文件路径不能为空！");
            }
            filePath = filePath.trim();
            filePath = filePath.replaceAll("\\p{C}", "").trim();
            File file = new File(filePath);
            if (!file.exists()){
                log.error("文件不存在：{}",filePath);
                return AjaxResult.error("文件不存在："+filePath);
            }
            if (!file.canRead()){
                log.error("文件不可读：{}",filePath);
                return AjaxResult.error("文件不可读："+filePath);
            }

            //基础信息获取-拼接URL
            String ip = getLocalIP();
            int port = request.getServerPort();
            String encodedPath = URLEncoder.encode(filePath, "UTF-8");
            String scheme = request.getScheme(); // 获取 http 或 https
            String contextPath = request.getContextPath(); // 获取 context path
            //文本模式
            if (file.isFile()) {
                String downloadUrl = UriComponentsBuilder.newInstance()
                        .scheme(scheme)
                        .host(ip) // 使用你获取的IP
                        .port(port) // 使用你获取的端口
                        .path(contextPath) // 添加 context path
                        .path("/NoAuth/file/downloadFile") // 添加你的固定路径
                        .queryParam("path", encodedPath) // 添加查询参数
                        .build()
                        .toUriString();
                log.info("文件链接【File】生成成功: {}", downloadUrl);
                return AjaxResult.success("链接【file】生成成功：", downloadUrl);
            }else if(file.isDirectory()){
                //PS：如果要保护链接安全，使用base64加密
                //String encodedPath = Base64.getUrlEncoder().encodeToString(folderPath.getBytes(StandardCharsets.UTF_8));
                String downloadUrl = UriComponentsBuilder.newInstance()
                        .scheme(scheme)
                        .host(ip) // 使用你获取的IP
                        .port(port) // 使用你获取的端口
                        .path(contextPath) // 添加 context path
                        .path("/NoAuth/file/downloadZip") // 添加你的固定路径
                        .queryParam("path", encodedPath) // 添加查询参数
                        .build()
                        .toUriString();
                log.info("文件链接【ZIP】生成成功: {}", downloadUrl);
                return AjaxResult.success("链接【ZIP】生成成功：", downloadUrl);
            }

        } catch (Exception e) {
            return AjaxResult.error("链接生成失败: " + e.getMessage());
        }
        return AjaxResult.error("该路径既不是一个单独的文件，也不是一个文件夹！" );
    }


    /**
     * 文件下载接口
     */
    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam String path,HttpServletResponse response) {
        try {
            File file = new File(path);
            if (!file.exists() || !file.isFile()) { // 确保是存在的文件
                log.error("下载请求失败：文件不存在或不是一个有效文件: {}", path);
                sendErrorResponse(response, HttpStatus.NOT_FOUND, "请求的文件不存在或无效。");
                return;
            }
            // 设置响应头，使用通用的二进制流类型，或根据需要动态确定MIME类型
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String encodedFilename = encodeFilename(file.getName()); // 对文件名进行编码
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"");
            // 设置文件大小（可选，但有助于客户端显示下载进度）
            response.setContentLengthLong(file.length());

            // 使用try-with-resources确保流正确关闭
            try (InputStream inputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {

                FileCopyUtils.copy(inputStream, outputStream); // Spring提供的流拷贝工具类
                log.info("文件 {} 下载成功。", path);
                outputStream.flush(); // 确保所有数据写入响应
            }

        } catch (UnsupportedEncodingException e) {
            log.error("URL路径解码失败: {}", path, e);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "无效的请求路径编码。");
        } catch (FileNotFoundException e) {
            log.error("尝试下载时文件未找到（理论上前面已检查，但以防万一）: {}", path, e);
            if (!response.isCommitted()) { // 检查响应是否已提交
                sendErrorResponse(response, HttpStatus.NOT_FOUND, "请求的文件不存在。");
            }
        } catch (IOException e) {
            log.error("下载文件 {} 时发生IO错误: {}", path, e);
            // 如果IO错误发生在流传输过程中，可能无法再发送错误响应
            if (!response.isCommitted()) {
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "文件传输时发生错误。");
            }
        } catch (Exception e) {
            log.error("处理文件下载请求时发生未知错误，路径: {}", path, e);
            if (!response.isCommitted()) {
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "发生未知内部错误。");
            }
        }
    }


    /**
     * ZIP下载接口
     */
    /**
     * 处理文件下载请求，将指定文件夹打包成ZIP并提供下载
     *
     * @param path Base64编码后的文件夹路径 (从URL路径中获取)
     * @param response    HttpServletResponse 用于设置响应头和输出流
     */
    @GetMapping("/downloadZip")
    public void downloadFolderAsZip(@RequestParam String path, HttpServletResponse response) {
        //若使用base64加密，此时应该解密
        //byte[] decodedBytes = Base64.getUrlDecoder().decode(path);
        //path = new String(decodedBytes, StandardCharsets.UTF_8);
        File folder = new File(path);
        // 再次校验路径（防止链接被篡改或文件夹状态改变）
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("下载请求失败：路径不存在或不是文件夹: {}", path);
            sendErrorResponse(response, HttpStatus.NOT_FOUND, "请求的文件夹不存在或无效。");
            return;
        }

        // 3. 设置响应头
        String zipFileName = folder.getName() + ".zip"; // 使用文件夹名作为ZIP文件名
        try {
            // 对文件名进行URL编码，防止中文乱码
            String encodedFileName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setContentType("application/zip"); // 设置MIME类型为ZIP
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\""); // 提示浏览器下载
            log.info("开始打包并下载文件夹: {}, 输出文件名: {}", path, zipFileName);

            // 4. 创建ZIP输出流，并关联到HTTP响应的输出流
            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                // 5. 调用递归方法将文件夹内容添加到ZIP流中
                addFolderToZip(folder, folder.getName(), zipOut);
                log.info("文件夹 {} 打包完成。", path);
            } // try-with-resources 会自动关闭 zipOut

        } catch (IOException e) {
            log.error("打包或传输文件夹 {} 时发生IO错误", path, e);
            // 如果发生IO异常，可能响应头已经发送部分，此时设置状态码可能无效或引起问题
            // 但可以尝试记录错误。客户端通常会看到下载失败。
            // 注意：如果流已经开始写入，再设置HTTP状态码可能无效。
            if (!response.isCommitted()) {
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "处理文件时发生内部错误。");
            }
        } catch (Exception e) {
            log.error("处理下载请求时发生未知错误，文件夹: {}", path, e);
            if (!response.isCommitted()) {
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "发生未知内部错误。");
            }
        }
    }

    /**
     * 递归地将文件或文件夹添加到ZipOutputStream中
     *
     * @param fileToZip      当前要处理的文件或文件夹
     * @param parentEntryName 在ZIP文件中的父级路径名 (例如 "folderName/" 或 "folderName/subFolderName/")
     * @param zipOut         ZipOutputStream实例
     * @throws IOException IO异常
     */
    private void addFolderToZip(File fileToZip, String parentEntryName, ZipOutputStream zipOut) throws IOException {
        // 确保路径以'/'结尾，这样文件条目会是 "父路径/文件名"
        String entryNamePrefix = parentEntryName.endsWith("/") ? parentEntryName : parentEntryName + "/";

        // 遍历文件夹内容
        File[] children = fileToZip.listFiles();
        if (children == null) {
            log.warn("无法列出文件夹内容或文件夹为空: {}", fileToZip.getAbsolutePath());
            // 如果是根目录为空，可能需要创建一个空的文件夹条目（如果需要）
            if (fileToZip.isDirectory() && fileToZip.getParentFile() == null) { // 假设它是我们请求的根目录
                // ZipEntry dirEntry = new ZipEntry(entryNamePrefix);
                // zipOut.putNextEntry(dirEntry);
                // zipOut.closeEntry();
            }
            return; // 如果无法列出（权限问题？）或文件夹为空，则结束此分支
        }

        byte[] buffer = new byte[4096]; // 缓冲区
        for (File child : children) {
            String childEntryName = entryNamePrefix + child.getName();
            if (child.isDirectory()) {
                // 如果是子文件夹，递归调用
                log.debug("添加文件夹到ZIP: {}", childEntryName);
                // 对于目录，也创建一个条目，以 '/' 结尾
                ZipEntry dirEntry = new ZipEntry(childEntryName + "/");
                zipOut.putNextEntry(dirEntry);
                zipOut.closeEntry();
                // 递归处理子文件夹内容
                addFolderToZip(child, childEntryName, zipOut);
            } else {
                // 如果是文件，将其添加到ZIP中
                log.debug("添加文件到ZIP: {}", childEntryName);
                try (FileInputStream fis = new FileInputStream(child)) {
                    ZipEntry zipEntry = new ZipEntry(childEntryName);
                    zipOut.putNextEntry(zipEntry);

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, length);
                    }
                    zipOut.closeEntry(); // 关闭当前条目
                } catch (FileNotFoundException e) {
                    log.error("文件未找到，跳过: {}", child.getAbsolutePath(), e);
                    // 可以选择继续处理其他文件
                } catch (IOException e) {
                    log.error("读取文件 {} 或写入ZIP时出错", child.getAbsolutePath(), e);
                    throw e; // 向上抛出，让上层处理
                }
            }
        }
    }

    /**
     * 辅助方法：发送错误响应
     * @param response HttpServletResponse
     * @param status HttpStatus
     * @param message 错误消息
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) {
        try {
            response.setStatus(status.value());
            response.setContentType("text/plain; charset=UTF-8"); // 设置内容类型为纯文本
            response.getWriter().write(message);
        } catch (IOException e) {
            log.error("发送错误响应失败: {}", message, e);
        }
    }


    /**
     * 获取本机局域网IP地址
     */
    public static String getLocalIP() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // 过滤掉回环接口和未运行的接口
            if (iface.isLoopback() || !iface.isUp()) continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                // 优先返回IPv4地址，并且是站点本地地址（常见的私有IP段）
                if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {
                    return addr.getHostAddress();
                }
            }
        }
        // 如果没找到合适的私有IPv4，尝试返回一个非回环的IPv4 (可能对外)
        interfaces = NetworkInterface.getNetworkInterfaces(); // 重新获取
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) continue;
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    log.warn("未找到明确的站点本地IPv4地址，返回第一个找到的非回环IPv4: {}", addr.getHostAddress());
                    return addr.getHostAddress();
                }
            }
        }

        // 最后的回退
        log.warn("无法确定本机局域网IP地址，将回退到 127.0.0.1");
        return "127.0.0.1";
    }

    /**
     * 处理中文文件名编码问题
     */
    private static String encodeFilename(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return fileName;
        }
    }
}
