package com.game.room.action;

import com.game.core.action.BaseAction;
import com.game.core.config.IOptPlugin;
import com.game.core.config.IPluginCheckCanExecuteAction;
import com.game.core.config.TablePluginManager;
import com.game.core.constant.GameConst;
import com.game.core.room.BaseChairInfo;
import com.game.room.MjAutoCacheHandContainer;
import com.game.room.MjChairInfo;
import com.game.room.MjHandCardsContainer;
import com.game.room.MjTable;
import com.game.room.status.StepGameStatusData;
import com.lsocket.message.Response;
import com.module.core.ResponseCode;
import com.module.net.NetGame;

import java.util.ArrayList;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/19.
 */
public class GameAction extends BaseAction<MjTable> {

    @Override
    public void initAction(MjTable table) {
        table.getStepHistoryManager().getActionTypeSteps().add(new StepGameStatusData(MoAction.getInstance(),table.getBankId(),table.getBankId(),null));

        SuperGameStatusData statusData = table.getStatusData();
        MjChairInfo chairInfo = table.getChairByUid(table.getBankId());
        statusData.checkGang(chairInfo,0);
        statusData.checkChi(chairInfo,0);
        statusData.checkPeng(chairInfo,0);
        statusData.checkHu(chairInfo,0);
        statusData.checkDa(chairInfo,0);
    }

    @Override
    public void doAction(MjTable table, Response response, int roleId, NetGame.NetOprateData netOprateData){
        SuperGameStatusData statusData = table.getStatusData();
        boolean isMatch = statusData.checkMatch(table,roleId,netOprateData,response);
        if(!isMatch){
            return;
        }

        if(statusData.getFirst().isAuto()){
           this.doAction(table,null,statusData.getFirst().getUid(),null);
            return;
        }

        table.sendCanDoActionMsg(0);
        table.flushMsgQueue();
    }

    @Override
    public void overAction(MjTable table) {
        table.addRound(); //局数加一
    }

    @Override
    public void tick(MjTable table){
        SuperGameStatusData statusData = table.getStatusData();
        if(statusData.getFirst().isAuto()){
            return;
        }

        BaseChairInfo info = table.getChairByUid(statusData.getFirst().getUid());
        if(!info.isAuto() && !info.isRobot()){
            return;
        }

        NetGame.NetOprateData netOprateData = ((MjAutoCacheHandContainer)info.getHandsContainer().getAutoCacheHands()).getNetOprateData(statusData.getFirst());
        this.doAction(table,null,info.getId(),netOprateData);
    }
}
