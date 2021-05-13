package com.styx.support.service.file;

import com.styx.support.core.file.FileStoreService;
import com.styx.support.model.SysAttachment;
import com.styx.support.service.file.vo.FileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FileResourceContainer implements ApplicationRunner {

    //TODO 在这里可以做缓存处理，从内存或redis获取
    //TODO 可以修改为文件服务器获取，在这里返回的URL修改为文件服务器地址（需要在UploadAttachmentService保存文件中做相应修改）

    @Autowired
    private UploadAttachmentService attachmentService;

    @Autowired
    private FileStoreService fileStoreService;

    private static FileResourceContainer container;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        container = this;
    }

    public static List<FileResource> getFileResources(String... ids) {
        if (ids != null && ids.length > 0) {
            List<SysAttachment> attachments = container.attachmentService.getAttachments(ids);
            if (attachments != null && attachments.size() > 0) {
                List<FileResource> results = new ArrayList<>(attachments.size());
                for (SysAttachment attachment : attachments) {
                    results.add(convert(attachment));
                }
                return results;
            }
        }
        return Collections.emptyList();
    }

    public static FileResource getFileResource(String id) {
        if (id != null && id.length() > 0) {
            SysAttachment attachment = container.attachmentService.get(id);
            if (attachment != null) {
                return convert(attachment);
            }
        }
        return null;
    }

    public static FileResource convert(SysAttachment attachment) {
        FileResource fr = new FileResource();
        fr.setId(attachment.getId());

        String suffix = attachment.getSuffix();
        String name = attachment.getName() + (suffix == null ? "" : suffix);
        fr.setName(name);
        String url = container.fileStoreService.getFileUrl(attachment.getRelativePath());
        fr.setUrl(url);

        String trp = attachment.getThumbnailRelativePath();
        if (trp != null && trp.length() > 0) {
            fr.setThumbnailUrl(container.fileStoreService.getFileUrl(trp));
        } else {
            fr.setThumbnailUrl(url);
        }

        fr.setSize(attachment.getSize());

        return fr;
    }

}
