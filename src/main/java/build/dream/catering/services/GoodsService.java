package build.dream.catering.services;

import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.models.goods.*;
import build.dream.catering.utils.DatabaseHelper;
import build.dream.catering.utils.TenantConfigUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.UpdateModel;
import build.dream.common.utils.ZipUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class GoodsService extends BasicService {
    @Autowired
    private GoodsMapper goodsMapper;

    private static final String SELECT_GOODS_TABLE_COLUMN_NAMES = StringUtils.join(new String[]{"goods.id", "goods.name", "goods.tenant_id", "goods.tenant_code", "goods.branch_id", "goods.type", "goods.category_id"}, ", ");

    @Transactional(readOnly = true)
    public ApiRest listGoodsInfos(ListGoodsInfosModel listGoodsInfosModel) {
        BigInteger tenantId = listGoodsInfosModel.getTenantId();
        BigInteger branchId = listGoodsInfosModel.getBranchId();
        List<Goods> goodsInfos = goodsMapper.findAllGoodsInfos(tenantId, branchId, null);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

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
            goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            List<GoodsFlavorGroup> goodsFlavorGroups = DatabaseHelper.findAll(GoodsFlavorGroup.class, goodsFlavorGroupSearchModel);

            Map<BigInteger, List<Map<String, Object>>> goodsFlavorGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            if (CollectionUtils.isNotEmpty(goodsFlavorGroups)) {
                SearchModel goodsFlavorSearchModel = new SearchModel(true);
                goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                List<GoodsFlavor> goodsFlavors = DatabaseHelper.findAll(GoodsFlavor.class, goodsFlavorSearchModel);

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

            List<BigInteger> packageIds = new ArrayList<BigInteger>();

            for (Goods goods : goodsInfos) {
                int type = goods.getType();
                if (type == 2) {
                    packageIds.add(goods.getId());
                }
            }

            Map<BigInteger, List<Map<String, Object>>> packageGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            if (CollectionUtils.isNotEmpty(packageIds)) {
                Map<BigInteger, List<Map<String, Object>>> packageInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();

                List<Map<String, Object>> packageInfos = goodsMapper.listPackageInfos(packageIds);
                for (Map<String, Object> packageInfo : packageInfos) {
                    BigInteger packageGroupId = BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageGroupId"));
                    List<Map<String, Object>> packageInfoList = packageInfoMap.get(packageGroupId);
                    if (CollectionUtils.isEmpty(packageInfoList)) {
                        packageInfoList = new ArrayList<Map<String, Object>>();
                        packageInfoMap.put(packageGroupId, packageInfoList);
                    }
                    packageInfoList.add(packageInfo);
                }

                SearchModel packageGroupSearchModel = new SearchModel(true);
                packageGroupSearchModel.addSearchCondition("package_id", Constants.SQL_OPERATION_SYMBOL_IN, packageIds);
                List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class, packageGroupSearchModel);
                for (PackageGroup packageGroup : packageGroups) {
                    BigInteger packageId = packageGroup.getPackageId();
                    List<Map<String, Object>> packageGroupList = packageGroupMap.get(packageId);
                    if (CollectionUtils.isEmpty(packageGroupList)) {
                        packageGroupList = new ArrayList<Map<String, Object>>();
                        packageGroupMap.put(packageId, packageGroupList);
                    }

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("packageGroup", packageGroup);
                    map.put("packageGroupGoodses", packageInfoMap.get(packageGroup.getId()));

                    packageGroupList.add(map);
                }
            }

            for (Goods goods : goodsInfos) {
                BigInteger goodsId = goods.getId();
                int type = goods.getType();

                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("id", goodsId);
                goodsInfo.put("name", goods.getName());
                goodsInfo.put("tenantId", goods.getTenantId());
                goodsInfo.put("tenantCode", goods.getTenantCode());
                goodsInfo.put("branchId", goods.getBranchId());
                goodsInfo.put("type", type);
                goodsInfo.put("categoryId", goods.getCategoryId());
                goodsInfo.put("categoryName", goods.getCategoryName());
                if (type == 1) {
                    goodsInfo.put("goodsSpecifications", buildGoodsSpecificationInfos(goodsSpecificationMap.get(goodsId)));
                    goodsInfo.put("flavorGroups", goodsFlavorGroupMap.get(goodsId));
                } else if (type == 2) {
                    goodsInfo.put("groups", packageGroupMap.get(goodsId));
                }
                data.add(goodsInfo);
            }
        }
        return new ApiRest(data, "查询菜品信息成功！");
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
            List<GoodsFlavor> goodsFlavors = goodsFlavorMap.get(goodsFlavorGroup.getId());
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

        if (saveGoodsModel.getId() != null) {
            BigInteger goodsId = saveGoodsModel.getId();

            SearchModel goodsSearchModel = new SearchModel(true);
            goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            Goods goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
            Validate.notNull(goods, "商品不存在！");

            // 验证商品是否可以编辑
            validateCanNotOperate(tenantId, branchId, "goods", goodsId, 1);

            goods.setName(saveGoodsModel.getName());
            goods.setCategoryId(saveGoodsModel.getCategoryId());
            goods.setImageUrl(saveGoodsModel.getImageUrl());
            DatabaseHelper.update(goods);

            // 删除需要删除的规格
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsSpecificationIds())) {
                UpdateModel updateModel = new UpdateModel(true);
                updateModel.setTableName("goods_specification");
                updateModel.addContentValue("last_update_user_id", userId);
                updateModel.addContentValue("last_update_remark", "删除商品规格信息！");
                updateModel.addContentValue("deleted", 1);
                updateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                updateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsSpecificationIds());
                DatabaseHelper.universalUpdate(updateModel);
            }

            // 删除需要删除的口味组及其下的口味
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsFlavorGroupIds())) {
                UpdateModel deleteGoodsFlavorGroupUpdateModel = new UpdateModel(true);
                deleteGoodsFlavorGroupUpdateModel.setTableName("goods_flavor_group");
                deleteGoodsFlavorGroupUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsFlavorGroupUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
                deleteGoodsFlavorGroupUpdateModel.addContentValue("delete", 1);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsFlavorGroupUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsFlavorGroupIds());
                DatabaseHelper.universalUpdate(deleteGoodsFlavorGroupUpdateModel);

                UpdateModel deleteGoodsFlavorUpdateModel = new UpdateModel(true);
                deleteGoodsFlavorUpdateModel.setTableName("goods_flavor");
                deleteGoodsFlavorUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsFlavorUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                deleteGoodsFlavorUpdateModel.addContentValue("delete", 1);
                deleteGoodsFlavorUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsFlavorUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsFlavorUpdateModel.addSearchCondition("goods_flavor_group_id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsFlavorGroupIds());
                DatabaseHelper.universalUpdate(deleteGoodsFlavorUpdateModel);
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
                searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                searchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
                List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, searchModel);
                for (GoodsSpecification goodsSpecification : goodsSpecifications) {
                    goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
                }
            }

            // 处理所有规格，修改与更新
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                List<GoodsSpecification> insertGoodsSpecifications = new ArrayList<GoodsSpecification>();
                if (goodsSpecificationInfo.getId() != null) {
                    GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsSpecificationInfo.getId());
                    Validate.notNull(goodsSpecification, "商品规格不存在！");
                    goodsSpecification.setName(goodsSpecificationInfo.getName());
                    goodsSpecification.setPrice(goodsSpecificationInfo.getPrice());
                    DatabaseHelper.update(goodsSpecification);
                } else {
                    GoodsSpecification goodsSpecification = buildGoodsSpecification(tenantId, tenantCode, branchId, goodsId, goodsSpecificationInfo, userId);
                    insertGoodsSpecifications.add(goodsSpecification);
                }
                if (CollectionUtils.isNotEmpty(insertGoodsSpecifications)) {
                    DatabaseHelper.insertAll(insertGoodsSpecifications);
                }
            }

            List<SaveGoodsModel.FlavorGroupInfo> flavorGroupInfos = saveGoodsModel.getFlavorGroupInfos();
            if (CollectionUtils.isNotEmpty(flavorGroupInfos)) {
                // 用来保存需要修改的口味组id
                List<BigInteger> goodsFlavorGroupIds = new ArrayList<BigInteger>();
                // 用来保存需要删除的口味id
                List<BigInteger> deleteGoodsFlavorIds = new ArrayList<BigInteger>();
                // 用来保存需要修改的口味id
                List<BigInteger> goodsFlavorIds = new ArrayList<BigInteger>();
                for (SaveGoodsModel.FlavorGroupInfo goodsFlavorGroupInfo : flavorGroupInfos) {
                    if (goodsFlavorGroupInfo.getId() != null) {
                        goodsFlavorGroupIds.add(goodsFlavorGroupInfo.getId());

                        if (CollectionUtils.isNotEmpty(goodsFlavorGroupInfo.getDeleteGoodsFlavorIds())) {
                            deleteGoodsFlavorIds.addAll(goodsFlavorGroupInfo.getDeleteGoodsFlavorIds());
                        }

                        for (SaveGoodsModel.FlavorInfo flavorGroupInfo : goodsFlavorGroupInfo.getFlavorInfos()) {
                            if (flavorGroupInfo.getId() != null) {
                                goodsFlavorIds.add(flavorGroupInfo.getId());
                            }
                        }
                    }
                }

                // 删除需要删除的口味
                if (CollectionUtils.isNotEmpty(deleteGoodsFlavorIds)) {
                    UpdateModel deleteGoodsFlavorUpdateModel = new UpdateModel(true);
                    deleteGoodsFlavorUpdateModel.setTableName("goods_flavor");
                    deleteGoodsFlavorUpdateModel.addContentValue("last_update_user_id", userId);
                    deleteGoodsFlavorUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                    deleteGoodsFlavorUpdateModel.addContentValue("delete", 1);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                    deleteGoodsFlavorUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsFlavorIds);
                    DatabaseHelper.universalUpdate(deleteGoodsFlavorUpdateModel);
                }

                // 查询出需要修改的口味组
                SearchModel goodsFlavorGroupSearchModel = new SearchModel(true);
                goodsFlavorGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsFlavorGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsFlavorGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsFlavorGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorGroupIds);
                List<GoodsFlavorGroup> goodsFlavorGroups = DatabaseHelper.findAll(GoodsFlavorGroup.class, goodsFlavorGroupSearchModel);
                Map<BigInteger, GoodsFlavorGroup> goodsFlavorGroupMap = new HashMap<BigInteger, GoodsFlavorGroup>();
                for (GoodsFlavorGroup goodsFlavorGroup : goodsFlavorGroups) {
                    goodsFlavorGroupMap.put(goodsFlavorGroup.getId(), goodsFlavorGroup);
                }

                // 查询出需要修改的口味
                SearchModel goodsFlavorSearchModel = new SearchModel(true);
                goodsFlavorSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsFlavorSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsFlavorSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsFlavorSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsFlavorIds);
                List<GoodsFlavor> goodsFlavors = DatabaseHelper.findAll(GoodsFlavor.class, goodsFlavorSearchModel);
                Map<BigInteger, GoodsFlavor> goodsFlavorMap = new HashMap<BigInteger, GoodsFlavor>();
                for (GoodsFlavor goodsFlavor : goodsFlavors) {
                    goodsFlavorMap.put(goodsFlavor.getId(), goodsFlavor);
                }

                for (SaveGoodsModel.FlavorGroupInfo flavorGroupInfo : flavorGroupInfos) {
                    // 用来保存需要新增的口味，便于批量插入
                    List<GoodsFlavor> insertGoodsFlavors = new ArrayList<GoodsFlavor>();
                    if (flavorGroupInfo.getId() != null) {
                        GoodsFlavorGroup goodsFlavorGroup = goodsFlavorGroupMap.get(flavorGroupInfo.getId());
                        Validate.notNull(goodsFlavorGroup, "口味组不存在！");
                        goodsFlavorGroup.setName(flavorGroupInfo.getName());
                        goodsFlavorGroup.setLastUpdateUserId(userId);
                        goodsFlavorGroup.setLastUpdateRemark("修改口味组信息！");
                        DatabaseHelper.update(goodsFlavorGroup);

                        for (SaveGoodsModel.FlavorInfo flavorInfo : flavorGroupInfo.getFlavorInfos()) {
                            if (flavorInfo.getId() != null) {
                                GoodsFlavor goodsFlavor = goodsFlavorMap.get(flavorInfo.getId());
                                Validate.notNull(goodsFlavor, "商品口味不存在！");
                                goodsFlavor.setName(flavorInfo.getName());
                                goodsFlavor.setPrice(flavorInfo.getPrice() == null ? BigDecimal.ZERO : flavorInfo.getPrice());
                                goodsFlavor.setLastUpdateUserId(userId);
                                goodsFlavor.setLastUpdateRemark("修改口味信息！");
                                DatabaseHelper.update(goodsFlavor);
                            } else {
                                GoodsFlavor goodsFlavor = buildGoodsFlavor(flavorInfo, tenantId, tenantCode, branchId, goodsId, goodsFlavorGroup.getId(), userId);
                                insertGoodsFlavors.add(goodsFlavor);
//                                goodsFlavorMapper.insert(goodsFlavor);
                            }
                        }
                    } else {
                        GoodsFlavorGroup goodsFlavorGroup = buildGoodsFlavorGroup(tenantId, tenantCode, branchId, goodsId, flavorGroupInfo, userId);
                        DatabaseHelper.insert(goodsFlavorGroup);

                        for (SaveGoodsModel.FlavorInfo flavorInfo : flavorGroupInfo.getFlavorInfos()) {
                            GoodsFlavor goodsFlavor = buildGoodsFlavor(flavorInfo, tenantId, tenantCode, branchId, goodsId, goodsFlavorGroup.getId(), userId);
                            insertGoodsFlavors.add(goodsFlavor);
//                            goodsFlavorMapper.insert(goodsFlavor);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(insertGoodsFlavors)) {
                        DatabaseHelper.insertAll(insertGoodsFlavors);
                    }
                }
            }
        } else {
            // 新增商品
            Goods goods = new Goods();
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setName(saveGoodsModel.getName());
            goods.setType(saveGoodsModel.getType());
            goods.setCategoryId(saveGoodsModel.getCategoryId());
            goods.setImageUrl(saveGoodsModel.getImageUrl());
            goods.setCreateUserId(userId);
            goods.setLastUpdateUserId(userId);
            goods.setLastUpdateRemark("新增商品信息！");
            DatabaseHelper.insert(goods);

            BigInteger goodsId = goods.getId();
            // 新增所有规格
            List<GoodsSpecification> insertGoodsSpecifications = new ArrayList<GoodsSpecification>();
            List<SaveGoodsModel.GoodsSpecificationInfo> goodsSpecificationInfos = saveGoodsModel.getGoodsSpecificationInfos();
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                GoodsSpecification goodsSpecification = buildGoodsSpecification(tenantId, tenantCode, branchId, goodsId, goodsSpecificationInfo, userId);
                insertGoodsSpecifications.add(goodsSpecification);
            }
            DatabaseHelper.insertAll(insertGoodsSpecifications);

            List<SaveGoodsModel.FlavorGroupInfo> flavorGroupInfos = saveGoodsModel.getFlavorGroupInfos();
            if (CollectionUtils.isNotEmpty(flavorGroupInfos)) {
                List<GoodsFlavor> insertGoodsFlavors = new ArrayList<GoodsFlavor>();
                for (SaveGoodsModel.FlavorGroupInfo flavorGroupInfo : flavorGroupInfos) {
                    GoodsFlavorGroup goodsFlavorGroup = buildGoodsFlavorGroup(tenantId, tenantCode, branchId, goodsId, flavorGroupInfo, userId);
                    DatabaseHelper.insert(goodsFlavorGroup);

                    for (SaveGoodsModel.FlavorInfo flavorInfo : flavorGroupInfo.getFlavorInfos()) {
                        GoodsFlavor goodsFlavor = buildGoodsFlavor(flavorInfo, tenantId, tenantCode, branchId, goodsId, goodsFlavorGroup.getId(), userId);
                        insertGoodsFlavors.add(goodsFlavor);
                    }
                }
                DatabaseHelper.insertAll(insertGoodsFlavors);
            }
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存商品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    private GoodsFlavorGroup buildGoodsFlavorGroup(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.FlavorGroupInfo flavorGroupInfo, BigInteger userId) {
        GoodsFlavorGroup goodsFlavorGroup = new GoodsFlavorGroup();
        goodsFlavorGroup.setTenantId(tenantId);
        goodsFlavorGroup.setTenantCode(tenantCode);
        goodsFlavorGroup.setBranchId(branchId);
        goodsFlavorGroup.setGoodsId(goodsId);
        goodsFlavorGroup.setName(flavorGroupInfo.getName());
        goodsFlavorGroup.setCreateUserId(userId);
        goodsFlavorGroup.setLastUpdateUserId(userId);
        goodsFlavorGroup.setLastUpdateRemark("新增口味组信息！");
        return goodsFlavorGroup;
    }

    private GoodsFlavor buildGoodsFlavor(SaveGoodsModel.FlavorInfo flavorInfo, BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, BigInteger goodsFlavorGroupId, BigInteger userId) {
        GoodsFlavor goodsFlavor = new GoodsFlavor();
        goodsFlavor.setTenantId(tenantId);
        goodsFlavor.setTenantCode(tenantCode);
        goodsFlavor.setBranchId(branchId);
        goodsFlavor.setGoodsId(goodsId);
        goodsFlavor.setGoodsFlavorGroupId(goodsFlavorGroupId);
        goodsFlavor.setName(flavorInfo.getName());
        goodsFlavor.setPrice(flavorInfo.getPrice() == null ? BigDecimal.ZERO : flavorInfo.getPrice());
        goodsFlavor.setCreateUserId(userId);
        goodsFlavor.setLastUpdateUserId(userId);
        goodsFlavor.setLastUpdateRemark("新增口味信息！");
        return goodsFlavor;
    }

    private GoodsSpecification buildGoodsSpecification(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo, BigInteger userId) {
        GoodsSpecification goodsSpecification = new GoodsSpecification();
        goodsSpecification.setTenantId(tenantId);
        goodsSpecification.setTenantCode(tenantCode);
        goodsSpecification.setBranchId(branchId);
        goodsSpecification.setGoodsId(goodsId);
        goodsSpecification.setName(goodsSpecificationInfo.getName());
        goodsSpecification.setPrice(goodsSpecificationInfo.getPrice());
        goodsSpecification.setCreateUserId(userId);
        goodsSpecification.setLastUpdateUserId(userId);
        goodsSpecification.setLastUpdateRemark("新增规格信息！");
        return goodsSpecification;
    }

    /**
     * 删除菜品规格
     *
     * @param deleteGoodsSpecificationModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteGoodsSpecification(DeleteGoodsSpecificationModel deleteGoodsSpecificationModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGoodsSpecificationModel.getGoodsSpecificationId());
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGoodsSpecificationModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGoodsSpecificationModel.getBranchId());
        GoodsSpecification goodsSpecification = DatabaseHelper.find(GoodsSpecification.class, searchModel);
        Validate.notNull(goodsSpecification, "菜品规格不存在！");
        goodsSpecification.setDeleted(true);
        goodsSpecification.setLastUpdateUserId(deleteGoodsSpecificationModel.getUserId());
        goodsSpecification.setLastUpdateRemark("删除菜品规格信息！");
        DatabaseHelper.update(goodsSpecification);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除菜品规格成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 保存套餐
     *
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
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, listCategoriesModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, listCategoriesModel.getBranchId());
        List<GoodsCategory> goodsCategories = DatabaseHelper.findAll(GoodsCategory.class, searchModel);

        List<ZTreeNode> zTreeNodes = new ArrayList<ZTreeNode>();
        for (GoodsCategory goodsCategory : goodsCategories) {
            zTreeNodes.add(new ZTreeNode(goodsCategory.getId().toString(), goodsCategory.getName(), goodsCategory.getParentId().toString()));
        }
        return new ApiRest(zTreeNodes, "查询菜品分类成功！");
    }

    /**
     * 删除商品、商品规格、商品口味组、商品口味
     *
     * @param deleteGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteGoods(DeleteGoodsModel deleteGoodsModel) {
        BigInteger tenantId = deleteGoodsModel.getTenantId();
        BigInteger branchId = deleteGoodsModel.getBranchId();
        BigInteger userId = deleteGoodsModel.getUserId();
        BigInteger goodsId = deleteGoodsModel.getGoodsId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Goods goods = DatabaseHelper.find(Goods.class, searchModel);
        Validate.notNull(goods, "商品不存在！");

        validateCanNotOperate(tenantId, branchId, "goods", goodsId, 2);

        goods.setLastUpdateUserId(userId);
        goods.setLastUpdateRemark("删除商品信息！");
        goods.setDeleted(true);
        DatabaseHelper.update(goods);

        // 删除该商品的所有规格
        UpdateModel goodsSpecificationUpdateModel = new UpdateModel(true);
        goodsSpecificationUpdateModel.setTableName("goods_specification");
        goodsSpecificationUpdateModel.addContentValue("deleted", 1);
        goodsSpecificationUpdateModel.addContentValue("last_update_user_id", userId);
        goodsSpecificationUpdateModel.addContentValue("last_update_remark", "删除商品规格信息！");
        goodsSpecificationUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsSpecificationUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsSpecificationUpdateModel);

        // 删除该商品的所有口味组
        UpdateModel goodsFlavorGroupUpdateModel = new UpdateModel(true);
        goodsFlavorGroupUpdateModel.setTableName("goods_flavor_group");
        goodsFlavorGroupUpdateModel.addContentValue("deleted", 1);
        goodsFlavorGroupUpdateModel.addContentValue("last_update_user_id", userId);
        goodsFlavorGroupUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
        goodsFlavorGroupUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsFlavorGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsFlavorGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsFlavorGroupUpdateModel);

        // 删除该商品的所有口味
        UpdateModel goodsFlavorUpdateModel = new UpdateModel(true);
        goodsFlavorUpdateModel.setTableName("goods_flavor");
        goodsFlavorUpdateModel.addContentValue("deleted", 1);
        goodsFlavorUpdateModel.addContentValue("last_update_user_id", userId);
        goodsFlavorUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
        goodsFlavorUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsFlavorUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsFlavorUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsFlavorUpdateModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("删除商品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    /**
     * 导入商品信息
     *
     * @param importGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest importGoods(ImportGoodsModel importGoodsModel) {
        BigInteger tenantId = importGoodsModel.getTenantId();
        String tenantCode = importGoodsModel.getTenantCode();
        BigInteger branchId = importGoodsModel.getBranchId();
        BigInteger userId = importGoodsModel.getUserId();
        String zipGoodsInfos = importGoodsModel.getZipGoodsInfos();
        List<Map<String, Object>> goodsInfos = GsonUtils.fromJson(ZipUtils.unzipText(zipGoodsInfos), List.class);
        int count = goodsInfos.size();

        TenantConfig tenantConfig = TenantConfigUtils.addTenantConfig(BigInteger.ONE, "goods_num", count);
        int currentValue = tenantConfig.getCurrentValue();
        int maxValue = tenantConfig.getMaxValue();
        Validate.isTrue(currentValue <= maxValue, "您最多可以添加" + (maxValue - currentValue + count) + "条商品信息！");

        List<Goods> goodses = new ArrayList<Goods>();
        Map<String, Goods> goodsMap = new HashMap<String, Goods>();
        Map<String, GoodsSpecification> goodsSpecificationMap = new HashMap<String, GoodsSpecification>();

        for (Map<String, Object> goodsInfo : goodsInfos) {
            String uuid = UUID.randomUUID().toString();
            Goods goods = new Goods();
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setName(MapUtils.getString(goodsInfo, "name"));
            goods.setType(1);
            goods.setCategoryId(BigInteger.ONE);
            goods.setCreateUserId(userId);
            goods.setLastUpdateUserId(userId);
            goodsMap.put(uuid, goods);
            goodses.add(goods);

            GoodsSpecification goodsSpecification = new GoodsSpecification();
            goodsSpecification.setTenantId(tenantId);
            goodsSpecification.setTenantCode(tenantCode);
            goodsSpecification.setBranchId(branchId);
            goodsSpecification.setPrice(BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "price")));
            goodsSpecification.setCreateUserId(userId);
            goodsSpecification.setLastUpdateUserId(userId);
            goodsSpecificationMap.put(uuid, goodsSpecification);
        }

        DatabaseHelper.insertAll(goodses);

        List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
        for (Map.Entry<String, GoodsSpecification> entry : goodsSpecificationMap.entrySet()) {
            GoodsSpecification goodsSpecification = entry.getValue();
            goodsSpecification.setGoodsId(goodsMap.get(entry.getKey()).getId());
            goodsSpecifications.add(goodsSpecification);
        }

        DatabaseHelper.insertAll(goodsSpecifications);

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("导入商品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
