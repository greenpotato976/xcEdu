package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/16 13:24
 */
@Component
public class MediaProcessTask {
    @Autowired
    MediaFileRepository mediaFileRepository;


    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkeyMediaVideo;
    @Value("${xc-service-manage-media.mq.video-location}")
    String videoLocation;
    @Value("${xc-service-manage-media.mq.ffmpeg-path}")
    String ffmpegPath;


    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String message){
        //1、获取信息
        Map mediaIdMap = JSON.parseObject(message, Map.class);
        String mediaId = (String) mediaIdMap.get("mediaId");
        //参数校验
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(mediaId);
        if(!mediaFileOptional.isPresent()){
            return ;
        }
        //2、判断媒体文件是否需要处理（本视频处理程序目前只接收avi 视频的处理）当前只有 avi 文件需要处理，其它文件需要更新处理状态为 “无需处理”。
        MediaFile mediaFile = mediaFileOptional.get();
        String mediaFileType = mediaFile.getFileType();
        if(!mediaFileType.equals("avi")){
            mediaFile.setProcessStatus("303004");
            //保存
            mediaFileRepository.save(mediaFile);
            return ;
        } else {
            mediaFile.setProcessStatus("303001");
            //保存
            mediaFileRepository.save(mediaFile);
        }
        //3、使用工具类将avi文件生成mp4
        String video_path = videoLocation  + mediaFile.getFilePath() + mediaFile.getFileId() + ".avi";
        String mp4_name = mediaFile.getFileId() + ".mp4";
        String mp4_path = videoLocation  + mediaFile.getFilePath();
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath,video_path,mp4_name,mp4_path);
        String result = videoUtil.generateMp4();
        if(result == null || !"success".equals(result)){
            //4、处理失败需要在数据库记录处理日志，及处理状态为 “处理失败”
            mediaFile.setProcessStatus("303003");
            //记录失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //保存
            mediaFileRepository.save(mediaFile);
        }
        //4、将mp4生成m3u8和ts文件
        String mp4_video_path = videoLocation + mediaFile.getFilePath() + mp4_name;
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        String m3u8_path = videoLocation + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpegPath,mp4_video_path,m3u8_name,m3u8_path);
        //生成m3u8和ts文件
        String tsResult = hlsVideoUtil.generateM3u8();
        if(tsResult == null || !"success".equals(tsResult)){
            //处理失败
            mediaFile.setProcessStatus("303003");
            //记录失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(tsResult);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //保存
            mediaFileRepository.save(mediaFile);
        }
        //处理成功
        //获取ts文件列表
        mediaFile.setProcessStatus("303002");
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //保存fileUrl（此url就是视频播放的相对路径）
        mediaFile.setFileUrl(mediaFile.getFilePath() + "hls/" + m3u8_name);
        mediaFileRepository.save(mediaFile);
    }


}
