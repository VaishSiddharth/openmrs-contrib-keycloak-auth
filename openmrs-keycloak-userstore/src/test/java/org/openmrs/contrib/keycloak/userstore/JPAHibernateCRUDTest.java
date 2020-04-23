/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.contrib.keycloak.userstore;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.keycloak.models.UserModel;
import org.mockito.Mock;
import org.openmrs.contrib.keycloak.userstore.data.UserDao;
import org.openmrs.contrib.keycloak.userstore.models.OpenmrsUserModel;

public class JPAHibernateCRUDTest extends JPAHibernateTest {
	
	@Mock
	UserDao userDao;
	
	@Mock
	UserModel userModel;
	
	@Before
	public void setup() {
		userDao = new UserDao(em);
		//		userModel.setUsername("SidVaish");
	}
	
	@Test
	public void getUserByUsername() {
		OpenmrsUserModel query = userDao.getOpenmrsUserByUsername("admin");
		assertEquals("admin", query.getUsername());
		assertTrue("Error, random is too low", query.getUserId() == 152);
	}
	
	@Test
	public void getUserById() {
		OpenmrsUserModel query = userDao.getOpenmrsUserByUserId(186);
		assertEquals("Sid", query.getUsername());
	}
	
	//TODO Can't mock USerModel as its giving null pointer
	//	@Test
	//	public void getPasswordAndSalt() {
	//		String[] result = userDao.getUserPasswordAndSaltOnRecord(userModel);
	//		assertEquals("123", result[1]);
	//	}
	
	@Test
	public void getUserCount() {
		Number count = userDao.getOpenmrsUserCount();
		int numberOfUsers = count.intValue();
		assertEquals(3, numberOfUsers);
		
	}
	
	@Test
	public void searchUsers() {
		List<OpenmrsUserModel> query = userDao.searchForOpenmrsUserQuery("admin", 0, 1);
		assertTrue("Error, random is too low", query.get(0).getUserId() == 152);
		
	}
	
}
