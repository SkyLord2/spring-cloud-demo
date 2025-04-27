package com.cloud.wang.student.biz.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.wang.student.api.entity.TbStudent;
import com.cloud.wang.student.biz.student.mapper.TbStudentMapper;
import com.cloud.wang.student.biz.student.service.TbStudentService;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.jdbc.object.BatchSqlUpdate.DEFAULT_BATCH_SIZE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wang
 * @since 2022-05-18
 */
@Service
public class TbStudentServiceImpl extends ServiceImpl<TbStudentMapper, TbStudent> implements TbStudentService {

    @Override
    public boolean updateBatchByQueryWrapper(Collection<TbStudent> entityList, Function<TbStudent, QueryWrapper> queryWrapperFunction) {
        String sqlStatement = this.sqlStatement(SqlMethod.UPDATE);
        return this.executeBatch(entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            MapperMethod.ParamMap param = new MapperMethod.ParamMap();
            param.put(Constants.ENTITY, entity);
            param.put(Constants.WRAPPER, queryWrapperFunction.apply(entity));
            sqlSession.update(sqlStatement, param);
        });
    }
}
