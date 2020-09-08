package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/22 15:30
 */
@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;
    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    //从配置文件获取课程发布的基本配置
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;
    /**
     * 查询课程计划
     * @param courseId 课程id
     * @return 课程计划对象
     */
    public TeachplanNode selectList(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    /**
     * 课程计划添加
     * 事务的控制
     * @param teachplan 课程计划对象
     * @return 响应结果
     */
    @Transactional
    public ResponseResult teachPlanAdd(Teachplan teachplan) {
        if(teachplan == null || StringUtils.isEmpty(teachplan.getPname()) || StringUtils.isEmpty(teachplan.getCourseid())){
            //参数不合法
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获得课程id
        String courseId = teachplan.getCourseid();
        //获得父节点id和父节点课程计划对象
        String parentId = teachplan.getParentid();
        //如果父节点id为空
        if(StringUtils.isEmpty(parentId)){
            parentId = getTeachplanRoot(courseId);
        }
        Teachplan parentTeachplan = this.getTeachplanById(parentId);
        //获得父节点级别
        String parentGrade = parentTeachplan.getGrade();
        //创建新的课程对象用于存储
        Teachplan newTeachplan = new Teachplan();
        //对要存储的课程对象进行设置
        BeanUtils.copyProperties(teachplan,newTeachplan);
        //设置父节点id
        newTeachplan.setParentid(parentId);
        //未发布
        newTeachplan.setStatus("0");
        newTeachplan.setGrade("1".equals(parentGrade) ? "2":"3");
        teachplanRepository.save(newTeachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            //对象不存在
            return null;
        }
        CourseBase courseBase = optional.get();
        //调用dao查询teachplan表得到该课程的根结点（一级结点）
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size()<=0){
            //新添加一个课程的根结点
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setParentid("0");
            //一级结点
            teachplan.setGrade("1");
            teachplan.setStatus("0");
            teachplan.setPname(courseBase.getName());
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        return teachplanList.get(0).getId();
    }


    private Teachplan getTeachplanById(String teachplanId){
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        return optional.orElse(null);
    }

    /**
     * 根据公司id进行分页查询课程详情
     * @param page 页码
     * @param size 每页显示个数
     * @param courseListRequest 查询条件
     * @return 查询结果
     */
    public QueryResponseResult<CourseInfo> findCourseList(String companyId,int page, int size, CourseListRequest courseListRequest){
        //对参数进行处理,默认第一页每页显示10条记录
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            size = 10;
        }
        PageHelper.startPage(page,size);
        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        courseListRequest.setCompanyId(companyId);
        //调用dao查询
        Page<CourseInfo> courseInfoPage = courseBaseMapper.findList(courseListRequest);
        //构造返回对象
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
        courseInfoQueryResult.setList(courseInfoPage.getResult());
        courseInfoQueryResult.setTotal(courseInfoPage.getTotal());
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseInfoQueryResult);
    }

    /**
     * 添加课程
     * @param courseBase 课程对象
     * @return 相应结果对象
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase){
        if(courseBase == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    /**
     * 根据id课程查询
     * @param courseId 课程id
     * @return 课程对象
     */
    public CourseBase courseView(String courseId){
        if(StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        return optional.get();
    }

    /**
     * 修改课程信息
     * @param courseId 课程id
     * @param courseBase 修改课程对象信息
     * @return 包含courseId的响应对象
     */
    @Transactional
    public AddCourseResult update(String courseId,CourseBase courseBase){
        if(StringUtils.isEmpty(courseId) || courseBase == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        } else {
            //课程id正确
            CourseBase updateCourseBase = optional.get();
            BeanUtils.copyProperties(courseBase,updateCourseBase);
            courseBaseRepository.save(updateCourseBase);
            return new AddCourseResult(CommonCode.SUCCESS,courseId);
        }
        return new AddCourseResult(CommonCode.SERVER_ERROR,null);
    }

    /**
     * 根据id查询课程营销
     * @param id 主键id
     * @return 响应结果对象
     */
    public CourseMarket getCourseMarketById(String id){
        Optional<CourseMarket> optional = courseMarketRepository.findById(id);
        if(!optional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_MARKET_ISNULL);
        }
        return optional.get();
    }

    /**
     * 修改课程营销
     * @param courseId 课程id
     * @param courseMarket 课程营销
     * @return 响应结果
     */
    @Transactional
    public ResponseResult updateCourseMarket(String courseId,CourseMarket courseMarket){
        //对参数进行判断
        if(StringUtils.isEmpty(courseId) || courseMarket == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if(!optional.isPresent()){
            //未添加营销信息
            Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);
            if(!optionalCourseBase.isPresent()){
                //课程不存在
                ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
            } else{
                //课程存在但是营销信息不存在添加营销信息
                courseMarket.setId(courseId);
                courseMarketRepository.save(courseMarket);
                return new ResponseResult(CommonCode.SUCCESS);
            }
        } {
            //已添加营销信息,执行修改
            //查询营销信息
            CourseMarket updateCourseMarket = optional.get();
            //拷贝属性
            BeanUtils.copyProperties(courseMarket,updateCourseMarket);
            courseMarketRepository.save(updateCourseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }
    }

    /**
     * 添加课程图片
     * @param courseId 课程id
     * @param pic 图片id
     * @return 响应结果
     */
    @Transactional
    public ResponseResult addCoursePic(String courseId,String pic){
        //对参数进行判断
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if(!courseBaseOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        CoursePic coursePic = null;
        //对courseId进行查询
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
        if(coursePicOptional.isPresent()){
            coursePic = coursePicOptional.get();
        }
        if(coursePic == null){
            //如果数据库中没有就创建对象
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //持久化保存
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据courseId查询图片
     * @param courseId 课程id
     * @return 课程图片
     */
    public CoursePic findCoursePicList(String courseId) {
        //对参数进行判断
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if(!courseBaseOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
        return coursePicOptional.orElse(null);
    }

    /**
     * 删除课程图片
     * @param courseId 课程id
     * @return 响应结果
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //对参数进行校验
        if(courseId == null || StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if(!courseBaseOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        //执行删除，返回1表示删除成功，返回0表示删除失败
        Long flag = coursePicRepository.deleteByCourseid(courseId);
        if(flag > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 获得课程详情中的数据
     * @param courseId 课程id
     * @return 课程视图
     */
    public CourseView getCourseview(String courseId) {
        //对参数进行校验
        if(StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //组装courseView对象
        CourseView courseview = new CourseView();
        //组装课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if(courseBaseOptional.isPresent()){
            courseview.setCourseBase(courseBaseOptional.get());
        } else {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        //组装课程营销信息
        CourseMarket courseMarket = getCourseMarketById(courseId);
        courseview.setCourseMarket(courseMarket);
        //组装课程图片
        CoursePic coursePicList = findCoursePicList(courseId);
        courseview.setCoursePic(coursePicList);
        //组装课程营销计划
        TeachplanNode teachplanNode = selectList(courseId);
        courseview.setTeachplanNode(teachplanNode);
        return courseview;
    }

    /**
     * 查询课程详情
     * @param courseId 课程id
     * @return 课程信息
     */
    public CourseBase findCourseBaseById(String courseId){
        //获取课程信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(courseId);
        if(!optionalCourseBase.isPresent()){
            //课程不存在抛出异常
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
            return null;
        }
        return optionalCourseBase.get();
    }
    /**
     * 课程预览
     * @param courseId 课程id
     * @return 课程预览结果
     */
    public CoursePublishResult preview(String courseId) {
        //参数校验
        if(StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //对课程营销进行校验
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        if(!courseMarketOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_MARKET_ISNULL);
        }
        //获取课程信息
        CourseBase courseBaseById = this.findCourseBaseById(courseId);

        //拼装页面基本信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageName(courseBaseById.getId() + ".html");
        //页面别名
        cmsPage.setPageAliase(courseBaseById.getName());

        //远程调用，保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //页面id
        String cmsPageId = cmsPageResult.getCmsPage().getPageId();
        //返回预览url
        String url = previewUrl + cmsPageId;
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    /**
     * 课程发布
     * @param courseId 课程id
     * @return 课程发布结果
     */
    @Transactional
    public CoursePublishResult coursePublish(String courseId) {
        //参数校验
        if(StringUtils.isEmpty(courseId)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获取课程信息
        CourseBase courseBaseById = this.findCourseBaseById(courseId);

        //拼装页面基本信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageCreateTime(new Date());
        //页面别名
        cmsPage.setPageAliase(courseBaseById.getName());
        //页面名字
        cmsPage.setPageName(courseBaseById.getId() + ".html");
        //调用一键发布接口
        CmsPostPageResult cmsPostPageResult = cmsPageClient.quickPost(cmsPage);
        if(!cmsPostPageResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //修改发布信息
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        //课程索引...
        //准备coursePub对象
        CoursePub coursePub = this.createCoursePub(courseId);
        //储存coursePub对象
        this.saveCoursePub(courseId,coursePub);
        //课程缓存...

        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanMediaPub中保存课程媒资信息
        this.saveTeachplanMediaPub(courseId);
        //return new CoursePublishResult(CommonCode.SUCCESS,"test");
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    /**
     * 将课程计划和媒资关联对象保存
     * 编写保存课程计划媒资信息方法，并在课程发布时调用此方法。
     * 本方法采用先删除该课程的媒资信息，再添加该课程的媒资信息
     * @param courseId 课程id
     */
    private void saveTeachplanMediaPub(String courseId){
        //先删除teachplanMediaPub中的数据
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        //从teachplanMedia中查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        //创建teachplanMediaPub集合
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        //将teachplanMedia中的对象添加时间戳添加到集合中
        for(TeachplanMedia teachplanMedia:teachplanMediaList){
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            //将属性拷贝
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            //将元素添加到集合中
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        //将teachplanMediaPub集合保存
        teachplanMediaPubRepository.saveAll(teachplanMediaPubList);
    }

    /**
     * 将其他表中的数组组装到coursePub中
     * @param courseId 课程id
     * @return coursePub
     */
    private CoursePub createCoursePub(String courseId){
        //准备coursePub
        CoursePub coursePub = new CoursePub();
        //组装courseBase
        CourseBase courseBase = findCourseBaseById(courseId);
        BeanUtils.copyProperties(courseBase,coursePub);
        //组装courseMarket
        CourseMarket courseMarket = findCourseMarket(courseId);
        BeanUtils.copyProperties(courseMarket,coursePub);
        //组装coursePic
        CoursePic coursePic = findCoursePicList(courseId);
        BeanUtils.copyProperties(coursePic,coursePub);
        //组装课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        String teachPlan = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachPlan);
        return coursePub;
    }

    /**
     * 根据课程id 查询营销信息
     * @param courseId 课程id
     * @return 课程营销对象
     */
    private CourseMarket findCourseMarket(String courseId){
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        if(!courseMarketOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEISNULL);
        }
        return courseMarketOptional.get();
    }

    /**
     * 根据coursePub储存在数据库中
     * 并且添加未添加的字段
     * @param courseId 课程id
     * @param coursePub 要储存的对象
     * @return coursePub
     */
    private CoursePub saveCoursePub(String courseId,CoursePub coursePub){
        CoursePub coursePubNew = null;
        //查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(courseId);
        //判断
        coursePubNew = coursePubOptional.orElseGet(CoursePub::new);
        //设置属性
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(courseId);
        //时间戳,给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    /**
     * 保存课程计划和媒资关联对象
     * @param teachplanMedia 程计划和媒资关联对象
     * @return 相应结果
     */
    public ResponseResult saveTeachPlanMedia(TeachplanMedia teachplanMedia) {
        //参数校验
        if(teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            //参数错误
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是3级
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(teachplanId);
        if(!teachplanOptional.isPresent()){
            //参数错误
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan = teachplanOptional.get();
        String grade = teachplan.getGrade();
        if(!"3".equals(grade) || StringUtils.isEmpty(grade)){
            //只允许选择第三级的课程计划关联视频
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询teachplanMedia
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if(teachplanMediaOptional.isPresent()){
            one = teachplanMediaOptional.get();
        } else {
            one = new TeachplanMedia();
        }
        //向数据库储存
        one.setTeachplanId(teachplanId);
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setMediaId(teachplanMedia.getMediaId());
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
