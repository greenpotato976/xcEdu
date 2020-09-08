package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/8/25 19:49
 */
@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 查询时间之前的数据
     * @param updateTime 时间
     * @param size 每页显示记录条数
     * @return XcTask集合
     */
    public List<XcTask> findXcTaskList (Date updateTime, int size){
        if(size <= 0){
            size = 10;
        }
        //准备分页参数
        Pageable pageable = new PageRequest(0,size);
        //调用dao查询
        Page<XcTask> xcTasksPage = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        //得到数据
        List<XcTask> xcTasks = xcTasksPage.getContent();
        return xcTasks;
    }

    /**
     * 根据交换机和路由key发送消息
     * @param xcTask 发送消息对象
     * @param ex 交换机
     * @param routingKey 路由key
     */
    public void publish(XcTask xcTask,String ex,String routingKey){
        //根据id查询发送对象
        Optional<XcTask> xcTaskOptional = xcTaskRepository.findById(xcTask.getId());
        if(xcTaskOptional.isPresent()){
            XcTask one = xcTaskOptional.get();
            //发送消息
            rabbitTemplate.convertAndSend(ex,routingKey,one);
            //更新任务时间
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }

    }

    /**
     * 修改version的值
     * @param id 主键
     * @param version version
     * @return 影响记录的条数
     */
    @Transactional
    public int getTask(String id,int version){
        //通过乐观锁的方式来更新数据表，如果结果大于0说明取到任务
        int count = xcTaskRepository.updateTaskVersion(id, version);
        return count;
    }

    /**
     * 完成任务把任务表数据删除添加到任务历史表中
     * @param taskId
     */
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optionalXcTask = xcTaskRepository.findById(taskId);
        if(optionalXcTask.isPresent()){
            //当前任务
            XcTask xcTask = optionalXcTask.get();
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
