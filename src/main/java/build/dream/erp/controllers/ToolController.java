package build.dream.erp.controllers;

import build.dream.common.annotations.SQLType;
import build.dream.common.api.ApiRest;
import build.dream.common.constants.SQLConstants;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import build.dream.common.utils.NamingStrategyUtils;
import org.apache.commons.lang.StringUtils;
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
            LogUtils.error("生成更新SQL失败", controllerSimpleName, "generateInsertSql", e);
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    @RequestMapping(value = "/generateCreateTableSql")
    @ResponseBody
    public String generateCreateTableSql() {
        String returnValue = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            String domainClassName = requestParameters.get("domainClassName");
            Validate.notNull(domainClassName, "参数(domainClassName)不能为空！");

            Class<?> domainClass = Class.forName(domainClassName);
            Field[] fields = domainClass.getDeclaredFields();
            String domainClassSimpleName = domainClass.getSimpleName();
            String tableName = NamingStrategyUtils.camelCaseToUnderscore(domainClassSimpleName.substring(0, 1).toLowerCase() + domainClassSimpleName.substring(1));
            StringBuffer createTableSql = new StringBuffer("DROP TABLE IF EXISTS ");
            createTableSql.append(tableName);
            createTableSql.append(";<br>");
            createTableSql.append("CREATE TABLE ");
            createTableSql.append(tableName);
            createTableSql.append("<br>(<br>");
            for (Field field : fields) {
                SQLType sqlType = field.getAnnotation(SQLType.class);
                if (sqlType == null) {
                    continue;
                }
                createTableSql.append(NamingStrategyUtils.camelCaseToUnderscore(field.getName()));
                createTableSql.append(" ").append(sqlType.value());
                if (StringUtils.isNotBlank(sqlType.length())) {
                    createTableSql.append(sqlType.length());
                }
                if (sqlType.notNull()) {
                    createTableSql.append(" ").append(SQLConstants.NOT_NULL);
                }
                if (sqlType.autoIncrement()) {
                    createTableSql.append(" ").append(SQLConstants.AUTO_INCREMENT);
                }
                if (sqlType.primaryKey()) {
                    createTableSql.append(" ").append(SQLConstants.PRIMARY_KEY);
                }
                if (StringUtils.isNotBlank(sqlType.defaultValue())) {
                    createTableSql.append(" ").append(SQLConstants.DEFAULT).append(" ").append(sqlType.defaultValue());
                }
                if (StringUtils.isNotBlank(sqlType.onUpdate())) {
                    createTableSql.append(" ").append(SQLConstants.ON_UPDATE).append(" ").append(sqlType.onUpdate());
                }
                if (StringUtils.isNotBlank(sqlType.comment())) {
                    createTableSql.append(" ").append(SQLConstants.COMMENT).append(" '").append(sqlType.comment()).append("'");
                }
                createTableSql.append(",");
                createTableSql.append("<br>");
            }
            createTableSql.deleteCharAt(createTableSql.length() - 1);
            createTableSql.deleteCharAt(createTableSql.length() - 1);
            createTableSql.deleteCharAt(createTableSql.length() - 1);
            createTableSql.deleteCharAt(createTableSql.length() - 1);
            createTableSql.deleteCharAt(createTableSql.length() - 1);
            createTableSql.append("<br>");
            createTableSql.append(")");
            String comment = requestParameters.get("comment");
            if (StringUtils.isNotBlank(comment)) {
                createTableSql.append(" ").append(SQLConstants.COMMENT).append(" '").append(comment).append("';");
            } else {
                createTableSql.append(";");
            }
            returnValue = createTableSql.toString();
        } catch (Exception e) {
            LogUtils.error("生成创建表SQL失败", controllerSimpleName, "generateInsertSql", e);
            returnValue = e.getMessage();
        }
        return returnValue;
    }
}
