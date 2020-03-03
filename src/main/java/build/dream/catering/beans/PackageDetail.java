package build.dream.catering.beans;

public class PackageDetail {
    private Long goodsId;
    private String goodsName;
    private boolean stocked;
    private Long goodsSpecificationId;
    private String goodsSpecificationName;
    private Double price;
    private Double quantity;
    private Long packageId;
    private Long packageGroupId;
    private String packageGroupName;
    private Integer packageGroupType;

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

    public boolean isStocked() {
        return stocked;
    }

    public void setStocked(boolean stocked) {
        this.stocked = stocked;
    }

    public Long getGoodsSpecificationId() {
        return goodsSpecificationId;
    }

    public void setGoodsSpecificationId(Long goodsSpecificationId) {
        this.goodsSpecificationId = goodsSpecificationId;
    }

    public String getGoodsSpecificationName() {
        return goodsSpecificationName;
    }

    public void setGoodsSpecificationName(String goodsSpecificationName) {
        this.goodsSpecificationName = goodsSpecificationName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getPackageGroupId() {
        return packageGroupId;
    }

    public void setPackageGroupId(Long packageGroupId) {
        this.packageGroupId = packageGroupId;
    }

    public String getPackageGroupName() {
        return packageGroupName;
    }

    public void setPackageGroupName(String packageGroupName) {
        this.packageGroupName = packageGroupName;
    }

    public Integer getPackageGroupType() {
        return packageGroupType;
    }

    public void setPackageGroupType(Integer packageGroupType) {
        this.packageGroupType = packageGroupType;
    }
}
