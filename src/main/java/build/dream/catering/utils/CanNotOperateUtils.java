package build.dream.catering.utils;

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
        CanNotOperateReason canNotOperateReason = CanNotOperateReason.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .tableId(tableId)
                .tableName(tableName)
                .causeTableId(causeTableId)
                .causeTableName(causeTableName)
                .operateType(operateType)
                .reason(reason)
                .build();
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
        }

        if (persistenceOperateType == 2) {
            throw new CanNotEditException(reason);
        }

        if (persistenceOperateType == 3) {
            throw new CanNotEditAndDeleteException(String.format(reason, operateType == 1 ? "编辑" : "删除"));
        }
    }

    public static void deleteCanNotOperateReason(BigInteger tenantId, BigInteger branchId, String causeTableName, BigInteger causeTableId) {
        DeleteModel deleteModel = DeleteModel.builder()
                .autoSetDeletedFalse()
                .equal(CanNotOperateReason.ColumnName.TENANT_ID, tenantId)
                .equal(CanNotOperateReason.ColumnName.BRANCH_ID, branchId)
                .equal(CanNotOperateReason.ColumnName.CAUSE_TABLE_ID, causeTableId)
                .equal(CanNotOperateReason.ColumnName.TABLE_NAME, causeTableName)
                .build();
        DatabaseHelper.delete(CanNotOperateReason.class, deleteModel);
    }
}
