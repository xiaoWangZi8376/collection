package com.lin.web.action;

import com.alibaba.fastjson.JSON;
import com.lin.common.entity.User;
import com.lin.common.vo.ResultVO;
import com.lin.service.service.UserService;
import com.lin.web.enums.StatusCodeEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/lin")
public class UserController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @RequestMapping("/user")
    public ResponseEntity<ResultVO> userList() {
        List<User> userList = userService.listUsers();
        System.out.println("jieguojie:" + JSON.toJSON(userList));
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage(), userList));
    }

    @RequestMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<ResultVO> userList(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }


    @PostMapping("/user")
    public ResponseEntity<ResultVO> insertUser(@RequestBody User user) {
        try {
            userService.insertUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode()).body(new ResultVO(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode(), StatusCodeEnum.DEFAULT_FAIL_CODE.getMessage()));
        }
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage()));
    }

    @PutMapping("/user")
    public ResponseEntity<ResultVO> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode()).body(new ResultVO(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode(), StatusCodeEnum.DEFAULT_FAIL_CODE.getMessage()));
        }
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage(), user));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ResultVO> deleteUser(@PathVariable("id") Integer id) {
        try {
            userService.deleteUserById(id);
        } catch (Exception e) {
            return ResponseEntity.status(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode()).body(new ResultVO(StatusCodeEnum.DEFAULT_FAIL_CODE.getCode(), StatusCodeEnum.DEFAULT_FAIL_CODE.getMessage()));
        }
        return ResponseEntity.ok().body(new ResultVO(StatusCodeEnum.SUCCESS_CODE.getCode(), StatusCodeEnum.SUCCESS_CODE.getMessage()));
    }


}
