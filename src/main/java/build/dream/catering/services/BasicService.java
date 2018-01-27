package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.exceptions.CanNotDeleteException;
import build.dream.catering.mappers.CanNotDeleteReasonMapper;
import build.dream.common.erp.catering.domains.CanNotDeleteReason;
import build.dream.common.utils.SearchModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

public class BasicService {
    @Autowired
    private CanNotDeleteReasonMapper canNotDeleteReasonMapper;

    public void validateCanBeDelete(BigInteger tenantId, BigInteger branchId, String tableName, BigInteger tableId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("table_name", Constants.SQL_OPERATION_SYMBOL_EQUALS, tableName);
        searchModel.addSearchCondition("table_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tableId);
        CanNotDeleteReason canNotDeleteReason = canNotDeleteReasonMapper.find(searchModel);
        if (canNotDeleteReason != null) {
            throw new CanNotDeleteException(canNotDeleteReason.getReason());
        }
    }

    public void deleteCanNotDeleteReason(BigInteger tenantId, BigInteger branchId, String causeTableName, BigInteger causeTableId) {
        SearchModel searchModel = new SearchModel();
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("cause_table_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, causeTableId);
        searchModel.addSearchCondition("cause_table_name", Constants.SQL_OPERATION_SYMBOL_EQUALS, causeTableName);
        canNotDeleteReasonMapper.delete(searchModel);
    }
}
