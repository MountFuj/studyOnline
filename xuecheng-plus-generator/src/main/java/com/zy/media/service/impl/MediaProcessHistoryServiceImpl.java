package com.zy.media.service.impl;

import com.zy.media.model.po.MediaProcessHistory;
import com.zy.media.mapper.MediaProcessHistoryMapper;
import com.zy.media.service.MediaProcessHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyu
 */
@Slf4j
@Service
public class MediaProcessHistoryServiceImpl extends ServiceImpl<MediaProcessHistoryMapper, MediaProcessHistory> implements MediaProcessHistoryService {

}
