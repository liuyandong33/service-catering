package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class SaveTableAreaModel extends CateringBasicModel {
    private Long id;

    @NotNull
    @Length(max = 20)
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
