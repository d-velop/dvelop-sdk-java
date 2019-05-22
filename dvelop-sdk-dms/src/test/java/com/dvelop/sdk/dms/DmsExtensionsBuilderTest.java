package com.dvelop.sdk.dms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.*;

class DmsExtensionsBuilderTest {

    @Test
    public void TestBuilder_happyPath_works() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        extensionBuilder.addDmsObjectDetailsContextAction("/foo/objectcontextaction")
                .uriTemplate("/some/where/{id}")
                .iconUri("/some/where/icon.png")
                .caption("de", "Dinge mit PDF machen")
                .caption("en", "Do stuff to pdf files")
                .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                .build();

        extensionBuilder.addDmsObjectListContextAction("/foo/listcontextaction")
                .uriTemplate("/some/where/{id}")
                .iconUri("/some/where/icon.png")
                .caption("de", "Dinge mit Listen machen")
                .caption("en", "Do stuff to lists")
                .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                .build();

        extensionBuilder.addDmsObjectDetailsPreview("/foo/objectdetailspreview")
                .uriTemplate("/some/where/{id}")
                .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                .build();

        DmsExtensions extensions = extensionBuilder.build();

        assertThat(extensions.getExtensions(), hasSize(3));
    }

    @Test
    public void TestBuilder_idEmpty_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("")
                    .uriTemplate("/some/where/{id}")
                    .iconUri("/some/where/icon.png")
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_idNull_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction(null)
                    .uriTemplate("/some/where/{id}")
                    .iconUri("/some/where/icon.png")
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_uriTemplateEmpty_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                    .uriTemplate("")
                    .iconUri("/some/where/icon.png")
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_uriTemplateNull_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                    .uriTemplate(null)
                    .iconUri("/some/where/icon.png")
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_iconUriEmpty_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                    .uriTemplate("/some/where")
                    .iconUri("")
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_iconUriNull_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                    .uriTemplate("/some/where")
                    .iconUri(null)
                    .caption("de", "Dinge mit PDF machen")
                    .caption("en", "Do stuff to pdf files")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_noCaptionsGiven_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                    .uriTemplate("/some/where")
                    .iconUri("/some/where/icon.png")
                    .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                    .build();

        });

        extensionBuilder.build();
    }

    @Test
    public void TestBuilder_multipleCaptionsForLanguageGiven_usesOnlyTheLast() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        extensionBuilder.addDmsObjectDetailsContextAction("/some/id")
                .uriTemplate("/some/where")
                .iconUri("/some/where/icon.png")
                .activationCondition("dmsobject.mainblob.content_type", "application/pdf")
                .caption("en", "things")
                .caption("en", "morethings")
                .build();

        DmsExtensions extensions = extensionBuilder.build();

        assertThat(extensions.getExtensions(), hasItem(hasProperty("captions", hasSize(1))));
    }

    @Test
    public void TestBuilder_noActivationConditions_failsWithException() {
        DmsExtensionsBuilder extensionBuilder = new DmsExtensionsBuilder();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {

            extensionBuilder.addDmsObjectDetailsContextAction("")
                    .uriTemplate("/some/where/{id}")
                    .iconUri("/some/where/icon.png")
                    .caption("de", "Dinge mit PDF machen")
                    .build();

        });

        extensionBuilder.build();
    }
}