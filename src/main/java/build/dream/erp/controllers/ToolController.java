package build.dream.erp.controllers;

import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.NamingStrategyUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Field;
import java.util.Map;

@Controller
@RequestMapping(value = "/tool")
public class ToolController extends BasicController {
    @RequestMapping(value = "/generateInsertSql")
    @ResponseBody
    public String generateInsertSql() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String domainClassName = requestParameters.get("domainClassName");
            Validate.notNull(domainClassName, "参数(domainClassName)不能为空！");

            Class<?> domainClass = Class.forName(domainClassName);
            Field[] fields = domainClass.getDeclaredFields();
            StringBuffer insertSql = new StringBuffer("INSERT INTO ");
            String domainClassSimpleName = domainClass.getSimpleName();
            String tableName = NamingStrategyUtils.camelCaseToUnderscore(domainClassSimpleName.substring(0, 1).toLowerCase() + domainClassSimpleName.substring(1));
            insertSql.append(tableName);
            insertSql.append("(");
            int index = 1;
            for (Field field : fields) {
                String fieldName = field.getName();
                if ("id".equals(fieldName) || "createTime".equals(fieldName) || "lastUpdateTime".equals(fieldName) || "deleted".equals(fieldName)) {
                    continue;
                }
                insertSql.append(NamingStrategyUtils.camelCaseToUnderscore(fieldName)).append(", ");
                if (index % 3 == 0) {
                    insertSql.append("<br>");
                }
                index++;
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.append(")");
            insertSql.append("<br>");
            insertSql.append("VALUES(");

            index = 1;
            for (Field field : fields) {
                String fieldName = field.getName();
                if ("id".equals(fieldName) || "createTime".equals(fieldName) || "lastUpdateTime".equals(fieldName) || "deleted".equals(fieldName)) {
                    continue;
                }
                insertSql.append("#{").append(fieldName).append("}, ");
                if (index % 3 == 0) {
                    insertSql.append("<br>");
                }
                index++;
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.append(")");

            returnValue = insertSql.toString();
        } catch (Exception e) {
            LogUtils.error("生成插入SQL失败", controllerSimpleName, "generateInsertSql", e);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    @RequestMapping(value = "/generateUpdateSql")
    @ResponseBody
    public String generateUpdateSql() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String domainClassName = requestParameters.get("domainClassName");
            Validate.notNull(domainClassName, "参数(domainClassName)不能为空！");

            Class<?> domainClass = Class.forName(domainClassName);
            Field[] fields = domainClass.getDeclaredFields();
            StringBuffer updateSql = new StringBuffer("UPDATE ");
            String domainClassSimpleName = domainClass.getSimpleName();
            String tableName = NamingStrategyUtils.camelCaseToUnderscore(domainClassSimpleName.substring(0, 1).toLowerCase() + domainClassSimpleName.substring(1));
            updateSql.append(tableName);
            updateSql.append("<br>SET ");

            for (Field field : fields) {
                String fieldName = field.getName();
                if ("id".equals(fieldName) || "createTime".equals(fieldName) || "lastUpdateTime".equals(fieldName)) {
                    continue;
                }
                updateSql.append(NamingStrategyUtils.camelCaseToUnderscore(fieldName));
                updateSql.append(" = ");
                updateSql.append("#{").append(fieldName).append("},<br>");
            }
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.append("<br>WHERE id = #{id}");
            returnValue = updateSql.toString();
        } catch (Exception e) {
            LogUtils.error("生成插入SQL失败", controllerSimpleName, "generateInsertSql", e);
            returnValue = e.getMessage();
        }
        return returnValue;
    }
}
