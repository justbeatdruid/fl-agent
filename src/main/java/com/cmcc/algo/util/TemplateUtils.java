package com.cmcc.algo.util;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;

@Component
@Slf4j
public class TemplateUtils {

    public static String useTemplate(Map dataMap, String templateName){
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig(Charset.defaultCharset(),"", TemplateConfig.ResourceMode.CLASSPATH));
        Template temp = engine.getTemplate(templateName);
        return temp.render(dataMap);
    }
}
