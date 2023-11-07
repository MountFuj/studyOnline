package com.zy.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zy.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangyu
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    @Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardindex} and t.status='1' limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("count") int count);

    @Update("update media_process m set m.status='4' where (m.status='1' or m.status='3') and m.fail_count<3 and m.id=#{id}")
    int startTask(@Param("id") long id);
}
