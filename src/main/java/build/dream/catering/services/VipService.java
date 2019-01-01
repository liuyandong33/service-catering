package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.vip.*;
import build.dream.catering.utils.SequenceUtils;
import build.dream.catering.utils.VipUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.Branch;
import build.dream.common.catering.domains.Vip;
import build.dream.common.catering.domains.VipAccount;
import build.dream.common.catering.domains.VipType;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VipService {
    /**
     * 保存会员类型
     *
     * @param saveVipTypeModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipType(SaveVipTypeModel saveVipTypeModel) {
        BigInteger id = saveVipTypeModel.getId();
        BigInteger tenantId = saveVipTypeModel.obtainTenantId();
        String tenantCode = saveVipTypeModel.obtainTenantCode();
        BigInteger branchId = saveVipTypeModel.obtainBranchId();
        String name = saveVipTypeModel.getName();
        Integer discountPolicy = saveVipTypeModel.getDiscountPolicy();
        BigDecimal discountRate = saveVipTypeModel.getDiscountRate();
        Boolean enableBonus = saveVipTypeModel.getEnableBonus();
        Integer bonusCoefficient = saveVipTypeModel.getBonusCoefficient();
        BigInteger userId = saveVipTypeModel.obtainUserId();
        Integer vipSharedType = saveVipTypeModel.obtainVipSharedType();

        VipType vipType = null;
        if (id != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(VipType.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(VipType.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            if (vipSharedType == 1) {

            } else if (vipSharedType == 2) {
                searchModel.addSearchCondition(VipType.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            } else if (vipSharedType == 3) {
                Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
                searchModel.addSearchCondition(VipType.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branch.getVipGroupId());
            }
            vipType = DatabaseHelper.find(VipType.class, searchModel);
            ValidateUtils.notNull(vipType, "会员类型不存在！");

            vipType.setName(name);
            vipType.setDiscountPolicy(discountPolicy);
            vipType.setDiscountRate(discountRate != null ? discountRate : Constants.DECIMAL_DEFAULT_VALUE);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient != null ? bonusCoefficient : Constants.INT_DEFAULT_VALUE);
            vipType.setUpdatedUserId(userId);
            DatabaseHelper.update(vipType);
        } else {
            vipType = new VipType();
            vipType.setTenantId(tenantId);
            vipType.setTenantCode(tenantCode);
            if (vipSharedType == 1) {
                vipType.setVipGroupId(Constants.BIGINT_DEFAULT_VALUE);
                vipType.setBranchId(Constants.BIGINT_DEFAULT_VALUE);
            } else if (vipSharedType == 2) {
                vipType.setBranchId(branchId);
                vipType.setVipGroupId(Constants.BIGINT_DEFAULT_VALUE);
            } else if (vipSharedType == 3) {
                Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
                vipType.setBranchId(Constants.BIGINT_DEFAULT_VALUE);
                vipType.setVipGroupId(branch.getVipGroupId());
            }
            vipType.setName(name);
            vipType.setDiscountPolicy(discountPolicy);
            vipType.setDiscountRate(discountRate != null ? discountRate : Constants.DECIMAL_DEFAULT_VALUE);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient != null ? bonusCoefficient : Constants.INT_DEFAULT_VALUE);
            vipType.setCreatedUserId(userId);
            vipType.setUpdatedUserId(userId);
            DatabaseHelper.insert(vipType);
        }

        return ApiRest.builder().data(vipType).message("保存会员类型成功！").successful(true).build();
    }

    /**
     * 获取会员信息
     *
     * @param obtainVipInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainVipInfo(ObtainVipInfoModel obtainVipInfoModel) {
        BigInteger tenantId = obtainVipInfoModel.obtainTenantId();
        BigInteger branchId = obtainVipInfoModel.obtainBranchId();
        BigInteger vipId = obtainVipInfoModel.getVipId();
        String vipCode = obtainVipInfoModel.getVipCode();
        String phoneNumber = obtainVipInfoModel.getPhoneNumber();
        String openId = obtainVipInfoModel.getOpenId();
        String alipayUserId = obtainVipInfoModel.getAlipayUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Vip.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        if (vipId != null) {
            searchModel.addSearchCondition(Vip.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipId);
        }
        if (StringUtils.isNotBlank(vipCode)) {
            searchModel.addSearchCondition(Vip.ColumnName.VIP_CODE, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipCode);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            searchModel.addSearchCondition(Vip.ColumnName.PHONE_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
        }
        if (StringUtils.isNotBlank(openId)) {
            searchModel.addSearchCondition(Vip.ColumnName.OPEN_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, openId);
        }
        if (StringUtils.isNotBlank(alipayUserId)) {
            searchModel.addSearchCondition(Vip.ColumnName.ALIPAY_USER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, alipayUserId);
        }
        Vip vip = VipUtils.find(searchModel);
        return ApiRest.builder().data(vip).message("获取会员信息成功！").successful(true).build();
    }

    /**
     * 保存会员信息
     *
     * @param saveVipInfoModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipInfo(SaveVipInfoModel saveVipInfoModel) {
        BigInteger tenantId = saveVipInfoModel.obtainTenantId();
        String tenantCode = saveVipInfoModel.obtainTenantCode();
        BigInteger branchId = saveVipInfoModel.obtainBranchId();
        BigInteger vipId = saveVipInfoModel.getVipId();
        BigInteger vipTypeId = saveVipInfoModel.getVipTypeId();
        String vipName = saveVipInfoModel.getVipName();
        Date birthday = saveVipInfoModel.getBirthday();
        String phoneNumber = saveVipInfoModel.getPhoneNumber();
        String openId = saveVipInfoModel.getOpenId();
        String mainOpenId = saveVipInfoModel.getMainOpenId();
        String alipayUserId = saveVipInfoModel.getAlipayUserId();
        BigInteger userId = saveVipInfoModel.obtainUserId();


        Vip vip = null;
        if (saveVipInfoModel.getVipId() != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(Vip.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition(Vip.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipId);
            vip = VipUtils.find(searchModel);
            ValidateUtils.notNull(vip, "会员不存在！");

            vip.setVipTypeId(vipTypeId);
            vip.setVipName(vipName);
            vip.setBirthday(birthday);
            vip.setPhoneNumber(phoneNumber);
            vip.setOpenId(StringUtils.isNotBlank(openId) ? openId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setMainOpenId(StringUtils.isNotBlank(mainOpenId) ? mainOpenId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setAlipayUserId(StringUtils.isNotBlank(alipayUserId) ? alipayUserId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setUpdatedUserId(userId);
            vip.setUpdatedRemark("修改会员信息！");
            VipUtils.update(vip);
        } else {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(Vip.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition(Vip.ColumnName.PHONE_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
            long count = VipUtils.count(searchModel);
            ValidateUtils.isTrue(count == 0, "手机号已存在！");

            vip = new Vip();
            vip.setTenantId(tenantId);
            vip.setTenantCode(tenantCode);
            vip.setBranchId(branchId);
            vip.setVipTypeId(vipTypeId);
            String vipCode = new SimpleDateFormat("yyyyMMdd").format(new Date()) + SerialNumberGenerator.nextSerialNumber(8, SequenceUtils.nextValue("vip_number"));
            vip.setVipCode(vipCode);
            vip.setVipName(vipName);
            vip.setBirthday(birthday);
            vip.setPhoneNumber(phoneNumber);
            if (StringUtils.isNotBlank(openId)) {
                vip.setOpenId(openId);
            }

            if (StringUtils.isNotBlank(mainOpenId)) {
                vip.setMainOpenId(mainOpenId);
            }

            if (StringUtils.isNotBlank(alipayUserId)) {
                vip.setAlipayUserId(alipayUserId);
            }
            vip.setCreatedUserId(userId);
            vip.setUpdatedUserId(userId);
            vip.setUpdatedRemark("新增会员信息！");
            VipUtils.insert(vip);
        }
        return ApiRest.builder().data(vip).message("保存会员信息成功！").successful(true).build();
    }

    /**
     * 修改会员共享类型
     *
     * @param changeVipSharedTypeModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest changeVipSharedType(ChangeVipSharedTypeModel changeVipSharedTypeModel) {
        BigInteger tenantId = changeVipSharedTypeModel.obtainTenantId();
        int vipSharedType = changeVipSharedTypeModel.getVipSharedType();

        Tenant tenant = TenantUtils.obtainTenantInfo(tenantId);
        int oldVipSharedType = tenant.getVipSharedType();
        if (vipSharedType == oldVipSharedType) {
            return ApiRest.builder().message("修改会员共享类型修改成功！").successful(true).build();
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(Branch.ColumnName.TYPE, Constants.SQL_OPERATION_SYMBOL_EQUAL, Constants.BRANCH_TYPE_HEADQUARTERS);
        Branch headquartersBranch = DatabaseHelper.find(Branch.class, searchModel);

        BigInteger headquartersBranchId = headquartersBranch.getId();
        String tenantCode = tenant.getCode();

        if (vipSharedType == 1) {
            List<VipAccount> vipAccounts = DatabaseHelper.findAll(VipAccount.class, TupleUtils.buildTuple3(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
            Map<BigInteger, List<VipAccount>> vipAccountsMap = new HashMap<BigInteger, List<VipAccount>>();
            Map<BigInteger, VipAccount> vipAccountMap = new HashMap<BigInteger, VipAccount>();
            for (VipAccount vipAccount : vipAccounts) {
                BigInteger vipId = vipAccount.getVipId();
                List<VipAccount> vipAccountList = vipAccountsMap.get(vipId);
                if (CollectionUtils.isEmpty(vipAccountList)) {
                    vipAccountList = new ArrayList<VipAccount>();
                    vipAccountsMap.put(vipId, vipAccountList);
                }
                vipAccountList.add(vipAccount);
                if (headquartersBranchId.equals(vipAccount.getBranchId())) {
                    vipAccountMap.put(vipId, vipAccount);
                } else {
                    vipAccount.setUpdatedRemark("修改会员共享类型为全部共享，删除会员账户！");
                    DatabaseHelper.update(vipAccount);
                }
            }

            for (Map.Entry<BigInteger, List<VipAccount>> entry : vipAccountsMap.entrySet()) {
                BigDecimal pointSum = BigDecimal.ZERO;
                BigDecimal accumulativePointSum = BigDecimal.ZERO;
                BigDecimal balanceSum = BigDecimal.ZERO;
                BigDecimal accumulativeRechargeSum = BigDecimal.ZERO;

                BigInteger vipId = entry.getKey();
                List<VipAccount> vipAccountList = entry.getValue();
                for (VipAccount vipAccount : vipAccountList) {
                    pointSum = pointSum.add(vipAccount.getPoint());
                    accumulativePointSum = accumulativePointSum.add(vipAccount.getAccumulativePoint());
                    balanceSum = balanceSum.add(vipAccount.getBalance());
                    accumulativeRechargeSum = accumulativeRechargeSum.add(vipAccount.getAccumulativeRecharge());
                }

                VipAccount vipAccount = vipAccountMap.get(vipId);
                if (vipAccount == null) {
                    vipAccount = VipAccount.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .vipId(vipId)
                            .point(pointSum)
                            .accumulativePoint(accumulativePointSum)
                            .balance(balanceSum)
                            .accumulativeRecharge(accumulativeRechargeSum)
                            .build();
                    DatabaseHelper.insert(vipAccount);
                } else {
                    vipAccount.setPoint(pointSum);
                    vipAccount.setAccumulativePoint(accumulativePointSum);
                    vipAccount.setBalance(balanceSum);
                    vipAccount.setAccumulativeRecharge(accumulativeRechargeSum);
                    DatabaseHelper.update(vipAccount);
                }
            }
        } else if (vipSharedType == 2) {
            if (oldVipSharedType == 1) {
                List<VipAccount> vipAccounts = DatabaseHelper.findAll(VipAccount.class, TupleUtils.buildTuple3(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
                for (VipAccount vipAccount : vipAccounts) {
                    vipAccount.setBranchId(headquartersBranchId);
                    DatabaseHelper.update(vipAccount);
                }
            } else if (oldVipSharedType == 3) {
                List<VipAccount> vipAccounts = DatabaseHelper.findAll(VipAccount.class, TupleUtils.buildTuple3(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
                Map<BigInteger, List<VipAccount>> vipAccountsMap = new HashMap<BigInteger, List<VipAccount>>();
                for (VipAccount vipAccount : vipAccounts) {
                    BigInteger vipId = vipAccount.getVipId();
                    List<VipAccount> vipAccountList = vipAccountsMap.get(vipId);
                    if (CollectionUtils.isEmpty(vipAccountList)) {
                        vipAccountList = new ArrayList<VipAccount>();
                        vipAccountsMap.put(vipId, vipAccountList);
                    }
                    vipAccountList.add(vipAccount);
                    vipAccount.setDeleted(true);
                    DatabaseHelper.update(vipAccount);
                }

                for (Map.Entry<BigInteger, List<VipAccount>> entry : vipAccountsMap.entrySet()) {
                    BigDecimal pointSum = BigDecimal.ZERO;
                    BigDecimal accumulativePointSum = BigDecimal.ZERO;
                    BigDecimal balanceSum = BigDecimal.ZERO;
                    BigDecimal accumulativeRechargeSum = BigDecimal.ZERO;

                    BigInteger vipId = entry.getKey();
                    List<VipAccount> vipAccountList = entry.getValue();
                    for (VipAccount vipAccount : vipAccountList) {
                        pointSum = pointSum.add(vipAccount.getPoint());
                        accumulativePointSum = accumulativePointSum.add(vipAccount.getAccumulativePoint());
                        balanceSum = balanceSum.add(vipAccount.getBalance());
                        accumulativeRechargeSum = accumulativeRechargeSum.add(vipAccount.getAccumulativeRecharge());
                    }

                    VipAccount vipAccount = VipAccount.builder()
                            .tenantId(tenantId)
                            .tenantCode(tenantCode)
                            .vipId(vipId)
                            .point(pointSum)
                            .accumulativePoint(accumulativePointSum)
                            .balance(balanceSum)
                            .accumulativeRecharge(accumulativeRechargeSum)
                            .build();
                    DatabaseHelper.insert(vipAccount);
                }
            }
        } else if (vipSharedType == 3) {
            BigInteger vipGroupId = headquartersBranch.getVipGroupId();
            ValidateUtils.isTrue(BigInteger.ONE.compareTo(vipGroupId) != 0, "请设置总部所在的会员分组！");
            if (oldVipSharedType == 1) {
                List<VipAccount> vipAccounts = DatabaseHelper.findAll(VipAccount.class, TupleUtils.buildTuple3(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
                for (VipAccount vipAccount : vipAccounts) {
                    vipAccount.setVipGroupId(vipGroupId);
                    DatabaseHelper.update(vipAccount);
                }
            } else if (oldVipSharedType == 2) {
                List<VipAccount> vipAccounts = DatabaseHelper.findAll(VipAccount.class, TupleUtils.buildTuple3(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId));
                Map<BigInteger, List<VipAccount>> vipAccountsMap = new HashMap<BigInteger, List<VipAccount>>();
                Map<BigInteger, VipAccount> vipAccountMap = new HashMap<BigInteger, VipAccount>();
                for (VipAccount vipAccount : vipAccounts) {
                    BigInteger vipId = vipAccount.getVipId();
                    List<VipAccount> vipAccountList = vipAccountsMap.get(vipId);
                    if (CollectionUtils.isEmpty(vipAccountList)) {
                        vipAccountList = new ArrayList<VipAccount>();
                        vipAccountsMap.put(vipId, vipAccountList);
                    }
                    vipAccountList.add(vipAccount);
                    if (headquartersBranchId.equals(vipAccount.getBranchId())) {
                        vipAccountMap.put(vipId, vipAccount);
                    } else {
                        vipAccount.setUpdatedRemark("修改会员共享类型为全部共享，删除会员账户！");
                        DatabaseHelper.update(vipAccount);
                    }
                }
            }
        }
        TenantUtils.updateTenantInfo(tenantId, TupleUtils.buildTuple2("vipSharedType", String.valueOf(vipSharedType)));
        return ApiRest.builder().message("修改会员共享类型修改成功！").successful(true).build();
    }

    /**
     * 查询会员类型
     *
     * @param listVipTypesModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listVipTypes(ListVipTypesModel listVipTypesModel) {
        BigInteger tenantId = listVipTypesModel.obtainTenantId();
        BigInteger branchId = listVipTypesModel.obtainBranchId();
        Integer vipSharedType = listVipTypesModel.obtainVipSharedType();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(VipType.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        if (vipSharedType == 1) {

        } else if (vipSharedType == 2) {
            searchModel.addSearchCondition(VipType.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        } else if (vipSharedType == 3) {
            Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
            searchModel.addSearchCondition(VipType.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branch.getVipGroupId());
        }
        List<VipType> vipTypes = DatabaseHelper.findAll(VipType.class, searchModel);
        return ApiRest.builder().data(vipTypes).message("查询会员类型成功！").successful(true).build();
    }
}
