package build.dream.catering.models.vip;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class SaveVipGroupModel extends CateringBasicModel {
    private Long id;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
