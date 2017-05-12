package com.game.room.action;

import com.game.core.config.IOptPlugin;
import com.game.core.config.IPluginCheckCanExecuteAction;
import com.game.core.config.TablePluginManager;
import com.game.room.MjChairInfo;
import com.game.socket.module.UserVistor;
import com.game.core.action.BaseAction;
import com.game.room.MjTable;
import com.lsocket.message.Response;
import com.module.net.NetGame;

import java.util.ArrayList;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/19.
 */
public class GameAction extends BaseAction<MjTable> {
    @Override
    public boolean isChangeToNextStatus(MjTable table) {
        return false;
    }

    @Override
    public void initAction(MjTable table) {
        GameStatusData statusData = table.getStatusData();
        statusData.checkGang(table.getChairByUid(table.getBankId()),0);
    }

    @Override
    public void doAction(MjTable table, Response response, UserVistor visitor, NetGame.NetOprateData netOprateData) {
    }

    @Override
    public void overAction(MjTable table) {
        table.addRound(); //局数加一
    }

    public void check(MjChairInfo chairInfo, int card){
        ArrayList<IOptPlugin> optPlugins = TablePluginManager.getInstance().getOptPlugin(chairInfo.getTableVo().getGameId(),this.getActionType());
        if(optPlugins == null){
            return;
        }

        for(int i= 0;i<optPlugins.size();i++){
            ((IPluginCheckCanExecuteAction)optPlugins.get(i)).checkExecute(chairInfo,card,null);
        }

    }
}
