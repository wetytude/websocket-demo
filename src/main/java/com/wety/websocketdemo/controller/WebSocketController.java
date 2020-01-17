package com.wety.websocketdemo.controller;

import com.wety.websocketdemo.server.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName WebSocketController
 * @Author wety
 * @Description //
 * @CreateTime 2020/01/15 21:39
 * @Version 1.0
 */
@Controller
@RequestMapping("/websocket")
public class WebSocketController {

    /**
     * 调用 WebSocketServer 的消息推送方法进行消息推送
     * 如: http://localhost:8080/websocket/push.do?sid=&message=hello
     *
     * @Author wety
     * @CreateTime 2020/01/15 21:40
     * @param sid sid 连接 WebSocketServer 的前端的唯一标识。如果sid为空，即表示向所有连接 WebSocketServer 的前端发送相关消息
     * @param message 需要发送的消息内容
     * @Return void
     */
    @ResponseBody
    @RequestMapping("push.do")
    public void pushToWeb(@RequestParam(name = "sid", defaultValue = "") String sid, @RequestParam(name = "message")  String message) {
        WebSocketServer.sendInfo(message,sid);
    }
}
