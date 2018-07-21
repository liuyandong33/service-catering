package build.dream.catering.utils;

import build.dream.catering.mappers.VipMapper;
import build.dream.common.erp.catering.domains.Vip;
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

    public static long insert(Vip vip) {
        return DatabaseHelper.insert(vip);
    }

    public static long update(Vip vip) {
        return DatabaseHelper.update(vip);
    }

    public static Vip find(SearchModel searchModel) {
        return DatabaseHelper.find(Vip.class, searchModel);
    }

    public static List<Vip> findAll(SearchModel searchModel) {
        return DatabaseHelper.findAll(Vip.class, searchModel);
    }

    public static long count(SearchModel searchModel) {
        return DatabaseHelper.count(Vip.class, searchModel);
    }

    public static List<Vip> findAllPaged(SearchModel searchModel) {
        return DatabaseHelper.findAllPaged(Vip.class, searchModel);
    }

    public static BigDecimal deductingVipPoint(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal point) {
        BigDecimal surplusPoint = vipMapper.callProcedureDeductingVipPoint(tenantId, branchId, vipId, point);
        ValidateUtils.isTrue(surplusPoint.compareTo(BigDecimal.ZERO) >= 0, "积分不足！");
        return surplusPoint;
    }

    public static BigDecimal deductingVipBalance(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal balance) {
        BigDecimal surplusBalance = vipMapper.callProcedureDeductingVipBalance(tenantId, branchId, vipId, balance);
        ValidateUtils.isTrue(surplusBalance.compareTo(BigDecimal.ZERO) >= 0, "余额不足！");
        return surplusBalance;
    }

    public static BigDecimal addVipPoint(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal point) {
        BigDecimal surplusPoint = vipMapper.callProcedureAddVipPoint(tenantId, branchId, vipId, point);
        return surplusPoint;
    }

    public static BigDecimal addVipBalance(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigDecimal balance) {
        BigDecimal surplusBalance = vipMapper.callProcedureAddVipBalance(tenantId, branchId, vipId, balance);
        return surplusBalance;
    }
}
