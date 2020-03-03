package build.dream.catering.models.meituan;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class DeliveringOrderModel extends CateringBasicModel {
    @NotNull
    private Long dietOrderId;

    private String courierName;

    private String courierPhone;

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierPhone() {
        return courierPhone;
    }

    public void setCourierPhone(String courierPhone) {
        this.courierPhone = courierPhone;
    }
}
