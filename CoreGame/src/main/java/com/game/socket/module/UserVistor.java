package com.game.socket.module;

import com.game.socket.GameSocket;
import com.lsocket.message.Request;
import com.lsocket.message.Response;
import com.lsocket.module.SocketSystemCode;
import com.lsocket.module.Visitor;
import com.module.core.ResponseCode;
import com.module.net.DB;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/25.
 */
public class UserVistor extends Visitor<Request,Response,ResponseCode.Error> {
    private int roleId;//roleId
    private int roomId;
    private int module;
    private long heartTime;
    private int heartNum;
    private int connectErrorCount;

    private DB.UK uk;
    private int card;

    public UserVistor(GameSocket socketServer, org.apache.mina.core.session.IoSession ioSession, long timeOutTime) {
        super(socketServer, ioSession, timeOutTime);
    }

    public UserVistor() {
        super(null, null, 0);
    }

    @Override
    public void sendError(SocketSystemCode socketSystemCode) {

    }

    @Override
    public void sendMsg(Response sendMsg) {
        this.getIoSession().write(sendMsg);
    }

    @Override
    public void sendError(ResponseCode.Error code) {
        this.getIoSession().write(code.getMsg());
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getModule() {
        return module;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public long getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(long heartTime) {
        this.heartTime = heartTime;
    }

    public int getHeartNum() {
        return heartNum;
    }

    public void setHeartNum(int heartNum) {
        this.heartNum = heartNum;
    }

    public int getConnectErrorCount() {
        return connectErrorCount;
    }

    public int getCard() {
        return card;
    }

    public DB.UK getUk() {
        return uk;
    }

    public void setUk(DB.UK uk) {
        this.uk = uk;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public void setConnectErrorCount(int connectErrorCount) {
        this.connectErrorCount = connectErrorCount;
    }
}
