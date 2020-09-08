package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/17 19:39
 */
@Service
public class MediaFileService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        //对条件查询参数进行校验
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //构造条件查询对象
        MediaFile mediaFileCondition = new MediaFile();
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFileCondition.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFileCondition.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFileCondition.setTag(queryMediaFileRequest.getTag());
        }
        //查询条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains()) //模糊匹配
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains()) //模糊匹配文件原始名称
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());//精确匹配
        Example<MediaFile> example = Example.of(mediaFileCondition, exampleMatcher);
        //对分页参数进行校验
        if(page <= 0){
            page = 1;
        }
        page = page - 1;
        if(size <= 0){
            size = 10;
        }
        PageRequest pageRequest = new PageRequest(page, size);
        //执行查询
        Page<MediaFile> mediaFiles = mediaFileRepository.findAll(example, pageRequest);
        QueryResult<MediaFile> mediaFileQueryResult = new QueryResult<>();
        mediaFileQueryResult.setTotal(mediaFiles.getTotalElements());
        mediaFileQueryResult.setList(mediaFiles.getContent());
        return new QueryResponseResult<MediaFile>(CommonCode.SUCCESS,mediaFileQueryResult);
    }
}
