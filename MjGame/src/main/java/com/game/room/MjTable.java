package com.game.room;

import com.game.Handler.MjCmd;
import com.game.core.constant.GameConst;
import com.game.core.factory.TableProducer;
import com.game.core.room.BaseTableVo;
import com.game.room.calculator.MjCalculator;
import com.game.room.util.MJTool;
import com.game.socket.module.UserVistor;
import com.game.util.ProbuffTool;
import com.lgame.util.comm.RandomTool;
import com.lsocket.message.Response;
import com.module.core.ResponseCode;
import com.module.net.NetGame;
import com.module.net.NetResult;

import java.util.*;

/**
 * Created by leroy:656515489@qq.com
 * 2017/4/19.
 */
public class MjTable extends BaseTableVo<MjStatus,MjChairInfo> {
    /** 庄家id */
    private int bankId;
    ////顺序东北西南->0-3

    private int nextBankerUid;
    protected int lastBankUid = -1;

    private int maxFan;
    private int type;
    protected static List<Integer> maxFanSet = MJTool.getList(4,8,10);

    public MjTable(int ownerId,int maxSize,int id,int gameId, TableProducer tableProducer) {
        super(ownerId,maxSize,id, MjStatus.Idle,gameId,tableProducer);
    }

    @Override
    protected void initStatus() {
        this.setAllStatus(new MjStatus[]{MjStatus.Idle, MjStatus.Pao, MjStatus.DingZhuang, MjStatus.FaPai, MjStatus.Game});
    }

    @Override
    protected void initChair(int maxSize) {
        chairs = new MjChairInfo[maxSize];
    }

    @Override
    protected void initCalculator() {
        calculator = new MjCalculator(this);
    }

    @Override
    public void cleanTableCache() {
        super.cleanTableCache();
        stepHistory.clean();
        cardPoolEngine.init();
        calculator.clear();
    }

    @Override
    protected void initStepHistory() {
        stepHistory = new MjStepHistory();
    }

    @Override
    public int getGameResponseCmd() {
        return MjCmd.Game.getValue();
    }

    @Override
    public int getGameResponseModule() {
        return MjCmd.Game.getModule();
    }

    @Override
    protected void initCardPoolEngine() {
        cardPoolEngine = new MjCardPoolEngine(this.getGameId(),null);
    }

