package com.lin.web.action;

import com.lin.common.dto.PrdUser;
import com.lin.common.dto.SysUser;
import com.lin.common.dto.User;
import com.lin.common.vo.ResultVO;
import com.lin.service.service.PrdUserService;
import com.lin.service.service.UserService;
import com.lin.service.service.impl.PrdUserServiceImpl;
import com.lin.service.service.impl.SysUserServiceImpl;
import com.lin.service.service.impl.UserServiceImpl;
import com.lin.web.enums.StatusCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lin")
public class UserController {

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PrdUserServiceImpl PrdUserService;


    @RequestMapping("/sysUserList")
    @ResponseBody
    public ResponseEntity<ResultVO> sysUserList(@PathVariable("id") String id) {
        SysUser user = sysUserService.getUserById(Long.valueOf(id));
        return ResponseEntity.ok().body(
                new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(),
                        StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }

    @RequestMapping("/devUserList")
    @ResponseBody
    public ResponseEntity<ResultVO> devUserList(@PathVariable("id") String id) {
        User user = userService.selectByPrimaryKey(Long.valueOf(id));
        return ResponseEntity.ok().body(
                new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(),
                        StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }

    @RequestMapping("/prdUserList")
    @ResponseBody
    public ResponseEntity<ResultVO> prdUserList(@PathVariable("id") String id) {
        PrdUser user = PrdUserService.selectByPrimaryKey(Integer.valueOf(id));
        return ResponseEntity.ok().body(
                new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(),
                        StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }



}
