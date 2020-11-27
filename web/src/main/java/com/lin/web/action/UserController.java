package com.lin.web.action;

import com.lin.common.dto.SysUser;
import com.lin.common.vo.ResultVO;
import com.lin.service.service.impl.UserServiceImpl;
import com.lin.web.enums.StatusCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/lin")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;


    @RequestMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<ResultVO> userList(@PathVariable("id") String id) {
        SysUser user = userServiceImpl.getUserById(id);
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }



}
