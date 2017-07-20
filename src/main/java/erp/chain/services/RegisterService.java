package erp.chain.services;

import erp.chain.constants.Constants;
import erp.chain.domains.Tenant;
import erp.chain.mappers.SequenceMapper;
import erp.chain.mappers.TenantMapper;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.SerialNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyandong on 2017/7/18.
 */
@Service
public class RegisterService {
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private SequenceMapper sequenceMapper;

    @Transactional
    public Map<String, Object> registerTenant(Map<String, String> arguments) throws NoSuchFieldException, InstantiationException, ParseException, IllegalAccessException {
        Tenant tenant = ApplicationHandler.instantiateDomain(Tenant.class, arguments);
        String tenantCode = SerialNumberGenerator.nextSerialNumber(8, sequenceMapper.nextValue(Constants.SEQUENCE_NAME_TENANT_CODE));
        String goodsTableName = "goods_" + tenantCode;
        String saleTableName = "sale_" + tenantCode;
        tenant.setCode(tenantCode);
        tenant.setGoodsTableName(goodsTableName);
        tenant.setSaleTableName(saleTableName);
        tenantMapper.insert(tenant);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tenant", tenant);
        return data;
    }
}
