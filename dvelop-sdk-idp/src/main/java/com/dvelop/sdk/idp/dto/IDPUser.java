package com.dvelop.sdk.idp.dto;

import com.dvelop.sdk.idp.IDPConstants;

import java.util.List;

public class IDPUser {

    public static class Name {
        String familyName, givenName;

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
    }

    String id;
    String userName;

    Name name;
    String displayName;

    List<Value> emails;
    List<DisplayValue> groups;
    List<DisplayValue> photos;

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

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Value> getEmails() {
        return emails;
    }

    public void setEmails(List<Value> emails) {
        this.emails = emails;
    }

    public List<DisplayValue> getGroups() {
        return groups;
    }

    public void setGroups(List<DisplayValue> groups) {
        this.groups = groups;
    }

    public List<DisplayValue> getPhotos() {
        return photos;
    }

    public void setPhotos(List<DisplayValue> photos) {
        this.photos = photos;
    }

    public boolean isExternal() {
        return isUserInGroup(IDPConstants.GROUP_ID_EXTERNAL_USER);
    }

    public boolean isUserInGroup( String groupId){
        return groups.stream().anyMatch( g -> g.getValue() == groupId );
    }

}
