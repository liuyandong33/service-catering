package build.dream.catering.models.anubis;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class OrderComplaintModel extends CateringBasicModel {
    @NotNull
    private Long orderId;

    @NotNull
    private Integer orderComplaintCode;

    @Length(max = 128)
    private String orderComplaintDesc;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderComplaintCode() {
        return orderComplaintCode;
    }

    public void setOrderComplaintCode(Integer orderComplaintCode) {
        this.orderComplaintCode = orderComplaintCode;
    }

    public String getOrderComplaintDesc() {
        return orderComplaintDesc;
    }

    public void setOrderComplaintDesc(String orderComplaintDesc) {
        this.orderComplaintDesc = orderComplaintDesc;
    }
}
