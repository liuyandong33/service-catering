package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SaveTableAreaModel extends CateringBasicModel {
    private BigInteger id;

    @NotNull
    @Length(max = 20)
    private String name;

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
}
