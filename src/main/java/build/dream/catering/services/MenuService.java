package build.dream.catering.services;

import build.dream.catering.beans.PackageDetail;
import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.MenuMapper;
import build.dream.catering.models.menu.ObtainMenuInfoModel;
import build.dream.catering.models.menu.SaveMenuModel;
import build.dream.catering.utils.GoodsUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.*;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.DeleteModel;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;

    /**
     * 保存菜牌信息
     *
     * @param saveMenuModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveMenu(SaveMenuModel saveMenuModel) {
        BigInteger tenantId = saveMenuModel.obtainTenantId();
        String tenantCode = saveMenuModel.obtainTenantCode();
        BigInteger userId = saveMenuModel.obtainUserId();
        BigInteger id = saveMenuModel.getId();
        String code = saveMenuModel.getCode();
        String name = saveMenuModel.getName();
        Date startTime = saveMenuModel.getStartTime();
        Date endTime = saveMenuModel.getEndTime();
        Integer status = saveMenuModel.getStatus();
        Integer effectiveScope = saveMenuModel.getEffectiveScope();
        List<BigInteger> branchIds = saveMenuModel.getBranchIds();
        List<SaveMenuModel.Detail> details = saveMenuModel.getDetails();

        Menu menu = null;
        BigInteger menuId = null;
        if (id == null) {
            menu = Menu.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .code(code)
                    .name(name)
                    .startTime(startTime)
                    .endTime(endTime)
                    .status(status)
                    .effectiveScope(effectiveScope)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增菜牌信息！")
                    .build();
            DatabaseHelper.insert(menu);

            menuId = menu.getId();
            menuMapper.insertAllMenuBranchR(menuId, tenantId, tenantCode, branchIds);
        } else {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(Menu.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(Menu.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            menu = DatabaseHelper.find(Menu.class, searchModel);
            ValidateUtils.notNull(menu, "菜牌不存在！");

            menu.setName(name);
            menu.setStartTime(startTime);
            menu.setEndTime(endTime);
            menu.setStatus(status);
            menu.setEffectiveScope(effectiveScope);
            menu.setUpdatedUserId(userId);
            menu.setUpdatedRemark("修改菜牌信息！");

            DatabaseHelper.update(menu);

            menuId = menu.getId();
            menuMapper.deleteAllMenuBranchR(menuId, tenantId);
            menuMapper.insertAllMenuBranchR(menuId, tenantId, tenantCode, branchIds);

            DeleteModel deleteModel = new DeleteModel();
            deleteModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, menuId);
            deleteModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            DatabaseHelper.delete(MenuDetail.class, deleteModel);
        }

        List<MenuDetail> menuDetails = new ArrayList<MenuDetail>();
        for (SaveMenuModel.Detail detail : details) {
            MenuDetail menuDetail = MenuDetail.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .menuId(menuId)
                    .goodsId(detail.getGoodsId())
                    .goodsUnitId(detail.getGoodsSpecificationId())
                    .goodsUnitId(detail.getGoodsUnitId())
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增菜牌明细数据！")
                    .build();
            menuDetails.add(menuDetail);
        }

        DatabaseHelper.insertAll(menuDetails);
        return ApiRest.builder().data(menu).message("保存菜牌成功！").successful(true).build();
    }

    /**
     * 获取菜牌信息
     *
     * @param obtainMenuInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainMenuInfo(ObtainMenuInfoModel obtainMenuInfoModel) {
        BigInteger tenantId = obtainMenuInfoModel.obtainTenantId();
        BigInteger branchId = obtainMenuInfoModel.obtainBranchId();
        int effectiveScope = obtainMenuInfoModel.getEffectiveScope();

        Menu menu = menuMapper.findEffectiveMenu(tenantId, branchId, effectiveScope);
        ValidateUtils.notNull(menu, "未检索到有效菜牌！");

        List<Map<String, Object>> menuDetails = menuMapper.findAllMenuDetails(tenantId, menu.getId());
        Map<BigInteger, Set<BigInteger>> categoryIdGoodsIdMap = new HashMap<BigInteger, Set<BigInteger>>();
        Map<BigInteger, List<Map<String, Object>>> goodsIdMenuDetailMap = new HashMap<BigInteger, List<Map<String, Object>>>();
        Set<BigInteger> goodsIds = new HashSet<BigInteger>();
        Set<BigInteger> packageIds = new HashSet<BigInteger>();
        for (Map<String, Object> menuDetail : menuDetails) {
            BigInteger categoryId = BigInteger.valueOf(MapUtils.getLongValue(menuDetail, "categoryId"));
            BigInteger goodsId = BigInteger.valueOf(MapUtils.getLongValue(menuDetail, "goodsId"));
            int goodsType = MapUtils.getIntValue(menuDetail, "goodsType");

            if (goodsType == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                goodsIds.add(goodsId);
            } else if (goodsType == Constants.GOODS_TYPE_PACKAGE) {
                packageIds.add(goodsId);
            }

            Set<BigInteger> ids = categoryIdGoodsIdMap.get(categoryId);
            if (CollectionUtils.isEmpty(ids)) {
                ids = new HashSet<BigInteger>();
                categoryIdGoodsIdMap.put(categoryId, ids);
            }
            ids.add(goodsId);

            List<Map<String, Object>> mapList = goodsIdMenuDetailMap.get(goodsId);
            if (CollectionUtils.isEmpty(mapList)) {
                mapList = new ArrayList<Map<String, Object>>();
                goodsIdMenuDetailMap.put(goodsId, mapList);
            }
            mapList.add(menuDetail);
        }

        Map<BigInteger, List<GoodsAttributeGroup>> goodsAttributeGroupMap = GoodsUtils.obtainGoodsAttributeGroupInfos(tenantId, branchId, goodsIds);
        Map<BigInteger, List<GoodsAttribute>> goodsAttributeMap = GoodsUtils.obtainGoodsAttributeInfos(tenantId, branchId, goodsIds);
        Map<BigInteger, List<PackageDetail>> packageDetailMap = GoodsUtils.obtainPackageGroupDetailInfos(tenantId, branchId, packageIds);
        Map<BigInteger, List<PackageGroup>> packageGroupMap = GoodsUtils.obtainPackageGroupInfos(tenantId, branchId, packageIds);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Map.Entry<BigInteger, Set<BigInteger>> entry : categoryIdGoodsIdMap.entrySet()) {
            BigInteger categoryId = entry.getKey();
            Set<BigInteger> ids = entry.getValue();

            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", categoryId);
            item.put("name", MapUtils.getString(goodsIdMenuDetailMap.get(ids.iterator().next()).get(0), "categoryName"));

            List<Map<String, Object>> goodsInfos = new ArrayList<Map<String, Object>>();
            for (BigInteger goodsId : ids) {
                List<Map<String, Object>> details = goodsIdMenuDetailMap.get(goodsId);
                Map<String, Object> info = details.get(0);

                int goodsType = MapUtils.getIntValue(info, "goodsType");
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("id", MapUtils.getLongValue(info, "goodsId"));
                goodsInfo.put("name", MapUtils.getString(info, "goodsName"));
                goodsInfo.put("type", goodsType);
                if (goodsType == Constants.GOODS_TYPE_ORDINARY_GOODS) {
                    List<Map<String, Object>> specifications = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> detail : details) {
                        Map<String, Object> specification = new HashMap<String, Object>();
                        specification.put("id", MapUtils.getLongValue(detail, "goodsSpecificationId"));
                        specification.put("name", MapUtils.getString(detail, "goodsSpecificationName"));
                        specification.put("price", MapUtils.getDoubleValue(detail, "price"));
                        specifications.add(specification);
                        List<GoodsAttributeGroup> goodsAttributeGroups = goodsAttributeGroupMap.get(goodsId);
                        if (CollectionUtils.isNotEmpty(goodsAttributeGroups)) {
                            List<Map<String, Object>> attributeGroups = GoodsUtils.buildGoodsAttributeGroups(goodsAttributeGroups, goodsAttributeMap.get(goodsId));
                            goodsInfo.put("attributeGroups", attributeGroups);
                        }
                    }
                    goodsInfo.put("specifications", specifications);
                } else if (goodsType == Constants.GOODS_TYPE_PACKAGE) {
                    goodsInfo.put("groups", GoodsUtils.buildPackageGroupInfos(packageGroupMap.get(goodsId), packageDetailMap.get(goodsId)));
                }

                goodsInfos.add(goodsInfo);
            }
            item.put("goodsInfos", goodsInfos);
            data.add(item);
        }

        return ApiRest.builder().data(data).message("查询菜牌信息成功！").successful(true).build();
    }
}
