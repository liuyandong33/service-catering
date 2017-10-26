package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.erp.domains.Goods;
import build.dream.common.erp.domains.GoodsFlavor;
import build.dream.common.erp.domains.GoodsFlavorGroup;
import build.dream.common.erp.domains.GoodsSpecification;
import build.dream.common.utils.BeanUtils;
import build.dream.common.utils.PagedSearchModel;
import build.dream.common.utils.SearchModel;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.GoodsFlavorGroupMapper;
import build.dream.erp.mappers.GoodsFlavorMapper;
import build.dream.erp.mappers.GoodsMapper;
import build.dream.erp.mappers.GoodsSpecificationMapper;
import build.dream.erp.models.goods.GoodsFlavorGroupModel;
import build.dream.erp.models.goods.GoodsFlavorModel;
import build.dream.erp.models.goods.SaveGoodsModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private GoodsFlavorGroupMapper goodsFlavorGroupMapper;
    @Autowired
    private GoodsFlavorMapper goodsFlavorMapper;

    @Transactional(readOnly = true)
    public ApiRest listGoodses(Map<String, String> parameters) {
        BigInteger tenantId = BigInteger.valueOf(Long.valueOf(parameters.get("tenantId")));
        BigInteger branchId = BigInteger.valueOf(Long.valueOf(parameters.get("branchId")));
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
        List<Goods> goodses = goodsMapper.findAllPaged(goodsPagedSearchModel);
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (Goods goods : goodses) {
            goodsIds.add(goods.getId());
        }

        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsSpecificationSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);

        Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = new HashMap<BigInteger, List<GoodsSpecification>>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            List<GoodsSpecification> goodsSpecificationList = goodsSpecificationMap.get(goodsSpecification.getGoodsId());
            if (goodsSpecificationList == null) {
                goodsSpecificationList = new ArrayList<GoodsSpecification>();
                goodsSpecificationMap.put(goodsSpecification.getGoodsId(), goodsSpecificationList);
            }
            goodsSpecificationList.add(goodsSpecification);
        }

        SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
        goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsFlavorGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsFlavorGroup> goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);

        List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
        for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
            goodsFlavorGroupIds.add(goodsFlavorGroup.getId());
        }

        SearchModel goodsFlavorSearchModel = new SearchModel(true);
        goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        goodsFlavorSearchModel.addSearchCondition("goods_flavor_group_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
        List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.findAll(goodsFlavorSearchModel);

        Map<BigInteger, List<GoodsFlavor>> goodsFlavorMap = new HashMap<BigInteger, List<GoodsFlavor>>();
        for (GoodsFlavor goodsFlavor : goodsFlavors) {
            List<GoodsFlavor> goodsFlavorList = goodsFlavorMap.get(goodsFlavor.getGoodsFlavorGroupId());
            if (goodsFlavorList == null) {
                goodsFlavorList = new ArrayList<GoodsFlavor>();
                goodsFlavorMap.put(goodsFlavor.getGoodsFlavorGroupId(), goodsFlavorList);
            }
            goodsFlavorList.add(goodsFlavor);
        }

        Map<BigInteger, List<Map<String, Object>>> goodsFlavorGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
            List<Map<String, Object>> goodsFlavorGroupList = goodsFlavorGroupMap.get(goodsFlavorGroup.getGoodsId());
            if (goodsFlavorGroupList == null) {
                goodsFlavorGroupList = new ArrayList<Map<String, Object>>();
                goodsFlavorGroupMap.put(goodsFlavorGroup.getGoodsId(), goodsFlavorGroupList);
            }
            Map<String, Object> map = BeanUtils.beanToMap(goodsFlavorGroup);
            map.put("goodsFlavors", goodsFlavorMap.get(goodsFlavorGroup.getId()));
            goodsFlavorGroupList.add(map);
        }

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Goods goods : goodses) {
            Map<String, Object> goodsMap = BeanUtils.beanToMap(goods);
            goodsMap.put("goodsSpecifications", goodsSpecificationMap.get(goods.getId()));
            goodsMap.put("goodsFlavorGroups", goodsFlavorGroupMap.get(goods.getId()));
            data.add(goodsMap);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setSuccessful(true);
        apiRest.setMessage("查询菜品信息成功！");
        return apiRest;
    }

    @Transactional
    public ApiRest saveGoods(SaveGoodsModel saveGoodsModel) {
        BigInteger tenantId = saveGoodsModel.getTenantId();
        BigInteger branchId = saveGoodsModel.getBranchId();
        BigInteger userId = saveGoodsModel.getUserId();
        String tenantCode = saveGoodsModel.getTenantCode();

        if (saveGoodsModel.getId() != null) {
            SearchModel goodsSearchModel = new SearchModel(true);
            goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveGoodsModel.getId());
            Goods goods = goodsMapper.find(goodsSearchModel);
            Validate.notNull(goods, "产品不存在！");

            goods.setName(saveGoodsModel.getName());
            goods.setLastUpdateRemark("修改产品信息！");
            goodsMapper.update(goods);
        } else {
            Goods goods = new Goods();
            goods.setName(saveGoodsModel.getName());
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setCreateUserId(userId);
            goods.setLastUpdateUserId(userId);
            goods.setLastUpdateRemark("新增产品信息！");
            goodsMapper.insert(goods);

            Map<Integer, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<Integer, GoodsFlavorGroup>();
            Map<GoodsFlavor, Integer> goodsFlavorMap = new HashMap<GoodsFlavor, Integer>();
            for (GoodsFlavorGroupModel goodsFlavorGroupModel : saveGoodsModel.getGoodsFlavorGroupModels()) {
                GoodsFlavorGroup goodsFlavorGroup = new GoodsFlavorGroup();
                goodsFlavorGroup.setGoodsId(goods.getId());
                goodsFlavorGroup.setName(goodsFlavorGroupModel.getName());
                goodsFlavorGroup.setTenantId(tenantId);
                goodsFlavorGroup.setBranchId(branchId);
                goodsFlavorGroup.setTenantCode(tenantCode);
                goodsFlavorGroup.setLastUpdateRemark("新增菜品口味组！");

                goodsFlavorGroupMap.put(goodsFlavorGroup.hashCode(), goodsFlavorGroup);

                for (GoodsFlavorModel goodsFlavorModel : goodsFlavorGroupModel.getGoodsFlavorModels()) {
                    GoodsFlavor goodsFlavor = new GoodsFlavor();
                    goodsFlavor.setName(goodsFlavorModel.getName());
                    goodsFlavor.setPrice(goodsFlavorModel.getPrice());
                    goodsFlavor.setTenantId(tenantId);
                    goodsFlavor.setTenantCode(tenantCode);
                    goodsFlavor.setBranchId(branchId);
                    goodsFlavor.setCreateUserId(userId);
                    goodsFlavor.setLastUpdateUserId(userId);
                    goodsFlavor.setLastUpdateRemark("新增菜品口味！");
                    goodsFlavorMap.put(goodsFlavor, goodsFlavorGroup.hashCode());
                }
            }

            for (Map.Entry<GoodsFlavor, Integer> entry : goodsFlavorMap.entrySet()) {
                entry.getKey().setGoodsFlavorGroupId(goodsFlavorGroupMap.get(entry.getValue()).getId());
            }

            goodsFlavorGroupMapper.insertAll(goodsFlavorGroupMap.values());
            goodsFlavorMapper.insertAll(goodsFlavorMap.keySet());
        }
        return null;
    }
}
