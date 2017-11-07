package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.erp.domains.Branch;
import build.dream.common.erp.domains.GoodsCategory;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.erp.services.MeiTuanService;
import build.dream.erp.utils.ElemeUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/meiTuan")
public class MeiTuanController extends BasicController {
    @Autowired
    private MeiTuanService meiTuanService;

    @RequestMapping(value = "/bindingStore")
    @ResponseBody
    public String generateBindingStoreLink() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String tenantId = requestParameters.get("tenantId");
            Validate.notNull(tenantId, ApplicationHandler.obtainParameterErrorMessage("tenantId"));

            String branchId = requestParameters.get("branchId");
            Validate.notNull(branchId, ApplicationHandler.obtainParameterErrorMessage("branchId"));

            String businessId = requestParameters.get("businessId");
            Validate.notNull(businessId, ApplicationHandler.obtainParameterErrorMessage("businessId"));

            apiRest = meiTuanService.bindingStore(NumberUtils.createBigInteger(tenantId), NumberUtils.createBigInteger(branchId), businessId);
        } catch (Exception e) {
            LogUtils.error("绑定门店失败", controllerSimpleName, "createCategoryWithChildren", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
