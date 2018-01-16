package build.dream.catering.models.goods;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class GoodsFlavorGroupModel {
    private BigInteger id;

    @NotNull
    private BigInteger goodsId;

    @NotNull
    private String name;

    @NotNull
    private List<GoodsFlavorModel> goodsFlavorModels;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(BigInteger goodsId) {
        this.goodsId = goodsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GoodsFlavorModel> getGoodsFlavorModels() {
        return goodsFlavorModels;
    }

    public void setGoodsFlavorModels(List<GoodsFlavorModel> goodsFlavorModels) {
        this.goodsFlavorModels = goodsFlavorModels;
    }
}
