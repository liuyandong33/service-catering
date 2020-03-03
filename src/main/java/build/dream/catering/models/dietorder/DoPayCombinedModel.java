package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DoPayCombinedModel extends BasicModel {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long branchId;

    @NotNull
    private Long vipId;

    @NotNull
    private Long dietOrderId;

    @NotEmpty
    private List<PaymentInfo> paymentInfos;

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

    public Long getVipId() {
        return vipId;
    }

    public void setVipId(Long vipId) {
        this.vipId = vipId;
    }

    public Long getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(Long dietOrderId) {
        this.dietOrderId = dietOrderId;
    }

    public List<PaymentInfo> getPaymentInfos() {
        return paymentInfos;
    }

    public void setPaymentInfos(List<PaymentInfo> paymentInfos) {
        this.paymentInfos = paymentInfos;
    }

    public static class PaymentInfo extends BasicModel {
        @NotNull
        private String paymentCode;

        @NotNull
        private Double paidAmount;

        @NotNull
        private Integer paidScene;

        public String getPaymentCode() {
            return paymentCode;
        }

        public void setPaymentCode(String paymentCode) {
            this.paymentCode = paymentCode;
        }

        public Double getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(Double paidAmount) {
            this.paidAmount = paidAmount;
        }

        public Integer getPaidScene() {
            return paidScene;
        }

        public void setPaidScene(Integer paidScene) {
            this.paidScene = paidScene;
        }
    }

    public static class WeiXinPaymentInfo extends PaymentInfo {
        @Length(max = 128)
        private String openId;

        @Length(max = 128)
        private String subOpenId;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getSubOpenId() {
            return subOpenId;
        }

        public void setSubOpenId(String subOpenId) {
            this.subOpenId = subOpenId;
        }
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (PaymentInfo paymentInfo : paymentInfos) {
            ApplicationHandler.isTrue(paymentInfo.validate(), "paymentInfos");
        }
    }
}
