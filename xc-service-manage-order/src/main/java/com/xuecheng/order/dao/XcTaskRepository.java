package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
/**
 * @author 1159588554@qq.com
 * @date 2020/8/25 19:43
 */
public interface XcTaskRepository extends JpaRepository<XcTask,String> {
    /**
     * 查询时间之前的数据
     * @param pageable 分页
     * @param updateTime 时间
     * @return 任务对象
     */
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    /**
     * 根据id和version修改version
     * @param id 主键
     * @param version 版本
     * @return 影响记录条数
     */
    @Modifying
    @Query("update XcTask t set t.version = :version+1 where t.id = :id and t.version = :version")
    int updateTaskVersion(@Param("id") String id,@Param("version") int version);
}
