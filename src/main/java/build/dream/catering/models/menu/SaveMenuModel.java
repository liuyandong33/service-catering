package build.dream.catering.models.menu;

import build.dream.common.models.BasicModel;
import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ValidateUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class SaveMenuModel extends CateringBasicModel {
    private BigInteger id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private Date startTime;

    @NotNull
    private Date endTime;

    @NotNull
    private Integer status;

    @NotNull
    private Integer effectiveScope;

    @NotEmpty
    private List<BigInteger> branchIds;

    @NotEmpty
    private List<Detail> details;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEffectiveScope() {
        return effectiveScope;
    }

    public void setEffectiveScope(Integer effectiveScope) {
        this.effectiveScope = effectiveScope;
    }

    public List<BigInteger> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<BigInteger> branchIds) {
        this.branchIds = branchIds;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (Detail detail : details) {
            ValidateUtils.isTrue(detail.validate(), "details");
        }
    }

    public static class Detail extends BasicModel {
        @NotNull
        private BigInteger goodsId;

        @NotNull
        private BigInteger goodsSpecificationId;

        @NotNull
        private BigInteger goodsUnitId;

        public BigInteger getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(BigInteger goodsId) {
            this.goodsId = goodsId;
        }

        public BigInteger getGoodsSpecificationId() {
            return goodsSpecificationId;
        }

        public void setGoodsSpecificationId(BigInteger goodsSpecificationId) {
            this.goodsSpecificationId = goodsSpecificationId;
        }

        public BigInteger getGoodsUnitId() {
            return goodsUnitId;
        }

        public void setGoodsUnitId(BigInteger goodsUnitId) {
            this.goodsUnitId = goodsUnitId;
        }
    }
}
