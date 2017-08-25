package build.dream.erp.services;

import build.dream.common.saas.domains.Tenant;
import build.dream.erp.mappers.TenantMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class DemoService {
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Transactional
    public void saveTenant() {
        for (int i = 0; i < 10000; i++) {
            Tenant tenant = new Tenant();
            tenant.setCode(String.valueOf(10000 + i));
            tenant.setName("tenant_name_" + (10000 + i));
            tenantMapper.insert(tenant);
        }
    }

    @Transactional(readOnly = true)
    public List<build.dream.erp.domains.Tenant> list(Map<String, String> parameters) throws SQLException {
        String page = parameters.get("page");
        if (StringUtils.isBlank(page)) {
            page = "1";
        }

        String rows = parameters.get("rows");
        if (StringUtils.isBlank(rows)) {
            rows = "20";
        }

        return tenantMapper.findAll(Integer.valueOf(page), Integer.valueOf(rows));
    }
}