    public Map<Integer,Integer[]> randomChairPosition(int diceCount){

        List<Integer[]> playerScore = new ArrayList<>(chairs.length);
        Map<Integer,Integer[]> diceDetail = new HashMap<>(chairs.length);
        for(int i = 0;i<chairs.length;i++){
            int count = diceCount;
            Integer[] allCounts = new Integer[diceCount];
            int allCountVaule = 0;
            while (count-- > 0){
                allCounts[count] = RandomTool.Next(6)+1;
                allCountVaule+=allCounts[count];
            }

            playerScore.add(new Integer[]{chairs[i].getId(),allCountVaule});
            diceDetail.put(chairs[i].getId(),allCounts);
        }


        Collections.sort(playerScore, new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] o1, Integer[] o2) {
                return o2[1] - o1[1] ;
            }
        });

        for(int i=0; i<chairs.length; i++){
            chairs[i] = getChairByUid(playerScore.get(i)[0]);
            chairs[i].setIdx(i);
        }

        return diceDetail;
    }


    @Override
    public ResponseCode.Error setSelected(List<Integer> typeList) {
        ResponseCode.Error error = super.setSelected(typeList);
        if(error != ResponseCode.Error.succ){
            return error;
        }

        int maxRate = typeList.get(1);
        int type = typeList.get(2);
        this.setMaxFan(maxRate);
        this.setType(type);

        if((type & GameConst.XXMjType.SIFENG) == GameConst.XXMjType.SIFENG){
            cardPoolEngine.setUserSetStaticCardPool(MJTool.SIFENGZHONGFABAI);
        }

        return ResponseCode.Error.succ;
    }

    @Override
    protected NetGame.NetExtraData.Builder getTableExtrData(int roleId) {
        NetGame.NetExtraData.Builder extra = NetGame.NetExtraData.newBuilder();
        extra.addList(this.getBankId());
        extra.addList(this.getCardPool().getRemainCount());
        extra.addList(this.getCardPool().getAllSize());

        int uid = getFocusIdex() == -1?0:getChairs()[getFocusIdex()].getId();
        extra.addOperates(ProbuffTool.getTurnData(uid,0,this.getTimeOutRemain()));

        NetGame.NetOprateData.Builder netOprateData = getStatusData().getCanDoDatas(this,roleId);
        if(netOprateData.getKvDatasCount() != 0){
            extra.addOperates(netOprateData);
        }

        NetGame.NetOprateData.Builder details = getStatusData().getStatusDetail(this);
        if(details!=null){
            extra.addOperates(details);
        }

        return extra;
    }

    @Override
    protected NetGame.NetExtraData getOtherNetExtraData(MjChairInfo mjChairInfo) {
        NetGame.NetExtraData.Builder extra = getNetExtraData(mjChairInfo);
        extra.addKvDatas(getHandCards(mjChairInfo,false));
        return extra.build();
    }

    @Override
    protected NetGame.NetExtraData getSelfNetExtraData(MjChairInfo mjChairInfo) {
        NetGame.NetExtraData.Builder extra = getNetExtraData(mjChairInfo);
        extra.addKvDatas(getHandCards(mjChairInfo,true));
        return extra.build();
    }

    public NetGame.NetExtraData.Builder getNetExtraData(MjChairInfo mjChairInfo) {
        NetGame.NetExtraData.Builder extra = NetGame.NetExtraData.newBuilder();
        extra.addKvDatas(getOutCard(mjChairInfo));
        extra.addAllKvDatas(getCardsDetail(mjChairInfo));
        extra.addList(mjChairInfo.getTotalScore());
        return extra;
    }

    /**
     * 某个玩家的桌面牌数据（含手牌，胡碰等(不含出牌)）
     * @param mjChairInfo
     * @return
     */
    public List<NetGame.NetKvData> getCardsDetail(MjChairInfo mjChairInfo){
        List<NetGame.NetKvData> kvDatas = new LinkedList<>();
        kvDatas.add(getHu(mjChairInfo));


        List<GroupCard> groupCards = mjChairInfo.getHandsContainer().getChiGangList();
        if(!groupCards.isEmpty()){
            for(GroupCard groupCard:groupCards){
                kvDatas.add(getPengChiGangCardType(groupCard));
            }
        }

        groupCards = mjChairInfo.getHandsContainer().getPengList();
        if(!groupCards.isEmpty()){
            for(GroupCard groupCard:groupCards){
                kvDatas.add(getPengChiGangCardType(groupCard));
            }
        }

        return kvDatas;
    }

    protected final NetGame.NetKvData getOutCard(MjChairInfo mjChairInfo){
        NetGame.NetKvData.Builder netKvData = NetGame.NetKvData.newBuilder();
        netKvData.setK(1);
        List<Integer> outCards = ((MjCardPoolEngine)this.getCardPool()).getOutPool(mjChairInfo.getId());
        if(outCards != null && !outCards.isEmpty()){
            netKvData.addAllDlist(outCards);
        }
        return netKvData.build();
    }

    protected final NetGame.NetKvData getHandCards(MjChairInfo mjChairInfo,boolean isMySelf){
        NetGame.NetKvData.Builder netKvData = NetGame.NetKvData.newBuilder();
        netKvData.setK(2);
        netKvData.setV(mjChairInfo.getHandsContainer().getHandCards().size());
        if(isMySelf){
            netKvData.addAllDlist(mjChairInfo.getHandsContainer().getHandCards());
        }
        return netKvData.build();
    }

    protected final NetGame.NetKvData getHu(MjChairInfo mjChairInfo){
        NetGame.NetKvData.Builder netKvData = NetGame.NetKvData.newBuilder();
        netKvData.setK(4);
        netKvData.addAllDlist(mjChairInfo.getHandsContainer().getHuCards());
        return netKvData.build();
    }

    protected final NetGame.NetKvData getPengChiGangCardType(GroupCard groupCard){
        NetGame.NetKvData.Builder netKvData = NetGame.NetKvData.newBuilder();
        netKvData.setK(groupCard.getType());
        netKvData.addAllDlist(groupCard.getCards());
        return netKvData.build();
    }

    @Override
    public MjChairInfo createChair(UserVistor visitor) {
        MjChairInfo chairInfo = new MjChairInfo(visitor.getRoleId(),this);
        chairInfo.setIp(visitor.getIp().getIp());
        return chairInfo;
    }

    public boolean isGameOver() {
        if((type & GameConst.XXMjType.SIFENG) == GameConst.XXMjType.SIFENG){
            return getCardPool().getRemainCount() == 14;
        }

        return getCardPool().getRemainCount() == 0;
    }


    @Override
    protected Response getGameOverResult() {
        return Response.defaultResponse(GameConst.MOUDLE_Mj,MjCmd.Result.getValue(),0, (NetResult.RQMjREsult) this.getCalculator().executeCalculator());
    }

    //////////////////////////////////////////////////////

    /**
     * 可以操作的集合
     * @param
     */
    public NetGame.NetOprateData.Builder getCanDoActionsNetOprateData(int roleId){
        NetGame.NetOprateData.Builder canDoActions = NetGame.NetOprateData.newBuilder();
        canDoActions.setOtype(GameConst.MJ.ACTION_TYPE_CanDoActions);
        canDoActions.setUid(roleId);
        return canDoActions;
    }

    ///////////////////胡牌判断//////////////////////////////////////////
    public boolean checkQYiSe(List<Integer> handCards, List groupCards) {
        return MJTool.oneCorlor(handCards,groupCards);
    }


    //////////////////////////////////////////////////////////////////////////
    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
        this.setFocusIdex(this.getChairByUid(bankId).getIdx());
    }

    public int getNextBankerUid() {
        return nextBankerUid;
    }

    public int getMaxFan() {
        return maxFan;
    }

    public void setMaxFan(int maxFan) {
        if(!maxFanSet.contains(maxFan)){
            maxFan = maxFanSet.get(0);
        }
        this.maxFan = maxFan;
    }

    public int getLastBankUid() {
        return lastBankUid;
    }

    public void setLastBankUid(int lastBankUid) {
        this.lastBankUid = lastBankUid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNextBankerUid(int nextBankerUid) {
        this.nextBankerUid = nextBankerUid;
    }

    public boolean isAutoHu() {
        return false;
    }
}
