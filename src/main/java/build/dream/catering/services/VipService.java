package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.VipMapper;
import build.dream.catering.models.vip.*;
import build.dream.catering.utils.SequenceUtils;
import build.dream.catering.utils.VipUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.catering.domains.*;
import build.dream.common.saas.domains.Tenant;
import build.dream.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VipService {
    @Autowired
    private VipMapper vipMapper;

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
        int vipSharedType = obtainVipInfoModel.obtainVipSharedType();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
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

        SearchModel vipAccountSearchModel = new SearchModel(true);
        vipAccountSearchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        if (vipSharedType == 1) {

        } else if (vipSharedType == 2) {
            vipAccountSearchModel.addSearchCondition(Vip.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        } else if (vipSharedType == 3) {
            Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
            vipAccountSearchModel.addSearchCondition(VipAccount.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branch.getVipGroupId());
        }
        VipAccount vipAccount = DatabaseHelper.find(VipAccount.class, vipAccountSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vip", vip);
        data.put("vipAccount", vipAccount);

        return ApiRest.builder().data(data).message("获取会员信息成功！").successful(true).build();
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
        int vipSharedType = saveVipInfoModel.obtainVipSharedType();

        VipType vipType = DatabaseHelper.find(VipType.class, TupleUtils.buildTuple3(VipType.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(VipType.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipTypeId));
        ValidateUtils.notNull(vipType, "会员类型不存在！");

        Vip vip = null;
        if (vipId != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(Vip.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipId);
            vip = VipUtils.find(searchModel);
            ValidateUtils.notNull(vip, "会员不存在！");

            vip.setVipName(vipName);
            vip.setBirthday(birthday);
            vip.setPhoneNumber(phoneNumber);
            vip.setOpenId(StringUtils.isBlank(openId) ? Constants.VARCHAR_DEFAULT_VALUE : openId);
            vip.setMainOpenId(StringUtils.isBlank(mainOpenId) ? Constants.VARCHAR_DEFAULT_VALUE : mainOpenId);
            vip.setAlipayUserId(StringUtils.isBlank(alipayUserId) ? Constants.VARCHAR_DEFAULT_VALUE : alipayUserId);
            vip.setUpdatedUserId(userId);
            vip.setUpdatedRemark("修改会员信息！");
            VipUtils.update(vip);
        } else {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(Vip.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(Vip.ColumnName.PHONE_NUMBER, Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
            long count = VipUtils.count(searchModel);
            ValidateUtils.isTrue(count == 0, "手机号已存在！");

            String vipCode = new SimpleDateFormat("yyyyMMdd").format(new Date()) + SerialNumberGenerator.nextSerialNumber(8, SequenceUtils.nextValue("vip_number"));

            vip = Vip.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(branchId)
                    .vipCode(vipCode)
                    .vipName(vipName)
                    .birthday(birthday)
                    .phoneNumber(phoneNumber)
                    .openId(StringUtils.isBlank(openId) ? Constants.VARCHAR_DEFAULT_VALUE : openId)
                    .mainOpenId(StringUtils.isBlank(mainOpenId) ? Constants.VARCHAR_DEFAULT_VALUE : mainOpenId)
                    .alipayUserId(StringUtils.isBlank(alipayUserId) ? Constants.VARCHAR_DEFAULT_VALUE : alipayUserId)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增会员信息！")
                    .build();
            VipUtils.insert(vip);
        }

        SearchModel vipAccountSearchModel = new SearchModel(true);
        vipAccountSearchModel.addSearchCondition(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        vipAccountSearchModel.addSearchCondition(VipAccount.ColumnName.VIP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vip.getId());

        BigInteger vipAccountBranchId = null;
        BigInteger vipAccountVipGroupId = null;
        if (vipSharedType == 1) {
            vipAccountBranchId = BigInteger.ZERO;
            vipAccountVipGroupId = BigInteger.ZERO;
        } else if (vipSharedType == 2) {
            vipAccountSearchModel.addSearchCondition(VipAccount.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);

            vipAccountBranchId = branchId;
            vipAccountVipGroupId = BigInteger.ZERO;
        } else if (vipSharedType == 3) {
            Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
            BigInteger vipGroupId = branch.getVipGroupId();
            vipAccountSearchModel.addSearchCondition(VipAccount.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipGroupId);

            vipAccountBranchId = BigInteger.ZERO;
            vipAccountVipGroupId = vipGroupId;
        }

        VipAccount vipAccount = DatabaseHelper.find(VipAccount.class, vipAccountSearchModel);
        if (vipAccount == null) {
            vipAccount = VipAccount.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .branchId(vipAccountBranchId)
                    .vipTypeId(vipTypeId)
                    .vipGroupId(vipAccountVipGroupId)
                    .point(BigDecimal.ZERO)
                    .accumulativePoint(BigDecimal.ZERO)
                    .balance(BigDecimal.ZERO)
                    .accumulativeRecharge(BigDecimal.ZERO)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("创建会员账户！")
                    .build();
            DatabaseHelper.insert(vipAccount);
        } else {
            vipAccount.setVipTypeId(vipTypeId);
            vipAccount.setUpdatedRemark("修改会员账户！");
            DatabaseHelper.update(vipAccount);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vip", vip);
        data.put("vipAccount", vipAccount);

        return ApiRest.builder().data(data).message("保存会员信息成功！").successful(true).build();
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

    /**
     * 删除会员类型
     *
     * @param deleteVipTypeModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteVipType(DeleteVipTypeModel deleteVipTypeModel) {
        BigInteger tenantId = deleteVipTypeModel.obtainTenantId();
        BigInteger branchId = deleteVipTypeModel.obtainBranchId();
        BigInteger vipTypeId = deleteVipTypeModel.getVipTypeId();
        BigInteger userId = deleteVipTypeModel.obtainUserId();
        Integer vipSharedType = deleteVipTypeModel.obtainVipSharedType();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(VipType.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        if (vipSharedType == 1) {

        } else if (vipSharedType == 2) {
            searchModel.addSearchCondition(VipType.ColumnName.BRANCH_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        } else if (vipSharedType == 3) {
            Branch branch = DatabaseHelper.find(Branch.class, TupleUtils.buildTuple3(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId), TupleUtils.buildTuple3(Branch.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId));
            searchModel.addSearchCondition(VipType.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, branch.getVipGroupId());
        }
        searchModel.addSearchCondition(VipType.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipTypeId);
        VipType vipType = DatabaseHelper.find(VipType.class, searchModel);
        ValidateUtils.notNull(vipType, "会员类型不存在！");

        PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
        pagedSearchModel.addSearchCondition(VipAccount.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        pagedSearchModel.addSearchCondition(VipAccount.ColumnName.VIP_TYPE_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipTypeId);
        pagedSearchModel.setPage(1);
        pagedSearchModel.setRows(1);
        List<VipAccount> vipAccounts = DatabaseHelper.findAllPaged(VipAccount.class, pagedSearchModel);
        ValidateUtils.isTrue(CollectionUtils.isEmpty(vipAccounts), "会员类型【" + vipType.getName() + "】下存在会员，不能删除！");

        vipType.setUpdatedUserId(userId);
        vipType.setUpdatedRemark("删除会员类型！");
        vipType.setDeletedTime(new Date());
        vipType.setDeleted(true);
        DatabaseHelper.update(vipType);

        return ApiRest.builder().message("删除会员类型成功！").successful(true).build();
    }

    /**
     * 删除会员分组
     *
     * @param deleteVipGroupModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest deleteVipGroup(DeleteVipGroupModel deleteVipGroupModel) {
        BigInteger tenantId = deleteVipGroupModel.obtainTenantId();
        BigInteger vipGroupId = deleteVipGroupModel.getVipGroupId();
        BigInteger userId = deleteVipGroupModel.obtainUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(VipGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition(VipGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipGroupId);
        VipGroup vipGroup = DatabaseHelper.find(VipGroup.class, searchModel);
        ValidateUtils.notNull(vipGroup, "会员分组不存在！");

        PagedSearchModel pagedSearchModel = new PagedSearchModel(true);
        pagedSearchModel.addSearchCondition(Branch.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        pagedSearchModel.addSearchCondition(Branch.ColumnName.VIP_GROUP_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, vipGroupId);
        pagedSearchModel.setPage(1);
        pagedSearchModel.setRows(1);
        List<Branch> branches = DatabaseHelper.findAllPaged(Branch.class, searchModel);
        ValidateUtils.isTrue(CollectionUtils.isEmpty(branches), "会员分组【" + vipGroup.getName() + "】已存在门店，不能删除！");

        vipGroup.setUpdatedUserId(userId);
        vipGroup.setUpdatedRemark("删除会员分组！");
        vipGroup.setDeletedTime(new Date());
        vipGroup.setDeleted(true);
        DatabaseHelper.update(vipGroup);

        return ApiRest.builder().message("删除会员分组成功！").successful(true).build();
    }

    /**
     * 获取会员分组
     *
     * @param listVipGroupsModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest listVipGroups(ListVipGroupsModel listVipGroupsModel) {
        BigInteger tenantId = listVipGroupsModel.obtainTenantId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition(VipGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        List<VipGroup> vipGroups = DatabaseHelper.findAll(VipGroup.class, searchModel);

        return ApiRest.builder().data(vipGroups).message("获取会员分组成功！").successful(true).build();
    }

    /**
     * 保存会员分组
     *
     * @param saveVipGroupModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipGroup(SaveVipGroupModel saveVipGroupModel) {
        BigInteger tenantId = saveVipGroupModel.obtainTenantId();
        String tenantCode = saveVipGroupModel.obtainTenantCode();
        BigInteger userId = saveVipGroupModel.obtainUserId();
        BigInteger id = saveVipGroupModel.getId();
        String name = saveVipGroupModel.getName();

        VipGroup vipGroup = null;
        if (id != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition(VipGroup.ColumnName.TENANT_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition(VipGroup.ColumnName.ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            vipGroup = DatabaseHelper.find(VipGroup.class, searchModel);
            ValidateUtils.notNull(vipGroup, "会员分组不存在！");

            vipGroup.setName(name);
            vipGroup.setUpdatedUserId(userId);
            vipGroup.setUpdatedRemark("修改会员分组！");
            DatabaseHelper.update(vipGroup);
        } else {
            vipGroup = VipGroup.builder()
                    .tenantId(tenantId)
                    .tenantCode(tenantCode)
                    .name(name)
                    .createdUserId(userId)
                    .updatedUserId(userId)
                    .updatedRemark("新增会员分组！")
                    .build();
            DatabaseHelper.insert(vipGroup);
        }
        return ApiRest.builder().data(vipGroup).message("保存会员分组成功！").successful(true).build();
    }

    @Transactional(readOnly = true)
    public ApiRest listVipInfos(ListVipInfosModel listVipInfosModel) {
        BigInteger tenantId = listVipInfosModel.obtainTenantId();
        BigInteger branchId = listVipInfosModel.obtainBranchId();
        int sharedType = listVipInfosModel.obtainVipSharedType();
        int page = listVipInfosModel.getPage();
        int rows = listVipInfosModel.getRows();
        long count = vipMapper.countVipInfos(tenantId, branchId, sharedType);

        List<Map<String, Object>> vipInfos = null;
        if (count > 0) {
            vipInfos = vipMapper.listVipInfos(tenantId, branchId, sharedType, (page - 1) * rows, rows);
        } else {
            vipInfos = new ArrayList<Map<String, Object>>();
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("total", count);
        data.put("rows", vipInfos);

        return ApiRest.builder().data(data).message("获取会员信息成功！").successful(true).build();
    }
}
