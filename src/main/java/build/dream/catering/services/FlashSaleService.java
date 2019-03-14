package build.dream.catering.services;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class FlashSaleService {
    public void saveFlashSaleOrder(BigInteger tenantId, BigInteger branchId, BigInteger vipId, BigInteger activityId, String uuir) {
        System.out.println("开始保存订单！");
    }
}
