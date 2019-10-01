package build.dream.catering.services;

import build.dream.catering.beans.PackageDetail;
import build.dream.catering.beans.ZTreeNode;
import build.dream.catering.constants.Constants;
import build.dream.catering.domains.ElasticSearchGoods;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.models.goods.*;
import build.dream.catering.repositories.ElasticSearchGoodsRepository;
import build.dream.catering.utils.CanNotOperateUtils;
import build.dream.catering.utils.ElasticSearchUtils;
import build.dream.catering.utils.GoodsUtils;
import build.dream.catering.utils.TenantConfigUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private ElasticSearchGoodsRepository elasticSearchGoodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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
        BigInteger branchId = listModel.obtainBranchId();
        int page = listModel.getPage();
        int rows = listModel.getRows();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
        searchConditions.add(new SearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
        searchConditions.add(new SearchCondition(Goods.ColumnName.DELETED, Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));

        SearchModel searchModel = new SearchModel(searchConditions);
        long count = DatabaseHelper.count(Goods.class, searchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> goodsInfos = new ArrayList<Map<String, Object>>();

        data.put("total", count);
        data.put("rows", goodsInfos);

        ApiRest apiRest = ApiRest.builder().data(data).message("查询商品列表成功！").successful(true).build();
        if (count <= 0) {
            return apiRest;
        }

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
            Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = goodsSpecifications.stream().collect(Collectors.groupingBy(GoodsSpecification::getGoodsId));

            SearchModel goodsAttributeGroupSearchModel = new SearchModel(searchConditionList);
            List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);
            Map<BigInteger, List<GoodsAttributeGroup>> goodsAttributeGroupMap = goodsAttributeGroups.stream().collect(Collectors.groupingBy(GoodsAttributeGroup::getGoodsId));

            SearchModel goodsAttributeSearchModel = new SearchModel(searchConditionList);
            List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);
            Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = goodsAttributes.stream().collect(Collectors.groupingBy(GoodsAttribute::getGoodsId));

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
                        Map<String, Object> goodsAttributeGroupInfo = new HashMap<String, Object>();
                        goodsAttributeGroupInfo.put("attributeGroup", goodsAttributeGroup);
                        goodsAttributeGroupInfo.put("attributes", goodsAttributeMap.get(goodsAttributeGroup.getId()));
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
            Map<BigInteger, List<PackageGroup>> packageGroupMap = packageGroups.stream().collect(Collectors.groupingBy(PackageGroup::getPackageId));

            List<PackageDetail> packageDetails = goodsMapper.listPackageInfos(tenantId, branchId, packageIds, null);
            Map<BigInteger, List<PackageDetail>> packageDetailMap = packageDetails.stream().collect(Collectors.groupingBy(PackageDetail::getPackageGroupId));

            for (Goods goods : goodsList) {
                if (goods.getType() == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                    continue;
                }
                List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
                List<PackageGroup> packageGroupList = packageGroupMap.get(goods.getId());
                for (PackageGroup packageGroup : packageGroupList) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("group", packageGroup);
                    item.put("details", packageDetailMap.get(packageGroup.getId()));
                    groups.add(item);
                }

                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goods", goods);
                goodsInfo.put("groups", groups);

                goodsInfos.add(goodsInfo);
            }
        }
        return apiRest;
    }


    @Transactional(readOnly = true)
    public ApiRest obtainGoodsInfo(ObtainGoodsInfoModel obtainGoodsInfoModel) {
        BigInteger tenantId = obtainGoodsInfoModel.obtainTenantId();
        BigInteger branchId = obtainGoodsInfoModel.obtainBranchId();
        BigInteger goodsId = obtainGoodsInfoModel.getGoodsId();

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        goodsSearchModel.addSearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        goodsSearchModel.addSearchCondition(Goods.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
        Goods goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
        ValidateUtils.notNull(goods, "商品不存在！");

        Map<String, Object> data = new HashMap<String, Object>();
        int type = goods.getType();
        if (type == Constants.GOODS_TYPE_ORDINARY_GOODS) {
            SearchModel goodsSpecificationSearchModel = new SearchModel(true);
            goodsSpecificationSearchModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, goodsSpecificationSearchModel);

            SearchModel goodsUnitSearchModel = new SearchModel(true);
            goodsUnitSearchModel.addSearchCondition(GoodsUnit.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsUnit> goodsUnits = DatabaseHelper.findAll(GoodsUnit.class, goodsUnitSearchModel);

            data.put("goods", goods);
            data.put("goodsSpecifications", goodsSpecifications);
            data.put("goodsUnits", goodsUnits);

            SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
            goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, goodsAttributeGroupSearchModel);

            if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
                SearchModel goodsAttributeSearchModel = new SearchModel(true);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
                List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, goodsAttributeSearchModel);
                Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = goodsAttributes.stream().collect(Collectors.groupingBy(GoodsAttribute::getGoodsAttributeGroupId));

                List<Map<String, Object>> attributeGroups = new ArrayList<Map<String, Object>>();
                for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                    Map<String, Object> attributeGroup = new HashMap<String, Object>();
                    attributeGroup.put("attributeGroup", goodsAttributeGroup);
                    attributeGroup.put("attributes", goodsAttributeMap.get(goodsAttributeGroup.getId()));
                    attributeGroups.add(attributeGroup);
                }
                data.put("attributeGroups", attributeGroups);
            }
        } else if (type == Constants.GOODS_TYPE_PACKAGE) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(PackageGroup.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId);
            List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class, searchModel);

            List<BigInteger> packageIds = new ArrayList<BigInteger>();
            packageIds.add(goodsId);

            List<PackageDetail> packageDetails = goodsMapper.listPackageInfos(tenantId, branchId, packageIds, null);

            Map<BigInteger, List<PackageDetail>> packageDetailMap = packageDetails.stream().collect(Collectors.groupingBy(PackageDetail::getPackageGroupId));

            List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
            for (PackageGroup packageGroup : packageGroups) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("group", packageGroup);
                item.put("details", packageDetailMap.get(packageGroup.getId()));
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
        BigInteger categoryId = obtainAllGoodsInfosModel.getCategoryId();
        List<Goods> goodsInfos = goodsMapper.findAllByCategoryId(tenantId, branchId, categoryId);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isEmpty(goodsInfos)) {
            return ApiRest.builder().data(data).message("获取菜品信息成功！").successful(true).build();
        }

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> packageIds = new ArrayList<BigInteger>();
        for (Goods goods : goodsInfos) {
            BigInteger goodsId = goods.getId();
            int type = goods.getType();

            if (type == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                goodsIds.add(goodsId);
            } else if (type == Constants.GOODS_TYPE_PACKAGE) {
                packageIds.add(goodsId);
            }
        }

        Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = GoodsUtils.obtainGoodsSpecificationInfos(tenantId, branchId, goodsIds);
        Map<BigInteger, List<GoodsAttributeGroup>> goodsAttributeGroupMap = GoodsUtils.obtainGoodsAttributeGroupInfos(tenantId, branchId, goodsIds);
        Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = GoodsUtils.obtainGoodsAttributeInfos(tenantId, branchId, goodsIds);

        Map<BigInteger, List<PackageDetail>> packageDetailMap = GoodsUtils.obtainPackageGroupDetailInfos(tenantId, branchId, packageIds);
        Map<BigInteger, List<PackageGroup>> packageGroupMap = GoodsUtils.obtainPackageGroupInfos(tenantId, branchId, packageIds);

        for (Goods goods : goodsInfos) {
            BigInteger goodsId = goods.getId();
            int type = goods.getType();

            Map<String, Object> goodsInfo = null;
            if (type == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                goodsInfo = GoodsUtils.buildGoodsInfo(goods, goodsSpecificationMap.get(goodsId), goodsAttributeGroupMap.get(goodsId), goodsAttributeMap.get(goodsId));
            } else if (type == Constants.GOODS_TYPE_PACKAGE) {
                goodsInfo = GoodsUtils.buildPackageInfo(goods, packageGroupMap.get(goodsId), packageDetailMap.get(goodsId));
            }
            data.add(goodsInfo);
        }
        return ApiRest.builder().data(data).message("获取菜品信息成功！").successful(true).build();
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

        BigInteger id = saveGoodsModel.getId();
        String name = saveGoodsModel.getName();
        int type = saveGoodsModel.getType();
        BigInteger categoryId = saveGoodsModel.getCategoryId();
        String imageUrl = saveGoodsModel.getImageUrl();
        boolean stocked = saveGoodsModel.getStocked();
        List<SaveGoodsModel.GoodsSpecificationInfo> goodsSpecificationInfos = saveGoodsModel.getGoodsSpecificationInfos();
        List<SaveGoodsModel.AttributeGroupInfo> attributeGroupInfos = saveGoodsModel.getAttributeGroupInfos();
        List<BigInteger> deleteGoodsSpecificationIds = saveGoodsModel.getDeleteGoodsSpecificationIds();
        List<BigInteger> deleteGoodsAttributeGroupIds = saveGoodsModel.getDeleteGoodsAttributeGroupIds();

        Goods goods = null;
        if (id != null) {
            SearchModel goodsSearchModel = new SearchModel(true);
            goodsSearchModel.addSearchCondition(Goods.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            goodsSearchModel.addSearchCondition(Goods.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            goodsSearchModel.addSearchCondition(Goods.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            goods = DatabaseHelper.find(Goods.class, goodsSearchModel);
            ValidateUtils.notNull(goods, "商品不存在！");

            // 验证商品是否可以编辑
            CanNotOperateUtils.validateCanNotOperate(tenantId, branchId, Goods.TABLE_NAME, id, 2);

            goods.setName(name);
            goods.setCategoryId(categoryId);
            goods.setImageUrl(imageUrl);
            goods.setStocked(stocked);
            DatabaseHelper.update(goods);

            // 删除需要删除的规格
            if (CollectionUtils.isNotEmpty(deleteGoodsSpecificationIds)) {
                UpdateModel updateModel = new UpdateModel().builder()
                        .autoSetDeletedFalse()
                        .addContentValue(GoodsSpecification.ColumnName.UPDATED_USER_ID, userId, 1)
                        .addContentValue(GoodsSpecification.ColumnName.UPDATED_REMARK, "删除商品规格信息！", 1)
                        .addContentValue(GoodsSpecification.ColumnName.DELETED_TIME, new Date(), 1)
                        .addContentValue(GoodsSpecification.ColumnName.DELETED, 1, 1)
                        .addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                        .addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                        .addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsSpecificationIds)
                        .build();
                DatabaseHelper.universalUpdate(updateModel, GoodsSpecification.TABLE_NAME);
            }

            // 删除需要删除的口味组及其下的口味
            if (CollectionUtils.isNotEmpty(deleteGoodsAttributeGroupIds)) {
                UpdateModel deleteGoodsAttributeGroupUpdateModel = UpdateModel.builder()
                        .autoSetDeletedFalse()
                        .addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_USER_ID, userId, 1)
                        .addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_REMARK, "删除商品口味组信息！", 1)
                        .addContentValue(GoodsAttributeGroup.ColumnName.DELETED_TIME, new Date(), 1)
                        .addContentValue(GoodsAttributeGroup.ColumnName.DELETED, 1, 1)
                        .addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                        .addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                        .addSearchCondition(GoodsAttributeGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsAttributeGroupIds)
                        .build();
                DatabaseHelper.universalUpdate(deleteGoodsAttributeGroupUpdateModel, GoodsAttributeGroup.TABLE_NAME);

                UpdateModel deleteGoodsAttributeUpdateModel = UpdateModel.builder()
                        .autoSetDeletedFalse()
                        .addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId, 1)
                        .addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味信息！", 1)
                        .addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, new Date(), 1)
                        .addContentValue(GoodsAttribute.ColumnName.DELETED, 1, 1)
                        .addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                        .addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                        .addSearchCondition(GoodsAttribute.ColumnName.GOODS_ATTRIBUTE_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsAttributeGroupIds)
                        .build();
                DatabaseHelper.universalUpdate(deleteGoodsAttributeUpdateModel, GoodsAttribute.TABLE_NAME);
            }

            // 查询出需要修改的商品规格
            List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                BigInteger goodsSpecificationId = goodsSpecificationInfo.getId();
                if (goodsSpecificationId != null) {
                    goodsSpecificationIds.add(goodsSpecificationId);
                }
            }

            Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
            if (CollectionUtils.isNotEmpty(goodsSpecificationIds)) {
                SearchModel searchModel = new SearchModel(true);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
                searchModel.addSearchCondition(GoodsSpecification.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
                List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, searchModel);
                for (GoodsSpecification goodsSpecification : goodsSpecifications) {
                    goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
                }
            }

            // 处理所有规格，修改与更新
            List<GoodsSpecification> insertGoodsSpecifications = new ArrayList<GoodsSpecification>();
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                if (goodsSpecificationInfo.getId() != null) {
                    GoodsSpecification goodsSpecification = goodsSpecificationMap.get(goodsSpecificationInfo.getId());
                    ValidateUtils.notNull(goodsSpecification, "商品规格不存在！");
                    goodsSpecification.setName(goodsSpecificationInfo.getName());
                    goodsSpecification.setPrice(goodsSpecificationInfo.getPrice());
                    DatabaseHelper.update(goodsSpecification);
                } else {
                    GoodsSpecification goodsSpecification = GoodsUtils.buildGoodsSpecification(tenantId, tenantCode, branchId, id, goodsSpecificationInfo, userId);
                    insertGoodsSpecifications.add(goodsSpecification);
                }
            }
            if (CollectionUtils.isNotEmpty(insertGoodsSpecifications)) {
                DatabaseHelper.insertAll(insertGoodsSpecifications);
            }

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
                    UpdateModel deleteGoodsAttributeUpdateModel = UpdateModel.builder()
                            .autoSetDeletedFalse()
                            .addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId, 1)
                            .addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味信息！", 1)
                            .addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, new Date(), 1)
                            .addContentValue(GoodsAttribute.ColumnName.DELETED, 1, 1)
                            .addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                            .addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                            .addSearchCondition(GoodsAttribute.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGoodsAttributeIds)
                            .build();
                    DatabaseHelper.universalUpdate(deleteGoodsAttributeUpdateModel, GoodsAttribute.TABLE_NAME);
                }

                // 查询出需要修改的口味组
                SearchModel goodsAttributeGroupSearchModel = new SearchModel(true);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
                goodsAttributeGroupSearchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
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
                goodsAttributeSearchModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
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
                                GoodsAttribute goodsAttribute = GoodsUtils.buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, id, goodsAttributeGroup.getId(), userId);
                                insertGoodsAttributes.add(goodsAttribute);
                            }
                        }
                    } else {
                        GoodsAttributeGroup goodsAttributeGroup = GoodsUtils.buildGoodsAttributeGroup(tenantId, tenantCode, branchId, id, attributeGroupInfo, userId);
                        DatabaseHelper.insert(goodsAttributeGroup);

                        for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                            GoodsAttribute goodsAttribute = GoodsUtils.buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, id, goodsAttributeGroup.getId(), userId);
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
            goods = Goods.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .name(name)
                    .type(type)
                    .categoryId(categoryId)
                    .imageUrl(imageUrl)
                    .stocked(stocked)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增商品信息！")
                    .build();
            DatabaseHelper.insert(goods);

            BigInteger goodsId = goods.getId();
            // 新增所有规格
            List<GoodsSpecification> insertGoodsSpecifications = new ArrayList<GoodsSpecification>();
            for (SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo : goodsSpecificationInfos) {
                GoodsSpecification goodsSpecification = GoodsUtils.buildGoodsSpecification(tenantId, tenantCode, branchId, goodsId, goodsSpecificationInfo, userId);
                insertGoodsSpecifications.add(goodsSpecification);
            }
            DatabaseHelper.insertAll(insertGoodsSpecifications);

            if (CollectionUtils.isNotEmpty(attributeGroupInfos)) {
                List<GoodsAttribute> insertGoodsAttributes = new ArrayList<GoodsAttribute>();
                for (SaveGoodsModel.AttributeGroupInfo attributeGroupInfo : attributeGroupInfos) {
                    GoodsAttributeGroup goodsAttributeGroup = GoodsUtils.buildGoodsAttributeGroup(tenantId, tenantCode, branchId, goodsId, attributeGroupInfo, userId);
                    DatabaseHelper.insert(goodsAttributeGroup);

                    for (SaveGoodsModel.AttributeInfo attributeInfo : attributeGroupInfo.getAttributeInfos()) {
                        GoodsAttribute goodsAttribute = GoodsUtils.buildGoodsAttribute(attributeInfo, tenantId, tenantCode, branchId, goodsId, goodsAttributeGroup.getId(), userId);
                        insertGoodsAttributes.add(goodsAttribute);
                    }
                }
                DatabaseHelper.insertAll(insertGoodsAttributes);
            }
        }
