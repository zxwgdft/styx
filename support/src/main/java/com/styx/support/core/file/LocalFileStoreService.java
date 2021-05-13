package com.styx.support.core.file;

import com.styx.common.exception.BusinessException;
import com.styx.common.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储服务
 * <p>
 * 该实现适合单机部署项目，对于分布式多应用部署不太适合
 *
 * @author TontoZhou
 * @since 2020/6/28
 */
public class LocalFileStoreService implements FileStoreService {

    private String filePath;

    public LocalFileStoreService(String filePath) {
        this.filePath = filePath;
        initialize();
    }

    protected void initialize() {
        if (filePath.startsWith("file:")) {
            filePath = filePath.substring(5);
        }

        filePath = filePath.replaceAll("\\\\", "/");

        if (!filePath.endsWith("/")) {
            filePath += "/";
        }

        // 创建目录
        Path root = Paths.get(filePath);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("创建附件存放目录异常", e);
        }
    }

    @Override
    public void storeFile(InputStream input, String filePath, String fileName) {
        String path = this.filePath + filePath + "/" + fileName;
        try (OutputStream out = Files.newOutputStream(Paths.get(path))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = input.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new BusinessException("存储文件[" + path + "]失败", e);
        }
    }

    @Override
    public void deleteFile(String subPath, String fileName) {
        String path = StringUtil.isEmpty(subPath) ? (filePath + fileName) : (filePath + subPath + "/" + fileName);
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            throw new BusinessException("删除文件[" + path + "]失败", e);
        }
    }

    @Override
    public void checkAndMakeDirectory(String subPath) {
        Path path = Paths.get(filePath, subPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (FileAlreadyExistsException e1) {
                // 继续
            } catch (IOException e) {
                throw new BusinessException("创建文件目录[" + filePath + subPath + "]失败", e);
            }
        }
    }

    @Override
    public String getStoreType() {
        return "local";
    }

    @Override
    public String getFileUrl(String relativePath) {
        return "/file/" + relativePath;
    }
}
