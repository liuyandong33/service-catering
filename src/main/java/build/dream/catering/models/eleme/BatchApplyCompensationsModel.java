package build.dream.catering.models.eleme;

import build.dream.common.constraints.InList;
import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BatchApplyCompensationsModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotEmpty
    private List<CompensationRequest> requests;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public List<CompensationRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<CompensationRequest> requests) {
        this.requests = requests;
    }

    public void setRequests(String requests) {
        ApplicationHandler.isJson(requests, "requests");
        this.requests = GsonUtils.jsonToList(requests, CompensationRequest.class);
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (CompensationRequest compensationRequest : requests) {
            compensationRequest.validateAndThrow();
        }
    }

    public List<BigInteger> getElemeOrderIds() {
        List<BigInteger> elemeOrderIds = new ArrayList<BigInteger>();
        for (CompensationRequest compensationRequest : requests) {
            elemeOrderIds.add(compensationRequest.getElemeOrderId());
        }
        return elemeOrderIds;
    }

    public static class CompensationRequest extends BasicModel {
        @NotNull
        private BigInteger elemeOrderId;

        @InList(value = {"TAKE_ORDER_TIMEOUT", "DELIVERY_NOT_FINISHED", "DRIVER_SUBMIT_BEFORE_FINISHED", "FOOD_LOOKS_NOT_WELL", "DRIVER_LOOKS_NOT_WELL", "ADVISED_TO_CANCEL_BY_DRIVER", "DRIVER_ASK_FOR_ADDITIONAL_COST", "DRIVER_WRITE_WRONG_EXCEPTION_REASON", "DELIVERY_BY_SELF", "SYSTEM_ERROR", "DELIVERY_TIMEOUT"})
        private String reason;

        @NotNull
        private String description;

        public BigInteger getElemeOrderId() {
            return elemeOrderId;
        }

        public void setElemeOrderId(BigInteger elemeOrderId) {
            this.elemeOrderId = elemeOrderId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
