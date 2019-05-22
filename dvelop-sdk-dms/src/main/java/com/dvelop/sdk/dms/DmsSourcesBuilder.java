package com.dvelop.sdk.dms;

public class DmsSourcesBuilder {

    private DmsSources sources = new DmsSources();

    public DmsSourceBuilder addSource(String id, String displayName) {
        return new DmsSourceBuilder(this, id, displayName);
    }

    public DmsSources build() {
        return sources;
    }

    public static class DmsSourceBuilder {

        private DmsSources.DmsSource source = new DmsSources.DmsSource();
        private DmsSourcesBuilder parent;

        public DmsSourceBuilder(DmsSourcesBuilder parent, String id, String displayName) {
            this.parent = parent;
            source.setId(id);
            source.setDisplayName(displayName);
        }

        public DmsSourceBuilder addCategory(String key, String displayName) {
            DmsSources.DmsSourceCategory category = new DmsSources.DmsSourceCategory();
            category.setKey(key);
            category.setDisplayName(displayName);
            source.getCategories().add(category);
            return this;
        }

        public DmsSourceBuilder addProperty(String key, String displayName) {
            DmsSources.DmsSourceProperty property = new DmsSources.DmsSourceProperty();
            property.setKey(key);
            property.setDisplayName(displayName);
            source.getProperties().add(property);
            return this;
        }

        public DmsSourcesBuilder build() {
            parent.sources.getSources().add(source);
            return parent;
        }
    }
}
