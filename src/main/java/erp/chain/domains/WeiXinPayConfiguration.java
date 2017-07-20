package erp.chain.domains;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuyandong on 2017/7/20.
 */
public class WeiXinPayConfiguration {
    private BigInteger id;
    private BigInteger tenantId;
    private BigInteger branchId;
    private String weiXinPayAppId;
    private String weiXinPayMchId;
    private String weiXinPayKey;
    private String weiXinPaySubAppId;
    private String weiXinPaySubMchId;
    private Date createTime;
    private BigInteger createUserId;
    private Date lastUpdateTime;
    private BigInteger lastUpdateUserId;
    private boolean deleted;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public String getWeiXinPayAppId() {
        return weiXinPayAppId;
    }

    public void setWeiXinPayAppId(String weiXinPayAppId) {
        this.weiXinPayAppId = weiXinPayAppId;
    }

    public String getWeiXinPayMchId() {
        return weiXinPayMchId;
    }

    public void setWeiXinPayMchId(String weiXinPayMchId) {
        this.weiXinPayMchId = weiXinPayMchId;
    }

    public String getWeiXinPayKey() {
        return weiXinPayKey;
    }

    public void setWeiXinPayKey(String weiXinPayKey) {
        this.weiXinPayKey = weiXinPayKey;
    }

    public String getWeiXinPaySubAppId() {
        return weiXinPaySubAppId;
    }

    public void setWeiXinPaySubAppId(String weiXinPaySubAppId) {
        this.weiXinPaySubAppId = weiXinPaySubAppId;
    }

    public String getWeiXinPaySubMchId() {
        return weiXinPaySubMchId;
    }

    public void setWeiXinPaySubMchId(String weiXinPaySubMchId) {
        this.weiXinPaySubMchId = weiXinPaySubMchId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigInteger getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(BigInteger createUserId) {
        this.createUserId = createUserId;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public BigInteger getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(BigInteger lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
