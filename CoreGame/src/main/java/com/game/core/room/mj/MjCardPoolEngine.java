package com.game.core.room.mj;

import com.game.core.room.BaseCardPoolEngine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/25.
 */
public class MjCardPoolEngine extends BaseCardPoolEngine<Integer> {
    protected Map<Integer,List<Integer>> outPool = new HashMap<>();
    private int lastPlayCard = 0;
    private int lastPlayCardUid = 0;

    public MjCardPoolEngine(int gameId){
        super(gameId);
    }

    @Override
    public void init() {
        outPool.clear();
        lastPlayCard = 0;
        lastPlayCardUid = 0;
        super.init();
    }

    @Override
    public void shuffle() {

    }

    @Override
    public int getRemainCount() {
        return cardPool.size();
    }

    @Override
    public List<Integer> getRemainCards() {
        return cardPool;
    }

    public int removeLastCard(){
        int card = lastPlayCard;
        lastPlayCard = 0;
        return card;
    }

    protected void putOutCard(int uid,int c){
        List<Integer> cards = outPool.get(uid);
        if(cards == null){
            cards = new LinkedList<>();
            outPool.put(uid,cards);
        }

        cards.add(c);
    }

    public void playOutCard(int uid,int c){
        if(lastPlayCard != 0){
            putOutCard(uid,c);
        }

        lastPlayCard = c;
        lastPlayCardUid = uid;
    }
}