package com.wety.websocketdemo.server;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务器端
 *
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *
 * @Author wety
 * @CreateTime 2020/01/15 21:35
 * @Version 1.0
*/
@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收sid
     */
    private String sid="";

    /**
     * 连接建立成功调用的方法
     *
     * @Author wety
     * @CreateTime 2020/1/16 08:07
     * @param session
     * @param sid
     * @Return void
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid) {
        this.session = session;
        //加入set中
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        System.out.println("有新窗口开始监听:"+sid+",当前在线人数为" + getOnlineCount());
        this.sid=sid;
        this.sendMessage("连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @Author wety
     * @CreateTime 2020/1/16 08:05
     * @param message 客户端发送过来的消息
     * @param session
     * @Return void
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("收到来自窗口"+sid+"的信息:"+message);
        //群发消息
        for (WebSocketServer webSocketServer : webSocketSet) {
            if(webSocketServer.sid.equals(sid)){
                webSocketServer.sendMessage(message);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务器主动推送消息
     */
    public void sendMessage(String message) {
        System.out.println("推送消息到窗口"+sid+"，推送内容:"+message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 群发自定义消息
     *
     * @Author wety
     * @CreateTime 2020/1/16 08:05
     * @param message 消息内容
     * @param sid 所要发送的对象
     * @Return void
     */
    public static void sendInfo(String message,@PathParam("sid") String sid) {
        for (WebSocketServer webSocketServer : webSocketSet) {
            if (sid == null || sid.isEmpty()){
                // 如果sid为空，即群发消息
                webSocketServer.sendMessage(message);
            }else {
                if(webSocketServer.sid.equals(sid)){

                    webSocketServer.sendMessage(message);
                }
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
