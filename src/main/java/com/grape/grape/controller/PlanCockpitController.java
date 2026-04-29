package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.cockpit.PlanCockpitVO;
import com.grape.grape.service.biz.PlanCockpitBizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/planCockpit")
public class PlanCockpitController {

    @Resource
    private PlanCockpitBizService planCockpitBizService;

    @GetMapping
    public Resp getPlanCockpitData() {
        PlanCockpitVO data = planCockpitBizService.getPlanCockpitData();
        return Resp.ok(data);
    }
}