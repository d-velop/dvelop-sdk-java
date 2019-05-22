package com.dvelop.sdk.dms;

import java.util.HashSet;
import java.util.Set;

public class DmsSources {

    private Set<DmsSource> sources = new HashSet<>();

    public Set<DmsSource> getSources() {
        return sources;
    }

    public void setSources(Set<DmsSource> sources) {
        this.sources = sources;
    }

    public static class DmsSource {
        private String id;
        private String displayName;
        private Set<DmsSourceCategory> categories = new HashSet<>();
        private Set<DmsSourceProperty> properties = new HashSet<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Set<DmsSourceCategory> getCategories() {
            return categories;
        }

        public void setCategories(Set<DmsSourceCategory> categories) {
            this.categories = categories;
        }

        public Set<DmsSourceProperty> getProperties() {
            return properties;
        }

        public void setProperties(Set<DmsSourceProperty> properties) {
            this.properties = properties;
        }
    }

    public static class DmsSourceCategory {
        String key;
        String displayName;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class DmsSourceProperty {
        String key;
        String displayName;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

}
