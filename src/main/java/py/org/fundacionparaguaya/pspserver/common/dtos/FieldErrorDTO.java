package py.org.fundacionparaguaya.pspserver.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final Collection<String> messages;

    public FieldErrorDTO(String dto, String field, Collection<String> messages) {
        this.objectName = dto;
        this.field = field;
        this.messages = messages;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public Collection<String> getMessages() {
        return messages;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("objectName", objectName)
                .add("field", field)
                .add("messages", messages)
                .toString();
    }
}
