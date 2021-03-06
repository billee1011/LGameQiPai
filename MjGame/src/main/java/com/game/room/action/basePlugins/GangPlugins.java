package com.game.room.action.basePlugins;

import com.game.core.room.BaseChairInfo;
import com.game.room.MjTable;
import com.game.room.status.StepGameStatusData;
import com.lsocket.message.Response;

import java.util.List;

/**
 * Created by leroy:656515489@qq.com
 * 2017/5/11.
 */
public abstract class GangPlugins<T extends MjTable> extends AbstractActionPlugin<T> implements IPluginCheckCanExecuteAction<T,StepGameStatusData>{
    @Override
    public boolean checkExecute(StepGameStatusData stepGameStatusData,BaseChairInfo chair, int card, Object parems) {
        return false;
    }

    @Override
    public void createCanExecuteAction(T room,StepGameStatusData stepGameStatusData) {

    }

    @Override
    public boolean doOperation(T table, Response response, int roleId, StepGameStatusData stepGameStatusData) {
        if (stepGameStatusData.getiOptPlugin().getPlugin().getSubType() != this.getPlugin().getSubType()) {
            return false;
        }
        return true;
    }

    @Override
    public int chickMatch(T table,List<Integer> card, StepGameStatusData stepData) {
        return stepData.getCards().get(0) == card.get(0)?1:0;
    }
}
