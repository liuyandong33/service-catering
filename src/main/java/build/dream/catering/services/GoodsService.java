package build.dream.catering.services;

import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.goods.*;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.UpdateModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
    @Autowired
    private PackageGroupMapper packageGroupMapper;
    @Autowired
    private PackageGroupGoodsMapper packageGroupGoodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private UniversalMapper universalMapper;

    private static final String SELECT_GOODS_TABLE_COLUMN_NAMES = StringUtils.join(new String[]{"goods.id", "goods.name", "goods.tenant_id", "goods.tenant_code", "goods.branch_id", "goods.goods_type", "goods.category_id"}, ", ");

    @Transactional(readOnly = true)
    public ApiRest listGoodses(ListGoodsesModel listGoodsesModel) {
        BigInteger tenantId = listGoodsesModel.getTenantId();
        BigInteger branchId = listGoodsesModel.getBranchId();
        String queryGoodsInfosSql = "SELECT " + SELECT_GOODS_TABLE_COLUMN_NAMES + ", goods_category.name AS category_name, goods_category.description FROM goods LEFT OUTER JOIN goods_category ON goods_category.id = goods.category_id WHERE goods.tenant_id = #{tenantId} AND goods.branch_id = #{branchId} AND goods.deleted = 0";
        Map<String, Object> queryGoodsInfosParameters = new HashMap<String, Object>();
        queryGoodsInfosParameters.put("sql", queryGoodsInfosSql);
        queryGoodsInfosParameters.put("tenantId", tenantId);
        queryGoodsInfosParameters.put("branchId", branchId);
        List<Map<String, Object>> goodsInfos = universalMapper.executeQuery(queryGoodsInfosParameters);

        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
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
            List<GoodsFlavorGroup> goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);

            Map<BigInteger, List<Map<String, Object>>> goodsFlavorGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            if (CollectionUtils.isNotEmpty(goodsFlavorGroups)) {
                SearchModel goodsFlavorSearchModel = new SearchModel(true);
                goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
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
                goodsFlavorGroupMap = buildFlavorGroups(goodsFlavorGroups, goodsFlavorMap);
            }

            for (Map<String, Object> goodsInfo : goodsInfos) {
                BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id"));
                goodsInfo.put("goodsSpecifications", buildGoodsSpecificationInfos(goodsSpecificationMap.get(goodsId)));
                goodsInfo.put("flavorGroups", goodsFlavorGroupMap.get(goodsId));
            }
        }
        return new ApiRest(goodsInfos, "查询菜品信息成功！");
    }

    public List<Map<String, Object>> buildGoodsSpecificationInfos(List<GoodsSpecification> goodsSpecifications) {
        List<Map<String, Object>> goodsSpecificationInfos = new ArrayList<Map<String, Object>>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            Map<String, Object> goodsSpecificationInfo = new HashMap<String, Object>();
            goodsSpecificationInfo.put("id", goodsSpecification.getId());
            goodsSpecificationInfo.put("name", goodsSpecification.getName());
            goodsSpecificationInfo.put("price", goodsSpecification.getPrice());
            goodsSpecificationInfos.add(goodsSpecificationInfo);
        }
        return goodsSpecificationInfos;
    }

    public Map<BigInteger, List<Map<String, Object>>> buildFlavorGroups(List<GoodsFlavorGroup> goodsFlavorGroups, Map<BigInteger, List<GoodsFlavor>> goodsFlavorMap) {
        Map<BigInteger, List<Map<String, Object>>> flavorGroups = new HashMap<BigInteger, List<Map<String, Object>>>();
        for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
            Map<String, Object> goodsFlavorGroupInfo = new HashMap<String, Object>();
            goodsFlavorGroupInfo.put("id", goodsFlavorGroup.getId());
            goodsFlavorGroupInfo.put("name", goodsFlavorGroup.getName());

            List<Map<String, Object>> goodsFlavorInfos = new ArrayList<Map<String, Object>>();
            List<GoodsFlavor> goodsFlavors = goodsFlavorMap.get(goodsFlavorGroup.getGoodsId());
            for (GoodsFlavor goodsFlavor : goodsFlavors) {
                Map<String, Object> goodsFlavorInfo = new HashMap<String, Object>();
                goodsFlavorInfo.put("id", goodsFlavor.getId());
                goodsFlavorInfo.put("name", goodsFlavor.getName());
                goodsFlavorInfo.put("price", goodsFlavor.getPrice());
                goodsFlavorInfos.add(goodsFlavorInfo);
            }
            goodsFlavorGroupInfo.put("flavors", goodsFlavorInfos);
            List<Map<String, Object>> goodsFlavorGroupInfos = flavorGroups.get(goodsFlavorGroup.getGoodsId());
            if (goodsFlavorGroupInfos == null) {
                goodsFlavorGroupInfos = new ArrayList<Map<String, Object>>();
                flavorGroups.put(goodsFlavorGroup.getGoodsId(), goodsFlavorGroupInfos);
            }
            goodsFlavorGroupInfos.add(goodsFlavorGroupInfo);
        }
        return flavorGroups;
    }

    /**
     * 保存菜品信息
     *
     * @param saveGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveGoods(SaveGoodsModel saveGoodsModel) {
        return new ApiRest();
    }

    /**
     * 删除菜品规格
     * @param deleteGoodsSpecificationModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteGoodsSpecification(DeleteGoodsSpecificationModel deleteGoodsSpecificationModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsSpecificationModel.getGoodsSpecificationId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsSpecificationModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsSpecificationModel.getBranchId());
        GoodsSpecification goodsSpecification = goodsSpecificationMapper.find(searchModel);
        Validate.notNull(goodsSpecification, "菜品规格不存在！");
        goodsSpecification.setDeleted(true);
        goodsSpecification.setLastUpdateUserId(deleteGoodsSpecificationModel.getUserId());
        goodsSpecification.setLastUpdateRemark("删除菜品规格信息！");
        goodsSpecificationMapper.update(goodsSpecification);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除菜品规格成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 保存套餐
     * @param savePackageModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePackage(SavePackageModel savePackageModel) {
        return new ApiRest();
    }

    @Transactional(readOnly = true)
    public ApiRest listCategories(ListCategoriesModel listCategoriesModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listCategoriesModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, listCategoriesModel.getBranchId());
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findAll(searchModel);

        List<ZTreeNode> zTreeNodes = new ArrayList<ZTreeNode>();
        for (GoodsCategory goodsCategory : goodsCategories) {
            zTreeNodes.add(new ZTreeNode(goodsCategory.getId().toString(), goodsCategory.getName(), goodsCategory.getParentId().toString()));
        }
        return new ApiRest(zTreeNodes, "查询菜品分类成功！");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteGoods(DeleteGoodsModel deleteGoodsModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getGoodsId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getBranchId());
        Goods goods = goodsMapper.find(searchModel);
        Validate.notNull(goods, "菜品不存在！");
        goods.setLastUpdateUserId(deleteGoodsModel.getUserId());
        goods.setLastUpdateRemark("删除菜品信息！");
        goods.setDeleted(true);
        goodsMapper.update(goods);

        // 删除该菜品的所有规格
        UpdateModel goodsSpecificationUpdateModel = new UpdateModel(true);
        goodsSpecificationUpdateModel.setTableName("goods_specification");
        goodsSpecificationUpdateModel.addContentValue("deleted", 1);
        goodsSpecificationUpdateModel.addContentValue("last_update_user_id", deleteGoodsModel.getUserId());
        goodsSpecificationUpdateModel.addContentValue("last_update_remark", "删除菜品信息！");
        goodsSpecificationUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getGoodsId());
        goodsSpecificationUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getTenantId());
        goodsSpecificationUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getBranchId());
        universalMapper.universalUpdate(goodsSpecificationUpdateModel);

        // 删除该菜品的所有口味组
        UpdateModel goodsFlavorGroupUpdateModel = new UpdateModel(true);
        goodsFlavorGroupUpdateModel.setTableName("goods_flavor_group");
        goodsFlavorGroupUpdateModel.addContentValue("deleted", 1);
        goodsFlavorGroupUpdateModel.addContentValue("last_update_user_id", deleteGoodsModel.getUserId());
        goodsFlavorGroupUpdateModel.addContentValue("last_update_remark", "删除菜品信息！");
        goodsFlavorGroupUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getGoodsId());
        goodsFlavorGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getTenantId());
        goodsFlavorGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getBranchId());
        universalMapper.universalUpdate(goodsFlavorGroupUpdateModel);

        // 删除该菜品的所有口味
        String deleteAllGoodsFlavorSql = "UPDATE goods_flavor SET last_update_user_id = #{userId}, last_update_remark = #{lastUpdateRemark}, deleted = 1 WHERE goods_flavor_group_id IN (SELECT id FROM goods_flavor_group WHERE goods_id = #{goodsId})";
        Map<String, Object> deleteAllGoodsFlavorParameters = new HashMap<String, Object>();
        deleteAllGoodsFlavorParameters.put("sql", deleteAllGoodsFlavorSql);
        deleteAllGoodsFlavorParameters.put("userId", deleteGoodsModel.getUserId());
        deleteAllGoodsFlavorParameters.put("lastUpdateRemark", "删除菜品信息！");
        deleteAllGoodsFlavorParameters.put("goodsId", deleteGoodsModel.getGoodsId());
        universalMapper.executeUpdate(deleteAllGoodsFlavorParameters);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除菜品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
