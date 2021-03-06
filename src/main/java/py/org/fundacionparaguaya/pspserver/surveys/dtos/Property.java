package py.org.fundacionparaguaya.pspserver.surveys.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/*
 * FP-PSP Server
 * A sample API to manage surveys
 *
 * OpenAPI spec version: 1.0.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

/**
 * Holds info representing the definition of the field
 */
@ApiModel(description = "Holds info representing the definition of the field")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Property implements Serializable {

    @JsonProperty("type")
    private TypeEnum type = null;

    @JsonProperty("title")
    private PropertyTitle title = null;

    @JsonProperty(value = "shortName", required = false)
    private PropertyTitle shortName = null;

    @JsonProperty("description")
    private PropertyTitle description = null;

    @JsonProperty("default")
    private Object defaultValue;

    @JsonProperty("format")
    private FormatEnum format = null;

    @JsonProperty("enum")
    private List<Object> enumValues = null;

    @JsonProperty("enumNames")
    private List<Object> enumNames = null;

    @JsonProperty("items")
    private Items items = null;

    public Property type(TypeEnum type) {
        this.type = type;
        return this;
    }

    public Property format(FormatEnum formatEnum) {
        this.format = formatEnum;
        return this;
    }

    public Property enumValues(List<Object> enumValues) {
        this.enumValues = enumValues;
        return this;
    }

    public static List<Object> getDefaultEnumValues() {
        return Arrays.asList("red", "blue", "green");
    }

    public Property itemsValue(Items items) {
        this.items = items;
        return this;
    }

    /**
     * The type of this field
     *
     * @return type
     **/
    @JsonProperty("type")
    @ApiModelProperty(value = "The type of this field")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public Property title(PropertyTitle title) {
        this.title = title;
        return this;
    }

    public Property shortName(PropertyTitle shortName) {
        this.shortName = shortName;
        return this;
    }

    public Property description(PropertyTitle description) {
        this.description = description;
        return this;
    }

    /**
     * The title of this field
     *
     * @return title
     **/
    @JsonProperty("title")
    @ApiModelProperty(value = "The title of this field")
    public PropertyTitle getTitle() {
        return title;
    }

    public void setTitle(PropertyTitle title) {
        this.title = title;
    }

    /**
     * The short name of this field
     * This field is used for Reports headers and filter names
     *
     * @return shortName
     **/
    @JsonProperty("shortName")
    @ApiModelProperty(value = "The short name of this field")
    public PropertyTitle getShortName() {
        return shortName;
    }

    public void setShortName(PropertyTitle shortName) {
        this.shortName = shortName;
    }

    /**
     * The title of this field
     *
     * @return title
     **/
    @JsonProperty("description")
    @ApiModelProperty(value = "The description of this field")
    public PropertyTitle getDescription() {
        return description;
    }

    public void setDescription(PropertyTitle description) {
        this.description = description;
    }

    @JsonProperty("items")
    public Items getItems() {
        return items;
    }

    public boolean valueIsOfValidType(Object value) {
        return this.getType().apply(value);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * The type of this field
     */
    public enum TypeEnum implements Function<Object, Boolean> {
        STRING("string") {
            @Override
            public Boolean apply(Object value) {
                return value instanceof String;
            }
        },
        NUMBER("number") {
            @Override
            public Boolean apply(Object value) {
                return value instanceof Float
                        || value instanceof Double
                        || value instanceof Integer;
            }
        },
        BOOLEAN("boolean") {
            @Override
            public Boolean apply(Object value) {
                return value instanceof Boolean;
            }
        },
        OBJECT("object") {
            @Override
            public Boolean apply(Object value) {
                return true;
            }
        },
        INTEGER("integer") {
            @Override
            public Boolean apply(Object value) {
                return value instanceof Integer;
            }
        },
        ARRAY("array") {
            @Override
            public Boolean apply(Object value) {
                return value instanceof ArrayList<?>;
            }
        };

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }

            return null;
        }

    }

    /**
     * The type of this field
     */
    public enum FormatEnum {
        DATA_URL("data-url"), DATE("date"), EMAIL("email");

        private String value;

        FormatEnum(String value) {
            this.value = value;
        }

        public static final List<String> FILE_TYPES = Arrays.asList("video",
                "image");

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static FormatEnum fromValue(String text) {
            for (FormatEnum b : FormatEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }

            return null;
        }

        public static FormatEnum fromOdkType(String type) {
            FormatEnum formatEnum = FormatEnum.fromValue(type);
            if (formatEnum != null) {
                return formatEnum;
            }
            if (FILE_TYPES.contains(type)) {
                return FormatEnum.DATA_URL;
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Property property = (Property) o;
        return Objects.equals(this.type, property.type)
                && Objects.equals(this.title, property.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Property {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public class Items implements Serializable {
        @JsonProperty("type")
        private TypeEnum type = null;

        @JsonProperty("enum")
        private List<Object> enumValues = null;

        public TypeEnum getType() {
            return this.type;
        }

        public List<Object> getEnumValues() {
            return this.enumValues;
        }

        /**
         * Valid if enumValues contains "value"
         *
         * @param value
         * @return true(contains), false(not contain)
         */
        public boolean validateContent(Object value) {
            //Case: the enumValues are json or map, the value is in the property "value"
            if (TypeEnum.OBJECT.equals(this.type) && this.enumValues != null
                    && !this.enumValues.isEmpty() && this.enumValues.get(0) instanceof Map) {
                for (Object o : this.enumValues) {
                    Map<?, ?> json = (Map<?, ?>) o;
                    if (json.get("value").equals(value)) {
                        return true;
                    }
                }
            } else {
                if (this.enumValues.contains(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
