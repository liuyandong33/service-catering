package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.SequenceMapper;
import build.dream.catering.mappers.VipMapper;
import build.dream.catering.models.vip.ObtainVipInfoModel;
import build.dream.catering.models.vip.SaveVipInfoModel;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.Vip;
import build.dream.common.utils.SearchModel;
import build.dream.common.utils.SerialNumberGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class VipService {
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private SequenceMapper sequenceMapper;

    @Transactional(readOnly = true)
    public ApiRest obtainVipInfo(ObtainVipInfoModel obtainVipInfoModel) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getTenantId());
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getBranchId());
        if (obtainVipInfoModel.getVipId() != null) {
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getVipId());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getVipCode())) {
            searchModel.addSearchCondition("vip_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getVipCode());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getPhoneNumber())) {
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getOpenId())) {
            searchModel.addSearchCondition("open_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getOpenId());
        }
        if (StringUtils.isNotBlank(obtainVipInfoModel.getAlipayUserId())) {
            searchModel.addSearchCondition("alipay_user_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, obtainVipInfoModel.getAlipayUserId());
        }
        Vip vip = vipMapper.find(searchModel);
        return new ApiRest(vip, "获取会员信息成功！");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveVipInfo(SaveVipInfoModel saveVipInfoModel) {
        BigInteger tenantId = saveVipInfoModel.getTenantId();
        BigInteger branchId = saveVipInfoModel.getBranchId();
        BigInteger userId = saveVipInfoModel.getUserId();
        if (saveVipInfoModel.getVipId() != null) {
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, saveVipInfoModel.getVipId());
            Vip vip = vipMapper.find(searchModel);
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
            vipMapper.update(vip);
        } else {
            String phoneNumber = saveVipInfoModel.getPhoneNumber();
            SearchModel searchModel = new SearchModel(true);
            searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            searchModel.addSearchCondition("phone_number", Constants.SQL_OPERATION_SYMBOL_EQUALS, phoneNumber);
            long count = vipMapper.count(searchModel);
            Validate.isTrue(count == 0, "手机号已存在！");

            Vip vip = new Vip();
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
            vip.setLastUpdateUserId(saveVipInfoModel.getUserId());
            vip.setCreateUserId(userId);
            vip.setLastUpdateUserId(userId);
            vip.setLastUpdateRemark("新增会员信息！");
            vipMapper.insert(vip);
        }
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存会员信息成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}