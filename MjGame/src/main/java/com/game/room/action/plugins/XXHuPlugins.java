package com.game.room.action.plugins;

import com.game.core.room.BaseChairInfo;
import com.game.core.room.BaseTableVo;
import com.game.room.MjTable;
import com.game.room.action.basePlugins.HuPlugins;
import com.game.room.status.StepGameStatusData;
import com.lsocket.message.Response;

/**
 * Created by leroy:656515489@qq.com
 * 2017/5/11.
 */
public class XXHuPlugins extends HuPlugins<MjTable>{
    @Override
    public boolean checkExecute(BaseChairInfo chair, int card, Object parems) {
        return false;
    }

    @Override
    public HuPlugins createNew() {
        return new HuPlugins();
    }

    @Override
    public boolean doOperation(MjTable table, Response response, int roleId, StepGameStatusData stepGameStatusData) {
        if (stepGameStatusData.getiOptPlugin().getPlugin().getSubType() != this.getPlugin().getSubType()) {
            return false;
        }
        table.setNextBankerUid(roleId);
        payment(table,stepGameStatusData);
        createCanExecuteAction(table,stepGameStatusData);
        return true;
    }


}
