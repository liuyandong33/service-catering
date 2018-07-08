package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.catering.models.vip.SaveVipTypeModel;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class VipService {
    @Autowired
    private SequenceMapper sequenceMapper;

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
            vipType.setDiscountRate(discountRate);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient);
            vipType.setLastUpdateUserId(userId);
            DatabaseHelper.update(vipType);
        } else {
            vipType = new VipType();
            vipType.setTenantId(tenantId);
            vipType.setTenantCode(tenantCode);
            vipType.setBranchId(branchId);
            vipType.setName(name);
            vipType.setDiscountPolicy(discountPolicy);
            vipType.setDiscountRate(discountRate);
            vipType.setEnableBonus(enableBonus);
            vipType.setBonusCoefficient(bonusCoefficient);
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
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getBranchId());
        if (obtainVipInfoModel.getVipId() != null) {
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getVipId());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getVipCode())) {
            searchModel.addSearchCondition("vip_code", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getVipCode());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getPhoneNumber())) {
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getOpenId())) {
            searchModel.addSearchCondition("open_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getOpenId());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getAlipayUserId())) {
            searchModel.addSearchCondition("alipay_user_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, obtainVipInfoModel.getAlipayUserId());
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
        BigInteger branchId = saveVipInfoModel.getBranchId();
        BigInteger userId = saveVipInfoModel.getUserId();

        Vip vip = null;
        if (saveVipInfoModel.getVipId() != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUAL, saveVipInfoModel.getVipId());
            vip = VipUtils.find(searchModel);
            Validate.notNull(vip, "会员不存在！");

            if (StringUtils.isNotBlank(saveVipInfoModel.getVipName())) {
                vip.setVipName(saveVipInfoModel.getVipName());
            }

            if (saveVipInfoModel.getBirthday() != null) {
                vip.setBirthday(saveVipInfoModel.getBirthday());
            }
            vip.setOpenId(saveVipInfoModel.getOpenId());
            vip.setMainOpenId(saveVipInfoModel.getMainOpenId());
            vip.setAlipayUserId(saveVipInfoModel.getAlipayUserId());
            vip.setLastUpdateUserId(userId);
            vip.setLastUpdateRemark("修改会员信息！");
            VipUtils.update(vip);
        } else {
            String phoneNumber = saveVipInfoModel.getPhoneNumber();
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, tenantId);
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUAL, phoneNumber);
            long count = VipUtils.count(searchModel);
            Validate.isTrue(count == 0, "手机号已存在！");

            vip = new Vip();
            vip.setTenantId(saveVipInfoModel.getTenantId());
            vip.setTenantCode(saveVipInfoModel.getTenantCode());
            vip.setBranchId(saveVipInfoModel.getBranchId());
            String vipCode = new SimpleDateFormat("yyyyMMdd").format(new Date()) + SerialNumberGenerator.nextSerialNumber(8, sequenceMapper.nextValue("vip_number"));
            vip.setVipCode(vipCode);
            vip.setVipName(saveVipInfoModel.getVipName());
            vip.setBirthday(saveVipInfoModel.getBirthday());
            vip.setPhoneNumber(phoneNumber);
            vip.setOpenId(saveVipInfoModel.getOpenId());
            vip.setMainOpenId(saveVipInfoModel.getMainOpenId());
            vip.setAlipayUserId(saveVipInfoModel.getAlipayUserId());
            vip.setBonus(0);
            vip.setLastUpdateUserId(saveVipInfoModel.getUserId());
            vip.setCreateUserId(userId);
            vip.setLastUpdateUserId(userId);
            vip.setLastUpdateRemark("新增会员信息！");
            VipUtils.insert(vip);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setData(vip);
        apiRest.setMessage("保存会员信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
