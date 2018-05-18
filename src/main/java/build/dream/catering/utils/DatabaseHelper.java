package build.dream.catering.utils;

import build.dream.catering.mappers.UniversalMapper;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.NamingStrategyUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static UniversalMapper UNIVERSAL_MAPPER;

    public static UniversalMapper obtainUniversalMapper() {
        if (UNIVERSAL_MAPPER == null) {
            UNIVERSAL_MAPPER = ApplicationHandler.getBean(UniversalMapper.class);
        }
        return UNIVERSAL_MAPPER;
    }

    public static long insert(Object domain) {
        return obtainUniversalMapper().insert(domain);
    }

    public static long update(Object domain) {
        return obtainUniversalMapper().update(domain);
    }

    public static <T> T find(Class<T> domainClass, SearchModel searchModel) {
        String simpleName = domainClass.getSimpleName();
        String tableName = NamingStrategyUtils.camelCaseToUnderscore(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        return find(domainClass, tableName, searchModel);
    }

    public static <T> T find(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            Map<String, Object> map = obtainUniversalMapper().find(searchModel);
            T t = domainClass.newInstance();
            BeanUtils.populate(t, map);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> findAll(Class<T> domainClass, SearchModel searchModel) {
        String simpleName = domainClass.getSimpleName();
        String tableName = NamingStrategyUtils.camelCaseToUnderscore(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        return findAll(domainClass, tableName, searchModel);
    }

    public static <T> List<T> findAll(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            List<Map<String, Object>> result = obtainUniversalMapper().findAll(searchModel);
            List<T> list = new ArrayList<T>();
            for (Map<String, Object> map : result) {
                T t = domainClass.newInstance();
                BeanUtils.populate(t, map);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> long count(Class<T> domainClass, SearchModel searchModel) {
        String simpleName = domainClass.getSimpleName();
        String tableName = NamingStrategyUtils.camelCaseToUnderscore(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        return count(tableName, searchModel);
    }

    public static long count(String tableName, SearchModel searchModel) {
        searchModel.setTableName(tableName);
        return obtainUniversalMapper().count(searchModel);
    }

    public static <T> List<T> findAllPaged(Class<T> domainClass, SearchModel searchModel) {
        String simpleName = domainClass.getSimpleName();
        String tableName = NamingStrategyUtils.camelCaseToUnderscore(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        return findAllPaged(domainClass, tableName, searchModel);
    }

    public static <T> List<T> findAllPaged(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            List<Map<String, Object>> result = obtainUniversalMapper().findAllPaged(searchModel);
            List<T> list = new ArrayList<T>();
            for (Map<String, Object> map : result) {
                T t = domainClass.newInstance();
                BeanUtils.populate(t, map);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
