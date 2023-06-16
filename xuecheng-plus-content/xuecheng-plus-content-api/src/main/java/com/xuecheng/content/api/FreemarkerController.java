package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FreemarkerController {
    @GetMapping("/testfreemaker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //返回页面名称
        modelAndView.setViewName("test");
        //设置模板数据
        modelAndView.addObject("name","hsy");
        return modelAndView;
    }
}
