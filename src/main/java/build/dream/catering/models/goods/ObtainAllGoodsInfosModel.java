package build.dream.catering.models.goods;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class ObtainAllGoodsInfosModel extends CateringBasicModel {
    @NotNull
    private Long categoryId;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
