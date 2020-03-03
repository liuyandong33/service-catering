package build.dream.catering.models.eleme;

import build.dream.common.models.CateringBasicModel;
import build.dream.common.utils.ApplicationHandler;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ReplyReminderModel extends CateringBasicModel {
    private static final String[] TYPES = {"custom", "hasOut", "inCooking", "weather", "shortHand"};
    private static final String CUSTOM = "custom";

    @NotNull
    private Long orderId;

    private String type;

    @Length(max = 30)
    private String content;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        ApplicationHandler.inArray(TYPES, type, "type");
        if (CUSTOM.equals(type)) {
            ApplicationHandler.notBlank(content, "content");
        }
    }
}
