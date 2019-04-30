package build.dream.catering.models.flashsale;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2019-03-13.
 */
public class SaveFlashSaleActivityModel extends CateringBasicModel {
    @NotNull
    private BigInteger goodsId;

    @Length(max = 50)
    private String goodsName;

    @Length(max = 255)
    private String imageUrl;

    @NotNull
    @Length(max = 50)
    private String name;

    @NotNull
    private Date startTime;

    @NotNull
    private Date endTime;

    @NotNull
    private Boolean limited;

    private BigDecimal limitQuantity;

    @NotNull
    private Integer beforeShowTime;

    @NotNull
    private Integer timeUnit;

    @NotNull
    private BigDecimal originalPrice;

    @NotNull
    private BigDecimal flashSalePrice;

    @NotNull
    private BigDecimal flashSaleStock;

    @Length(max = 255)
    private String description;

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Boolean getLimited() {
        return limited;
    }

    public void setLimited(Boolean limited) {
        this.limited = limited;
    }

    public BigDecimal getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(BigDecimal limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    public Integer getBeforeShowTime() {
        return beforeShowTime;
    }

    public void setBeforeShowTime(Integer beforeShowTime) {
        this.beforeShowTime = beforeShowTime;
    }

    public Integer getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Integer timeUnit) {
        this.timeUnit = timeUnit;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getFlashSalePrice() {
        return flashSalePrice;
    }

    public void setFlashSalePrice(BigDecimal flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }

    public BigDecimal getFlashSaleStock() {
        return flashSaleStock;
    }

    public void setFlashSaleStock(BigDecimal flashSaleStock) {
        this.flashSaleStock = flashSaleStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
