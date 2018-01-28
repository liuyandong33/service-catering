package build.dream.catering.utils;

import build.dream.common.erp.catering.domains.CanNotDeleteReason;

import java.math.BigInteger;

public class CanNotDeleteReasonUtils {
    public static CanNotDeleteReason constructCanNotDeleteReason(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger tableId, String tableName, BigInteger causeTableId, String causeTableName, String reason) {
        CanNotDeleteReason canNotDeleteReason = new CanNotDeleteReason();
        canNotDeleteReason.setTenantId(tenantId);
        canNotDeleteReason.setTenantCode(tenantCode);
        canNotDeleteReason.setBranchId(branchId);
        canNotDeleteReason.setTableId(tableId);
        canNotDeleteReason.setTableName(tableName);
        canNotDeleteReason.setCauseTableId(causeTableId);
        canNotDeleteReason.setCauseTableName(causeTableName);
        canNotDeleteReason.setReason(reason);
        return canNotDeleteReason;
    }
}
