package com.dvelop.sdk.dms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes an extension for the DMS app.
 * Use {@link DmsExtensionsBuilder} to conveniently construct a DmsExtension
 *
 * <pre>
 *
 *     {@code
 *
 *     DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();
 *
 *     extensionBuilder.addDmsObjectDetailsContextAction("/myapp/mycontextaction")
 *             .uriTemplate("/some/where/{dmsobject.mainblob.content_type}")
 *             .iconUri("/some/where/icon.png")
 *             .caption("en", "Do stuff with pdf files")
 *             .caption("de", "Dinge mit PDF-Dateien machen")
 *             .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
 *             .build();
 *
 *     DmsExtensions extensions = extensionBuilder.build();
 *
 *     return Response.ok(extensions, "application/hal+json").build();
 *
 *     }
 *
 * </pre>
 *
 * @see DmsExtensionsBuilder
 *
 */
public class DmsExtensions {

    private Set<DmsExtension> extensions = new HashSet<>();

    public Set<DmsExtension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Set<DmsExtension> extensions) {
        this.extensions = extensions;
    }

    public DmsExtension addExtension(String id, String uriTemplate, DmsExtensionContext context) {
        DmsExtension extension = new DmsExtension();

        extension.setId(id);
        extension.setUriTemplate(uriTemplate);
        extension.setContext(context);

        return extension;
    }

    public enum DmsExtensionContext {
        DMS_OBJECT_DETAILS_PREVIEW("DmsObjectDetailsPreview"),
        DMS_OBJECT_LIST_CONTEXT_ACTION("DmsObjectListContextAction"),
        DMS_OBJECT_DETAILS_CONTEXT_ACTION("DmsObjectDetailsContextAction");

        private String value;

        DmsExtensionContext(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum DmsExtensionActivationConditionOperator {
        OR("or"),;

        private String value;

        DmsExtensionActivationConditionOperator(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class DmsExtension {
        private String id;
        private Set<DmsExtensionActivationCondition> activationConditions = new HashSet<>();
        private DmsExtensionContext context;
        private String uriTemplate;
        private Set<DmsExtensionCaption> captions = new HashSet<>();
        private String iconUri;

        public String getIconUri() {
            return iconUri;
        }

        public void setIconUri(String iconUri) {
            this.iconUri = iconUri;
        }

        public DmsExtension icon(String uri) {
            iconUri = uri;
            return this;
        }

        public Set<DmsExtensionCaption> getCaptions() {
            return captions;
        }

        public void setCaptions(Set<DmsExtensionCaption> captions) {
            this.captions = captions;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<DmsExtensionActivationCondition> getActivationConditions() {
            return activationConditions;
        }

        public void setActivationConditions(Set<DmsExtensionActivationCondition> activationConditions) {
            this.activationConditions = activationConditions;
        }

        public DmsExtensionContext getContext() {
            return context;
        }

        public void setContext(DmsExtensionContext context) {
            this.context = context;
        }

        public String getUriTemplate() {
            return uriTemplate;
        }

        public void setUriTemplate(String uriTemplate) {
            this.uriTemplate = uriTemplate;
        }

        public DmsExtension addActivationCondition(String propertyId, String... values) {
            return addActivationCondition(propertyId, DmsExtensionActivationConditionOperator.OR, values);
        }

        public DmsExtension addActivationCondition(String propertyId, DmsExtensionActivationConditionOperator operator, String... values) {
            DmsExtensionActivationCondition activationCondition = new DmsExtensionActivationCondition();
            activationCondition.setPropertyId(propertyId);
            activationCondition.setOperator(operator);
            activationCondition.getValues().addAll(Arrays.asList(values));

            this.getActivationConditions().add(activationCondition);

            return this;
        }

        public DmsExtension addCaption(String culture, String caption) {
            DmsExtensionCaption c = new DmsExtensionCaption();
            c.setCulture(culture);
            c.setCaption(caption);
            captions.add(c);
            return this;
        }
    }

    public static class DmsExtensionActivationCondition {
        private String propertyId;
        private DmsExtensionActivationConditionOperator operator = DmsExtensionActivationConditionOperator.OR;
        private Set<String> values = new HashSet<>();

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public DmsExtensionActivationConditionOperator getOperator() {
            return operator;
        }

        public void setOperator(DmsExtensionActivationConditionOperator operator) {
            this.operator = operator;
        }

        public Set<String> getValues() {
            return values;
        }

        public void setValues(Set<String> values) {
            this.values = values;
        }
    }

    public static class DmsExtensionCaption {
        private String culture;
        private String caption;

        public String getCulture() {
            return culture;
        }

        public void setCulture(String culture) {
            this.culture = culture;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        @Override
        public int hashCode() {
            return culture.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(! (obj instanceof DmsExtensionCaption)){
                return false;
            } else {
                return ((DmsExtensionCaption) obj).culture.equals(culture);
            }
        }
    }


}
