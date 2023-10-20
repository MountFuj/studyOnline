package com.zy.content.service.impl;

import com.zy.content.model.po.MqMessage;
import com.zy.content.mapper.MqMessageMapper;
import com.zy.content.service.MqMessageService;
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
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

}
