package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/14 18:35
 */
@Service
public class MediaUploadService {
    @Autowired
    MediaFileRepository mediaFileRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.upload-location}")
    String uploadLocation;
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkey_media_video;

    /**
     * 检查上传文件是否存在
     * 创建文件目录
     *
     * 检查文件信息是否已经存在本地以及mongodb内,其中一者不存在则重新注册
     * @param fileMd5 文件唯一表示
     * @param fileName 文件名字
     * @param fileSize 文件大小
     * @param mimeType 文件类型
     * @param fileExt 文件扩展名
     * @return 相应结果
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        //检查上传文件是否存在
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        boolean fileExists = file.exists();
        //检查上传文件是否在mongodb中
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(fileMd5);
        if(fileExists && mediaFileOptional.isPresent()){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //文件不存在
        //检查文件根目录是否存在
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        File fileFolderFile = new File(fileFolderPath);
        if(!fileFolderFile.exists()){
            //不存在创建
            fileFolderFile.mkdirs();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 规则：
     *  一级目录：md5的第一个字符
     *  二级目录：md5的第二个字符
     *  三级目录：md5
     * 根据文件md5得到文件的所属目录
     * G:/Javaresource/xuecheng/develop/video/a/b/abcd123456789/
     */
    private String getFileFolderPath(String fileMd5){
        return uploadLocation + fileMd5.substring(0,1)+ "/" + fileMd5.substring(1,2) +"/"+ fileMd5 + "/";
    }

    /**
     * 获得文件路径
     * G:/Javaresource/xuecheng/develop/video/a/b/abcd123456789/abcd123456789.avi
     */
    private String getFilePath(String fileMd5,String fileExt){
        return this.getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
    }

    /**
     *获得分块文件的根目录
     * G:/Javaresource/xuecheng/develop/video/a/b/abcd123456789/chunk/
     */
    private String getChunkFileFolderPath(String fileMd5){
        return this.getFileFolderPath(fileMd5) + "/chunk/";
    }

    /**
     *检查分块是否存在
     */
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        boolean fileExist = false;
        //获得分块的根目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //分块文件
        File chunkFile = new File(chunkFileFolderPath + chunk);
        //判断分块文件是否存在
        if(chunkFile.exists()){
            //存在
            fileExist = true;
        }
        return new CheckChunkResult(CommonCode.SUCCESS,fileExist);
    }

    /**
     * 上传分块文件
     */
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5) {
        //检查分块目录，如果不存在则要自动创建
        //获得分块根目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolderFile = new File(chunkFileFolderPath);
        if(!chunkFileFolderFile.exists()){
            chunkFileFolderFile.mkdirs();
        }
        //获得分块文件对象
        File chunkFile = new File(chunkFileFolderPath + chunk);
        OutputStream fileOutputStream = null;
        InputStream fileInputStream = null;
        try {
            //获得分块文件输出流
            fileOutputStream = new FileOutputStream(chunkFile);
            //获得上传分块的输入流
            fileInputStream = file.getInputStream();
            //进行流拷贝
            IOUtils.copy(fileInputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 合并分块
     */
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        //获取所有分块对象
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolderFile = new File(chunkFileFolderPath);
        File[] chunkFiles = chunkFileFolderFile.listFiles();
        //获得文件集合
        List<File> chunkFileList = Arrays.asList(chunkFiles);
        //创建合并文件
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        //执行合并
        mergeFile = this.mergeFile(chunkFileList,mergeFile);
        //判断是否成功
        if(mergeFile == null){
            //合并失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //对合并文件和前端传入的文件进行比较
        boolean checkFileMd5 = this.checkFileMd5(mergeFile,fileMd5);
        if(!checkFileMd5){
            //校验出错
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //向mongodb存数据
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileName);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFilePath(fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/");
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimeType);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理消息
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 将分块文件合并
     */
    private File mergeFile(List<File> chunkFileList,File mergeFile){
        try {
            //如果合并文件已存在则删除，否则创建新文件
            if(mergeFile.exists()){
                //存在删除文件
                mergeFile.delete();
            } else {
                //不存在创建新文件
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(chunkFileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if(Integer.parseInt(o1.getName())<Integer.parseInt(o2.getName())){
                        return -1;
                    }
                    return 1;
                }
            });
            //创建一个写对象
            RandomAccessFile rafWrite = new RandomAccessFile(mergeFile, "rw");
            //创建缓冲区
            byte[] bytes = new byte[1024];
            //遍历分块文件
            for(File chunkFile:chunkFileList){
                RandomAccessFile rafRead = new RandomAccessFile(chunkFile, "r");
                //进行读分块文件
                int len = -1;
                while((len = rafRead.read(bytes)) != -1){
                    //进行写
                    rafWrite.write(bytes,0,len);
                }
                rafRead.close();
            }
            rafWrite.close();
            return mergeFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验文件的md5值是否和前端传入的md5一到
     */
    private boolean checkFileMd5(File mergeFile,String fileMd5){
        try {
            //获得合并文件输入流
            FileInputStream fileInputStream = new FileInputStream(mergeFile);
            //获得
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            //进行比较
            if(fileMd5.equalsIgnoreCase(md5Hex)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 向mq发送消息执行生成m3u8文件
     * @param mediaId 视频文件id
     * @return 相应结果
     */
    public ResponseResult sendProcessVideoMsg(String mediaId){
        //参数校验
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(mediaId);
        if(!mediaFileOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //构造消息
        Map<String,String> messageMap = new HashMap<>();
        messageMap.put("mediaId",mediaId);
        String message = JSON.toJSONString(messageMap);
        //发送消息
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,message);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
