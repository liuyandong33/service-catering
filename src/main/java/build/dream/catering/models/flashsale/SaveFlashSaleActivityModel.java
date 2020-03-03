package build.dream.catering.models.flashsale;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by liuyandong on 2019-03-13.
 */
public class SaveFlashSaleActivityModel extends CateringBasicModel {
    @NotNull
    private Long goodsId;

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

    private Double limitQuantity;

    @NotNull
    private Integer beforeShowTime;

    @NotNull
    private Integer timeUnit;

    @NotNull
    private Double originalPrice;

    @NotNull
    private Double flashSalePrice;

    @NotNull
    private Double flashSaleStock;

    @Length(max = 255)
    private String description;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
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

    public Double getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(Double limitQuantity) {
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

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getFlashSalePrice() {
        return flashSalePrice;
    }

    public void setFlashSalePrice(Double flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }

    public Double getFlashSaleStock() {
        return flashSaleStock;
    }

    public void setFlashSaleStock(Double flashSaleStock) {
        this.flashSaleStock = flashSaleStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
