package com.cloud.wang.student.biz.student.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.wang.student.api.entity.TbStudent;

import java.util.Collection;
import java.util.function.Function;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wang
 * @since 2022-05-18
 */
public interface TbStudentService extends IService<TbStudent> {

    boolean updateBatchByQueryWrapper(Collection<TbStudent> entityList, Function<TbStudent, QueryWrapper> queryWrapperFunction);

}
