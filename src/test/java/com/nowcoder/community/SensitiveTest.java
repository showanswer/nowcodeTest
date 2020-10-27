package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void  testSensitiveFilter(){
        String text="这里可以赌博，可以聊天，可以吸毒，可以嫖娼，很淫荡，可以开票";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        String texts="这里可以*赌*博$，可以*聊*天$，可以吸毒，可以嫖*娼$，很淫*荡，还可以开票";
        texts = sensitiveFilter.filter(texts);
        System.out.println(texts);
    }

}
