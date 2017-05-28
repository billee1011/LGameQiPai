package com.game.room.action.basePlugins;

import com.game.core.room.BaseChairInfo;
import com.game.log.MJLog;
import com.game.room.MjAutoCacheHandContainer;
import com.game.room.MjTable;
import com.game.room.action.HuAction;
import com.game.room.action.SuperGameStatusData;
import com.game.room.status.StepGameStatusData;
import com.game.room.util.MJTool;
import com.lsocket.message.Response;

import java.util.List;

/**
 * Created by leroy:656515489@qq.com
 * 2017/5/11.
 */
public class HuPlugins<T extends MjTable> extends AbstractActionPlugin<T> implements IPluginCheckCanExecuteAction<T,StepGameStatusData>{
    @Override
    public boolean checkExecute(StepGameStatusData stepGameStatusData,BaseChairInfo chair, int card, Object parems) {
        MJLog.huCheck("card:"+card+" fromId:"+stepGameStatusData.getUid(),chair.getId(), (MjTable) chair.getTableVo());

        HuAction.CheckHuType huType = (HuAction.CheckHuType) parems;
        if(card == 0){
            return MJTool.isSimpleHu(MJTool.toCardArray(chair.getHandsContainer().getHandCards(),0),((MjAutoCacheHandContainer)chair.getHandsContainer().getAutoCacheHands()).getCardNumMap());
        }

        int[] cards = MJTool.toCardArray(chair.getHandsContainer().getHandCards(),1);
        cards[cards.length-1] = card;

        return MJTool.isSimpleHu(cards,null);
    }


    @Override
    public void createCanExecuteAction(T room,StepGameStatusData stepGameStatusData) {
        ((SuperGameStatusData)room.getStatusData()).checkMo(room,0);
    }

    @Override
    public HuPlugins createNew() {
        return new HuPlugins();
    }

    @Override
    public boolean doOperation(T table, Response response, int roleId, StepGameStatusData stepGameStatusData) {
        if (stepGameStatusData.getiOptPlugin().getPlugin().getSubType() != this.getPlugin().getSubType()) {
            return false;
        }
        table.setNextBankerUid(roleId);
        payment(table,stepGameStatusData);
        return true;
    }

    @Override
    public int chickMatch(T table,List<Integer> card, StepGameStatusData stepData) {
        return 1;
    }
}
