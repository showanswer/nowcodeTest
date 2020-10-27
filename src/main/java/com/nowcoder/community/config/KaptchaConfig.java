package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
//验证码  kaptcha
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer KaptchaProducer(){
        //将验证码的相关设置存在 Properties对象中
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        //随机字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        //字符长度 就是验证码有几个字符
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //采用哪个干扰类  即图片字体上有横线阴影 倒转     com.google.code.kaptcha.impl.NoNoise设置为没有干扰
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        //传入参数  存到config对象中
        Config config=new Config(properties);
        //生成kaptcha对象
        DefaultKaptcha kaptcha=new DefaultKaptcha();
        //将配置对象 传到kaptcha对象中
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
