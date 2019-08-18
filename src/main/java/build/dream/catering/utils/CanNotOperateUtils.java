package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.exceptions.CanNotDeleteException;
import build.dream.catering.exceptions.CanNotEditAndDeleteException;
import build.dream.catering.exceptions.CanNotEditException;
import build.dream.common.domains.catering.CanNotOperateReason;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.DeleteModel;
import build.dream.common.utils.SearchModel;

import java.math.BigInteger;
import java.util.Objects;

public class CanNotOperateUtils {
    public static CanNotOperateReason buildCanNotOperateReason(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger tableId, String tableName, BigInteger causeTableId, String causeTableName, Integer operateType, String reason) {
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

    public static void validateCanNotOperate(BigInteger tenantId, BigInteger branchId, String tableName, BigInteger tableId, int operateType) {
        SearchModel searchModel = SearchModel.builder()
                .equal(CanNotOperateReason.ColumnName.TENANT_ID, tenantId)
                .equal(CanNotOperateReason.ColumnName.BRANCH_ID, branchId)
                .equal(CanNotOperateReason.ColumnName.TABLE_NAME, tableName)
                .equal(CanNotOperateReason.ColumnName.TABLE_ID, tableId)
                .in(CanNotOperateReason.ColumnName.OPERATE_TYPE, new int[]{operateType, 3})
                .build();
        CanNotOperateReason canNotOperateReason = DatabaseHelper.find(CanNotOperateReason.class, searchModel);
        if (Objects.isNull(canNotOperateReason)) {
            return;
        }

        int persistenceOperateType = canNotOperateReason.getOperateType();
        String reason = canNotOperateReason.getReason();
        if (persistenceOperateType == 1) {
            throw new CanNotDeleteException(reason);
        } else if (persistenceOperateType == 2) {
            throw new CanNotEditException(reason);
        } else if (persistenceOperateType == 3) {
            throw new CanNotEditAndDeleteException(String.format(reason, operateType == 1 ? "编辑" : "删除"));
        }
    }

    public static void deleteCanNotOperateReason(BigInteger tenantId, BigInteger branchId, String causeTableName, BigInteger causeTableId) {
        DeleteModel deleteModel = new DeleteModel();
        deleteModel.addSearchCondition(CanNotOperateReason.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        deleteModel.addSearchCondition(CanNotOperateReason.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        deleteModel.addSearchCondition(CanNotOperateReason.ColumnName.CAUSE_TABLE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, causeTableId);
        deleteModel.addSearchCondition(CanNotOperateReason.ColumnName.CAUSE_TABLE_NAME, Constants.SQL_OPERATION_SYMBOL_EQUAL, causeTableName);
        DatabaseHelper.delete(CanNotOperateReason.class, deleteModel);
    }
}
