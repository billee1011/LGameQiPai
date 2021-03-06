package com.game.room.action.basePlugins;

import com.game.core.room.BaseChairInfo;
import com.game.log.MJLog;
import com.game.room.MjChairInfo;
import com.game.room.MjStatus;
import com.game.room.MjTable;
import com.game.room.action.SuperGameStatusData;
import com.game.room.status.StepGameStatusData;
import com.lsocket.message.Response;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by leroy:656515489@qq.com
 * 2017/5/11.
 */
public class MoPlugins<T extends MjTable> extends AbstractActionPlugin<T> implements IPluginCheckCanExecuteAction<T,StepGameStatusData>{
    @Override
    public boolean checkExecute(StepGameStatusData stepGameStatusData,BaseChairInfo chair, int card, Object parems) {
        return false;
    }

    @Override
    public void createCanExecuteAction(T table, StepGameStatusData stepGameStatusData) {
        SuperGameStatusData statusData = table.getStatusData();
        MjChairInfo chairInfo = table.getChairs()[table.getFocusIdex()];
        statusData.checkGang(chairInfo,stepGameStatusData,0);
        statusData.checkHu(chairInfo,stepGameStatusData,0);
        statusData.checkTing(chairInfo,stepGameStatusData);
        statusData.checkDa(chairInfo);
    }

    @Override
    public MoPlugins createNew() {
        return new MoPlugins();
    }

    @Override
    public boolean doOperation(T table, Response response, int roleId, StepGameStatusData stepGameStatusData) {
        test(table);

        int card = (int) table.getCardPool().getRemainCards().remove(0);
        table.getChairByUid(roleId).getHandsContainer().addHandCards(card);
        stepGameStatusData.setCard(card);

        MJLog.play("摸牌",card,roleId,table);

        createCanExecuteAction(table,stepGameStatusData);
        return true;
    }

    public void test(T table){
        if(table.getCurRount() != 1){
           return;
        }
        if(table.getStepHistoryManager().getSize() != 1){
            return;
        }

        BaseChairInfo bank = table.getChairByUid(table.getBankId());
        List<Integer> newHands = new LinkedList<>();
        Collections.addAll(newHands,11,21,22,23,24,24,24,25,26,27,12,13,14);
        bank.getHandsContainer().setHandCards(newHands);

        table.getCardPool().getRemainCards().set(0,11);
        MjStatus.FaPai.getAction().overAction(table);
    }


    @Override
    public int chickMatch(T table,List<Integer> card, StepGameStatusData stepData) {
        return 1;
    }
}
