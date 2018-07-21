package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.catering.models.vip.SaveVipTypeModel;
import build.dream.catering.utils.SequenceUtils;
import build.dream.catering.utils.VipUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Vip;
import build.dream.common.erp.catering.domains.VipType;
import build.dream.common.utils.DatabaseHelper;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        BigInteger tenantId = saveVipTypeModel.getTenantId();
        String tenantCode = saveVipTypeModel.getTenantCode();
        BigInteger branchId = saveVipTypeModel.getBranchId();
        String name = saveVipTypeModel.getName();
        Integer discountPolicy = saveVipTypeModel.getDiscountPolicy();
        BigDecimal discountRate = saveVipTypeModel.getDiscountRate();
        Boolean enableBonus = saveVipTypeModel.getEnableBonus();
        Integer bonusCoefficient = saveVipTypeModel.getBonusCoefficient();
        BigInteger userId = saveVipTypeModel.getUserId();

        VipType vipType = null;
        if (id != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, id);
            vipType = DatabaseHelper.find(VipType.class, searchModel);
            ValidateUtils.notNull(vipType, "会员类型不存在！");

            vipType.setName(name);
            vipType.setDiscountPolicy(discountPolicy);
            vipType.setDiscountRate(discountRate != null ? discountRate : Constants.DECIMAL_DEFAULT_VALUE);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient != null ? bonusCoefficient : Constants.INT_DEFAULT_VALUE);
            vipType.setLastUpdateUserId(userId);
            DatabaseHelper.update(vipType);
        } else {
            vipType = new VipType();
            vipType.setTenantId(tenantId);
            vipType.setTenantCode(tenantCode);
            vipType.setBranchId(branchId);
            vipType.setName(name);
            vipType.setDiscountPolicy(discountPolicy);
            vipType.setDiscountRate(discountRate != null ? discountRate : Constants.DECIMAL_DEFAULT_VALUE);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient != null ? bonusCoefficient : Constants.INT_DEFAULT_VALUE);
            vipType.setCreateUserId(userId);
            vipType.setLastUpdateUserId(userId);
            DatabaseHelper.insert(vipType);
        }

        return new ApiRest(vipType, "保存会员类型成功！");
    }

    /**
     * 获取会员信息
     *
     * @param obtainVipInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainVipInfo(ObtainVipInfoModel obtainVipInfoModel) {
        BigInteger tenantId = obtainVipInfoModel.getTenantId();
        BigInteger branchId = obtainVipInfoModel.getBranchId();
        BigInteger vipId = obtainVipInfoModel.getVipId();
        String vipCode = obtainVipInfoModel.getVipCode();
        String phoneNumber = obtainVipInfoModel.getPhoneNumber();
        String openId = obtainVipInfoModel.getOpenId();
        String alipayUserId = obtainVipInfoModel.getAlipayUserId();

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
        if (vipId != null) {
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, vipId);
        }
        if (StringUtils.isNotBlank(vipCode)) {
            searchModel.addSearchCondition("vip_code", Constants.SQL_OPERATION_SYMBOL_EQUAL, vipCode);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
        }
        if (StringUtils.isNotBlank(openId)) {
            searchModel.addSearchCondition("open_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, openId);
        }
        if (StringUtils.isNotBlank(alipayUserId)) {
            searchModel.addSearchCondition("alipay_user_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, alipayUserId);
        }
        Vip vip = VipUtils.find(searchModel);
        return new ApiRest(vip, "获取会员信息成功！");
    }

    /**
     * 保存会员信息
     *
     * @param saveVipInfoModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipInfo(SaveVipInfoModel saveVipInfoModel) {
        BigInteger tenantId = saveVipInfoModel.getTenantId();
        String tenantCode = saveVipInfoModel.getTenantCode();
        BigInteger branchId = saveVipInfoModel.getBranchId();
        BigInteger vipId = saveVipInfoModel.getVipId();
        BigInteger vipTypeId = saveVipInfoModel.getVipTypeId();
        String vipName = saveVipInfoModel.getVipName();
        Date birthday = saveVipInfoModel.getBirthday();
        String phoneNumber = saveVipInfoModel.getPhoneNumber();
        String openId = saveVipInfoModel.getOpenId();
        String mainOpenId = saveVipInfoModel.getMainOpenId();
        String alipayUserId = saveVipInfoModel.getAlipayUserId();
        BigInteger userId = saveVipInfoModel.getUserId();


        Vip vip = null;
        if (saveVipInfoModel.getVipId() != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, vipId);
            vip = VipUtils.find(searchModel);
            Validate.notNull(vip, "会员不存在！");

            vip.setVipTypeId(vipTypeId);
            vip.setVipName(vipName);
            vip.setBirthday(birthday);
            vip.setPhoneNumber(phoneNumber);
            vip.setOpenId(StringUtils.isNotBlank(openId) ? openId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setMainOpenId(StringUtils.isNotBlank(mainOpenId) ? mainOpenId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setAlipayUserId(StringUtils.isNotBlank(alipayUserId) ? alipayUserId : Constants.VARCHAR_DEFAULT_VALUE);
            vip.setLastUpdateUserId(userId);
            vip.setLastUpdateRemark("修改会员信息！");
            VipUtils.update(vip);
        } else {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
            long count = VipUtils.count(searchModel);
            Validate.isTrue(count == 0, "手机号已存在！");

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
            vip.setCreateUserId(userId);
            vip.setLastUpdateUserId(userId);
            vip.setLastUpdateRemark("新增会员信息！");
            VipUtils.insert(vip);
        }
        return new ApiRest(vip, "保存会员信息成功！");
    }
}
