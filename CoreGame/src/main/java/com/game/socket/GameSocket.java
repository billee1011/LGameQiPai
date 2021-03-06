package com.game.socket;

import com.game.manager.CoreServiceManager;
import com.game.manager.DBServiceManager;
import com.game.socket.codec.RequestDecoderRemote;
import com.game.socket.codec.ResponseEncoderRemote;
import com.game.socket.control.CoreDispatcherRmote;
import com.game.socket.listen.GameHeartLinster;
import com.game.socket.module.UserVistor;
import com.lsocket.codec.RequestDecoder;
import com.lsocket.codec.ResponseEncoder;
import com.lsocket.config.SocketConfig;
import com.lsocket.core.SocketServer;
import com.lsocket.listen.HeartListen;
import com.lsocket.module.ModuleDispaterInstance;
import org.apache.mina.core.session.IoSession;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/6.
 */
public class GameSocket extends SocketServer<UserVistor>{
    public final static GameSocket gameSocket = new GameSocket();

    private GameSocket(){
        super(new CoreDispatcherRmote());
        CoreServiceManager.getIntance().load();
        DBServiceManager.getInstance().load();
    }

    public static GameSocket getIntance(){
        return gameSocket;
    }

    @Override
    public UserVistor createVistor(IoSession session, long timeOutTime) {
        return new UserVistor(this,session,timeOutTime);
    }

    @Override
    public ResponseEncoder initResponseEncoder() {
        return new ResponseEncoderRemote();
    }

    @Override
    public RequestDecoder initRequestDecoder() {
        return new RequestDecoderRemote();
    }

    @Override
    public HeartListen initHeartListen() {
        return new GameHeartLinster();
    }

    @Override
    public SocketConfig initConfig() {
        return SocketConfig.getInstance();
    }

    @Override
    public ModuleDispaterInstance getInnerModuleDispaterConfig() {
        ModuleDispaterInstance ins = new ModuleDispaterInstance();
        List<ModuleDispaterInstance.Obj> objs = new LinkedList<>();
        objs.add(new ModuleDispaterInstance.Obj("com.game.Handler"));
        ins.setObjList(objs);
        return ins;
    }

}
