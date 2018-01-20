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

    @Transactional(readOnly = true)
    public ApiRest listGoodses(ListGoodsesModel listGoodsesModel) {
        BigInteger tenantId = listGoodsesModel.getTenantId();
        BigInteger branchId = listGoodsesModel.getBranchId();
        String countSql = "SELECT COUNT(1) FROM goods LEFT OUTER JOIN goods_category ON goods_category.id = goods.category_id WHERE goods.tenant_id = #{tenantId} AND goods.branch_id = #{branchId} AND goods.deleted = 0";
        Map<String, Object> countGoodsParameters = new HashMap<String, Object>();
        countGoodsParameters.put("tenantId", tenantId);
        countGoodsParameters.put("branchId", branchId);
        countGoodsParameters.put("sql", countSql);
        long total = universalMapper.universalCount(countGoodsParameters);

        List<Map<String, Object>> goodsInfos = new ArrayList<Map<String, Object>>();
        if (total > 0) {
            String queryGoodsInfosSql = "SELECT goods.*, goods_category.name AS category_name, goods_category.description FROM goods LEFT OUTER JOIN goods_category ON goods_category.id = goods.category_id WHERE goods.tenant_id = #{tenantId} AND goods.branch_id = #{branchId} AND goods.deleted = 0 LIMIT #{offset}, #{maxResults}";
            Map<String, Object> queryGoodsInfosParameters = new HashMap<String, Object>();
            queryGoodsInfosParameters.put("sql", queryGoodsInfosSql);
            queryGoodsInfosParameters.put("tenantId", tenantId);
            queryGoodsInfosParameters.put("branchId", branchId);
            queryGoodsInfosParameters.put("offset", listGoodsesModel.getOffset());
            queryGoodsInfosParameters.put("maxResults", listGoodsesModel.getMaxResults());
            goodsInfos = universalMapper.executeQuery(queryGoodsInfosParameters);

            List<BigInteger> goodsIds = new ArrayList<BigInteger>();
            for (Map<String, Object> goodsInfo : goodsInfos) {
                goodsIds.add(BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id")));
            }

            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
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
            goodsFlavorGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
            List<GoodsFlavorGroup> goodsFlavorGroups = goodsFlavorGroupMapper.findAll(goodsFlavorGroupSearchModel);

            Map<BigInteger, List<Map<String, Object>>> goodsFlavorGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            if (CollectionUtils.isNotEmpty(goodsFlavorGroups)) {
                List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
                for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
                    goodsFlavorGroupIds.add(goodsFlavorGroup.getId());
                }

                SearchModel goodsFlavorSearchModel = new SearchModel(true);
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
                goodsFlavorGroupMap = buildFlavorGroups(goodsFlavorGroups, goodsFlavorMap);
            }

            for (Map<String, Object> goodsInfo : goodsInfos) {
                BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(goodsInfo, "id"));
                goodsInfo.put("goodsSpecifications", buildGoodsSpecificationInfos(goodsSpecificationMap.get(goodsId)));
                goodsInfo.put("flavorGroups", goodsFlavorGroupMap.get(goodsId));
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", total);
        data.put("rows", goodsInfos);
        return new ApiRest(data, "查询菜品信息成功！");
    }

    public Map<BigInteger, List<Map<String, Object>>> buildFlavorGroups(List<GoodsFlavorGroup> goodsFlavorGroups, Map<BigInteger, List<GoodsFlavor>> goodsFlavorMap) {
        Map<BigInteger, List<Map<String, Object>>> flavorGroups = new HashMap<BigInteger, List<Map<String,Object>>>();
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

    /**
     * 保存菜品信息
     *
     * @param saveGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
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

            List<SaveGoodsModel.GoodsSpecificationModel> goodsSpecificationModels = saveGoodsModel.getGoodsSpecificationModels();
            List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
            for (SaveGoodsModel.GoodsSpecificationModel goodsSpecificationModel : goodsSpecificationModels) {
                if (goodsSpecificationModel.getId() != null) {
                    goodsSpecificationIds.add(goodsSpecificationModel.getId());
                }
            }
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveGoodsModel.getId());
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
            List<GoodsSpecification> persistenceGoodsSpecifications = goodsSpecificationMapper.findAll(searchModel);

            Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
            for (GoodsSpecification goodsSpecification : persistenceGoodsSpecifications) {
                goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
            }

            List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
            for (SaveGoodsModel.GoodsSpecificationModel goodsSpecificationModel : goodsSpecificationModels) {
                if (goodsSpecificationModel.getId() != null) {
                    GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsSpecificationModel.getId());
                    Validate.notNull(goodsSpecification, "菜品规格不存在！");
                    goodsSpecification.setName(goodsSpecificationModel.getName());
                    goodsSpecification.setPrice(goodsSpecificationModel.getPrice());
                    goodsSpecification.setCreateUserId(userId);
                    goodsSpecification.setLastUpdateUserId(userId);
                    goodsSpecification.setLastUpdateRemark("修改规格信息！");
                    goodsSpecificationMapper.update(goodsSpecification);
                } else {
                    goodsSpecifications.add(buildGoodsSpecification(goodsSpecificationModel, goods.getId(), userId, "新增规格信息！"));
                }
            }
            goodsSpecificationMapper.insertAll(goodsSpecifications);
        } else {
            Goods goods = new Goods();
            goods.setName(saveGoodsModel.getName());
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setGoodsType(Constants.GOODS_TYPE_ORDINARY_GOODS);
            goods.setCreateUserId(userId);
            goods.setLastUpdateUserId(userId);
            goods.setLastUpdateRemark("新增产品信息！");
            goodsMapper.insert(goods);

            List<SaveGoodsModel.GoodsSpecificationModel> goodsSpecificationModels = saveGoodsModel.getGoodsSpecificationModels();
            List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
            for (SaveGoodsModel.GoodsSpecificationModel goodsSpecificationModel : goodsSpecificationModels) {
                goodsSpecifications.add(buildGoodsSpecification(goodsSpecificationModel, goods.getId(), userId, "新增规格信息！"));
            }
            goodsSpecificationMapper.insertAll(goodsSpecifications);
            saveGoodsFlavorGroups(saveGoodsModel.getGoodsFlavorGroupModels(), goods.getId(), userId);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存菜品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 批量插入菜品口味组以及口味
     *
     * @param goodsFlavorGroupModels
     * @param goodsId
     * @param userId
     */
    private void saveGoodsFlavorGroups(List<SaveGoodsModel.GoodsFlavorGroupModel> goodsFlavorGroupModels, BigInteger goodsId, BigInteger userId) {
        if (CollectionUtils.isNotEmpty(goodsFlavorGroupModels)) {
            Map<Integer, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<Integer, GoodsFlavorGroup>();
            Map<GoodsFlavor, Integer> goodsFlavorMap = new HashMap<GoodsFlavor, Integer>();
            for (SaveGoodsModel.GoodsFlavorGroupModel goodsFlavorGroupModel : goodsFlavorGroupModels) {
                GoodsFlavorGroup goodsFlavorGroup = new GoodsFlavorGroup();
                goodsFlavorGroup.setGoodsId(goodsId);
                goodsFlavorGroup.setName(goodsFlavorGroupModel.getName());
                goodsFlavorGroup.setCreateUserId(userId);
                goodsFlavorGroup.setLastUpdateUserId(userId);
                goodsFlavorGroup.setLastUpdateRemark("新增菜品口味组！");

                goodsFlavorGroupMap.put(goodsFlavorGroup.hashCode(), goodsFlavorGroup);

                for (SaveGoodsModel.GoodsFlavorModel goodsFlavorModel : goodsFlavorGroupModel.getGoodsFlavorModels()) {
                    GoodsFlavor goodsFlavor = new GoodsFlavor();
                    goodsFlavor.setName(goodsFlavorModel.getName());
                    goodsFlavor.setPrice(goodsFlavorModel.getPrice());
                    goodsFlavor.setCreateUserId(userId);
                    goodsFlavor.setLastUpdateUserId(userId);
                    goodsFlavor.setLastUpdateRemark("新增菜品口味！");
                    goodsFlavorMap.put(goodsFlavor, goodsFlavorGroup.hashCode());
                }
            }

            goodsFlavorGroupMapper.insertAll(new ArrayList<GoodsFlavorGroup>(goodsFlavorGroupMap.values()));
            for (Map.Entry<GoodsFlavor, Integer> entry : goodsFlavorMap.entrySet()) {
                entry.getKey().setGoodsFlavorGroupId(goodsFlavorGroupMap.get(entry.getValue()).getId());
            }
            goodsFlavorMapper.insertAll(new ArrayList<GoodsFlavor>(goodsFlavorMap.keySet()));
        }
    }

    /**
     * 构建菜品规格
     * @param goodsSpecificationModel
     * @param goodsId
     * @param userId
     * @param lastUpdateRemark
     * @return
     */
    private GoodsSpecification buildGoodsSpecification(SaveGoodsModel.GoodsSpecificationModel goodsSpecificationModel, BigInteger goodsId, BigInteger userId, String lastUpdateRemark) {
        GoodsSpecification goodsSpecification = new GoodsSpecification();
        goodsSpecification.setGoodsId(goodsId);
        goodsSpecification.setName(goodsSpecificationModel.getName());
        goodsSpecification.setPrice(goodsSpecificationModel.getPrice());
        goodsSpecification.setCreateUserId(userId);
        goodsSpecification.setLastUpdateUserId(userId);
        goodsSpecification.setLastUpdateRemark(lastUpdateRemark);
        return goodsSpecification;
    }

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

    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePackage(SavePackageModel savePackageModel) {
        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (SavePackageModel.PackageGroupModel packageGroupModel : savePackageModel.getPackageGroupModels()) {
            for (SavePackageModel.PackageGroupGoodsModel packageGroupGoodsModel : packageGroupModel.getPackageGroupGoodsModels()) {
                goodsIds.add(packageGroupGoodsModel.getGoodsId());
            }
        }

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, savePackageModel.getTenantId());
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, savePackageModel.getBranchId());
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> goodses = goodsMapper.findAll(goodsSearchModel);
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        BigInteger packageId = savePackageModel.getPackageId();
        BigInteger tenantId = savePackageModel.getTenantId();
        String tenantCode = savePackageModel.getTenantCode();
        BigInteger branchId = savePackageModel.getBranchId();
        BigInteger userId = savePackageModel.getUserId();
        List<PackageGroupGoods> packageGroupGoodses = new ArrayList<PackageGroupGoods>();
        if (packageId != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, packageId);
            Goods goods = goodsMapper.find(searchModel);
            Validate.notNull(goods, "套餐不存在！");

            goods.setName(savePackageModel.getName());
            goodsMapper.update(goods);

            List<BigInteger> packageGroupIds = new ArrayList<BigInteger>();
            List<SavePackageModel.PackageGroupModel> packageGroupModels = savePackageModel.getPackageGroupModels();
            for (SavePackageModel.PackageGroupModel packageGroupModel : packageGroupModels) {
                if (packageGroupModel.getId() != null) {
                    packageGroupIds.add(packageGroupModel.getId());
                }
            }

            List<PackageGroup> packageGroups = null;
            Map<BigInteger, PackageGroup> packageGroupMap = new HashMap<BigInteger, PackageGroup>();
            if (CollectionUtils.isNotEmpty(packageGroupIds)) {
                SearchModel packageGroupSearchModel = new SearchModel(true);
                packageGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, packageGroupIds);
                packageGroups = packageGroupMapper.findAll(packageGroupSearchModel);
                for (PackageGroup packageGroup : packageGroups) {
                    packageGroupMap.put(packageGroup.getId(), packageGroup);
                }

                SearchModel deleteAllSearchModel = new SearchModel(false);
                deleteAllSearchModel.addSearchCondition("package_group_id", Constants.SQL_OPERATION_SYMBOL_IN, packageGroupIds);
                packageGroupGoodsMapper.deleteAll(deleteAllSearchModel);
            }

            for (SavePackageModel.PackageGroupModel packageGroupModel : packageGroupModels) {
                if (packageGroupModel.getId() != null) {
                    PackageGroup packageGroup = packageGroupMap.get(packageGroupModel.getId());
                    Validate.notNull(packageGroup, "套餐组不存在！");
                    packageGroup.setGroupType(packageGroupModel.getGroupType());
                    if (packageGroupModel.getGroupType() == 2) {
                        packageGroup.setOptionalQuantity(packageGroupModel.getOptionalQuantity());
                    } else {
                        packageGroup.setOptionalQuantity(null);
                    }
                    packageGroup.setOptionalQuantity(packageGroupModel.getOptionalQuantity());
                    packageGroupMapper.update(packageGroup);
                    packageGroupGoodses.addAll(buildPackageGroupGoodses(packageGroupModel.getPackageGroupGoodsModels(), packageGroup.getId(), goodsMap));
                } else {
                    packageGroupGoodses.addAll(savePackageGroup(packageGroupModel, goods.getId(), tenantId, tenantCode, branchId, userId, "修改套餐信息！", goodsMap));
                }
            }
        } else {
            Goods goods = new Goods();
            goods.setName(savePackageModel.getName());
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setGoodsType(2);
            goods.setCreateUserId(userId);
            goods.setLastUpdateUserId(userId);
            goods.setLastUpdateRemark("新增套餐信息！");
            goodsMapper.insert(goods);

            List<SavePackageModel.PackageGroupModel> packageGroupModels = savePackageModel.getPackageGroupModels();
            for (SavePackageModel.PackageGroupModel packageGroupModel : packageGroupModels) {
                packageGroupGoodses.addAll(savePackageGroup(packageGroupModel, goods.getId(), tenantId, tenantCode, branchId, userId, "新增套餐信息！", goodsMap));
            }
        }
        packageGroupGoodsMapper.insertAll(packageGroupGoodses);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存套餐信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    private List<PackageGroupGoods> savePackageGroup(SavePackageModel.PackageGroupModel packageGroupModel, BigInteger goodsId, BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger userId, String lastUpdateRemark, Map<BigInteger, Goods> goodsMap) {
        PackageGroup packageGroup = new PackageGroup();
        packageGroup.setPackageId(goodsId);
        packageGroup.setGroupType(packageGroupModel.getGroupType());
        if (packageGroupModel.getGroupType() == 2) {
            packageGroup.setOptionalQuantity(packageGroupModel.getOptionalQuantity());
        }
        packageGroup.setCreateUserId(userId);
        packageGroup.setLastUpdateUserId(userId);
        packageGroup.setLastUpdateRemark(lastUpdateRemark);
        packageGroupMapper.insert(packageGroup);
        return buildPackageGroupGoodses(packageGroupModel.getPackageGroupGoodsModels(), packageGroup.getId(), goodsMap);
    }

    private List<PackageGroupGoods> buildPackageGroupGoodses(List<SavePackageModel.PackageGroupGoodsModel> packageGroupGoodsModels, BigInteger packageGroupId, Map<BigInteger, Goods> goodsMap) {
        List<PackageGroupGoods> packageGroupGoodses = new ArrayList<PackageGroupGoods>();
        for (SavePackageModel.PackageGroupGoodsModel packageGroupGoodsModel : packageGroupGoodsModels) {
            PackageGroupGoods packageGroupGoods = new PackageGroupGoods();
            packageGroupGoods.setPackageGroupId(packageGroupId);
            Goods goods = goodsMap.get(packageGroupGoodsModel.getGoodsId());
            Validate.notNull(goods, "套餐组中包含不存在的产品！");
            packageGroupGoods.setGoodsId(packageGroupGoodsModel.getGoodsId());
            packageGroupGoods.setQuantity(packageGroupGoodsModel.getQuantity());
            packageGroupGoodses.add(packageGroupGoods);
        }
        return packageGroupGoodses;
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
