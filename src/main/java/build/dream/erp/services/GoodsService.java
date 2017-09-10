package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Goods;
import build.dream.common.erp.domains.GoodsFlavor;
import build.dream.common.erp.domains.GoodsSpecification;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.SearchModel;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.GoodsFlavorMapper;
import build.dream.erp.mappers.GoodsMapper;
import build.dream.erp.mappers.GoodsSpecificationMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;

    @Transactional(readOnly = true)
    public ApiRest listGoodses(Map<String, String> parameters) {
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(parameters.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(parameters.get("branchId")));
        String tenantCode = parameters.get("tenantCode");
        PagedSearchModel goodsPagedSearchModel = new PagedSearchModel(true);
        goodsPagedSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsPagedSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        String page = parameters.get("page");
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        String rows = parameters.get("rows");
        if (StringUtils.isBlank(rows)) {
            rows = "20";
        }
        goodsPagedSearchModel.setOffsetAndMaxResults(Integer.valueOf(page), Integer.valueOf(rows));
        List<Goods> goodses = goodsMapper.findAllPaged("goods_" + tenantCode, goodsPagedSearchModel);
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (Goods goods : goodses) {
            goodsIds.add(goods.getId());
        }

        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsSpecificationSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll("goods_specification_" + tenantCode, goodsSpecificationSearchModel);

        Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = new HashMap<BigInteger, List<GoodsSpecification>>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            List<GoodsSpecification> goodsSpecificationList = goodsSpecificationMap.get(goodsSpecification.getGoodsId());
            if (goodsSpecificationList == null) {
                goodsSpecificationList = new ArrayList<GoodsSpecification>();
                goodsSpecificationMap.put(goodsSpecification.getGoodsId(), goodsSpecificationList);
            }
            goodsSpecificationList.add(goodsSpecification);
        }

        SearchModel goodsFlavorSearchModel = new SearchModel(true);
        goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsFlavorSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.findAll("goods_flavor_" + tenantCode, goodsFlavorSearchModel);

        Map<BigInteger, List<GoodsFlavor>> goodsFlavorMap = new HashMap<BigInteger, List<GoodsFlavor>>();
        for (GoodsFlavor goodsFlavor : goodsFlavors) {
            List<GoodsFlavor> goodsFlavorList = goodsFlavorMap.get(goodsFlavor.getGoodsId());
            if (goodsFlavorList == null) {
                goodsFlavorList = new ArrayList<GoodsFlavor>();
                goodsFlavorMap.put(goodsFlavor.getGoodsId(), goodsFlavorList);
            }
            goodsFlavorList.add(goodsFlavor);
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Goods goods : goodses) {
            Map<String, Object> goodsInfoMap = new LinkedHashMap<String, Object>();
            goodsInfoMap.put("goods", goods);
            goodsInfoMap.put("goodsSpecifications", goodsSpecificationMap.get(goods.getId()));
            goodsInfoMap.put("goodsFlavors", goodsFlavorMap.get(goods.getId()));
            data.add(goodsInfoMap);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setSuccessful(true);
        apiRest.setMessage("查询菜品信息成功！");
        return apiRest;
    }
}
