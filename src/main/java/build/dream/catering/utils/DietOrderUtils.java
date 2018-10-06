package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.constants.DietOrderConstants;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.models.alipay.AlipayTradeRefundModel;
import build.dream.common.models.weixinpay.RefundModel;
import build.dream.common.utils.*;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietOrderUtils {
    public static Map<BigInteger, List<DietOrderDetail>> splitDietOrderDetails(List<DietOrderDetail> dietOrderDetails) {
        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = new HashMap<BigInteger, List<DietOrderDetail>>();
        for (DietOrderDetail dietOrderDetail : dietOrderDetails) {
            BigInteger dietOrderGroupId = dietOrderDetail.getDietOrderGroupId();
            List<DietOrderDetail> dietOrderDetailList = dietOrderDetailMap.get(dietOrderGroupId);
            if (CollectionUtils.isEmpty(dietOrderDetailList)) {
                dietOrderDetailList = new ArrayList<DietOrderDetail>();
                dietOrderDetailMap.put(dietOrderGroupId, dietOrderDetailList);
            }
            dietOrderDetailList.add(dietOrderDetail);
        }
        return dietOrderDetailMap;
    }

    public static void recoveryStock(DietOrder dietOrder) {
        BigInteger dietOrderId = dietOrder.getId();

        List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
        searchConditions.add(new SearchCondition("deleted", Constants.SQL_OPERATION_SYMBOL_EQUAL, 0));
        searchConditions.add(new SearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId));
        SearchModel dietOrderGroupSearchModel = new SearchModel();
        dietOrderGroupSearchModel.setSearchConditions(searchConditions);
        List<DietOrderGroup> dietOrderGroups = DatabaseHelper.findAll(DietOrderGroup.class, dietOrderGroupSearchModel);

        SearchModel dietOrderDetailSearchModel = new SearchModel();
        dietOrderDetailSearchModel.setSearchConditions(searchConditions);
        List<DietOrderDetail> dietOrderDetails = DatabaseHelper.findAll(DietOrderDetail.class, dietOrderDetailSearchModel);

        Map<BigInteger, List<DietOrderDetail>> dietOrderDetailMap = DietOrderUtils.splitDietOrderDetails(dietOrderDetails);

        List<DietOrderDetail> normalDietOrderDetails = new ArrayList<DietOrderDetail>();
        for (DietOrderGroup dietOrderGroup : dietOrderGroups) {
            String type = dietOrderGroup.getType();
            BigInteger dietOrderGroupId = dietOrderGroup.getId();
            if (DietOrderConstants.GROUP_TYPE_NORMAL.equals(type)) {
                normalDietOrderDetails.addAll(dietOrderDetailMap.get(dietOrderGroupId));
            }
        }

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        for (DietOrderDetail normalDietOrderDetail : normalDietOrderDetails) {
            goodsIds.add(normalDietOrderDetail.getGoodsId());
        }

        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        List<Goods> goodsList = DatabaseHelper.findAll(Goods.class, goodsSearchModel);
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodsList) {
            goodsMap.put(goods.getId(), goods);
        }

        for (DietOrderDetail normalDietOrderDetail : normalDietOrderDetails) {
            BigInteger goodsId = normalDietOrderDetail.getGoodsId();
            Goods goods = goodsMap.get(goodsId);
            if (goods.isStocked()) {
                GoodsUtils.addGoodsStock(goodsId, normalDietOrderDetail.getGoodsSpecificationId(), normalDietOrderDetail.getQuantity());
            }
        }
    }

    public static void refund(DietOrder dietOrder) throws IOException, DocumentException {
        BigInteger tenantId = dietOrder.getTenantId();
        BigInteger branchId = dietOrder.getBranchId();
        BigInteger dietOrderId = dietOrder.getId();
        BigInteger vipId = dietOrder.getVipId();
        Vip vip = null;
        if (vipId.compareTo(Constants.BIGINT_DEFAULT_VALUE) != 0) {
            vip = VipUtils.find(vipId);
        }

        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("diet_order_id", Constants.SQL_OPERATION_SYMBOL_EQUAL, dietOrderId);
        List<DietOrderPayment> dietOrderPayments = DatabaseHelper.findAll(DietOrderPayment.class, searchModel);

        for (DietOrderPayment dietOrderPayment : dietOrderPayments) {
            String paymentCode = dietOrderPayment.getPaymentCode();
            String extraInfo = dietOrderPayment.getExtraInfo();
            if (Constants.PAYMENT_CODE_HYJF.equals(paymentCode)) {
                VipUtils.addVipPoint(vip.getTenantId(), vip.getBranchId(), vipId, dietOrderPayment.getPaidAmount().multiply(BigDecimal.valueOf(Double.valueOf(extraInfo))));
            } else if (Constants.PAYMENT_CODE_HYQB.equals(paymentCode)) {
                VipUtils.addVipBalance(vip.getTenantId(), vip.getBranchId(), vipId, dietOrderPayment.getPaidAmount());
            } else if (Constants.PAYMENT_CODE_WX.equals(paymentCode)) {
                RefundModel refundModel = new RefundModel();
                refundModel.setOutTradeNo(dietOrder.getOrderNumber());
                refundModel.setOutRefundNo(dietOrder.getOrderNumber());
                refundModel.setTotalFee(dietOrder.getPayableAmount().multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue());
                refundModel.setRefundFee(dietOrder.getPayableAmount().multiply(Constants.BIG_DECIMAL_ONE_HUNDRED).intValue());
                JSONObject extraInfoJsonObject = JSONObject.fromObject(extraInfo);
                JSONObject attachJsonObject = JSONObject.fromObject(extraInfoJsonObject.getString("attach"));

                refundModel.setTradeType(WeiXinPayUtils.obtainTradeType(attachJsonObject.getInt("paidScene")));
                WeiXinPayUtils.refund(tenantId.toString(), branchId.toString(), refundModel);
            } else if (Constants.PAYMENT_CODE_ALIPAY.equals(paymentCode)) {
                AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
                AlipayUtils.alipayTradeRefund(tenantId.toString(), branchId.toString(), null, alipayTradeRefundModel);
            }
        }
    }
}
