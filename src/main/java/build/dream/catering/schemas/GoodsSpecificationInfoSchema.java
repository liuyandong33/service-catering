package build.dream.catering.schemas;

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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GoodsSpecificationInfoSchema {
    private static Map<String, JsonValidator> VALIDATORS;
    public static Map<String, JsonValidator> obtainValidators() {
        if (VALIDATORS == null) {
            try {
                InputStream inputStream = GoodsSpecificationInfoSchema.class.getClassLoader().getResourceAsStream("build/dream/catering/schemas/GoodsSpecificationInfoSchema.json");
                JsonSchema jsonSchema = JsonSchemaFactory.getInstance().getSchema(inputStream);
                Class<JsonSchema> jsonSchemaClass = JsonSchema.class;
                Field field = jsonSchemaClass.getDeclaredField("validators");
                field.setAccessible(true);
                VALIDATORS = (Map<String, JsonValidator>) field.get(jsonSchema);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return VALIDATORS;
    }

    public static void validate(JsonNode jsonNode) {
        Map<String, JsonValidator> validators = obtainValidators();
        if (MapUtils.isNotEmpty(validators)) {
            for (JsonValidator validator : validators.values()) {
                Set<ValidationMessage> errors = validator.validate(jsonNode);
                Validate.isTrue(CollectionUtils.isEmpty(errors), "验证未通过！");
            }
        }
        int a = 100;
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        validate(objectMapper.readTree("{}"));
    }
}
