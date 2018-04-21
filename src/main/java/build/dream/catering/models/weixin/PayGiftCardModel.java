package build.dream.catering.models.weixin;

import build.dream.common.models.BasicModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class PayGiftCardModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger userId;

    @NotEmpty
    private List<String> mchIdList;

    @NotNull
    private Date beginTime;

    @NotNull
    private Date endTime;

    @NotNull
    private BigInteger weiXinCardId;

    @NotNull
    private Integer leastCost;

    @NotNull
    private Integer maxCost;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public List<String> getMchIdList() {
        return mchIdList;
    }

    public void setMchIdList(List<String> mchIdList) {
        this.mchIdList = mchIdList;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigInteger getWeiXinCardId() {
        return weiXinCardId;
    }

    public void setWeiXinCardId(BigInteger weiXinCardId) {
        this.weiXinCardId = weiXinCardId;
    }

    public Integer getLeastCost() {
        return leastCost;
    }

    public void setLeastCost(Integer leastCost) {
        this.leastCost = leastCost;
    }

    public Integer getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Integer maxCost) {
        this.maxCost = maxCost;
    }
}
