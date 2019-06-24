package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.dada.SignedDadaModel;
import build.dream.catering.models.dada.SyncShopModel;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.Branch;
import build.dream.common.models.data.AddMerchantModel;
import build.dream.common.models.data.AddShopModel;
import build.dream.common.models.data.DadaCommonParamsModel;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.*;
import org.apache.commons.collections.MapUtils;
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

        Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
        BigInteger dadaSourceId = tenant.getDadaSourceId();
        ValidateUtils.isTrue(dadaSourceId.compareTo(BigInteger.ZERO) != 0, "未开通达达配送！");

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, branchIds)
                .build();
        List<Branch> branches = DatabaseHelper.findAll(Branch.class, searchModel);
        List<AddShopModel> addShopModels = new ArrayList<AddShopModel>();

        DadaCommonParamsModel dadaCommonParamsModel = DadaCommonParamsModel.builder()
                .sourceId(dadaSourceId.toString())
                .build();

        Map<String, Branch> branchMap = new HashMap<String, Branch>();
        for (Branch branch : branches) {
            String originShopId = tenantId + "Z" + branch.getId();
            AddShopModel addShopModel = AddShopModel.builder()
                    .stationName(branch.getName())
                    .business(1)
                    .cityName(branch.getCityName())
                    .areaName(branch.getDistrictName())
                    .stationAddress(branch.getAddress())
                    .lng(Double.valueOf(branch.getLongitude()))
                    .lat(Double.valueOf(branch.getLatitude()))
                    .contactName(branch.getLinkman())
                    .phone(branch.getContactPhone())
                    .originShopId(originShopId)
                    .build();
            addShopModels.add(addShopModel);
            branchMap.put(originShopId, branch);
        }
        Map<String, Object> addShopResult = DadaUtils.addShop(dadaCommonParamsModel, addShopModels);
        Map<String, Object> result = MapUtils.getMap(addShopResult, "result");
        List<Map<String, Object>> successList = (List<Map<String, Object>>) result.get("successList");

        for (Map<String, Object> map : successList) {
            String originShopId = MapUtils.getString(map, "originShopId");
            Branch branch = branchMap.get(originShopId);
            branch.setDadaOriginShopId(originShopId);
            DatabaseHelper.update(branch);
        }

        return ApiRest.builder().message("同步门店成功！").successful(true).build();
    }

    /**
     * 签约达达配送
     *
     * @param signedDadaModel
     * @return
     */
    public ApiRest signedDada(SignedDadaModel signedDadaModel) {
        BigInteger tenantId = signedDadaModel.getTenantId();
        Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);

        AddMerchantModel addMerchantModel = AddMerchantModel.builder()
                .mobile("")
                .cityName("")
                .enterpriseName(tenant.getName())
                .enterpriseAddress("")
                .contactName("")
                .contactPhone("")
                .build();

        Map<String, Object> result = DadaUtils.addMerchant(addMerchantModel);
        String dadaSourceId = MapUtils.getString(result, "result");
        TenantUtils.updateTenantInfo(tenant.getId(), TupleUtils.buildTuple2("dadaSourceId", dadaSourceId));

        return ApiRest.builder().message("签约达达配送成功！").successful(true).build();
    }
}
