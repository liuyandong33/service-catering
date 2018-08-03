package build.dream.catering.services;

import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.models.goods.*;
import build.dream.catering.utils.TenantConfigUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

    /**
     * 查询商品数量
     *
     * @param countModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest count(CountModel countModel) {
        BigInteger tenantId = countModel.getTenantId();
        BigInteger branchId = countModel.getBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        long count = DatabaseHelper.count(Goods.class, searchModel);

        return new ApiRest(count, "查询商品数量成功！");
    }

    /**
     * 查询商品列表
     *
     * @param listModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest list(ListModel listModel) {
        BigInteger tenantId = listModel.getTenantId();
        BigInteger branchId = listModel.getBranchId();
        int page = listModel.getPage();
        int rows = listModel.getRows();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        searchConditions.add(new SearchCondition("deleted", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel searchModel = new SearchModel(searchConditions);
        long count = DatabaseHelper.count(Goods.class, searchModel);

        List<Goods> goodsList = new ArrayList<Goods>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(searchConditions, page, rows);
            goodsList = DatabaseHelper.findAllPaged(Goods.class, pagedSearchModel);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", goodsList);
        return new ApiRest(data, "查询商品列表成功！");
    }


    @Transactional(readOnly = true)
    public ApiRest obtainGoodsInfo(ObtainGoodsInfoModel obtainGoodsInfoModel) {
        BigInteger tenantId = obtainGoodsInfoModel.getTenantId();
        BigInteger branchId = obtainGoodsInfoModel.getBranchId();
        BigInteger goodsId = obtainGoodsInfoModel.getGoodsId();

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        Goods goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
        ValidateUtils.notNull(goods, "商品不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        int type = goods.getType();
        if (type == 1) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

            SearchModel goodsUnitSearchModel = new SearchModel(true);
            goodsUnitSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsUnit> goodsUnits = DatabaseHelper.findAll(GoodsUnit.class, goodsUnitSearchModel);

            data.put("goods", goods);
            data.put("goodsSpecifications", goodsSpecifications);
            data.put("goodsUnits", goodsUnits);
            SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
            goodsAttributeGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
            if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
                SearchModel goodsAttributeSearchModel = new SearchModel(true);
                goodsAttributeSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);
                Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = new HashMap<BigInteger, List<GoodsAttribute>>();
                for (GoodsAttribute goodsAttribute : goodsAttributes) {
                    BigInteger goodsAttributeGroupId = goodsAttribute.getGoodsAttributeGroupId();
                    List<GoodsAttribute> goodsAttributeList = goodsAttributeMap.get(goodsAttributeGroupId);
                    if (CollectionUtils.isEmpty(goodsAttributeList)) {
                        goodsAttributeList = new ArrayList<GoodsAttribute>();
                        goodsAttributeMap.put(goodsAttributeGroupId, goodsAttributeList);
                    }
                    goodsAttributeList.add(goodsAttribute);
                }
                List<Map<String, Object>> attributeGroups = new ArrayList<Map<String, Object>>();
                for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                    Map<String, Object> attributeGroup = new HashMap<String, Object>();
                    attributeGroup.put("attributeGroup", goodsAttributeGroup);
                    attributeGroup.put("attributes", goodsAttributeMap.get(goodsAttributeGroup.getId()));
                    attributeGroups.add(attributeGroup);
                }
                data.put("attributeGroups", attributeGroups);
            }
        } else if (type == 2) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("package_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class, searchModel);

            List<BigInteger> packageIds = new ArrayList<BigInteger>();
            packageIds.add(goodsId);
            List<Map<String, Object>> packageInfos = goodsMapper.listPackageInfos(packageIds);

            Map<BigInteger, List<Map<String, Object>>> packageInfoMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            for (Map<String, Object> packageInfo : packageInfos) {
                BigInteger packageGroupId = BigInteger.valueOf(MapUtils.getLong(packageInfo, "packageGroupId"));
                List<Map<String, Object>> packageInfoList = packageInfoMap.get(packageGroupId);
                if (CollectionUtils.isEmpty(packageInfoList)) {
                    packageInfoList = new ArrayList<Map<String, Object>>();
                    packageInfoMap.put(packageGroupId, packageInfoList);
                }
                packageInfoList.add(packageInfo);
            }

            List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
            for (PackageGroup packageGroup : packageGroups) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("group", packageGroup);
                item.put("details", packageInfoMap.get(packageGroup.getId()));
                groups.add(item);
            }
            data.put("goods", goods);
            data.put("groups", groups);
        }
        return new ApiRest(data, "获取商品信息成功！");
    }

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

            SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
            goodsAttributeGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsAttributeGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);

            Map<BigInteger, List<Map<String, Object>>> goodsAttributeGroupMap = new HashMap<BigInteger, List<Map<String, Object>>>();
            if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
                SearchModel goodsAttributeSearchModel = new SearchModel(true);
                goodsAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);

                Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = new HashMap<BigInteger, List<GoodsAttribute>>();
                for (GoodsAttribute goodsAttribute : goodsAttributes) {
                    List<GoodsAttribute> goodsAttributeList = goodsAttributeMap.get(goodsAttribute.getGoodsAttributeGroupId());
                    if (goodsAttributeList == null) {
                        goodsAttributeList = new ArrayList<GoodsAttribute>();
                        goodsAttributeMap.put(goodsAttribute.getGoodsAttributeGroupId(), goodsAttributeList);
                    }
                    goodsAttributeList.add(goodsAttribute);
                }
                goodsAttributeGroupMap = buildAttributeGroups(goodsAttributeGroups, goodsAttributeMap);
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
                    map.put("group", packageGroup);
                    map.put("details", packageInfoMap.get(packageGroup.getId()));

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
                    goodsInfo.put("attributeGroups", goodsAttributeGroupMap.get(goodsId));
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

    public Map<BigInteger, List<Map<String, Object>>> buildAttributeGroups(List<GoodsAttributeGroup> goodsAttributeGroups, Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap) {
        Map<BigInteger, List<Map<String, Object>>> attributeGroups = new HashMap<BigInteger, List<Map<String, Object>>>();
        for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
            Map<String, Object> goodsAttributeGroupInfo = new HashMap<String, Object>();
            goodsAttributeGroupInfo.put("id", goodsAttributeGroup.getId());
            goodsAttributeGroupInfo.put("name", goodsAttributeGroup.getName());

            List<Map<String, Object>> goodsAttributeInfos = new ArrayList<Map<String, Object>>();
            List<GoodsAttribute> goodsAttributes = goodsAttributeMap.get(goodsAttributeGroup.getId());
            for (GoodsAttribute goodsAttribute : goodsAttributes) {
                Map<String, Object> goodsAttributeInfo = new HashMap<String, Object>();
                goodsAttributeInfo.put("id", goodsAttribute.getId());
                goodsAttributeInfo.put("name", goodsAttribute.getName());
                goodsAttributeInfo.put("price", goodsAttribute.getPrice());
                goodsAttributeInfos.add(goodsAttributeInfo);
            }
            goodsAttributeGroupInfo.put("attributes", goodsAttributeInfos);
            List<Map<String, Object>> goodsAttributeGroupInfos = attributeGroups.get(goodsAttributeGroup.getGoodsId());
            if (goodsAttributeGroupInfos == null) {
                goodsAttributeGroupInfos = new ArrayList<Map<String, Object>>();
                attributeGroups.put(goodsAttributeGroup.getGoodsId(), goodsAttributeGroupInfos);
            }
            goodsAttributeGroupInfos.add(goodsAttributeGroupInfo);
        }
        return attributeGroups;
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
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsAttributeGroupIds())) {
                UpdateModel deleteGoodsAttributeGroupUpdateModel = new UpdateModel(true);
                deleteGoodsAttributeGroupUpdateModel.setTableName("goods_attribute_group");
                deleteGoodsAttributeGroupUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsAttributeGroupUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
                deleteGoodsAttributeGroupUpdateModel.addContentValue("delete", 1);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsAttributeGroupIds());
                DatabaseHelper.universalUpdate(deleteGoodsAttributeGroupUpdateModel);

                UpdateModel deleteGoodsAttributeUpdateModel = new UpdateModel(true);
                deleteGoodsAttributeUpdateModel.setTableName("goods_attribute");
                deleteGoodsAttributeUpdateModel.addContentValue("last_update_user_id", userId);
                deleteGoodsAttributeUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                deleteGoodsAttributeUpdateModel.addContentValue("delete", 1);
                deleteGoodsAttributeUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsAttributeUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsAttributeUpdateModel.addSearchCondition("goods_attribute_group_id", Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsAttributeGroupIds());
                DatabaseHelper.universalUpdate(deleteGoodsAttributeUpdateModel);
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

            List<SaveGoodsModel.AttributeGroupInfo> attributeGroupInfos = saveGoodsModel.getAttributeGroupInfos();
            if (CollectionUtils.isNotEmpty(attributeGroupInfos)) {
                // 用来保存需要修改的口味组id
                List<BigInteger> goodsAttributeGroupIds = new ArrayList<BigInteger>();
                // 用来保存需要删除的口味id
                List<BigInteger> deleteGoodsAttributeIds = new ArrayList<BigInteger>();
                // 用来保存需要修改的口味id
                List<BigInteger> goodsAttributeIds = new ArrayList<BigInteger>();
                for (SaveGoodsModel.AttributeGroupInfo attributeGroupInfo : attributeGroupInfos) {
                    if (attributeGroupInfo.getId() != null) {
                        goodsAttributeGroupIds.add(attributeGroupInfo.getId());

                        if (CollectionUtils.isNotEmpty(attributeGroupInfo.getDeleteGoodsAttributeIds())) {
                            deleteGoodsAttributeIds.addAll(attributeGroupInfo.getDeleteGoodsAttributeIds());
                        }

                        for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                            if (attributeInfo.getId() != null) {
                                goodsAttributeIds.add(attributeInfo.getId());
                            }
                        }
                    }
                }

                // 删除需要删除的口味
                if (CollectionUtils.isNotEmpty(deleteGoodsAttributeIds)) {
                    UpdateModel deleteGoodsAttributeUpdateModel = new UpdateModel(true);
                    deleteGoodsAttributeUpdateModel.setTableName("goods_attribute");
                    deleteGoodsAttributeUpdateModel.addContentValue("last_update_user_id", userId);
                    deleteGoodsAttributeUpdateModel.addContentValue("last_update_remark", "删除商品口味信息！");
                    deleteGoodsAttributeUpdateModel.addContentValue("delete", 1);
                    deleteGoodsAttributeUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                    deleteGoodsAttributeUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                    deleteGoodsAttributeUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsAttributeIds);
                    DatabaseHelper.universalUpdate(deleteGoodsAttributeUpdateModel);
                }

                // 查询出需要修改的口味组
                SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
                goodsAttributeGroupSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeGroupSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeGroupSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsAttributeGroupSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeGroupIds);
                List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
                Map<BigInteger, GoodsAttributeGroup> goodsAttributeGroupMap = new HashMap<BigInteger, GoodsAttributeGroup>();
                for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                    goodsAttributeGroupMap.put(goodsAttributeGroup.getId(), goodsAttributeGroup);
                }

                // 查询出需要修改的口味
                SearchModel goodsAttributeSearchModel = new SearchModel(true);
                goodsAttributeSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsAttributeSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeIds);
                List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);
                Map<BigInteger, GoodsAttribute> goodsAttributeMap = new HashMap<BigInteger, GoodsAttribute>();
                for (GoodsAttribute goodsAttribute : goodsAttributes) {
                    goodsAttributeMap.put(goodsAttribute.getId(), goodsAttribute);
                }

                for (SaveGoodsModel.AttributeGroupInfo attributeGroupInfo : attributeGroupInfos) {
                    // 用来保存需要新增的口味，便于批量插入
                    List<GoodsAttribute> insertGoodsAttributes = new ArrayList<GoodsAttribute>();
                    if (attributeGroupInfo.getId() != null) {
                        GoodsAttributeGroup goodsAttributeGroup = goodsAttributeGroupMap.get(attributeGroupInfo.getId());
                        Validate.notNull(goodsAttributeGroup, "口味组不存在！");
                        goodsAttributeGroup.setName(attributeGroupInfo.getName());
                        goodsAttributeGroup.setLastUpdateUserId(userId);
                        goodsAttributeGroup.setLastUpdateRemark("修改口味组信息！");
                        DatabaseHelper.update(goodsAttributeGroup);

                        for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                            if (attributeInfo.getId() != null) {
                                GoodsAttribute goodsAttribute = goodsAttributeMap.get(attributeInfo.getId());
                                Validate.notNull(goodsAttribute, "商品口味不存在！");
                                goodsAttribute.setName(attributeInfo.getName());
                                goodsAttribute.setPrice(attributeInfo.getPrice() == null ? BigDecimal.ZERO : attributeInfo.getPrice());
                                goodsAttribute.setLastUpdateUserId(userId);
                                goodsAttribute.setLastUpdateRemark("修改口味信息！");
                                DatabaseHelper.update(goodsAttribute);
                            } else {
                                GoodsAttribute goodsAttribute = buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, goodsId, goodsAttributeGroup.getId(), userId);
                                insertGoodsAttributes.add(goodsAttribute);
                            }
                        }
                    } else {
                        GoodsAttributeGroup goodsAttributeGroup = buildGoodsAttributeGroup(tenantId, tenantCode, branchId, goodsId, attributeGroupInfo, userId);
                        DatabaseHelper.insert(goodsAttributeGroup);

                        for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                            GoodsAttribute goodsAttribute = buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, goodsId, goodsAttributeGroup.getId(), userId);
                            insertGoodsAttributes.add(goodsAttribute);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(insertGoodsAttributes)) {
                        DatabaseHelper.insertAll(insertGoodsAttributes);
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

            List<SaveGoodsModel.AttributeGroupInfo> attributeGroupInfos = saveGoodsModel.getAttributeGroupInfos();
            if (CollectionUtils.isNotEmpty(attributeGroupInfos)) {
                List<GoodsAttribute> insertGoodsAttributes = new ArrayList<GoodsAttribute>();
                for (SaveGoodsModel.AttributeGroupInfo attributeGroupInfo : attributeGroupInfos) {
                    GoodsAttributeGroup goodsAttributeGroup = buildGoodsAttributeGroup(tenantId, tenantCode, branchId, goodsId, attributeGroupInfo, userId);
                    DatabaseHelper.insert(goodsAttributeGroup);

                    for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                        GoodsAttribute goodsAttribute = buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, goodsId, goodsAttributeGroup.getId(), userId);
                        insertGoodsAttributes.add(goodsAttribute);
                    }
                }
                DatabaseHelper.insertAll(insertGoodsAttributes);
            }
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存商品信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }

    private GoodsAttributeGroup buildGoodsAttributeGroup(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.AttributeGroupInfo attributeGroupInfo, BigInteger userId) {
        GoodsAttributeGroup goodsAttributeGroup = new GoodsAttributeGroup();
        goodsAttributeGroup.setTenantId(tenantId);
        goodsAttributeGroup.setTenantCode(tenantCode);
        goodsAttributeGroup.setBranchId(branchId);
        goodsAttributeGroup.setGoodsId(goodsId);
        goodsAttributeGroup.setName(attributeGroupInfo.getName());
        goodsAttributeGroup.setCreateUserId(userId);
        goodsAttributeGroup.setLastUpdateUserId(userId);
        goodsAttributeGroup.setLastUpdateRemark("新增口味组信息！");
        return goodsAttributeGroup;
    }

    private GoodsAttribute buildGoodsAttribute(SaveGoodsModel.AttributeInfo attributeInfo, BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, BigInteger goodsAttributeGroupId, BigInteger userId) {
        GoodsAttribute goodsAttribute = new GoodsAttribute();
        goodsAttribute.setTenantId(tenantId);
        goodsAttribute.setTenantCode(tenantCode);
        goodsAttribute.setBranchId(branchId);
        goodsAttribute.setGoodsId(goodsId);
        goodsAttribute.setGoodsAttributeGroupId(goodsAttributeGroupId);
        goodsAttribute.setName(attributeInfo.getName());
        goodsAttribute.setPrice(attributeInfo.getPrice() == null ? BigDecimal.ZERO : attributeInfo.getPrice());
        goodsAttribute.setCreateUserId(userId);
        goodsAttribute.setLastUpdateUserId(userId);
        goodsAttribute.setLastUpdateRemark("新增属性信息！");
        return goodsAttribute;
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
        BigInteger tenantId = savePackageModel.getTenantId();
        String tenantCode = savePackageModel.getTenantCode();
        BigInteger branchId = savePackageModel.getBranchId();
        BigInteger userId = savePackageModel.getUserId();
        BigInteger id = savePackageModel.getId();
        String name = savePackageModel.getName();
        Integer type = savePackageModel.getType();
        BigInteger categoryId = savePackageModel.getCategoryId();
        String imageUrl = savePackageModel.getImageUrl();
        List<BigInteger> deleteGroupIds = savePackageModel.getDeleteGroupIds();
        List<SavePackageModel.Group> groups = savePackageModel.getGroups();
        BigDecimal price = savePackageModel.getPrice();

        Goods goods = null;
        if (id != null) {
            SearchModel goodsSearchModel = new SearchModel();
            goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
            ValidateUtils.notNull(goods, "商品不存在！");

            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            GoodsSpecification goodsSpecification = DatabaseHelper.find(GoodsSpecification.class, goodsSpecificationSearchModel);
            ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");

            if (CollectionUtils.isNotEmpty(deleteGroupIds)) {
                UpdateModel packageGroupUpdateModel = new UpdateModel(true);
                packageGroupUpdateModel.setTableName("package_group");
                packageGroupUpdateModel.addContentValue("deleted", 1);
                packageGroupUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGroupIds);
                DatabaseHelper.universalUpdate(packageGroupUpdateModel);

                UpdateModel packageGroupDetailUpdateModel = new UpdateModel();
                packageGroupDetailUpdateModel.setTableName("package_group_detail");
                packageGroupDetailUpdateModel.addContentValue("deleted", 1);
                packageGroupDetailUpdateModel.addSearchCondition("package_group_id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupIds);
                DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel);
            }
            SearchModel packageGroupSearchModel = new SearchModel(true);
            packageGroupSearchModel.addSearchCondition("package_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class, packageGroupSearchModel);

            List<BigInteger> packageGroupIds = new ArrayList<BigInteger>();
            Map<BigInteger, PackageGroup> packageGroupMap = new HashMap<BigInteger, PackageGroup>();
            for (PackageGroup packageGroup : packageGroups) {
                BigInteger packageGroupId = packageGroup.getId();
                packageGroupIds.add(packageGroupId);
                packageGroupMap.put(packageGroupId, packageGroup);
            }

            Map<String, PackageGroupDetail> packageGroupDetailMap = new HashMap<String, PackageGroupDetail>();
            if (CollectionUtils.isNotEmpty(packageGroupIds)) {
                SearchModel packageGroupDetailSearchModel = new SearchModel(true);
                packageGroupDetailSearchModel.addSearchCondition("package_group_id", Constants.SQL_OPERATION_SYMBOL_IN, packageGroupIds);
                List<PackageGroupDetail> packageGroupDetails = DatabaseHelper.findAll(PackageGroupDetail.class, packageGroupDetailSearchModel);
                for (PackageGroupDetail packageGroupDetail : packageGroupDetails) {
                    packageGroupDetailMap.put(packageGroupDetail.getGoodsId() + "_" + packageGroupDetail.getGoodsSpecificationId(), packageGroupDetail);
                }
            }

            for (SavePackageModel.Group group : groups) {
                BigInteger groupId = group.getId();
                String groupName = group.getGroupName();
                int groupType = group.getGroupType();
                int optionalQuantity = group.getOptionalQuantity();
                List<BigInteger> deleteGroupDetailIds = group.getDeleteGroupDetailIds();
                List<SavePackageModel.GroupDetail> groupDetails = group.getGroupDetails();

                if (CollectionUtils.isNotEmpty(deleteGroupDetailIds)) {
                    UpdateModel packageGroupDetailUpdateModel = new UpdateModel();
                    packageGroupDetailUpdateModel.setTableName("package_group_detail");
                    packageGroupDetailUpdateModel.addContentValue("deleted", 1);
                    packageGroupDetailUpdateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupDetailIds);
                    DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel);
                }

                PackageGroup packageGroup = null;
                if (groupId == null) {
                    PackageGroup.Builder packageGroupBuilder = PackageGroup.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .packageId(id)
                            .groupName(groupName)
                            .groupType(groupType);

                    if (groupType == 1) {
                        packageGroupBuilder.optionalQuantity(optionalQuantity);
                    }
                    packageGroup = packageGroupBuilder.createUserId(userId).lastUpdateUserId(userId).build();
                    DatabaseHelper.insert(packageGroup);
                } else {
                    packageGroup = packageGroupMap.get(groupId);
                    ValidateUtils.notNull(packageGroup, "套餐组不存在！");
                    packageGroup.setGroupName(groupName);
                    packageGroup.setGroupType(groupType);
                    if (groupType == 1) {
                        packageGroup.setOptionalQuantity(optionalQuantity);
                    }
                    packageGroup.setLastUpdateUserId(userId);
                    DatabaseHelper.update(packageGroup);
                }

                for (SavePackageModel.GroupDetail groupDetail : groupDetails) {
                    BigInteger goodsId = groupDetail.getGoodsId();
                    BigInteger goodsSpecificationId = groupDetail.getGoodsSpecificationId();
                    Integer quantity = groupDetail.getQuantity();
                    String key = goodsId + "_" + goodsSpecificationId;
                    PackageGroupDetail packageGroupDetail = packageGroupDetailMap.get(key);
                    if (packageGroupDetail == null) {
                        PackageGroupDetail.Builder packageGroupDetailBuilder = PackageGroupDetail.builder()
                                .tenantId(tenantId)
                                .tenantCode(tenantCode)
                                .branchId(branchId)
                                .packageId(id)
                                .packageGroupId(packageGroup.getId())
                                .goodsId(goodsId)
                                .goodsSpecificationId(goodsSpecificationId);
                        if (quantity != null) {
                            packageGroupDetailBuilder.quantity(quantity);
                        }
                        packageGroupDetail = packageGroupDetailBuilder.lastUpdateUserId(userId).build();
                        DatabaseHelper.insert(packageGroupDetail);
                    } else {
                        packageGroupDetail.setGoodsId(goodsId);
                        packageGroupDetail.setGoodsSpecificationId(goodsSpecificationId);
                        if (quantity != null) {
                            packageGroupDetail.setQuantity(quantity);
                        }
                        packageGroupDetail.setLastUpdateUserId(userId);
                        DatabaseHelper.update(packageGroupDetail);
                    }
                }
            }
            goods.setName(name);
            goods.setCategoryId(categoryId);
            goods.setImageUrl(imageUrl);
            goods.setLastUpdateUserId(userId);
            DatabaseHelper.update(goods);

            goodsSpecification.setPrice(price);
            goodsSpecification.setLastUpdateUserId(userId);
            DatabaseHelper.update(goodsSpecification);
        } else {
            goods = Goods.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .name(name)
                    .type(type)
                    .categoryId(categoryId)
                    .imageUrl(imageUrl)
                    .stocked(false)
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .build();
            DatabaseHelper.insert(goods);

            BigInteger packageId = goods.getId();

            GoodsSpecification goodsSpecification = GoodsSpecification.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .goodsId(packageId)
                    .name(Constants.VARCHAR_DEFAULT_VALUE)
                    .price(price)
                    .stock(Constants.DECIMAL_DEFAULT_VALUE)
                    .createUserId(userId)
                    .lastUpdateUserId(userId)
                    .build();
            DatabaseHelper.insert(goodsSpecification);

            for (SavePackageModel.Group group : groups) {
                String groupName = group.getGroupName();
                int groupType = group.getGroupType();
                Integer optionalQuantity = group.getOptionalQuantity();
                List<SavePackageModel.GroupDetail> groupDetails = group.getGroupDetails();

                PackageGroup.Builder packageGroupBuilder = PackageGroup.builder()
                        .tenantId(tenantId)
                        .tenantCode(tenantCode)
                        .branchId(branchId)
                        .packageId(packageId)
                        .groupName(groupName)
                        .groupType(groupType);
                if (groupType == 1) {
                    packageGroupBuilder.optionalQuantity(optionalQuantity);
                }
                PackageGroup packageGroup = packageGroupBuilder.createUserId(userId).lastUpdateUserId(userId).build();
                DatabaseHelper.insert(packageGroup);

                for (SavePackageModel.GroupDetail groupDetail : groupDetails) {
                    BigInteger goodsId = groupDetail.getGoodsId();
                    BigInteger goodsSpecificationId = groupDetail.getGoodsSpecificationId();
                    Integer quantity = groupDetail.getQuantity();
                    PackageGroupDetail.Builder packageGroupDetailBuilder = PackageGroupDetail.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .branchId(branchId)
                            .packageId(packageId)
                            .packageGroupId(packageGroup.getId())
                            .goodsId(goodsId)
                            .goodsSpecificationId(goodsSpecificationId);

                    if (quantity != null) {
                        packageGroupDetailBuilder.quantity(quantity);
                    }
                    PackageGroupDetail packageGroupDetail = packageGroupDetailBuilder.createUserId(userId).lastUpdateUserId(userId).build();
                    DatabaseHelper.insert(packageGroupDetail);
                }
            }
        }
        return ApiRest.builder().data(goods).message("保存套餐成功！").successful(true).build();
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
        UpdateModel goodsAttributeGroupUpdateModel = new UpdateModel(true);
        goodsAttributeGroupUpdateModel.setTableName("goods_attribute_group");
        goodsAttributeGroupUpdateModel.addContentValue("deleted", 1);
        goodsAttributeGroupUpdateModel.addContentValue("last_update_user_id", userId);
        goodsAttributeGroupUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
        goodsAttributeGroupUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsAttributeGroupUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsAttributeGroupUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsAttributeGroupUpdateModel);

        // 删除该商品的所有口味
        UpdateModel goodsAttributeUpdateModel = new UpdateModel(true);
        goodsAttributeUpdateModel.setTableName("goods_attribute");
        goodsAttributeUpdateModel.addContentValue("deleted", 1);
        goodsAttributeUpdateModel.addContentValue("last_update_user_id", userId);
        goodsAttributeUpdateModel.addContentValue("last_update_remark", "删除商品口味组信息！");
        goodsAttributeUpdateModel.addSearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsAttributeUpdateModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsAttributeUpdateModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsAttributeUpdateModel);

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
