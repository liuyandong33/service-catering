package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dada.SyncShopModel;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.Branch;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DadaService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest syncShop(SyncShopModel syncShopModel) {
        BigInteger tenantId = syncShopModel.obtainTenantId();
        List<BigInteger> branchIds = syncShopModel.getBranchIds();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, branchIds)
                .build();
        List<Branch> branches = DatabaseHelper.findAll(Branch.class, searchModel);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Branch branch : branches) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("station_name", branch.getName());
            map.put("business", 1);
            map.put("city_name", branch.getCityName());
            map.put("area_name", branch.getDistrictName());
            map.put("station_address", branch.getAddress());
            map.put("lng", Double.valueOf(branch.getLongitude()));
            map.put("lat", Double.valueOf(branch.getLatitude()));
            map.put("contact_name", branch.getLinkman());
            map.put("phone", branch.getContactPhone());
            map.put("origin_shop_id", tenantId + "Z" + branch.getId());
            list.add(map);
        }

        return ApiRest.builder().message("同步门店成功！").successful(true).build();
    }
}
