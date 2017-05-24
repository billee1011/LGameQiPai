package com.game.core.action;

import com.game.core.constant.GameConst;
import com.game.core.room.BaseChairInfo;
import com.game.core.room.BaseChairStatus;
import com.game.core.room.BaseTableVo;
import com.lsocket.message.Response;
import com.module.net.NetGame;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/19.
 */
public abstract class IdleAction<T extends BaseTableVo> extends BaseAction <T> {
    protected abstract <S extends BaseChairStatus> S getReadyStatus();

    public int getActionType(){
        return GameConst.ACTION_TYPE_READY;
    }

    @Override
    public void doAction(T table, Response response, int roleId, NetGame.NetOprateData netOprateData) {
        this.ready(table,roleId);
    }

    private void ready(T table,int roleId){
        BaseChairInfo chairInfo = table.getChairByUid(roleId);
        if(chairInfo.getStatus().getVal() == getReadyStatus().getVal()){
            return;
        }

        chairInfo.setStatus(getReadyStatus());
        if(table.getChairCountByStatus(chairInfo.getStatus()) == table.getChairs().length){
            table.getStatusData().setOver(true);
        }

        //发送数据
        table.sendChairStatusMsgWithOutUid(roleId);
    }
    
    @Override
    public void overAction(BaseTableVo table) {
        table.cleanTableCache();
    }

    @Override
    public void tick(T table){
        for(int i = 0;i<table.getChairs().length;i++){
            if(table.getChairs()[i] == null || (!table.getChairs()[i].isRobot() && !table.getChairs()[i].isAuto())){
                continue;
            }
            this.ready(table,table.getChairs()[i].getId());
        }
    }
}
