package build.dream.catering.beans;

import build.dream.common.catering.domains.DietOrderDetail;

import java.math.BigInteger;
import java.util.List;

public class PackageGroupDietOrderDetail {
    private BigInteger packageId;
    private BigInteger packageGroupId;
    private String packageGroupName;
    private List<DietOrderDetail> dietOrderDetails;

    public BigInteger getPackageId() {
        return packageId;
    }

    public void setPackageId(BigInteger packageId) {
        this.packageId = packageId;
    }

    public BigInteger getPackageGroupId() {
        return packageGroupId;
    }

    public void setPackageGroupId(BigInteger packageGroupId) {
        this.packageGroupId = packageGroupId;
    }

    public String getPackageGroupName() {
        return packageGroupName;
    }

    public void setPackageGroupName(String packageGroupName) {
        this.packageGroupName = packageGroupName;
    }

    public List<DietOrderDetail> getDietOrderDetails() {
        return dietOrderDetails;
    }

    public void setDietOrderDetails(List<DietOrderDetail> dietOrderDetails) {
        this.dietOrderDetails = dietOrderDetails;
    }
}
