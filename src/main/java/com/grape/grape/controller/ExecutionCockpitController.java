package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.cockpit.ExecutionCockpitVO;
import com.grape.grape.service.biz.ExecutionCockpitBizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/executionCockpit")
public class ExecutionCockpitController {

    @Resource
    private ExecutionCockpitBizService executionCockpitBizService;

    @GetMapping
    public Resp getExecutionCockpitData() {
        ExecutionCockpitVO data = executionCockpitBizService.getExecutionCockpitData();
        return Resp.ok(data);
    }
}