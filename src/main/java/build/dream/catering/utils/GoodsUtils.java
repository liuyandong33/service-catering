package build.dream.catering.utils;

import build.dream.catering.mappers.GoodsMapper;
import build.dream.common.catering.domains.*;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
