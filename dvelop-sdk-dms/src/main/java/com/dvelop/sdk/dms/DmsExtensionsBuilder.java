package com.dvelop.sdk.dms;

/**
 * Used to build {@link DmsExtensions}
 *
 * @see DmsExtensions
 */
public class DmsExtensionsBuilder {

    private DmsExtensions extensions = new DmsExtensions();

    public DmsExtensionsBuilder() {}

    public DmsContextActionBuilder addDmsObjectDetailsContextAction(String id) {
        return new DmsObjectDetailsContextActionBuilder(this, id);
    }

    public DmsContextActionBuilder addDmsObjectListContextAction(String id) {
        return new DmsObjectListContextActionBuilder(this, id);
    }

    public DmsObjectDetailsPreviewBuilder addDmsObjectDetailsPreview(String id) {
        return new DmsObjectDetailsPreviewBuilder(this, id);
    }

    public DmsExtensions build() {
        return extensions;
    }

    public class DmsObjectExtensionBuilder {
        protected DmsExtensions.DmsExtension extension = new DmsExtensions.DmsExtension();
        private DmsExtensionsBuilder parent;

        public DmsObjectExtensionBuilder(DmsExtensionsBuilder parent, String id, DmsExtensions.DmsExtensionContext context) {

            if( id == null || "".equals(id)) {
                throw new IllegalArgumentException("id must not be null or empty");
            }

            this.parent = parent;
            extension.setContext(context);
            extension.setId(id);
        }

        /**
         * Sets the uri to open to when the context action is called.
         * See the DMS api docs to find out which properties to use in uri templates.
         */
        public DmsObjectExtensionBuilder uriTemplate(String uriTemplate) {
            extension.setUriTemplate(uriTemplate);
            return this;
        }

        public DmsObjectExtensionBuilder activationCondition(String propertyId, String... values) {
            extension.addActivationCondition(propertyId, values);
            return this;
        }

        public DmsExtensionsBuilder build() {
            if ("".equals(extension.getId())) {
                throw new IllegalArgumentException("missing mandatory id for extension");
            }

            if (extension.getContext() == null) {
                throw new IllegalArgumentException("missing mandatory context for extension");
            }

            if (extension.getUriTemplate() == null || "".equals(extension.getUriTemplate())){
                throw new IllegalArgumentException("uriTemplate must not be null or empty");
            }

            if (extension.getActivationConditions().size() == 0) {
                throw new IllegalArgumentException("missing mandatory activationConditions for extension");
            }

            parent.extensions.getExtensions().add(extension);
            return parent;
        }
    }

    public class DmsObjectDetailsPreviewBuilder extends DmsObjectExtensionBuilder {
        public DmsObjectDetailsPreviewBuilder(DmsExtensionsBuilder parent, String id) {
            super(parent, id, DmsExtensions.DmsExtensionContext.DMS_OBJECT_DETAILS_CONTEXT_ACTION);
        }
    }

    public class DmsContextActionBuilder extends DmsObjectExtensionBuilder {

        public DmsContextActionBuilder(DmsExtensionsBuilder parent, String id, DmsExtensions.DmsExtensionContext context) {
            super(parent, id, context);
        }

        /**
         * Set an uri to an icon to display on the context action
         * @param iconUri uri to an icon, relative or absolute
         */
        public DmsContextActionBuilder iconUri(String iconUri) {
            extension.setIconUri(iconUri);
            return this;
        }

        /**
         * Add localized captions. Provide at least one caption
         * @param culture e.g. "en" or "en_EN"
         * @param caption localized caption for the given culture
         * @return
         */
        public DmsContextActionBuilder caption(String culture, String caption) {
            extension.addCaption(culture, caption);
            return this;
        }

        /**
         * @inheritDoc
         *
         */
        @Override
        public DmsContextActionBuilder uriTemplate(String uriTemplate) {
            super.uriTemplate(uriTemplate);
            return this;
        }

        /**
         * add a condition under which to display this extension. See the dms api docs for valid propertyIds.
         * Defaults to the OR operator for the values, since its the only one available at this time.
         * @param propertyId propertyId to check the value of
         * @param values a set of possible values for the property
         * @return
         */
        @Override
        public DmsContextActionBuilder activationCondition(String propertyId, String... values) {
            super.activationCondition(propertyId, values);
            return this;
        }

        @Override
        public DmsExtensionsBuilder build() {

            if (extension.getIconUri() == null || "".equals(extension.getIconUri())){
                throw new IllegalArgumentException("iconUri must not be null or empty");
            }

            if (extension.getCaptions().size() == 0) {
                throw new IllegalArgumentException("captions must not be empty");
            }
            return super.build();
        }
    }

    public class DmsObjectDetailsContextActionBuilder extends DmsContextActionBuilder {

        public DmsObjectDetailsContextActionBuilder(DmsExtensionsBuilder parent, String id) {
            super(parent, id, DmsExtensions.DmsExtensionContext.DMS_OBJECT_DETAILS_CONTEXT_ACTION);
        }
    }

    public class DmsObjectListContextActionBuilder extends DmsContextActionBuilder {

        public DmsObjectListContextActionBuilder(DmsExtensionsBuilder parent, String id) {
            super(parent, id, DmsExtensions.DmsExtensionContext.DMS_OBJECT_DETAILS_CONTEXT_ACTION);
        }
    }
}
