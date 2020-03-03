package build.dream.catering.models.user;

import build.dream.common.models.CateringBasicModel;

import javax.validation.constraints.NotNull;

public class AddUserModel extends CateringBasicModel {
    @NotNull
    Long branchId;

    @NotNull
    private String name;

    @NotNull
    private String mobile;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Boolean enabled;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
