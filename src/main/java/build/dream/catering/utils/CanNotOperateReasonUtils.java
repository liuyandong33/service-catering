package build.dream.catering.utils;

import build.dream.common.catering.domains.CanNotOperateReason;

import java.math.BigInteger;

public class CanNotOperateReasonUtils {
    public static CanNotOperateReason constructCanNotOperateReason(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger tableId, String tableName, BigInteger causeTableId, String causeTableName, Integer operateType, String reason) {
        CanNotOperateReason canNotOperateReason = new CanNotOperateReason();
        canNotOperateReason.setTenantId(tenantId);
        canNotOperateReason.setTenantCode(tenantCode);
        canNotOperateReason.setBranchId(branchId);
        canNotOperateReason.setTableId(tableId);
        canNotOperateReason.setTableName(tableName);
        canNotOperateReason.setCauseTableId(causeTableId);
        canNotOperateReason.setCauseTableName(causeTableName);
        canNotOperateReason.setOperateType(operateType);
        canNotOperateReason.setReason(reason);
        return canNotOperateReason;
    }
}
