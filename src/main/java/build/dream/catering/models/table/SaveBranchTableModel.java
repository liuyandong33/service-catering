package build.dream.catering.models.table;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SaveBranchTableModel extends CateringBasicModel {
    private Long id;

    @NotNull
    private Long tableAreaId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTableAreaId() {
        return tableAreaId;
    }

    public void setTableAreaId(Long tableAreaId) {
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
