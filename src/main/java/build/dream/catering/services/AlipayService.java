package build.dream.catering.services;

import build.dream.catering.models.alipay.CreateMemberCardTemplateModel;
import build.dream.common.api.ApiRest;
import build.dream.common.models.alipay.AlipayMarketingCardActivateUrlApplyModel;
import build.dream.common.models.alipay.AlipayMarketingCardFormTemplateSetModel;
import build.dream.common.models.alipay.AlipayMarketingCardTemplateCreateModel;
import build.dream.common.domains.saas.AlipayDeveloperAccount;
import build.dream.common.utils.AlipayUtils;
import build.dream.common.utils.ValidateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Map;

@Service
public class AlipayService {
    /**
     * 创建商户会员卡模板
     *
     * @param createMemberCardTemplateModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest createMemberCardTemplate(CreateMemberCardTemplateModel createMemberCardTemplateModel) {
        BigInteger tenantId = createMemberCardTemplateModel.obtainTenantId();
        BigInteger branchId = createMemberCardTemplateModel.obtainBranchId();

        AlipayMarketingCardTemplateCreateModel alipayMarketingCardTemplateCreateModel = AlipayMarketingCardTemplateCreateModel.builder()
                .build();

        Map<String, Object> cardTemplateCreateResult = AlipayUtils.alipayMarketingCardTemplateCreate(alipayMarketingCardTemplateCreateModel);
        String templateId = MapUtils.getString(cardTemplateCreateResult, "template_id");

        AlipayMarketingCardFormTemplateSetModel alipayMarketingCardFormTemplateSetModel = AlipayMarketingCardFormTemplateSetModel.builder()
                .build();
        Map<String, Object> cardFormTemplateSetResult = AlipayUtils.alipayMarketingCardFormTemplateSet(alipayMarketingCardFormTemplateSetModel);

        AlipayMarketingCardActivateUrlApplyModel alipayMarketingCardActivateUrlApplyModel = AlipayMarketingCardActivateUrlApplyModel.builder()
                .build();
        Map<String, Object> cardActivateUrlApplyResult = AlipayUtils.alipayMarketingCardActivateUrlApply(alipayMarketingCardActivateUrlApplyModel);

        return ApiRest.builder().build();
    }

    public ApiRest generateAppToAppAuthorizeUrl() {
        AlipayDeveloperAccount alipayDeveloperAccount = AlipayUtils.obtainAlipayDeveloperAccount("");
        ValidateUtils.notNull(alipayDeveloperAccount, "未配置支付宝开发者账号！");

        String appId = alipayDeveloperAccount.getAppId();

        String appToAppAuthorizeUrl = AlipayUtils.generateAppToAppAuthorizeUrl(appId, "");
        return ApiRest.builder().data(appToAppAuthorizeUrl).message("生成应用授权连接成功").successful(true).build();
    }
}
