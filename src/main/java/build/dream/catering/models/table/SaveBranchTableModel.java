package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

public class SaveBranchTableModel extends CateringBasicModel {
    private BigInteger id;

    @NotNull
    private BigInteger tableAreaId;

    @NotNull
    @Length(max = 20)
    private String code;

    @NotNull
    @Length(max = 20)
    private String name;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer status;

    @NotNull
    @Min(1)
    private Integer dinnersNumber;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getTableAreaId() {
        return tableAreaId;
    }

    public void setTableAreaId(BigInteger tableAreaId) {
        this.tableAreaId = tableAreaId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDinnersNumber() {
        return dinnersNumber;
    }

    public void setDinnersNumber(Integer dinnersNumber) {
        this.dinnersNumber = dinnersNumber;
    }
}
