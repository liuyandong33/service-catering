package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.models.o2o.ObtainVipInfoModel;
import build.dream.catering.utils.VipUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.domains.catering.Vip;
import build.dream.common.utils.SearchModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class O2OService {
    /**
     * 获取会员信息
     *
     * @param obtainVipInfoModel
     * @return
     */
    @Transactional(readOnly = true)
    public ApiRest obtainVipInfo(ObtainVipInfoModel obtainVipInfoModel) {
        BigInteger tenantId = obtainVipInfoModel.getTenantId();
        BigInteger vipId = obtainVipInfoModel.getVipId();
        String vipCode = obtainVipInfoModel.getVipCode();
        String phoneNumber = obtainVipInfoModel.getPhoneNumber();
        String mainOpenId = obtainVipInfoModel.getMainOpenId();
        String alipayUserId = obtainVipInfoModel.getAlipayUserId();

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
        if (StringUtils.isNotBlank(mainOpenId)) {
            searchModel.addSearchCondition(Vip.ColumnName.MAIN_OPEN_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, mainOpenId);
        }
        if (StringUtils.isNotBlank(alipayUserId)) {
            searchModel.addSearchCondition(Vip.ColumnName.ALIPAY_USER_ID, Constants.SQL_OPERATION_SYMBOL_EQUAL, alipayUserId);
        }
        Vip vip = VipUtils.find(searchModel);
        return ApiRest.builder().data(vip).className(Vip.class.getName()).message("获取会员信息成功！").successful(true).build();
    }
}
