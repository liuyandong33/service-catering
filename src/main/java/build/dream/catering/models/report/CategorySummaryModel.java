package build.dream.catering.models.report;

import build.dream.catering.constants.Constants;
import build.dream.common.models.CateringBasicModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class CategorySummaryModel extends CateringBasicModel {
    @NotNull
    @JsonFormat(pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date startTime;

    @NotNull
    @JsonFormat(pattern = Constants.DEFAULT_DATE_PATTERN)
    private Date endTime;

    @NotNull
    private Integer page;

    @NotNull
    private Integer rows;

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
