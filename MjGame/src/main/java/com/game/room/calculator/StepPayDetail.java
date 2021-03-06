package com.game.room.calculator;

import com.game.core.room.calculator.PayDetail;
import com.game.room.MjTable;
import com.module.net.NetGame;

import java.util.*;

/**
 * Created by leroy:656515489@qq.com
 * 2017/5/27.
 */
public class StepPayDetail {
    protected int toUid;
    protected int type;
    protected int gainTotal; //得分汇总
    protected List<Integer> fromUids;
    protected MjCalculator calculator;
    protected int multipleRateTotal = 0;//用户支付的乘法的番数汇总

    protected int addRateTotal = 0;//用户支付的加法的番数汇总

    /** 失分名目信息 玩家id-失分名目列表 */
    protected Map<Integer,Set<Integer>> lostDetailType = new HashMap<>(3);
    /** 得分名目列表 名目-分数 */
    protected Map<Integer,PayDetail> addDetailType = new HashMap<>(1);

    /**  玩家id-玩家得失分详情 */
    protected Map<Integer,List<NetGame.NetKvData>> payList = new HashMap<>();

    public void addPayDetail(MjCalculator calculator,PayDetail payDetail){
        calculator.fomateLog(payDetail.toJson());

        if(fromUids == null || payDetail.isFinalFirst()){
            this.calculator = calculator;
            fromUids = payDetail.getFromUids();
            toUid = payDetail.getToUid();
            type = payDetail.getType();
        }

        if(payDetail.isExtraAdd()){
            fromUids.addAll(payDetail.getFromUids());
        }

        if(payDetail.getPayType() == PayDetail.PayType.Multiple){
            multipleRateTotal += payDetail.getRate();
        }else {
            addRateTotal += payDetail.getRate();
        }

        if(payDetail.getFromUids() != null){//失分名目，只显示名目，不显示分数
            for(int lostUid:payDetail.getFromUids()){
                if(payDetail.getLostScoreType() == 0){
                    continue;
                }

                Set<Integer> losts = lostDetailType.get(lostUid);
                if(losts == null){
                    losts = new HashSet<>(1);
                    lostDetailType.put(lostUid,losts);
                }

                if(losts.contains(payDetail.getLostScoreType())){
                    continue;
                }
                losts.add(payDetail.getLostScoreType());
            }
        }

      if(!addDetailType.containsKey(payDetail.getSubType())){
          addDetailType.put(payDetail.getSubType(),payDetail);
      }
    }

    private boolean isExecute = false;
    public final boolean executeCalculator(MjTable room){
        if(isExecute){
            return false;
        }
        isExecute = true;
        execute(room);

        for(Map.Entry<Integer,Set<Integer>> entry:lostDetailType.entrySet()){
            for(Integer lostType:entry.getValue()){
                this.addPay(entry.getKey(),lostType,0,PayDetail.PayType.NULL);
            }
        }

        for(Map.Entry<Integer,PayDetail> entry:addDetailType.entrySet()){
            this.addPay(toUid,entry.getKey(),entry.getValue().getRate()*fromUids.size(),entry.getValue().getPayType());
        }

        calculator.fomateLog(toJson());
        return true;
    }

    protected boolean execute(MjTable room){
      /*  int multiRate = 1<<multipleRateTotal;

        int maxFanObj = room.getMaxFan();
        if(maxFanObj != 0 && multiRate>maxFanObj){
            multiRate = maxFanObj;
        }
*/
        int addRate = addRateTotal;
        boolean isFirstAdd = true;
      /*  int every = 0;
        if(isFirstAdd){//先加再乘
            every = addRate*multiRate;
        }else {
            every = multiRate+addRate;
        }*/

        int allScore=0;
        for(int uid : fromUids){
            int score = addRate;
            allScore+=score;
            room.getCalculator().addScore(uid,-score);
        }

        this.gainTotal = allScore;
        room.getCalculator().addScore(toUid,gainTotal);
        return true;
    }

    public String toJson(){
        StringBuilder sb = new StringBuilder("stepPayDetail:");
        sb.append("multipleRateTotal:").append(multipleRateTotal).append(",");
        sb.append("addRateTotal:").append(addRateTotal).append(",");
        sb.append("gainScore:").append(gainTotal).append(",");
        sb.append("toUid:").append(toUid).append(",");
        sb.append("fromUids:").append(Arrays.toString(fromUids.toArray())).append(",");
        return sb.toString();
    }

    /**
     *
     * @param uid
     * @param type
     * @param addRate
     * @param addType 0失分，不显示，1：分数, 2:番数
     */
    protected void addPay(int uid,Integer type,int addRate,PayDetail.PayType addType){
        List<NetGame.NetKvData> list = payList.get(uid);
        if(list == null){
            list = new LinkedList<>();
            payList.put(uid,list);
        }

        NetGame.NetKvData.Builder netKvData = NetGame.NetKvData.newBuilder();
        if(type != null && type > 0){
            netKvData.setK(type);
        }
        netKvData.setV(addRate*10+addType.ordinal());
        list.add(netKvData.build());
    }

    public Map<Integer, List<NetGame.NetKvData>> getPayList() {
        return payList;
    }
}
