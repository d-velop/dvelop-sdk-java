package com.dvelop.sdk.idp.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/*
 * Copyright (c) by d.velop AG. All Rights Reserved.
 *
 * Diese Datei ist eine vertrauliche und geschützte Information der d.velop AG.
 * Das Veröffentlichen und Nutzen dieser Informationen darf nur in Übereinstimmung
 * mit den Lizenzbestimmungen der d.velop AG geschehen.
 *
 * This file is confidential and protected information of the d.velop AG.
 * Publishing and use of this information may happen only according to the
 * licence regulations of the d.velop AG.
 *
 * @(#)date of creation: 03.04.2019
 * @(#)time of creation: 13:11
 * @(#)user of creation: swil
 *
 */class IDPUserTest {

 	IDPUser idpUser = new IDPUser();

	@Test
	void isUserInGroupWithGroupsNotSet() {
		assertThat(idpUser.isUserInGroup("groupId1"), is(false));
	}

	@Test
	public void isUserInGroupWithValidGroupAndStringLiterals() {
		setTestGroupsAsStringLiterals();
		assertThat(idpUser.isUserInGroup("groupId2"), is(true));
	}

	@Test
	public void isUserInGroupWithInvalidGroup() {
		setTestGroupsAsStringLiterals();
		assertThat(idpUser.isUserInGroup("groupId3"), is(false));
	}

	@Test
	public void isUserInGroupWithValidGroupAndStringObjects() {
		setTestGroupsAsObjects();
		assertThat(idpUser.isUserInGroup("groupId2"), is(true));
	}

	private void setTestGroupsAsStringLiterals() {
		List<DisplayValue> groups = new ArrayList<>();
		DisplayValue group = new DisplayValue();
		group.setValue("groupId1");
		groups.add(group);
		group = new DisplayValue();
		group.setValue("groupId2");
		groups.add(group);
		idpUser.setGroups(groups);
	}

	private void setTestGroupsAsObjects() {
		List<DisplayValue> groups = new ArrayList<>();
		DisplayValue group = new DisplayValue();
		group.setValue(new String ("groupId1"));
		groups.add(group);
		group = new DisplayValue();
		group.setValue(new String("groupId2"));
		groups.add(group);
		idpUser.setGroups(groups);
	}
}
