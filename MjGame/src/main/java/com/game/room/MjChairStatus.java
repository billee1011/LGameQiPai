package com.game.room;

import com.game.core.room.interfaces.BaseChairStatus;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/19.
 */
public enum MjChairStatus implements BaseChairStatus {
    Idle(0),
    ready(1),
    Game(2);
    private int value;
    MjChairStatus(int value){
        this.value = value;
    }

    public int getVal() {
        return value;
    }

}
