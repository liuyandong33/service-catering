package build.dream.catering.services;

import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.goods.*;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.UpdateModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        BigInteger tenantId = saveGoodsModel.getTenantId();
        String tenantCode = saveGoodsModel.getTenantCode();
        BigInteger branchId = saveGoodsModel.getBranchId();
        BigInteger userId = saveGoodsModel.getUserId();

        if (saveGoodsModel.getGoodsId() != null) {
            SearchModel goodsSearchModel = new SearchModel(true);
            goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveGoodsModel.getGoodsId());
            Goods goods = goodsMapper.find(goodsSearchModel);
            Validate.notNull(goods, "商品不存在！");

            goods.setName(saveGoodsModel.getGoodsName());
            goodsMapper.update(goods);

            // 删除需要删除的规格
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsSpecificationIds())) {
                UpdateModel updateModel = new UpdateModel(true);
                updateModel.setTableName("goods_specification");
                updateModel.addContentValue("last_update_user_id", userId);
                updateModel.addContentValue("last_update_remark", "删除商品规格信息！");
                updateModel.addContentValue("deleted", 1);
                updateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                updateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsSpecificationIds());
                universalMapper.universalUpdate(updateModel);
            }

            // 删除需要删除的口味组及其下的口味
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsFlavorGroupIds())) {
                UpdateModel deleteGoodsFlavorGroupUpdateModel = new UpdateModel(true);
                deleteGoodsFlavorGroupUpdateModel.setTableName("goods_flavor_group");
                deleteGoodsFlavorGroupUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsFlavorGroupUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
                deleteGoodsFlavorGroupUpdateModel.addContentValue("delete", 1);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsFlavorGroupIds());
                universalMapper.universalUpdate(deleteGoodsFlavorGroupUpdateModel);

                UpdateModel deleteGoodsFlavorUpdateModel = new UpdateModel(true);
                deleteGoodsFlavorUpdateModel.setTableName("goods_flavor");
                deleteGoodsFlavorUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsFlavorUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                deleteGoodsFlavorUpdateModel.addContentValue("delete", 1);
                deleteGoodsFlavorUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                deleteGoodsFlavorUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                deleteGoodsFlavorUpdateModel.addSearchCondition("goods_flavor_group_id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsFlavorGroupIds());
                universalMapper.universalUpdate(deleteGoodsFlavorUpdateModel);
            }

            // 查询出需要修改的商品规格
            List<SaveGoodsModel.GoodsSpecificationInfo> goodsSpecificationInfos = saveGoodsModel.getGoodsSpecificationInfos();
            List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                if (goodsSpecificationInfo.getId() != null) {
                    goodsSpecificationIds.add(goodsSpecificationInfo.getId());
                }
            }

            Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
            if (CollectionUtils.isNotEmpty(goodsSpecificationIds)) {
                SearchModel searchModel = new SearchModel(true);
                searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                searchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, goods.getId());
                searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
                List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(searchModel);
                for (GoodsSpecification goodsSpecification : goodsSpecifications) {
                    goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
                }
            }

            // 处理所有规格，修改与更新
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                if (goodsSpecificationInfo.getId() != null) {
                    GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsSpecificationInfo.getId());
                    Validate.notNull(goodsSpecification, "商品规格不存在！");
                    goodsSpecification.setName(goodsSpecificationInfo.getName());
                    goodsSpecification.setPrice(goodsSpecification.getPrice());
                    goodsSpecificationMapper.update(goodsSpecification);
                } else {
                    GoodsSpecification goodsSpecification = new GoodsSpecification();
                    goodsSpecification.setTenantId(tenantId);
                    goodsSpecification.setTenantCode(tenantCode);
                    goodsSpecification.setBranchId(branchId);
                    goodsSpecification.setGoodsId(saveGoodsModel.getGoodsId());
                    goodsSpecification.setName(goodsSpecificationInfo.getName());
                    goodsSpecification.setPrice(goodsSpecificationInfo.getPrice());
                    goodsSpecification.setCreateUserId(userId);
                    goodsSpecification.setLastUpdateUserId(userId);
                    goodsSpecification.setLastUpdateRemark("新增规格信息！");
                    goodsSpecificationMapper.insert(goodsSpecification);
                }
            }

            List<SaveGoodsModel.GoodsFlavorGroupModel> goodsFlavorGroupModels = saveGoodsModel.getGoodsFlavorGroupModels();
            if (CollectionUtils.isNotEmpty(goodsFlavorGroupModels)) {
                List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
                List<BigInteger> deleteGoodsFlavorIds = new ArrayList<BigInteger>();
                List<BigInteger> goodsFlavorIds = new ArrayList<BigInteger>();
                for (SaveGoodsModel.GoodsFlavorGroupModel goodsFlavorGroupModel : goodsFlavorGroupModels) {
                    if (goodsFlavorGroupModel.getId() != null) {
                        goodsFlavorGroupIds.add(goodsFlavorGroupModel.getId());

                        if (CollectionUtils.isNotEmpty(goodsFlavorGroupModel.getDeleteGoodsFlavorIds())) {
                            deleteGoodsFlavorIds.addAll(goodsFlavorGroupModel.getDeleteGoodsFlavorIds());
                        }

                        for (SaveGoodsModel.GoodsFlavorModel goodsFlavorModel : goodsFlavorGroupModel.getGoodsFlavorModels()) {
                            if (goodsFlavorModel.getId() != null) {
                                goodsFlavorIds.add(goodsFlavorModel.getId());
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(deleteGoodsFlavorIds)) {
                    UpdateModel deleteGoodsFlavorUpdateModel = new UpdateModel(true);
                    deleteGoodsFlavorUpdateModel.setTableName("goods_flavor");
                    deleteGoodsFlavorUpdateModel.addContentValue("last_update_user_id", userId);
                    deleteGoodsFlavorUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                    deleteGoodsFlavorUpdateModel.addContentValue("delete", 1);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsFlavorIds);
                    universalMapper.universalUpdate(deleteGoodsFlavorUpdateModel);
                }

                SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
                goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                goodsFlavorGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, goods.getId());
                goodsFlavorGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
                List<GoodsFlavorGroup> goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);
                Map<BigInteger, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<BigInteger, GoodsFlavorGroup>();
                for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
                    goodsFlavorGroupMap.put(goodsFlavorGroup.getId(), goodsFlavorGroup);
                }

                SearchModel goodsFlavorSearchModel = new SearchModel(true);
                goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
                goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
                goodsFlavorSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, goods.getId());
                goodsFlavorSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorIds);
                List<GoodsFlavor> goodsFlavors = goodsFlavorMapper.findAll(goodsFlavorSearchModel);
                Map<BigInteger, GoodsFlavor> goodsFlavorMap = new HashMap<BigInteger, GoodsFlavor>();
                for (GoodsFlavor goodsFlavor : goodsFlavors) {
                    goodsFlavorMap.put(goodsFlavor.getId(), goodsFlavor);
                }

                for (SaveGoodsModel.GoodsFlavorGroupModel goodsFlavorGroupModel : goodsFlavorGroupModels) {
                    if (goodsFlavorGroupModel.getId() != null) {
                        GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(goodsFlavorGroupModel.getId());
                        Validate.notNull(goodsFlavorGroup, "口味组不存在！");
                        goodsFlavorGroup.setName(goodsFlavorGroupModel.getName());
                        goodsFlavorGroup.setLastUpdateUserId(userId);
                        goodsFlavorGroup.setLastUpdateRemark("修改口味组信息！");
                        goodsFlavorGroupMapper.update(goodsFlavorGroup);

                        for (SaveGoodsModel.GoodsFlavorModel goodsFlavorModel : goodsFlavorGroupModel.getGoodsFlavorModels()) {
                            if (goodsFlavorModel.getId() != null) {
                                GoodsFlavor goodsFlavor = goodsFlavorMap.get(goodsFlavorModel.getId());
                                Validate.notNull(goodsFlavor, "商品口味不存在！");
                                goodsFlavor.setName(goodsFlavorModel.getName());
                                goodsFlavor.setPrice(goodsFlavorModel.getPrice() == null ? BigDecimal.ZERO : goodsFlavorModel.getPrice());
                                goodsFlavor.setLastUpdateUserId(userId);
                                goodsFlavor.setLastUpdateRemark("修改口味信息！");
                                goodsFlavorMapper.update(goodsFlavor);
                            } else {
                                GoodsFlavor goodsFlavor = buildGoodsFlavor(goodsFlavorModel, tenantId, tenantCode, branchId, goods.getId(), goodsFlavorGroup.getId(), userId);
                                goodsFlavorMapper.insert(goodsFlavor);
                            }
                        }
                    } else {
                        GoodsFlavorGroup goodsFlavorGroup = new GoodsFlavorGroup();
                        goodsFlavorGroup.setTenantId(tenantId);
                        goodsFlavorGroup.setTenantCode(tenantCode);
                        goodsFlavorGroup.setBranchId(branchId);
                        goodsFlavorGroup.setGoodsId(goods.getId());
                        goodsFlavorGroup.setName(goodsFlavorGroupModel.getName());
                        goodsFlavorGroup.setCreateUserId(userId);
                        goodsFlavorGroup.setLastUpdateUserId(userId);
                        goodsFlavorGroup.setLastUpdateRemark("新增口味组信息！");
                        goodsFlavorGroupMapper.insert(goodsFlavorGroup);

                        for (SaveGoodsModel.GoodsFlavorModel goodsFlavorModel : goodsFlavorGroupModel.getGoodsFlavorModels()) {
                            GoodsFlavor goodsFlavor = buildGoodsFlavor(goodsFlavorModel, tenantId, tenantCode, branchId, goods.getId(), goodsFlavorGroup.getId(), userId);
                            goodsFlavorMapper.insert(goodsFlavor);
                        }
                    }
                }
            }
        }
        return new ApiRest();
    }

    private GoodsFlavor buildGoodsFlavor(SaveGoodsModel.GoodsFlavorModel goodsFlavorModel, BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, BigInteger goodsFlavorGroupId, BigInteger userId) {
        GoodsFlavor goodsFlavor = new GoodsFlavor();
        goodsFlavor.setTenantId(tenantId);
        goodsFlavor.setTenantCode(tenantCode);
        goodsFlavor.setBranchId(branchId);
        goodsFlavor.setGoodsId(goodsId);
        goodsFlavor.setGoodsFlavorGroupId(goodsFlavorGroupId);
        goodsFlavor.setName(goodsFlavorModel.getName());
        goodsFlavor.setPrice(goodsFlavorModel.getPrice() == null ? BigDecimal.ZERO : goodsFlavorModel.getPrice());
        goodsFlavor.setCreateUserId(userId);
        goodsFlavor.setLastUpdateUserId(userId);
        goodsFlavor.setLastUpdateRemark("新增口味信息！");
        return goodsFlavor;
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
        goodsSpecificationUpdateModel.addContentValue("last_update_remark", "删除菜品规格信息！");
        goodsSpecificationUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getGoodsId());
        goodsSpecificationUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getTenantId());
        goodsSpecificationUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getBranchId());
        universalMapper.universalUpdate(goodsSpecificationUpdateModel);

        // 删除该菜品的所有口味组
        UpdateModel goodsFlavorGroupUpdateModel = new UpdateModel(true);
        goodsFlavorGroupUpdateModel.setTableName("goods_flavor_group");
        goodsFlavorGroupUpdateModel.addContentValue("deleted", 1);
        goodsFlavorGroupUpdateModel.addContentValue("last_update_user_id", deleteGoodsModel.getUserId());
        goodsFlavorGroupUpdateModel.addContentValue("last_update_remark", "删除菜品口味组信息！");
        goodsFlavorGroupUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getGoodsId());
        goodsFlavorGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getTenantId());
        goodsFlavorGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, deleteGoodsModel.getBranchId());
        universalMapper.universalUpdate(goodsFlavorGroupUpdateModel);

        // 删除该菜品的所有口味
        String deleteAllGoodsFlavorSql = "UPDATE goods_flavor SET last_update_user_id = #{userId}, last_update_remark = #{lastUpdateRemark}, deleted = 1 WHERE goods_flavor_group_id IN (SELECT id FROM goods_flavor_group WHERE goods_id = #{goodsId})";
        Map<String, Object> deleteAllGoodsFlavorParameters = new HashMap<String, Object>();
        deleteAllGoodsFlavorParameters.put("sql", deleteAllGoodsFlavorSql);
        deleteAllGoodsFlavorParameters.put("userId", deleteGoodsModel.getUserId());
        deleteAllGoodsFlavorParameters.put("lastUpdateRemark", "删除口味信息！");
        deleteAllGoodsFlavorParameters.put("goodsId", deleteGoodsModel.getGoodsId());
        universalMapper.executeUpdate(deleteAllGoodsFlavorParameters);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除菜品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
