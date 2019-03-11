package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.MenuMapper;
import build.dream.catering.models.menu.SaveMenuModel;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.Menu;
import build.dream.common.catering.domains.MenuDetail;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.DeleteModel;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

            BigInteger menuId = menu.getId();
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

            BigInteger menuId = menu.getId();
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
}
