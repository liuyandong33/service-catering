package build.dream.catering.services;

import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.models.goods.*;
import build.dream.catering.utils.TenantConfigUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
        BigInteger tenantId = countModel.obtainTenantId();
        BigInteger branchId = countModel.obtainBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        long count = DatabaseHelper.count(Goods.class, searchModel);

        return ApiRest.builder().data(count).message("查询商品数量成功！").successful(true).build();
    }

    /**
     * 查询商品列表
     *
     * @param listModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest list(ListModel listModel) {
        BigInteger tenantId = listModel.obtainTenantId();
        BigInteger branchId = listModel.obtainTenantId();
        int page = listModel.getPage();
        int rows = listModel.getRows();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        searchConditions.add(new SearchCondition(Goods.ColumnName.DELETED, Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel searchModel = new SearchModel(searchConditions);
        long count = DatabaseHelper.count(Goods.class, searchModel);

        List<Map<String, Object>> goodsInfos = new ArrayList<Map<String, Object>>();
        if (count > 0) {
            PagedSearchModel pagedSearchModel = new PagedSearchModel(searchConditions, page, rows);
            List<Goods> goodsList = DatabaseHelper.findAllPaged(Goods.class, pagedSearchModel);

            List<BigInteger> goodsIds = new ArrayList<BigInteger>();
            List<BigInteger> packageIds = new ArrayList<BigInteger>();
            for (Goods goods : goodsList) {
                int type = goods.getType();
                if (type == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                    goodsIds.add(goods.getId());
                } else if (type == Constants.GOODS_TYPE_PACKAGE) {
                    packageIds.add(goods.getId());
                }
            }

            if (CollectionUtils.isNotEmpty(goodsIds)) {
                List<SearchCondition> searchConditionList = new ArrayList<SearchCondition>();
                searchConditionList.add(new SearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
                searchConditionList.add(new SearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
                searchConditionList.add(new SearchCondition("deleted", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));
                searchConditionList.add(new SearchCondition("goods_id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds));

                SearchModel goodsSpecificationSearchModel = new SearchModel(searchConditionList);
                List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);
                Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = new HashMap<BigInteger, List<GoodsSpecification>>();
                for (GoodsSpecification goodsSpecification : goodsSpecifications) {
                    BigInteger goodsId = goodsSpecification.getGoodsId();
                    List<GoodsSpecification> goodsSpecificationList = goodsSpecificationMap.get(goodsId);
                    if (CollectionUtils.isEmpty(goodsSpecificationList)) {
                        goodsSpecificationList = new ArrayList<GoodsSpecification>();
                        goodsSpecificationMap.put(goodsId, goodsSpecificationList);
                    }
                    goodsSpecificationList.add(goodsSpecification);
                }

                SearchModel goodsAttributeGroupSearchModel = new SearchModel(searchConditionList);
                List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
                Map<BigInteger, List<GoodsAttributeGroup>> goodsAttributeGroupMap = new HashMap<BigInteger, List<GoodsAttributeGroup>>();
                for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                    BigInteger goodsId = goodsAttributeGroup.getGoodsId();
                    List<GoodsAttributeGroup> goodsAttributeGroupList = goodsAttributeGroupMap.get(goodsId);
                    if (CollectionUtils.isEmpty(goodsAttributeGroupList)) {
                        goodsAttributeGroupList = new ArrayList<GoodsAttributeGroup>();
                        goodsAttributeGroupMap.put(goodsId, goodsAttributeGroupList);
                    }
                    goodsAttributeGroupList.add(goodsAttributeGroup);
                }

                SearchModel goodsAttributeSearchModel = new SearchModel(searchConditionList);
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

                for (Goods goods : goodsList) {
                    if (goods.getType() == Constants.GOODS_TYPE_PACKAGE) {
                        continue;
                    }
                    BigInteger goodsId = goods.getId();
                    Map<String, Object> goodsInfo = new HashMap<String, Object>();
                    goodsInfo.put("goods", goods);
                    goodsInfo.put("goodsSpecifications", goodsSpecificationMap.get(goodsId));

                    List<GoodsAttributeGroup> goodsAttributeGroupList = goodsAttributeGroupMap.get(goodsId);
                    List<Map<String, Object>> goodsAttributeGroupInfos = new ArrayList<Map<String, Object>>();
                    if (CollectionUtils.isNotEmpty(goodsAttributeGroupList)) {
                        for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroupList) {
                            Map<String, Object> goodsAttributeGroupInfo = ApplicationHandler.toMap(goodsAttributeGroup);
                            goodsAttributeGroupInfo.put("goodsAttributes", goodsAttributeMap.get(goodsAttributeGroup.getId()));
                            goodsAttributeGroupInfos.add(goodsAttributeGroupInfo);
                        }
                    }

                    goodsInfo.put("attributeGroups", goodsAttributeGroupInfos);
                    goodsInfos.add(goodsInfo);
                }
            }

            if (CollectionUtils.isNotEmpty(packageIds)) {
                List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class,
                        TupleUtils.buildTuple3(PackageGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId),
                        TupleUtils.buildTuple3(PackageGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId),
                        TupleUtils.buildTuple3(PackageGroup.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_IN, packageIds));
                Map<BigInteger, List<PackageGroup>> packageGroupMap = new HashMap<BigInteger, List<PackageGroup>>();
                for (PackageGroup packageGroup : packageGroups) {
                    BigInteger packageId = packageGroup.getPackageId();
                    List<PackageGroup> packageGroupList = packageGroupMap.get(packageId);
                    if (CollectionUtils.isEmpty(packageGroupList)) {
                        packageGroupList = new ArrayList<PackageGroup>();
                        packageGroupMap.put(packageId, packageGroupList);
                    }
                    packageGroupList.add(packageGroup);
                }

                List<Map<String, Object>> packageInfos = goodsMapper.listPackageInfos(packageIds, null);
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

                for (Goods goods : goodsList) {
                    if (goods.getType() == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                        continue;
                    }
                    List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
                    List<PackageGroup> packageGroupList = packageGroupMap.get(goods.getId());
                    for (PackageGroup packageGroup : packageGroupList) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("group", packageGroup);
                        item.put("details", packageInfoMap.get(packageGroup.getId()));
                        groups.add(item);
                    }

                    Map<String, Object> goodsInfo = new HashMap<String, Object>();
                    goodsInfo.put("goods", goods);
                    goodsInfo.put("groups", groups);

                    goodsInfos.add(goodsInfo);
                }
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", goodsInfos);
        return ApiRest.builder().data(data).message("查询商品列表成功！").successful(true).build();
    }


    @Transactional(readOnly = true)
    public ApiRest obtainGoodsInfo(ObtainGoodsInfoModel obtainGoodsInfoModel) {
        BigInteger tenantId = obtainGoodsInfoModel.obtainTenantId();
        BigInteger branchId = obtainGoodsInfoModel.obtainBranchId();
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
            List<Map<String, Object>> packageInfos = goodsMapper.listPackageInfos(packageIds, null);

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
        return ApiRest.builder().data(data).message("获取商品信息成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest obtainAllGoodsInfos(ObtainAllGoodsInfosModel obtainAllGoodsInfosModel) {
        BigInteger tenantId = obtainAllGoodsInfosModel.obtainTenantId();
        BigInteger branchId = obtainAllGoodsInfosModel.obtainBranchId();
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

                List<Map<String, Object>> packageInfos = goodsMapper.listPackageInfos(packageIds, null);
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
        return ApiRest.builder().data(data).message("获取菜品信息成功！").successful(true).build();
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
        BigInteger tenantId = saveGoodsModel.obtainTenantId();
        String tenantCode = saveGoodsModel.obtainTenantCode();
        BigInteger branchId = saveGoodsModel.obtainBranchId();
        BigInteger userId = saveGoodsModel.obtainUserId();

        Goods goods = null;
        if (saveGoodsModel.getId() != null) {
            BigInteger goodsId = saveGoodsModel.getId();

            SearchModel goodsSearchModel = new SearchModel(true);
            goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
            ValidateUtils.notNull(goods, "商品不存在！");

            // 验证商品是否可以编辑
            validateCanNotOperate(tenantId, branchId, "goods", goodsId, 2);

            goods.setName(saveGoodsModel.getName());
            goods.setCategoryId(saveGoodsModel.getCategoryId());
            goods.setImageUrl(saveGoodsModel.getImageUrl());
            DatabaseHelper.update(goods);

            // 删除需要删除的规格
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsSpecificationIds())) {
                UpdateModel updateModel = new UpdateModel(true);
                updateModel.setTableName(GoodsSpecification.TABLE_NAME);
                updateModel.addContentValue(GoodsSpecification.ColumnName.UPDATED_USER_ID, userId);
                updateModel.addContentValue(GoodsSpecification.ColumnName.UPDATED_REMARK, "删除商品规格信息！");
                updateModel.addContentValue(GoodsSpecification.ColumnName.DELETED_TIME, new Date());
                updateModel.addContentValue(GoodsSpecification.ColumnName.DELETED, 1);
                updateModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                updateModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                updateModel.addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsSpecificationIds());
                DatabaseHelper.universalUpdate(updateModel);
            }

            // 删除需要删除的口味组及其下的口味
            if (CollectionUtils.isNotEmpty(saveGoodsModel.getDeleteGoodsAttributeGroupIds())) {
                UpdateModel deleteGoodsAttributeGroupUpdateModel = new UpdateModel(true);
                deleteGoodsAttributeGroupUpdateModel.setTableName(GoodsAttributeGroup.TABLE_NAME);
                deleteGoodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_USER_ID, userId);
                deleteGoodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_REMARK, "删除商品口味组信息！");
                deleteGoodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.DELETED_TIME, new Date());
                deleteGoodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.DELETED, 1);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsAttributeGroupIds());
                DatabaseHelper.universalUpdate(deleteGoodsAttributeGroupUpdateModel);

                UpdateModel deleteGoodsAttributeUpdateModel = new UpdateModel(true);
                deleteGoodsAttributeUpdateModel.setTableName(GoodsAttribute.TABLE_NAME);
                deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId);
                deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味信息！");
                deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, new Date());
                deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED, 1);
                deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ATTRIBUTE_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_IN, saveGoodsModel.getDeleteGoodsAttributeGroupIds());
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
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
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
                    ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");
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
                    deleteGoodsAttributeUpdateModel.setTableName(GoodsAttribute.TABLE_NAME);
                    deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId);
                    deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味信息！");
                    deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, new Date());
                    deleteGoodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED, 1);
                    deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                    deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                    deleteGoodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsAttributeIds);
                    DatabaseHelper.universalUpdate(deleteGoodsAttributeUpdateModel);
                }

                // 查询出需要修改的口味组
                SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeGroupIds);
                List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
                Map<BigInteger, GoodsAttributeGroup> goodsAttributeGroupMap = new HashMap<BigInteger, GoodsAttributeGroup>();
                for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                    goodsAttributeGroupMap.put(goodsAttributeGroup.getId(), goodsAttributeGroup);
                }

                // 查询出需要修改的口味
                SearchModel goodsAttributeSearchModel = new SearchModel(true);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsAttributeIds);
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
                        ValidateUtils.notNull(goodsAttributeGroup, "口味组不存在！");
                        goodsAttributeGroup.setName(attributeGroupInfo.getName());
                        goodsAttributeGroup.setUpdatedUserId(userId);
                        goodsAttributeGroup.setUpdatedRemark("修改口味组信息！");
                        DatabaseHelper.update(goodsAttributeGroup);

                        for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                            if (attributeInfo.getId() != null) {
                                GoodsAttribute goodsAttribute = goodsAttributeMap.get(attributeInfo.getId());
                                ValidateUtils.notNull(goodsAttribute, "商品口味不存在！");
                                goodsAttribute.setName(attributeInfo.getName());
                                goodsAttribute.setPrice(attributeInfo.getPrice() == null ? BigDecimal.ZERO : attributeInfo.getPrice());
                                goodsAttribute.setUpdatedUserId(userId);
                                goodsAttribute.setUpdatedRemark("修改口味信息！");
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
            goods = new Goods();
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setName(saveGoodsModel.getName());
            goods.setType(saveGoodsModel.getType());
            goods.setCategoryId(saveGoodsModel.getCategoryId());
            goods.setImageUrl(saveGoodsModel.getImageUrl());
            goods.setCreatedUserId(userId);
            goods.setUpdatedUserId(userId);
            goods.setUpdatedRemark("新增商品信息！");
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
        ElasticsearchUtils.index(Constants.ELASTICSEARCH_INDEX_GOODS, Goods.TABLE_NAME, goods);
        return ApiRest.builder().data(goods).message("保存商品信息成功！").successful(true).build();
    }

    private GoodsAttributeGroup buildGoodsAttributeGroup(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.AttributeGroupInfo attributeGroupInfo, BigInteger userId) {
        GoodsAttributeGroup goodsAttributeGroup = new GoodsAttributeGroup();
        goodsAttributeGroup.setTenantId(tenantId);
        goodsAttributeGroup.setTenantCode(tenantCode);
        goodsAttributeGroup.setBranchId(branchId);
        goodsAttributeGroup.setGoodsId(goodsId);
        goodsAttributeGroup.setName(attributeGroupInfo.getName());
        goodsAttributeGroup.setCreatedUserId(userId);
        goodsAttributeGroup.setUpdatedUserId(userId);
        goodsAttributeGroup.setUpdatedRemark("新增口味组信息！");
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
        goodsAttribute.setCreatedUserId(userId);
        goodsAttribute.setUpdatedUserId(userId);
        goodsAttribute.setUpdatedRemark("新增属性信息！");
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
        goodsSpecification.setCreatedUserId(userId);
        goodsSpecification.setUpdatedUserId(userId);
        goodsSpecification.setUpdatedRemark("新增规格信息！");
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
        BigInteger tenantId = deleteGoodsSpecificationModel.obtainTenantId();
        BigInteger branchId = deleteGoodsSpecificationModel.obtainBranchId();
        BigInteger userId = deleteGoodsSpecificationModel.obtainUserId();
        BigInteger goodsSpecificationId = deleteGoodsSpecificationModel.getGoodsSpecificationId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsSpecificationId);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        GoodsSpecification goodsSpecification = DatabaseHelper.find(GoodsSpecification.class, searchModel);

        ValidateUtils.notNull(goodsSpecification, "菜品规格不存在！");
        goodsSpecification.setDeleted(true);
        goodsSpecification.setUpdatedUserId(userId);
        goodsSpecification.setUpdatedRemark("删除菜品规格信息！");
        DatabaseHelper.update(goodsSpecification);
        return ApiRest.builder().message("删除菜品规格成功！").successful(true).build();
    }

    /**
     * 保存套餐
     *
     * @param savePackageModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest savePackage(SavePackageModel savePackageModel) {
        BigInteger tenantId = savePackageModel.obtainTenantId();
        String tenantCode = savePackageModel.obtainTenantCode();
        BigInteger branchId = savePackageModel.obtainBranchId();
        BigInteger userId = savePackageModel.obtainUserId();
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
            goodsSearchModel.addSearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSearchModel.addSearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSearchModel.addSearchCondition(Goods.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
            ValidateUtils.notNull(goods, "商品不存在！");

            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            GoodsSpecification goodsSpecification = DatabaseHelper.find(GoodsSpecification.class, goodsSpecificationSearchModel);
            ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");

            if (CollectionUtils.isNotEmpty(deleteGroupIds)) {
                UpdateModel packageGroupUpdateModel = new UpdateModel(true);
                packageGroupUpdateModel.setTableName(PackageGroup.TABLE_NAME);
                packageGroupUpdateModel.addContentValue(PackageGroup.ColumnName.DELETED_TIME, new Date());
                packageGroupUpdateModel.addContentValue(PackageGroup.ColumnName.DELETED, 1);
                packageGroupUpdateModel.addSearchCondition(PackageGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGroupIds);
                DatabaseHelper.universalUpdate(packageGroupUpdateModel);

                UpdateModel packageGroupDetailUpdateModel = new UpdateModel();
                packageGroupDetailUpdateModel.setTableName(PackageGroupDetail.TABLE_NAME);
                packageGroupDetailUpdateModel.addContentValue(PackageGroupDetail.ColumnName.DELETED_TIME, new Date());
                packageGroupDetailUpdateModel.addContentValue(PackageGroupDetail.ColumnName.DELETED, 1);
                packageGroupDetailUpdateModel.addSearchCondition(PackageGroupDetail.ColumnName.PACKAGE_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupIds);
                DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel);
            }
            SearchModel packageGroupSearchModel = new SearchModel(true);
            packageGroupSearchModel.addSearchCondition(PackageGroup.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
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
                packageGroupDetailSearchModel.addSearchCondition(PackageGroupDetail.ColumnName.PACKAGE_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_IN, packageGroupIds);
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
                    packageGroupDetailUpdateModel.setTableName(PackageGroupDetail.TABLE_NAME);
                    packageGroupDetailUpdateModel.addContentValue(PackageGroupDetail.ColumnName.DELETED_TIME, new Date());
                    packageGroupDetailUpdateModel.addContentValue(PackageGroupDetail.ColumnName.DELETED, 1);
                    packageGroupDetailUpdateModel.addSearchCondition(PackageGroupDetail.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupDetailIds);
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
                    packageGroup = packageGroupBuilder.createdUserId(userId).updatedUserId(userId).build();
                    DatabaseHelper.insert(packageGroup);
                } else {
                    packageGroup = packageGroupMap.get(groupId);
                    ValidateUtils.notNull(packageGroup, "套餐组不存在！");
                    packageGroup.setGroupName(groupName);
                    packageGroup.setGroupType(groupType);
                    if (groupType == 1) {
                        packageGroup.setOptionalQuantity(optionalQuantity);
                    }
                    packageGroup.setUpdatedUserId(userId);
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
                        packageGroupDetail = packageGroupDetailBuilder.updatedUserId(userId).build();
                        DatabaseHelper.insert(packageGroupDetail);
                    } else {
                        packageGroupDetail.setGoodsId(goodsId);
                        packageGroupDetail.setGoodsSpecificationId(goodsSpecificationId);
                        if (quantity != null) {
                            packageGroupDetail.setQuantity(quantity);
                        }
                        packageGroupDetail.setUpdatedUserId(userId);
                        DatabaseHelper.update(packageGroupDetail);
                    }
                }
            }
            goods.setName(name);
            goods.setCategoryId(categoryId);
            goods.setImageUrl(imageUrl);
            goods.setUpdatedUserId(userId);
            DatabaseHelper.update(goods);

            goodsSpecification.setPrice(price);
            goodsSpecification.setUpdatedUserId(userId);
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
                    .createdUserId(userId)
                    .updatedUserId(userId)
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
                    .createdUserId(userId)
                    .updatedUserId(userId)
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
                PackageGroup packageGroup = packageGroupBuilder.createdUserId(userId).updatedUserId(userId).build();
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
                    PackageGroupDetail packageGroupDetail = packageGroupDetailBuilder.createdUserId(userId).updatedUserId(userId).build();
                    DatabaseHelper.insert(packageGroupDetail);
                }
            }
        }
        ElasticsearchUtils.index(Constants.ELASTICSEARCH_INDEX_GOODS, Goods.TABLE_NAME, goods);
        return ApiRest.builder().data(goods).message("保存套餐成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest listCategories(ListCategoriesModel listCategoriesModel) {
        BigInteger tenantId = listCategoriesModel.obtainTenantId();
        BigInteger branchId = listCategoriesModel.obtainBranchId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(GoodsCategory.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(GoodsCategory.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        List<GoodsCategory> goodsCategories = DatabaseHelper.findAll(GoodsCategory.class, searchModel);

        List<ZTreeNode> zTreeNodes = new ArrayList<ZTreeNode>();
        for (GoodsCategory goodsCategory : goodsCategories) {
            zTreeNodes.add(new ZTreeNode(goodsCategory.getId().toString(), goodsCategory.getName(), goodsCategory.getParentId().toString()));
        }
        return ApiRest.builder().data(zTreeNodes).message("查询菜品分类成功！").successful(true).build();
    }

    /**
     * 删除商品、商品规格、商品口味组、商品口味
     *
     * @param deleteGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteGoods(DeleteGoodsModel deleteGoodsModel) {
        BigInteger tenantId = deleteGoodsModel.obtainTenantId();
        BigInteger branchId = deleteGoodsModel.obtainBranchId();
        BigInteger userId = deleteGoodsModel.obtainUserId();
        BigInteger goodsId = deleteGoodsModel.getGoodsId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Goods.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        searchModel.addSearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        Goods goods = DatabaseHelper.find(Goods.class, searchModel);
        ValidateUtils.notNull(goods, "商品不存在！");

        validateCanNotOperate(tenantId, branchId, Goods.TABLE_NAME, goodsId, 2);

        Date currentTime = new Date();
        goods.setUpdatedUserId(userId);
        goods.setUpdatedRemark("删除商品信息！");
        goods.setDeletedTime(currentTime);
        goods.setDeleted(true);
        DatabaseHelper.update(goods);

        // 删除该商品的所有规格
        UpdateModel goodsSpecificationUpdateModel = new UpdateModel(true);
        goodsSpecificationUpdateModel.setTableName(GoodsSpecification.TABLE_NAME);
        goodsSpecificationUpdateModel.addContentValue(GoodsSpecification.ColumnName.DELETED_TIME, currentTime);
        goodsSpecificationUpdateModel.addContentValue(GoodsSpecification.ColumnName.DELETED, 1);
        goodsSpecificationUpdateModel.addContentValue(GoodsSpecification.ColumnName.UPDATED_USER_ID, userId);
        goodsSpecificationUpdateModel.addContentValue(GoodsSpecification.ColumnName.UPDATED_REMARK, "删除商品规格信息！");
        goodsSpecificationUpdateModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsSpecificationUpdateModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSpecificationUpdateModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsSpecificationUpdateModel);

        // 删除该商品的所有口味组
        UpdateModel goodsAttributeGroupUpdateModel = new UpdateModel(true);
        goodsAttributeGroupUpdateModel.setTableName(GoodsAttributeGroup.TABLE_NAME);
        goodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.DELETED_TIME, currentTime);
        goodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.DELETED, 1);
        goodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_USER_ID, userId);
        goodsAttributeGroupUpdateModel.addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_REMARK, "删除商品口味组信息！");
        goodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsAttributeGroupUpdateModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsAttributeGroupUpdateModel);

        // 删除该商品的所有口味
        UpdateModel goodsAttributeUpdateModel = new UpdateModel(true);
        goodsAttributeUpdateModel.setTableName(GoodsAttribute.TABLE_NAME);
        goodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, currentTime);
        goodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.DELETED, 1);
        goodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId);
        goodsAttributeUpdateModel.addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味组信息！");
        goodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        goodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsAttributeUpdateModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        DatabaseHelper.universalUpdate(goodsAttributeUpdateModel);

        ElasticsearchUtils.delete(Constants.ELASTICSEARCH_INDEX_GOODS, Goods.TABLE_NAME, goodsId.toString());
        return ApiRest.builder().message("删除商品信息成功！").successful(true).build();
    }

    /**
     * 导入商品信息
     *
     * @param importGoodsModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest importGoods(ImportGoodsModel importGoodsModel) {
        BigInteger tenantId = importGoodsModel.obtainTenantId();
        String tenantCode = importGoodsModel.obtainTenantCode();
        BigInteger branchId = importGoodsModel.obtainBranchId();
        BigInteger userId = importGoodsModel.obtainUserId();
        String zipGoodsInfos = importGoodsModel.getZipGoodsInfos();
        List<Map<String, Object>> goodsInfos = GsonUtils.fromJson(ZipUtils.unzipText(zipGoodsInfos), List.class);
        int count = goodsInfos.size();

        TenantConfig tenantConfig = TenantConfigUtils.addTenantConfig(BigInteger.ONE, "goods_num", count);
        int currentValue = tenantConfig.getCurrentValue();
        int maxValue = tenantConfig.getMaxValue();
        ValidateUtils.isTrue(currentValue <= maxValue, "您最多可以添加" + (maxValue - currentValue + count) + "条商品信息！");

        List<Goods> goodsList = new ArrayList<Goods>();
        Map<String, Goods> goodsMap = new HashMap<String, Goods>();
        Map<String, GoodsSpecification> goodsSpecificationMap = new HashMap<String, GoodsSpecification>();

        for (Map<String, Object> goodsInfo : goodsInfos) {
            String uuid = UUID.randomUUID().toString();
            Goods goods = new Goods();
            goods.setTenantId(tenantId);
            goods.setTenantCode(tenantCode);
            goods.setBranchId(branchId);
            goods.setName(MapUtils.getString(goodsInfo, "name"));
            goods.setType(Constants.GOODS_TYPE_ORDINARY_GOODS);
            goods.setCategoryId(BigInteger.ONE);
            goods.setCreatedUserId(userId);
            goods.setUpdatedUserId(userId);
            goodsMap.put(uuid, goods);
            goodsList.add(goods);

            GoodsSpecification goodsSpecification = new GoodsSpecification();
            goodsSpecification.setTenantId(tenantId);
            goodsSpecification.setTenantCode(tenantCode);
            goodsSpecification.setBranchId(branchId);
            goodsSpecification.setPrice(BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "price")));
            goodsSpecification.setCreatedUserId(userId);
            goodsSpecification.setUpdatedUserId(userId);
            goodsSpecificationMap.put(uuid, goodsSpecification);
        }

        DatabaseHelper.insertAll(goodsList);

        List<GoodsSpecification> goodsSpecifications = new ArrayList<GoodsSpecification>();
        for (Map.Entry<String, GoodsSpecification> entry : goodsSpecificationMap.entrySet()) {
            GoodsSpecification goodsSpecification = entry.getValue();
            goodsSpecification.setGoodsId(goodsMap.get(entry.getKey()).getId());
            goodsSpecifications.add(goodsSpecification);
        }

        DatabaseHelper.insertAll(goodsSpecifications);

        ElasticsearchUtils.indexAll(Constants.ELASTICSEARCH_INDEX_GOODS, Goods.TABLE_NAME, goodsList);
        return ApiRest.builder().message("导入商品信息成功！").successful(true).build();
    }

    /**
     * 检索商品
     *
     * @param searchGoodsModel
     * @return
     */
    public ApiRest searchGoods(SearchGoodsModel searchGoodsModel) {
        BigInteger tenantId = searchGoodsModel.obtainTenantId();
        BigInteger branchId = searchGoodsModel.obtainBranchId();
        int page = searchGoodsModel.getPage();
        int rows = searchGoodsModel.getRows();
        BigInteger categoryId = searchGoodsModel.getCategoryId();
        String searchStr = searchGoodsModel.getSearchStr();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(Goods.FieldName.TENANT_ID, tenantId.longValue()));
        boolQueryBuilder.must(QueryBuilders.termQuery(Goods.FieldName.BRANCH_ID, branchId.longValue()));

        if (categoryId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(Goods.FieldName.CATEGORY_ID, categoryId.longValue()));
        }

        if (StringUtils.isNotBlank(searchStr)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(Goods.FieldName.NAME, searchStr));
        }

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags(Constants.ELASTICSEARCH_HIGHLIGHT_PRE_TAG);
        highlightBuilder.postTags(Constants.ELASTICSEARCH_HIGHLIGHT_POST_TAG);
        highlightBuilder.field(Goods.FieldName.NAME);

        SortBuilder sortBuilder = SortBuilders.fieldSort(Goods.FieldName.UPDATED_TIME).order(SortOrder.DESC);

        TransportClient transportClient = ElasticsearchUtils.obtainTransportClient();
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(Constants.ELASTICSEARCH_INDEX_GOODS)
                .setTypes(Goods.TABLE_NAME)
                .highlighter(highlightBuilder)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .addSort(sortBuilder)
                .setFrom((page - 1) * rows)
                .setSize(rows);
        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits searchHits = searchResponse.getHits();

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> result = searchHit.getSource();
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField nameHighlightField = highlightFields.get(Goods.FieldName.NAME);

            if (nameHighlightField != null) {
                Text[] nameFragments = nameHighlightField.getFragments();
                result.put(Goods.FieldName.NAME, StringUtils.join(nameFragments, ""));
            }

            results.add(result);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", searchHits.totalHits);
        data.put("rows", results);

        return ApiRest.builder().data(data).message("检索商品成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest test(BigInteger tenantId, BigInteger branchId) {
        List<Goods> goodsList = DatabaseHelper.callMapperMethod(GoodsMapper.class, "findAllGoodsInfos", TupleUtils.buildTuple2(BigInteger.class, tenantId), TupleUtils.buildTuple2(BigInteger.class, branchId), TupleUtils.buildTuple2(List.class, null));
        ElasticsearchUtils.indexAll(Constants.ELASTICSEARCH_INDEX_GOODS, Goods.TABLE_NAME, goodsList);
        return ApiRest.builder().message("操作成功！").successful(true).build();
    }
}
