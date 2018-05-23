package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.exceptions.CanNotDeleteException;
import build.dream.catering.exceptions.CanNotEditAndDeleteException;
import build.dream.catering.exceptions.CanNotEditException;
import build.dream.catering.mappers.CanNotOperateReasonMapper;
import build.dream.catering.utils.DatabaseHelper;
import build.dream.common.erp.catering.domains.CanNotOperateReason;
import build.dream.common.utils.SearchModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

public class BasicService {
    @Autowired
    private CanNotOperateReasonMapper canNotOperateReasonMapper;

    public void validateCanNotOperate(BigInteger tenantId, BigInteger branchId, String tableName, BigInteger tableId, int operateType) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("table_name", Constants.SQL_OPERATION_SYMBOL_EQUAL, tableName);
        searchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tableId);
        searchModel.addSearchCondition("operate_type", Constants.SQL_OPERATION_SYMBOL_IN, new int[]{operateType, 3});
        CanNotOperateReason canNotOperateReason = DatabaseHelper.find(CanNotOperateReason.class, searchModel);
        if (canNotOperateReason != null) {
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
    }

    public void deleteCanNotOperateReason(BigInteger tenantId, BigInteger branchId, String causeTableName, BigInteger causeTableId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition("cause_table_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, causeTableId);
        searchModel.addSearchCondition("cause_table_name", Constants.SQL_OPERATION_SYMBOL_EQUAL, causeTableName);
        canNotOperateReasonMapper.delete(searchModel);
    }
}
