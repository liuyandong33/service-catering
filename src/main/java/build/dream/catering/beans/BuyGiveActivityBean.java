package build.dream.catering.beans;

import java.math.BigInteger;
import java.util.Date;

public class BuyGiveActivityBean {
    /**
     * 商户ID
     */
    private BigInteger tenantId;
    /**
     * 商户编号
     */
    private String tenantCode;
    /**
     * 门店ID
     */
    private BigInteger branchId;
    /**
     * 活动ID
     */
    private BigInteger activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动类型
     */
    private Integer activityType;
    /**
     * 活动状态
     */
    private Integer activityStatus;
    /**
     * 活动开始时间
     */
    private Date startTime;
    /**
     * 活动结束时间
     */
    private Date endTime;
    /**
     * 购买商品id
     */
    private BigInteger buyGoodsId;
    /**
     * 购买商品名称
     */
    private String buyGoodsName;
    /**
     * 购买商品规格id
     */
    private BigInteger buyGoodsSpecificationId;
    /**
     * 购买商品规格名称
     */
    private String buyGoodsSpecificationName;
    /**
     * 购买数量
     */
    private Integer buyQuantity;
    /**
     * 赠送商品id
     */
    private BigInteger giveGoodsId;
    /**
     * 赠送商品名称
     */
    private String giveGoodsName;
    /**
     * 赠送商品规格id
     */
    private BigInteger giveGoodsSpecificationId;
    /**
     * 赠送商品规格名称
     */
    private String giveGoodsSpecificationName;
    /**
     * 赠送数量
     */
    private Integer giveQuantity;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigInteger getBuyGoodsId() {
        return buyGoodsId;
    }

    public void setBuyGoodsId(BigInteger buyGoodsId) {
        this.buyGoodsId = buyGoodsId;
    }

    public String getBuyGoodsName() {
        return buyGoodsName;
    }

    public void setBuyGoodsName(String buyGoodsName) {
        this.buyGoodsName = buyGoodsName;
    }

    public BigInteger getBuyGoodsSpecificationId() {
        return buyGoodsSpecificationId;
    }

    public void setBuyGoodsSpecificationId(BigInteger buyGoodsSpecificationId) {
        this.buyGoodsSpecificationId = buyGoodsSpecificationId;
    }

    public String getBuyGoodsSpecificationName() {
        return buyGoodsSpecificationName;
    }

    public void setBuyGoodsSpecificationName(String buyGoodsSpecificationName) {
        this.buyGoodsSpecificationName = buyGoodsSpecificationName;
    }

    public Integer getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(Integer buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public BigInteger getGiveGoodsId() {
        return giveGoodsId;
    }

    public void setGiveGoodsId(BigInteger giveGoodsId) {
        this.giveGoodsId = giveGoodsId;
    }

    public String getGiveGoodsName() {
        return giveGoodsName;
    }

    public void setGiveGoodsName(String giveGoodsName) {
        this.giveGoodsName = giveGoodsName;
    }

    public BigInteger getGiveGoodsSpecificationId() {
        return giveGoodsSpecificationId;
    }

    public void setGiveGoodsSpecificationId(BigInteger giveGoodsSpecificationId) {
        this.giveGoodsSpecificationId = giveGoodsSpecificationId;
    }

    public String getGiveGoodsSpecificationName() {
        return giveGoodsSpecificationName;
    }

    public void setGiveGoodsSpecificationName(String giveGoodsSpecificationName) {
        this.giveGoodsSpecificationName = giveGoodsSpecificationName;
    }

    public Integer getGiveQuantity() {
        return giveQuantity;
    }

    public void setGiveQuantity(Integer giveQuantity) {
        this.giveQuantity = giveQuantity;
    }
}
