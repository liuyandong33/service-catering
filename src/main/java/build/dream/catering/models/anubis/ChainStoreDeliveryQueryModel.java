package build.dream.catering.models.anubis;

import build.dream.common.models.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ChainStoreDeliveryQueryModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    @Length(max = 16)
    private String receiverLongitude;

    @NotNull
    @Length(max = 16)
    private String receiverLatitude;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getReceiverLongitude() {
        return receiverLongitude;
    }

    public void setReceiverLongitude(String receiverLongitude) {
        this.receiverLongitude = receiverLongitude;
    }

    public String getReceiverLatitude() {
        return receiverLatitude;
    }

    public void setReceiverLatitude(String receiverLatitude) {
        this.receiverLatitude = receiverLatitude;
    }
}
