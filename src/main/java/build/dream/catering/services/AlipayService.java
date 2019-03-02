package build.dream.catering.services;

import build.dream.catering.models.alipay.MarketingCardTemplateCreateModel;
import build.dream.common.api.ApiRest;
import build.dream.common.models.alipay.AlipayMarketingCardActivateUrlApplyModel;
import build.dream.common.models.alipay.AlipayMarketingCardFormTemplateSetModel;
import build.dream.common.models.alipay.AlipayMarketingCardTemplateCreateModel;
import build.dream.common.utils.AlipayUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Map;

@Service
public class AlipayService {
    @Transactional(rollbackFor = Exception.class)
    public ApiRest marketingCardTemplateCreate(MarketingCardTemplateCreateModel marketingCardTemplateCreateModel) {
        BigInteger tenantId = marketingCardTemplateCreateModel.obtainTenantId();
        BigInteger branchId = marketingCardTemplateCreateModel.obtainBranchId();

        AlipayMarketingCardTemplateCreateModel alipayMarketingCardTemplateCreateModel = AlipayMarketingCardTemplateCreateModel.builder()
                .tenantId(tenantId.toString())
                .branchId(branchId.toString())
                .build();

        Map<String, Object> cardTemplateCreateResult = AlipayUtils.alipayMarketingCardTemplateCreate(alipayMarketingCardTemplateCreateModel);
        String templateId = MapUtils.getString(cardTemplateCreateResult, "template_id");

        AlipayMarketingCardFormTemplateSetModel alipayMarketingCardFormTemplateSetModel = AlipayMarketingCardFormTemplateSetModel.builder()
                .tenantId(tenantId.toString())
                .branchId(branchId.toString())
                .build();
        Map<String, Object> cardFormTemplateSetResult = AlipayUtils.alipayMarketingCardFormTemplateSet(alipayMarketingCardFormTemplateSetModel);

        AlipayMarketingCardActivateUrlApplyModel alipayMarketingCardActivateUrlApplyModel = AlipayMarketingCardActivateUrlApplyModel.builder()
                .tenantId(tenantId.toString())
                .branchId(branchId.toString())
                .build();
        Map<String, Object> cardActivateUrlApplyResult = AlipayUtils.alipayMarketingCardActivateUrlApply(alipayMarketingCardActivateUrlApplyModel);

        return ApiRest.builder().build();
    }
}