//        ElasticsearchUtils.index(Constants.ELASTICSEARCH_INDEX_GOODS, goods);
        return ApiRest.builder().data(goods).message("保存商品信息成功！").successful(true).build();
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
                UpdateModel packageGroupUpdateModel = UpdateModel.builder()
                        .autoSetDeletedFalse()
                        .addContentValue(PackageGroup.ColumnName.DELETED_TIME, new Date(), 1)
                        .addContentValue(PackageGroup.ColumnName.DELETED, 1, 1)
                        .addSearchCondition(PackageGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, deleteGroupIds)
                        .build();
                DatabaseHelper.universalUpdate(packageGroupUpdateModel, PackageGroup.TABLE_NAME);

                UpdateModel packageGroupDetailUpdateModel = UpdateModel.builder()
                        .autoSetDeletedFalse()
                        .addContentValue(PackageGroupDetail.ColumnName.DELETED_TIME, new Date(), 1)
                        .addContentValue(PackageGroupDetail.ColumnName.DELETED, 1, 1)
                        .addSearchCondition(PackageGroupDetail.ColumnName.PACKAGE_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupIds)
                        .build();
                DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel, PackageGroupDetail.TABLE_NAME);
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
                    UpdateModel packageGroupDetailUpdateModel = UpdateModel.builder()
                            .autoSetDeletedFalse()
                            .addContentValue(PackageGroupDetail.ColumnName.DELETED_TIME, new Date(), 1)
                            .addContentValue(PackageGroupDetail.ColumnName.DELETED, 1, 1)
                            .addSearchCondition(PackageGroupDetail.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_IN, deleteGroupDetailIds)
                            .build();
                    DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel, PackageGroupDetail.TABLE_NAME);
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
//        ElasticsearchUtils.index(Constants.ELASTICSEARCH_INDEX_GOODS, goods);
        return ApiRest.builder().data(goods).message("保存套餐成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest listCategories(ListCategoriesModel listCategoriesModel) {
        BigInteger tenantId = listCategoriesModel.obtainTenantId();
        BigInteger branchId = listCategoriesModel.obtainBranchId();

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(GoodsCategory.ColumnName.TENANT_ID, tenantId)
                .equal(GoodsCategory.ColumnName.BRANCH_ID, branchId)
                .build();
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

        SearchModel searchModel = SearchModel.builder()
                .autoSetDeletedFalse()
                .equal(Goods.ColumnName.ID, goodsId)
                .equal(Goods.ColumnName.TENANT_ID, tenantId)
                .equal(Goods.ColumnName.BRANCH_ID, branchId)
                .build();
        Goods goods = DatabaseHelper.find(Goods.class, searchModel);
        ValidateUtils.notNull(goods, "商品不存在！");

        CanNotOperateUtils.validateCanNotOperate(tenantId, branchId, Goods.TABLE_NAME, goodsId, 2);

        Date now = new Date();
        goods.setUpdatedUserId(userId);
        goods.setUpdatedRemark("删除商品信息！");
        goods.setDeletedTime(now);
        goods.setDeleted(true);
        DatabaseHelper.update(goods);

        // 删除该商品的所有规格
        UpdateModel goodsSpecificationUpdateModel = UpdateModel.builder()
                .autoSetDeletedFalse()
                .addContentValue(GoodsSpecification.ColumnName.DELETED_TIME, now, 1)
                .addContentValue(GoodsSpecification.ColumnName.DELETED, 1, 1)
                .addContentValue(GoodsSpecification.ColumnName.UPDATED_USER_ID, userId, 1)
                .addContentValue(GoodsSpecification.ColumnName.UPDATED_REMARK, "删除商品规格信息！", 1)
                .addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId)
                .addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                .addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                .build();
        DatabaseHelper.universalUpdate(goodsSpecificationUpdateModel, GoodsSpecification.TABLE_NAME);

        int type = goods.getType();
        if (type == Constants.GOODS_TYPE_ORDINARY_GOODS) {
            // 删除该商品的所有口味组
            UpdateModel goodsAttributeGroupUpdateModel = UpdateModel.builder()
                    .autoSetDeletedFalse()
                    .addContentValue(GoodsAttributeGroup.ColumnName.DELETED_TIME, now, 1)
                    .addContentValue(GoodsAttributeGroup.ColumnName.DELETED, 1, 1)
                    .addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_USER_ID, userId, 1)
                    .addContentValue(GoodsAttributeGroup.ColumnName.UPDATED_REMARK, "删除商品口味组信息！", 1)
                    .addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId)
                    .addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                    .addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                    .build();
            DatabaseHelper.universalUpdate(goodsAttributeGroupUpdateModel, GoodsAttributeGroup.TABLE_NAME);

            // 删除该商品的所有口味
            UpdateModel goodsAttributeUpdateModel = UpdateModel.builder()
                    .autoSetDeletedFalse()
                    .addContentValue(GoodsAttribute.ColumnName.DELETED_TIME, now, 1)
                    .addContentValue(GoodsAttribute.ColumnName.DELETED, 1, 1)
                    .addContentValue(GoodsAttribute.ColumnName.UPDATED_USER_ID, userId, 1)
                    .addContentValue(GoodsAttribute.ColumnName.UPDATED_REMARK, "删除商品口味组信息！", 1)
                    .addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId)
                    .addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                    .addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                    .build();
            DatabaseHelper.universalUpdate(goodsAttributeUpdateModel, GoodsAttribute.TABLE_NAME);
        } else if (type == Constants.GOODS_TYPE_PACKAGE) {
            UpdateModel packageGroupUpdateModel = UpdateModel.builder()
                    .autoSetDeletedFalse()
                    .addContentValue(PackageGroup.ColumnName.DELETED_TIME, now, 1)
                    .addContentValue(PackageGroup.ColumnName.DELETED, 1, 1)
                    .addContentValue(PackageGroup.ColumnName.UPDATED_USER_ID, userId, 1)
                    .addContentValue(PackageGroup.ColumnName.UPDATED_REMARK, "删除套餐组信息！", 1)
                    .addSearchCondition(PackageGroup.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId)
                    .addSearchCondition(PackageGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                    .addSearchCondition(PackageGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                    .build();
            DatabaseHelper.universalUpdate(packageGroupUpdateModel, PackageGroup.TABLE_NAME);

            UpdateModel packageGroupDetailUpdateModel = UpdateModel.builder()
                    .autoSetDeletedFalse()
                    .addContentValue(PackageGroupDetail.ColumnName.DELETED_TIME, now, 1)
                    .addContentValue(PackageGroupDetail.ColumnName.DELETED, 1, 1)
                    .addContentValue(PackageGroupDetail.ColumnName.UPDATED_USER_ID, userId, 1)
                    .addContentValue(PackageGroupDetail.ColumnName.UPDATED_REMARK, "删除套餐明细信息！", 1)
                    .addSearchCondition(PackageGroupDetail.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, goodsId)
                    .addSearchCondition(PackageGroupDetail.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId)
                    .addSearchCondition(PackageGroupDetail.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId)
                    .build();
            DatabaseHelper.universalUpdate(packageGroupDetailUpdateModel, PackageGroupDetail.TABLE_NAME);
        }

//        ElasticsearchUtils.delete(Constants.ELASTICSEARCH_INDEX_GOODS, goodsId.toString());
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

        TenantConfig tenantConfig = TenantConfigUtils.addTenantConfig(tenantId, "goods_num", count);
        int currentValue = tenantConfig.getCurrentValue();
        int maxValue = tenantConfig.getMaxValue();
        ValidateUtils.isTrue(currentValue <= maxValue, "您最多可以添加" + (maxValue - currentValue + count) + "条商品信息！");

        List<Goods> goodsList = new ArrayList<Goods>();
        Map<String, Goods> goodsMap = new HashMap<String, Goods>();
        Map<String, GoodsSpecification> goodsSpecificationMap = new HashMap<String, GoodsSpecification>();

        for (Map<String, Object> goodsInfo : goodsInfos) {
            String uuid = UUID.randomUUID().toString();
            Goods goods = Goods.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .name(MapUtils.getString(goodsInfo, "name"))
                    .type(Constants.GOODS_TYPE_ORDINARY_GOODS)
                    .categoryId(BigInteger.ZERO)
                    .imageUrl(Constants.VARCHAR_DEFAULT_VALUE)
                    .stocked(false)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("导入商品信息！")
                    .build();
            goodsMap.put(uuid, goods);
            goodsList.add(goods);

            GoodsSpecification goodsSpecification = GoodsSpecification.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .name(Constants.VARCHAR_DEFAULT_VALUE)
                    .price(BigDecimal.valueOf(MapUtils.getDoubleValue(goodsInfo, "price")))
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("导入商品信息！")
                    .build();
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

        // 将数据保存到 ElasticSearch 中一份
        List<ElasticSearchGoods> elasticSearchGoods = goodsList.stream().map(goods -> ElasticSearchGoods.build(goods)).collect(Collectors.toList());
        elasticSearchGoodsRepository.saveAll(elasticSearchGoods);
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
        String searchString = searchGoodsModel.getSearchString();
        boolean highlight = searchGoodsModel.getHighlight();
        String preTag = searchGoodsModel.getPreTag();
        String postTag = searchGoodsModel.getPostTag();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchGoods.FieldName.TENANT_ID, tenantId.longValue()));
        boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchGoods.FieldName.BRANCH_ID, branchId.longValue()));

        if (categoryId != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery(ElasticSearchGoods.FieldName.CATEGORY_ID, categoryId.longValue()));
        }

        if (StringUtils.isNotBlank(searchString)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(ElasticSearchGoods.FieldName.NAME, searchString));
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder);
        if (highlight) {
            nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field(ElasticSearchGoods.FieldName.NAME).preTags(preTag).postTags(postTag));
        }

        Pageable pageable = PageRequest.of(page - 1, rows);
        nativeSearchQueryBuilder.withPageable(pageable);
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(ElasticSearchGoods.FieldName.UPDATED_TIME).order(SortOrder.DESC));

        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page<ElasticSearchGoods> elasticSearchGoodsPage = elasticSearchGoodsRepository.search(searchQuery);

        AggregatedPage<ElasticSearchGoods> aggregatedPage = elasticsearchTemplate.queryForPage(searchQuery, ElasticSearchGoods.class, ElasticSearchUtils.SEARCH_RESULT_MAPPER);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", aggregatedPage.getTotalElements());
        data.put("rows", aggregatedPage.getContent());

        return ApiRest.builder().data(data).message("检索商品成功！").successful(true).build();
    }

    /**
     * 保存所有
     *
     * @param indexAllModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest indexAll(IndexAllModel indexAllModel) {
        BigInteger tenantId = indexAllModel.obtainTenantId();
        BigInteger branchId = indexAllModel.obtainBranchId();

        List<Goods> goodsList = goodsMapper.findAll(tenantId, branchId);
        List<ElasticSearchGoods> elasticSearchGoodsList = goodsList.stream().map(goods -> ElasticSearchGoods.build(goods)).collect(Collectors.toList());
        elasticSearchGoodsRepository.saveAll(elasticSearchGoodsList);
        return ApiRest.builder().message("保存成功！").successful(true).build();
    }
}
