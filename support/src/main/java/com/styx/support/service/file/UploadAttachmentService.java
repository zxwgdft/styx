package com.styx.support.service.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.StringUtil;
import com.styx.common.utils.UUIDUtil;
import com.styx.common.utils.convert.DateFormatUtil;
import com.styx.support.core.file.FileStoreService;
import com.styx.support.mapper.SysAttachmentMapper;
import com.styx.support.model.SysAttachment;
import com.styx.support.service.file.dto.CommitOperate;
import com.styx.support.service.file.dto.FileCreateParam;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UploadAttachmentService extends ServiceSupport<SysAttachment> {

    // 单位M
    @Value("${attachment.max-file-size}")
    private int maxFileSize;

    // 附件删除后保留时间，默认10天，
    @Value("${attachment.delete-expire-day}")
    private int expireDay;

    private int maxFileNameSize = 100;
    private int maxFileByteSize;

    @Autowired
    private SysAttachmentMapper attachmentMapper;

    @Autowired
    private FileStoreService fileStoreService;

    @PostConstruct
    protected void initialize() {
        if (maxFileSize <= 0) {
            maxFileSize = 10;
        }
        maxFileByteSize = maxFileSize * 1024 * 1024;
    }


    /**
     * 图片限制参数,如果不想限制可以设置大点
     */
    private double max_picture_size = 5 * 1024 * 1024;
    private int max_thumbnail_width = 600;
    private int max_thumbnail_height = 600;
    private int min_thumbnail_width = 200;
    private int min_thumbnail_height = 200;

    /**
     * 创建图片与缩略图，如果开启限制图片，过大图片会被压缩，压缩策略为根据图片大小与基准大小比例作为缩放大小进行缩放
     *
     * @param param  文件创建参数
     * @param isTemp 是否临时文件
     */
    public SysAttachment createPictureAndThumbnail(FileCreateParam param, boolean isTemp) {
        long size = param.getSize();

        if (size > max_picture_size) {
            double scale = max_picture_size / size;
            scale = Math.sqrt(scale) * 0.8;
            param.setScale(scale);
        }

        Integer thumbnailHeight = param.getThumbnailHeight();
        Integer thumbnailWidth = param.getThumbnailWidth();

        if (thumbnailHeight == null) {
            thumbnailHeight = min_thumbnail_height;
        }

        if (thumbnailWidth == null) {
            thumbnailWidth = min_thumbnail_width;
        }

        param.setThumbnailHeight(Math.min(thumbnailHeight, max_thumbnail_height));
        param.setThumbnailWidth(Math.min(thumbnailWidth, max_thumbnail_width));
        param.setNeedThumbnail(true);
        return createFile(param, isTemp);
    }

    /**
     * 创建文件附件
     */
    public SysAttachment createFile(FileCreateParam param, boolean isTemp) {
        if (param.getSize() > maxFileByteSize) {
            throw new BusinessException("上传文件不能大于" + maxFileSize + "MB");
        }
        Date now = new Date();
        SysAttachment attachment = new SysAttachment();
        attachment.setId(UUIDUtil.createUUID());
        attachment.setSize(param.getSize());
        attachment.setCreateTime(now);
        attachment.setDeleted(true);
        attachment.setOperateTime(now);

        String filename = param.getFilename();
        if (filename != null && filename.length() > 0) {
            int i = filename.lastIndexOf(".");
            if (i >= 0) {
                attachment.setSuffix(filename.substring(i));
                attachment.setName(filename.substring(0, i));
            } else {
                attachment.setName(filename);
            }
        } else {
            throw new BusinessException("文件名不能为空");
        }

        filename = attachment.getName();
        if (filename.length() > maxFileNameSize) {
            attachment.setName(filename.substring(0, maxFileNameSize));
        }

        try {
            saveFile(param, attachment, null);
        } catch (IOException e) {
            throw new BusinessException("保存图片文件失败", e);
        }

        attachment.setStoreType(fileStoreService.getStoreType());

        //String creator = FileSecurityManager.getCurrentUserSession().getUserId();
        //attachment.setCreateBy(creator);

        save(attachment);
        return attachment;
    }

    /**
     * 保存文件附件
     */
    private SysAttachment saveFile(FileCreateParam param, SysAttachment attachment, String subPath) throws IOException {
        String filename = attachment.getId();
        String suffix = attachment.getSuffix();
        if (suffix != null) {
            filename += suffix;
        }

        if (subPath == null || subPath.length() == 0) {
            subPath = DateFormatUtil.getThreadSafeFormat("yyyyMMdd").format(new Date());
        }

        fileStoreService.checkAndMakeDirectory(subPath);

        String relativePath = subPath + "/" + filename;
        attachment.setRelativePath(relativePath);

        boolean created = false;
        InputStream input = param.getInput();

        // 如果是图片类型，需要查看图片相关处理参数
        if (param.isNeedThumbnail()) {
            Integer width = param.getWidth();
            Integer height = param.getHeight();
            Double scale = param.getScale();
            Double quality = param.getQuality();
            Integer thumbnailWidth = param.getThumbnailWidth();
            Integer thumbnailHeight = param.getThumbnailHeight();

            // 根据以下两个字段创建缩略图，并且缩略图是建立在原图基础上的，而不是改变过质量的原图
            // 如需要修改缩略图质量和规模，可加入参数
            if (thumbnailWidth != null && thumbnailHeight != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = input.read(buffer)) > -1) {
                    output.write(buffer, 0, len);
                }

                // 创建缩略图
                input = new ByteArrayInputStream(output.toByteArray());


                output = new ByteArrayOutputStream();
                Thumbnails.of(input).size(thumbnailWidth, thumbnailHeight).toOutputStream(output);

                input = new ByteArrayInputStream(output.toByteArray());

                String thumbnailName = "t_" + filename;
                String thumbnailRelativePath = subPath + "/" + thumbnailName;

                fileStoreService.storeFile(input, subPath, thumbnailName);

                attachment.setThumbnailRelativePath(thumbnailRelativePath);
                input.reset();
            }

            // 根据以下字段修改原图
            if ((width != null && height != null) || scale != null || quality != null) {
                // 有修改图片则使用缩放图片工具
                boolean changed = false;
                Builder<? extends InputStream> builder = Thumbnails.of(input);
                if (width != null && height != null) {
                    builder.size(width, height);
                    changed = true;
                }

                if (scale != null) {
                    builder.scale(scale, scale);
                    changed = true;
                }

                if (quality != null) {
                    if (!changed) {
                        // 如果没有设置size或缩放但是要压缩质量，则缩放原大小
                        builder.scale(1f);
                    }
                    builder.outputQuality(quality);
                }


                ByteArrayOutputStream output = new ByteArrayOutputStream();
                builder.toOutputStream(output);

                input = new ByteArrayInputStream(output.toByteArray());
                fileStoreService.storeFile(input, subPath, filename);
                created = true;
            }
        }

        if (!created) {
            fileStoreService.storeFile(input, subPath, filename);
        }

        return attachment;
    }


    /**
     * 获取文件附件记录
     */
    public List<SysAttachment> getAttachments(String... ids) {
        return (ids == null || ids.length == 0) ? Collections.emptyList() :
                findList(new LambdaQueryWrapper<SysAttachment>().in(SysAttachment::getId, ids));
    }


    /**
     * 操作文件，删除和保存附件
     */
    public void operateAttachments(CommitOperate operate) {
        String operateBy = operate.getOperateBy();
        if (StringUtil.isEmpty(operateBy)) {
            throw new BusinessException("文件操作人不能为空");
        }
        List<String> deleteIds = operate.getDeleteIds();
        operateAttachments(deleteIds, true, operateBy);

        List<String> saveIds = operate.getSaveIds();
        operateAttachments(saveIds, false, operateBy);
    }


    /**
     * 操作附件，删除或保存
     */
    private int operateAttachments(List<String> ids, boolean isDelete, String operateBy) {
        if (ids != null && ids.size() > 0) {
            return attachmentMapper.updateDeletedById(ids, isDelete, operateBy);
        }
        return 0;
    }


    /**
     * 清理删除的附件文件
     */
    public int cleanAttachmentFile() {
        int count = 0;

        List<SysAttachment> list = findList(new LambdaQueryWrapper<SysAttachment>()
                .eq(SysAttachment::getDeleted, true)
                .lt(SysAttachment::getOperateTime, new Date(System.currentTimeMillis() - (expireDay * 60L * 60 * 24 * 1000))));

        for (SysAttachment att : list) {
            String id = att.getId();
            if (deleteFile(id, att.getRelativePath()) &&
                    deleteFile(id, att.getThumbnailRelativePath())) {
                // 调取mapper删除方法，防止逻辑删除
                getCommonMapper().deleteById(id);
                count++;
            }
        }

        return count;
    }

    private boolean deleteFile(String id, String filePath) {
        try {
            if (StringUtil.isNotEmpty(filePath)) {
                int i = filePath.lastIndexOf('/');
                if (i >= 0) {
                    fileStoreService.deleteFile(filePath.substring(0, i), filePath.substring(i + 1));
                } else {
                    fileStoreService.deleteFile("", filePath);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("删除附件失败[id:" + id + ",path:" + filePath + "]");
        }
        return false;
    }

}