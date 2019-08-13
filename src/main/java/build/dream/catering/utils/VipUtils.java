package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.VipMapper;
import build.dream.common.domains.catering.Vip;
import build.dream.common.domains.catering.VipAccount;
import build.dream.common.domains.catering.VipType;
import build.dream.common.utils.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class VipUtils {
    private static VipMapper vipMapper;

    private static VipMapper obtainVipMapper() {
        if (vipMapper == null) {
            vipMapper = ApplicationHandler.getBean(VipMapper.class);
        }
        return vipMapper;
    }

    /**
     * 新增会员档案
     *
     * @param vip
     * @return
     */
    public static long insert(Vip vip) {
        return DatabaseHelper.insert(vip);
    }

    /**
     * 更新会员档案
     *
     * @param vip
     * @return
     */
    public static long update(Vip vip) {
        return DatabaseHelper.update(vip);
    }

    /**
     * 根据ID查询会员档案
     *
     * @param tenantId
     * @param id
     * @return
     */
    public static Vip find(BigInteger tenantId, BigInteger id) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Vip.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
        return find(searchModel);
    }

    /**
     * 查询会员档案
     *
     * @param searchModel
     * @return
     */
    public static Vip find(SearchModel searchModel) {
        return DatabaseHelper.find(Vip.class, searchModel);
    }

    /**
     * 查询会员档案
     *
     * @param searchModel
     * @return
     */
    public static List<Vip> findAll(SearchModel searchModel) {
        return DatabaseHelper.findAll(Vip.class, searchModel);
    }

    /**
     * 查询数量
     *
     * @param searchModel
     * @return
     */
    public static long count(SearchModel searchModel) {
        return DatabaseHelper.count(Vip.class, searchModel);
    }

    /**
     * 分页查询
     *
     * @param pagedSearchModel
     * @return
     */
    public static List<Vip> findAllPaged(PagedSearchModel pagedSearchModel) {
        return DatabaseHelper.findAllPaged(Vip.class, pagedSearchModel);
    }

    /**
     * 扣减会员积分
     *
     * @param tenantId
     * @param vipId
     * @param vipAccountId
     * @param point
     * @return
     */
    public static BigDecimal deductingVipPoint(BigInteger tenantId, BigInteger vipId, BigInteger vipAccountId, BigDecimal point) {
        BigDecimal surplusPoint = obtainVipMapper().callProcedureDeductingVipPoint(tenantId, vipAccountId, vipId, point);
        ValidateUtils.isTrue(surplusPoint.compareTo(BigDecimal.ZERO) >= 0, "积分不足！");
        return surplusPoint;
    }

    /**
     * 扣减会员储值
     *
     * @param tenantId
     * @param vipId
     * @param vipAccountId
     * @param balance
     * @return
     */
    public static BigDecimal deductingVipBalance(BigInteger tenantId, BigInteger vipId, BigInteger vipAccountId, BigDecimal balance) {
        BigDecimal surplusBalance = obtainVipMapper().callProcedureDeductingVipBalance(tenantId, vipAccountId, vipId, balance);
        ValidateUtils.isTrue(surplusBalance.compareTo(BigDecimal.ZERO) >= 0, "余额不足！");
        return surplusBalance;
    }

    /**
     * 增加会员积分
     *
     * @param tenantId
     * @param vipId
     * @param vipAccountId
     * @param point
     * @return
     */
    public static BigDecimal addVipPoint(BigInteger tenantId, BigInteger vipId, BigInteger vipAccountId, BigDecimal point) {
        BigDecimal surplusPoint = obtainVipMapper().callProcedureAddVipPoint(tenantId, vipId, vipAccountId, point);
        return surplusPoint;
    }

    /**
     * 增加会员储值
     *
     * @param tenantId
     * @param vipId
     * @param vipAccountId
     * @param balance
     * @return
     */
    public static BigDecimal addVipBalance(BigInteger tenantId, BigInteger vipId, BigInteger vipAccountId, BigDecimal balance) {
        BigDecimal surplusBalance = obtainVipMapper().callProcedureAddVipBalance(tenantId, vipId, vipAccountId, balance);
        return surplusBalance;
    }

    /**
     * 获取会员账户
     *
     * @param tenantId:      商户ID
     * @param branchId:      门店ID
     * @param vipId:         会员ID
     * @param vipSharedType: 会员共享类型，1-全部共享，2-全部独立，3-分组共享
     * @return
     */
    public static VipAccount obtainVipAccount(BigInteger tenantId, BigInteger branchId, BigInteger vipId, int vipSharedType) {
        return obtainVipMapper().obtainVipAccount(tenantId, branchId, vipId, vipSharedType);
    }

    /**
     * 获取会员类型
     *
     * @param tenantId
     * @param vipTypeId
     * @return
     */
    public static VipType obtainVipType(BigInteger tenantId, BigInteger vipTypeId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(VipType.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(VipType.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipTypeId);
        return DatabaseHelper.find(VipType.class, searchModel);
    }
}
