package com.game.core.room;

import com.game.core.room.card.BaseHandCardsContainer;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/21.
 */
public abstract class BaseChairInfo<Hands extends BaseHandCardsContainer> {
    protected BaseTableVo tableVo;
    protected int id;
    protected int idx;
    protected String image="bg";
    protected boolean isOnline = true;
    protected boolean isAuto = false;
    protected String ip;
    protected Hands handsContainer;
    protected boolean robot = false;
    protected int totalScore;

    public BaseChairInfo(BaseTableVo tableVo){
        handsContainer = initHands();
        this.tableVo = tableVo;
    }

    protected abstract Hands initHands();

    /**
     * 重置状态
     */
    public abstract void resetStatus();

    /***
     * 清除缓存
     */
    public abstract void clean();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Hands getHandsContainer() {
        return handsContainer;
    }

    public void setHandsContainer(Hands handsContainer) {
        this.handsContainer = handsContainer;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public BaseTableVo getTableVo() {
        return tableVo;
    }

    public String getImage() {
        return image;
    }

}
