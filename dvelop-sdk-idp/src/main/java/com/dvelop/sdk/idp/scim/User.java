package com.dvelop.sdk.idp.scim;

public class User {

    public class Username {
        String formatted;
        String familyName;
        String givenName;
        String middleName;
        String honoricPrefix;
        String honoricSuffix;

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getHonoricPrefix() {
            return honoricPrefix;
        }

        public void setHonoricPrefix(String honoricPrefix) {
            this.honoricPrefix = honoricPrefix;
        }

        public String getHonoricSuffix() {
            return honoricSuffix;
        }

        public void setHonoricSuffix(String honoricSuffix) {
            this.honoricSuffix = honoricSuffix;
        }
    }

    String id;
    String userName;
    Username name;
    String displayName;
    String profileUrl; // TODO: Make URI?
    String title;
    String[] emails;
    String[] photos;
    Group[] groups;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Username getName() {
        return name;
    }

    public void setName(Username name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getEmails() {
        return emails;
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
}
