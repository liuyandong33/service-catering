package build.dream.catering.models.dietorder;

import build.dream.common.models.BasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class DoPayCombinedModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private BigInteger branchId;

    @NotNull
    private BigInteger vipId;

    @NotNull
    private BigInteger dietOrderId;

    @NotEmpty
    private List<PaymentInfo> paymentInfos;

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

    public BigInteger getVipId() {
        return vipId;
    }

    public void setVipId(BigInteger vipId) {
        this.vipId = vipId;
    }

    public BigInteger getDietOrderId() {
        return dietOrderId;
    }

    public void setDietOrderId(BigInteger dietOrderId) {
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
        private BigDecimal paidAmount;

        @NotNull
        private Integer paidScene;

        public String getPaymentCode() {
            return paymentCode;
        }

        public void setPaymentCode(String paymentCode) {
            this.paymentCode = paymentCode;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
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
