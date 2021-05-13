package com.styx.support.web;

import com.styx.common.api.R;
import com.styx.support.model.SysAttachment;
import com.styx.support.service.file.FileResourceContainer;
import com.styx.support.service.file.UploadAttachmentService;
import com.styx.support.service.file.dto.CommitOperate;
import com.styx.support.service.file.dto.FileCreateParam;
import com.styx.support.service.file.dto.UploadFileBase64;
import com.styx.support.service.file.dto.UploadFileForm;
import com.styx.support.service.file.vo.FileResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/9/2
 */
@Api(tags = "文件操作")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private UploadAttachmentService attachmentService;

    @ApiOperation(value = "获取文件")
    @GetMapping("/get")
    public List<FileResource> getAttachments(@RequestParam @ApiParam("文件资源ID，可以是多个ID拼接") String id) {
        return FileResourceContainer.getFileResources(id.split(","));
    }

    @ApiOperation(value = "操作文件")
    @PostMapping("/operate")
    public R commitOperate(@RequestBody CommitOperate commitOperate) {
        attachmentService.operateAttachments(commitOperate);
        return R.success();
    }

    @ApiOperation(value = "上传文件（base64）")
    @PostMapping("/upload/base64")
    public List<FileResource> uploadAttachmentBase64(@RequestBody List<UploadFileBase64> uploadFiles) {
        List<FileResource> results = new ArrayList<>();
        for (UploadFileBase64 file : uploadFiles) {
            if (file != null) {
                FileCreateParam param = new FileCreateParam(file.getBase64str(), file.getFilename());
                SysAttachment attachment = null;
                if (file.isNeedThumbnail()) {
                    param.setThumbnailWidth(file.getThumbnailWidth());
                    param.setThumbnailHeight(file.getThumbnailHeight());
                    attachment = attachmentService.createPictureAndThumbnail(param, true);
                } else {
                    attachment = attachmentService.createFile(param, true);
                }

                if (attachment != null) {
                    FileResource fileResource = FileResourceContainer.convert(attachment);
                    results.add(fileResource);
                }
            }
        }
        return results;
    }


    @ApiOperation(value = "上传文件（form）")
    @PostMapping("/upload/form")
    public List<FileResource> uploadAttachmentForm(UploadFileForm uploadFileForm) {
        List<FileResource> results = new ArrayList<>();
        MultipartFile[] uploadFiles = uploadFileForm.getFiles();
        if (uploadFiles != null) {
            for (MultipartFile file : uploadFiles) {
                FileCreateParam param = new FileCreateParam(file);
                if (uploadFileForm.getWxFileName() != null){
                    param.setFilename(uploadFileForm.getWxFileName());
                }
                SysAttachment attachment = null;
                if (uploadFileForm.isNeedThumbnail()) {
                    param.setThumbnailWidth(uploadFileForm.getThumbnailWidth());
                    param.setThumbnailHeight(uploadFileForm.getThumbnailHeight());
                    attachment = attachmentService.createPictureAndThumbnail(param, true);
                } else {
                    attachment = attachmentService.createFile(param, true);
                }

                if (attachment != null) {
                    FileResource fileResource = FileResourceContainer.convert(attachment);
                    results.add(fileResource);
                }
            }
        }

        return results;
    }

}
