package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.GoodsMapper;
import build.dream.catering.models.goods.SaveGoodsModel;
import build.dream.common.catering.domains.*;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class GoodsUtils {
    private static GoodsMapper goodsMapper;

    private static GoodsMapper obtainGoodsMapper() {
        if (goodsMapper == null) {
            goodsMapper = ApplicationHandler.getBean(GoodsMapper.class);
        }
        return goodsMapper;
    }

    public static BigDecimal deductingGoodsStock(BigInteger goodsId, BigInteger goodsSpecificationId, BigDecimal quantity) {
        BigDecimal stock = obtainGoodsMapper().deductingGoodsStock(goodsId, goodsSpecificationId, quantity);
        ValidateUtils.isTrue(stock.compareTo(BigDecimal.ZERO) >= 0, "库存不足！");
        return stock;
    }

    public static BigDecimal addGoodsStock(BigInteger goodsId, BigInteger goodsSpecificationId, BigDecimal quantity) {
        BigDecimal stock = obtainGoodsMapper().addGoodsStock(goodsId, goodsSpecificationId, quantity);
        return stock;
    }

    public static Map<String, Object> buildGoodsInfo(Goods goods, List<GoodsSpecification> goodsSpecifications, List<GoodsAttributeGroup> goodsAttributeGroups, List<GoodsAttribute> goodsAttributes) {
        Map<String, Object> goodsInfo = new HashMap<String, Object>();
        goodsInfo.put(Goods.FieldName.ID, goods.getId());
        goodsInfo.put(Goods.FieldName.NAME, goods.getName());
        goodsInfo.put(Goods.FieldName.TENANT_ID, goods.getTenantId());
        goodsInfo.put(Goods.FieldName.TENANT_CODE, goods.getTenantCode());
        goodsInfo.put(Goods.FieldName.BRANCH_ID, goods.getBranchId());
        goodsInfo.put(Goods.FieldName.TYPE, goods.getType());
        goodsInfo.put(Goods.FieldName.CATEGORY_ID, goods.getCategoryId());
        goodsInfo.put(Goods.FieldName.CATEGORY_NAME, goods.getCategoryName());
        goodsInfo.put("goodsSpecifications", buildGoodsSpecificationInfos(goodsSpecifications));

        if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
            goodsInfo.put("attributeGroups", buildGoodsAttributeGroups(goodsAttributeGroups, goodsAttributes));
        }
        return goodsInfo;
    }

    public static List<Map<String, Object>> buildGoodsAttributeGroups(List<GoodsAttributeGroup> goodsAttributeGroups, List<GoodsAttribute> goodsAttributes) {
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
            BigInteger goodsAttributeGroupId = goodsAttributeGroup.getId();
            Map<String, Object> attributeGroup = new HashMap<String, Object>();
            attributeGroup.put(GoodsAttributeGroup.FieldName.ID, goodsAttributeGroup.getId());
            attributeGroup.put(GoodsAttributeGroup.FieldName.NAME, goodsAttributeGroup.getName());

            List<GoodsAttribute> goodsAttributeList = goodsAttributeMap.get(goodsAttributeGroupId);
            List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
            for (GoodsAttribute goodsAttribute : goodsAttributeList) {
                Map<String, Object> attribute = new HashMap<String, Object>();
                attribute.put(GoodsAttribute.FieldName.ID, goodsAttribute.getId());
                attribute.put(GoodsAttribute.FieldName.NAME, goodsAttribute.getName());
                attribute.put(GoodsAttribute.FieldName.PRICE, goodsAttribute.getPrice());
                attributes.add(attribute);
            }
            attributeGroup.put("attributes", attributes);
            attributeGroups.add(attributeGroup);
        }
        return attributeGroups;
    }

    public static List<Map<String, Object>> buildGoodsSpecificationInfos(List<GoodsSpecification> goodsSpecifications) {
        List<Map<String, Object>> goodsSpecificationInfos = new ArrayList<Map<String, Object>>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            Map<String, Object> goodsSpecificationInfo = new HashMap<String, Object>();
            goodsSpecificationInfo.put(GoodsSpecification.FieldName.ID, goodsSpecification.getId());
            goodsSpecificationInfo.put(GoodsSpecification.FieldName.NAME, goodsSpecification.getName());
            goodsSpecificationInfo.put(GoodsSpecification.FieldName.PRICE, goodsSpecification.getPrice());
            goodsSpecificationInfos.add(goodsSpecificationInfo);
        }
        return goodsSpecificationInfos;
    }

    public static Map<String, Object> buildPackageInfo(Goods goods, List<PackageGroup> packageGroups, List<Map<String, Object>> packageGroupDetails) {
        Map<String, Object> goodsInfo = new HashMap<String, Object>();
        goodsInfo.put(Goods.FieldName.ID, goods.getId());
        goodsInfo.put(Goods.FieldName.NAME, goods.getName());
        goodsInfo.put(Goods.FieldName.TENANT_ID, goods.getTenantId());
        goodsInfo.put(Goods.FieldName.TENANT_CODE, goods.getTenantCode());
        goodsInfo.put(Goods.FieldName.BRANCH_ID, goods.getBranchId());
        goodsInfo.put(Goods.FieldName.TYPE, goods.getType());
        goodsInfo.put(Goods.FieldName.CATEGORY_ID, goods.getCategoryId());
        goodsInfo.put(Goods.FieldName.CATEGORY_NAME, goods.getCategoryName());
        goodsInfo.put(Goods.FieldName.IMAGE_URL, goods.getImageUrl());

        Map<BigInteger, List<Map<String, Object>>> packageGroupDetailMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        for (Map<String, Object> packageGroupDetail : packageGroupDetails) {
            BigInteger packageGroupId = BigInteger.valueOf(MapUtils.getLongValue(packageGroupDetail, "packageGroupId"));
            List<Map<String, Object>> packageGroupDetailList = packageGroupDetailMap.get(packageGroupId);
            if (CollectionUtils.isEmpty(packageGroupDetailList)) {
                packageGroupDetailList = new ArrayList<Map<String, Object>>();
                packageGroupDetailMap.put(packageGroupId, packageGroupDetailList);
            }
            packageGroupDetailList.add(packageGroupDetail);
        }

        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        for (PackageGroup packageGroup : packageGroups) {
            BigInteger packageGroupId = packageGroup.getId();

            Map<String, Object> group = new HashMap<String, Object>();
            group.put(PackageGroup.FieldName.ID, packageGroup.getId());
            group.put(PackageGroup.FieldName.GROUP_NAME, packageGroup.getGroupName());
            group.put(PackageGroup.FieldName.GROUP_TYPE, packageGroup.getGroupType());
            group.put(PackageGroup.FieldName.OPTIONAL_QUANTITY, packageGroup.getOptionalQuantity());
            group.put("details", packageGroupDetailMap.get(packageGroupId));

            groups.add(group);
        }

        goodsInfo.put("groups", groups);
        return goodsInfo;
    }

    public static GoodsAttributeGroup buildGoodsAttributeGroup(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.AttributeGroupInfo attributeGroupInfo, BigInteger userId) {
        GoodsAttributeGroup goodsAttributeGroup = GoodsAttributeGroup.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .goodsId(goodsId)
                .name(attributeGroupInfo.getName())
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("新增口味组信息！")
                .build();
        return goodsAttributeGroup;
    }

    public static GoodsAttribute buildGoodsAttribute(SaveGoodsModel.AttributeInfo attributeInfo, BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, BigInteger goodsAttributeGroupId, BigInteger userId) {
        GoodsAttribute goodsAttribute = GoodsAttribute.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .goodsId(goodsId)
                .goodsAttributeGroupId(goodsAttributeGroupId)
                .name(attributeInfo.getName())
                .price(attributeInfo.getPrice() == null ? Constants.DECIMAL_DEFAULT_VALUE : attributeInfo.getPrice())
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("新增属性信息！")
                .build();
        return goodsAttribute;
    }

    public static GoodsSpecification buildGoodsSpecification(BigInteger tenantId, String tenantCode, BigInteger branchId, BigInteger goodsId, SaveGoodsModel.GoodsSpecificationInfo goodsSpecificationInfo, BigInteger userId) {
        GoodsSpecification goodsSpecification = GoodsSpecification.builder()
                .tenantId(tenantId)
                .tenantCode(tenantCode)
                .branchId(branchId)
                .goodsId(goodsId)
                .name(goodsSpecificationInfo.getName())
                .price(goodsSpecificationInfo.getPrice())
                .stock(goodsSpecificationInfo.getStock())
                .createdUserId(userId)
                .updatedUserId(userId)
                .updatedRemark("新增规格信息！")
                .build();
        return goodsSpecification;
    }

    public static List<Map<String, Object>> listPackageInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> packageIds, Integer groupType) {
        return obtainGoodsMapper().listPackageInfos(tenantId, branchId, packageIds, groupType);
    }

    public static List<Goods> findAllByIdInList(BigInteger tenantId, BigInteger branchId, List<BigInteger> goodsIds) {
        return goodsMapper.findAllByIdInList(tenantId, branchId, goodsIds);
    }

    public static Map<BigInteger, List<GoodsAttributeGroup>> obtainGoodsAttributeGroupInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> goodsIds) {
        Map<BigInteger, List<GoodsAttributeGroup>> goodsAttributeGroupMap = new HashMap<BigInteger, List<GoodsAttributeGroup>>();
        if (CollectionUtils.isEmpty(goodsIds)) {
            return goodsAttributeGroupMap;
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(GoodsAttributeGroup.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsAttributeGroup> goodsAttributeGroups = DatabaseHelper.findAll(GoodsAttributeGroup.class, searchModel);

        if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
            for (GoodsAttributeGroup goodsAttributeGroup : goodsAttributeGroups) {
                BigInteger goodsId = goodsAttributeGroup.getGoodsId();
                List<GoodsAttributeGroup> goodsAttributeGroupList = goodsAttributeGroupMap.get(goodsId);
                if (CollectionUtils.isEmpty(goodsAttributeGroupList)) {
                    goodsAttributeGroupList = new ArrayList<GoodsAttributeGroup>();
                    goodsAttributeGroupMap.put(goodsId, goodsAttributeGroupList);
                }
                goodsAttributeGroupList.add(goodsAttributeGroup);
            }
        }
        return goodsAttributeGroupMap;
    }

    public static Map<BigInteger, List<GoodsAttribute>> obtainGoodsAttributeInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> goodsIds) {
        Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = new HashMap<BigInteger, List<GoodsAttribute>>();
        if (CollectionUtils.isEmpty(goodsIds)) {
            return goodsAttributeMap;
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(GoodsAttribute.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(GoodsAttribute.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(GoodsAttribute.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsAttribute> goodsAttributes = DatabaseHelper.findAll(GoodsAttribute.class, searchModel);

        if (CollectionUtils.isNotEmpty(goodsAttributes)) {
            for (GoodsAttribute goodsAttribute : goodsAttributes) {
                BigInteger goodsId = goodsAttribute.getGoodsId();
                List<GoodsAttribute> goodsAttributeList = goodsAttributeMap.get(goodsId);
                if (CollectionUtils.isEmpty(goodsAttributeList)) {
                    goodsAttributeList = new ArrayList<GoodsAttribute>();
                    goodsAttributeMap.put(goodsId, goodsAttributeList);
                }
                goodsAttributeList.add(goodsAttribute);
            }
        }
        return goodsAttributeMap;
    }

    public static Map<BigInteger, List<GoodsSpecification>> obtainGoodsSpecificationInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> goodsIds) {
        Map<BigInteger, List<GoodsSpecification>> goodsSpecificationMap = new HashMap<BigInteger, List<GoodsSpecification>>();
        if (CollectionUtils.isEmpty(goodsIds)) {
            return goodsSpecificationMap;
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        searchModel.addSearchCondition(GoodsSpecification.ColumnName.GOODS_ID, Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<GoodsSpecification> goodsSpecifications = DatabaseHelper.findAll(GoodsSpecification.class, searchModel);

        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            List<GoodsSpecification> goodsSpecificationList = goodsSpecificationMap.get(goodsSpecification.getGoodsId());
            if (goodsSpecificationList == null) {
                goodsSpecificationList = new ArrayList<GoodsSpecification>();
                goodsSpecificationMap.put(goodsSpecification.getGoodsId(), goodsSpecificationList);
            }
            goodsSpecificationList.add(goodsSpecification);
        }
        return goodsSpecificationMap;
    }

    public static Map<BigInteger, List<Map<String, Object>>> obtainPackageGroupDetailInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> packageIds) {
        Map<BigInteger, List<Map<String, Object>>> packageGroupDetailMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        if (CollectionUtils.isEmpty(packageIds)) {
            return packageGroupDetailMap;
        }

        List<Map<String, Object>> packageInfos = listPackageInfos(tenantId, branchId, packageIds, null);

        for (Map<String, Object> packageInfo : packageInfos) {
            BigInteger packageId = BigInteger.valueOf(MapUtils.getLongValue(packageInfo, "packageId"));
            List<Map<String, Object>> packageGroupDetails = packageGroupDetailMap.get(packageId);
            if (CollectionUtils.isEmpty(packageGroupDetails)) {
                packageGroupDetails = new ArrayList<Map<String, Object>>();
                packageGroupDetailMap.put(packageId, packageGroupDetails);
            }
            packageGroupDetails.add(packageInfo);
        }
        return packageGroupDetailMap;
    }

    public static Map<BigInteger, List<PackageGroup>> obtainPackageGroupInfos(BigInteger tenantId, BigInteger branchId, Collection<BigInteger> packageIds) {
        Map<BigInteger, List<PackageGroup>> packageGroupMap = new HashMap<BigInteger, List<PackageGroup>>();
        if (CollectionUtils.isEmpty(packageIds)) {
            return packageGroupMap;
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(PackageGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_IN, tenantId);
        searchModel.addSearchCondition(PackageGroup.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_IN, branchId);
        searchModel.addSearchCondition(PackageGroup.ColumnName.PACKAGE_ID, Constants.SQL_OPERATION_SYMBOL_IN, packageIds);
        List<PackageGroup> packageGroups = DatabaseHelper.findAll(PackageGroup.class, searchModel);

        for (PackageGroup packageGroup : packageGroups) {
            BigInteger packageId = packageGroup.getPackageId();
            List<PackageGroup> packageGroupList = packageGroupMap.get(packageId);
            if (CollectionUtils.isEmpty(packageGroupList)) {
                packageGroupList = new ArrayList<PackageGroup>();
                packageGroupMap.put(packageId, packageGroupList);
            }
            packageGroupList.add(packageGroup);
        }
        return packageGroupMap;
    }
}
