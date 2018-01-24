package build.dream.catering.utils;

import build.dream.catering.constants.Constants;
import build.dream.common.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.JsonValidator;
import com.networknt.schema.ValidationMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSchemaValidateUtils {
    public static JsonSchemaFactory JSON_SCHEMA_FACTORY = null;
    public static Map<String, Map<String, JsonValidator>> VALIDATORS_MAP = null;
    private static ObjectMapper objectMapper = null;

    private static ObjectMapper obtainObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static JsonSchemaFactory obtainJsonSchemaFactory() {
        if (JSON_SCHEMA_FACTORY == null) {
            JSON_SCHEMA_FACTORY = JsonSchemaFactory.getInstance();
        }
        return JSON_SCHEMA_FACTORY;
    }

    public static Map<String, JsonValidator> obtainValidators(String schemaFilePath) {
        if (VALIDATORS_MAP == null) {
            VALIDATORS_MAP = new ConcurrentHashMap<String, Map<String, JsonValidator>>();
        }

        Map<String, JsonValidator> validators = VALIDATORS_MAP.get(schemaFilePath);
        if (validators == null) {
            try {
                InputStream inputStream = JsonSchemaValidateUtils.class.getClassLoader().getResourceAsStream(schemaFilePath);
                JsonSchema jsonSchema = obtainJsonSchemaFactory().getSchema(inputStream);
                Class<JsonSchema> jsonSchemaClass = JsonSchema.class;
                Field field = jsonSchemaClass.getDeclaredField("validators");
                field.setAccessible(true);
                validators = (Map<String, JsonValidator>) field.get(jsonSchema);
                VALIDATORS_MAP.put(schemaFilePath, validators);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return validators;
    }

    public static void validateAndThrow(JsonNode jsonNode, String schemaFilePath) {
        Map<String, JsonValidator> validators = obtainValidators(schemaFilePath);
        if (MapUtils.isNotEmpty(validators)) {
            for (JsonValidator validator : validators.values()) {
                Set<ValidationMessage> errors = validator.validate(jsonNode);
                Validate.isTrue(CollectionUtils.isEmpty(errors), "验证未通过！");
            }
        }
    }

    public static boolean validate(String jsonString, String schemaFilePath) throws IOException {
        Map<String, JsonValidator> validators = obtainValidators(schemaFilePath);
        if (MapUtils.isNotEmpty(validators)) {
            JsonNode jsonNode = obtainObjectMapper().readTree(jsonString);
            for (JsonValidator validator : validators.values()) {
                Set<ValidationMessage> errors = validator.validate(jsonNode);
                if (CollectionUtils.isNotEmpty(errors)) {
                    return false;
                }
            }
        }
        return true;
    }
}
