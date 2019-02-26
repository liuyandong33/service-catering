package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.VipMapper;
import build.dream.common.catering.domains.Vip;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.ValidateUtils;

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
     * @param id
     * @return
     */
    public static Vip find(BigInteger id) {
        SearchModel searchModel = new SearchModel(true);
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
     * @param searchModel
     * @return
     */
    public static List<Vip> findAllPaged(SearchModel searchModel) {
        return DatabaseHelper.findAllPaged(Vip.class, searchModel);
    }

    /**
     * 扣减会员积分
     *
     * @param tenantId
     * @param branchId
     * @param vipId
     * @param point
     * @return
     */
    public static BigDecimal deductingVipPoint(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal point) {
        BigDecimal surplusPoint = obtainVipMapper().callProcedureDeductingVipPoint(tenantId, branchId, vipId, point);
        ValidateUtils.isTrue(surplusPoint.compareTo(BigDecimal.ZERO) >= 0, "积分不足！");
        return surplusPoint;
    }

    /**
     * 扣减会员储值
     *
     * @param tenantId
     * @param branchId
     * @param vipId
     * @param balance
     * @return
     */
    public static BigDecimal deductingVipBalance(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal balance) {
        BigDecimal surplusBalance = obtainVipMapper().callProcedureDeductingVipBalance(tenantId, branchId, vipId, balance);
        ValidateUtils.isTrue(surplusBalance.compareTo(BigDecimal.ZERO) >= 0, "余额不足！");
        return surplusBalance;
    }

    /**
     * 增加会员积分
     *
     * @param tenantId
     * @param branchId
     * @param vipId
     * @param point
     * @return
     */
    public static BigDecimal addVipPoint(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal point) {
        BigDecimal surplusPoint = obtainVipMapper().callProcedureAddVipPoint(tenantId, branchId, vipId, point);
        return surplusPoint;
    }

    /**
     * 增加会员储值
     *
     * @param tenantId
     * @param branchId
     * @param vipId
     * @param balance
     * @return
     */
    public static BigDecimal addVipBalance(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal balance) {
        BigDecimal surplusBalance = obtainVipMapper().callProcedureAddVipBalance(tenantId, branchId, vipId, balance);
        return surplusBalance;
    }
}
