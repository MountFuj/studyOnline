package com.zy.content;

import com.zy.content.mapper.TeachplanMapper;
import com.zy.content.mapper.TeachplanMediaMapper;
import com.zy.content.model.dto.TeachplanDto;
import com.zy.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/17 9:53
 */
@SpringBootTest
public class TeachPlanServiceTest {
    @Autowired
    private TeachplanService teachplanService;

    @Test
    void testTeachplanService() {
        teachplanService.deleteTeachplan(43l);
    }
}
