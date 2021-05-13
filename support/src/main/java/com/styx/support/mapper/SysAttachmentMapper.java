package com.styx.support.mapper;

import com.styx.common.service.mybatis.CommonMapper;
import com.styx.support.model.SysAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAttachmentMapper extends CommonMapper<SysAttachment> {

    int updateDeletedById(@Param("ids") List<String> ids, @Param("isDelete") boolean isDelete, @Param("operateBy") String operateBy);
}