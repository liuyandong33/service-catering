package build.dream.catering.models.goods;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GoodsFlavorModel {
    private BigInteger id;
    private String name;
    private BigDecimal price;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
