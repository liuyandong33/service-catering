package build.dream.catering.beans;

import build.dream.common.domains.catering.DietOrderDetail;

import java.util.List;

public class PackageGroupDietOrderDetail {
    private Long packageId;
    private Long packageGroupId;
    private String packageGroupName;
    private List<DietOrderDetail> dietOrderDetails;

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getPackageGroupId() {
        return packageGroupId;
    }

    public void setPackageGroupId(Long packageGroupId) {
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
