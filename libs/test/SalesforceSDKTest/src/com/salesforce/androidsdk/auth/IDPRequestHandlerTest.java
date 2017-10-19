/*
 * Copyright (c) 2017-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.androidsdk.auth;

import android.app.Application;
import android.app.Instrumentation;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.salesforce.androidsdk.TestCredentials;
import com.salesforce.androidsdk.TestForceApp;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.accounts.UserAccountBuilder;
import com.salesforce.androidsdk.accounts.UserAccountTest;
import com.salesforce.androidsdk.analytics.security.Encryptor;
import com.salesforce.androidsdk.auth.idp.IDPRequestHandler;
import com.salesforce.androidsdk.auth.idp.SPConfig;

import junit.framework.Assert;

/**
 * Tests for IDPRequestHandler.
 *
 * @author bhariharan
 */
public class IDPRequestHandlerTest extends InstrumentationTestCase {

    private static final String TEST_CONSUMER_KEY = "test_consumer_key";
    private static final String TEST_CALLBACK_URL = "test_callback_url";
    private static final String[] TEST_SCOPES = new String[] {"test_scope_1", "test_scope_2"};
    private static final String TEST_LOGIN_URL = "test_login_url";
    private static final String TEST_USER_HINT = "test_user_hint";
    private static final String TEST_CODE_CHALLENGE = Encryptor.hash("test_salt_1", "test_salt_2");
    private static final String TAG = "IDPRequestHandlerTest";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final Application app = Instrumentation.newApplication(TestForceApp.class,
                getInstrumentation().getContext());
        getInstrumentation().callApplicationOnCreate(app);
        TestCredentials.init(getInstrumentation().getContext());
    }

    /**
     * Test for null SPConfig.
     */
    public void testNullSPConfig() {
        try {
            new IDPRequestHandler(null, buildTestUserAccount());
            Assert.fail("Exception should have been thrown for missing mandatory SPConfig");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for null UserAccount.
     */
    public void testNullUserAccount() {
        final SPConfig spConfig = new SPConfig(TEST_CONSUMER_KEY, TEST_CALLBACK_URL,
                TEST_CODE_CHALLENGE, TEST_SCOPES, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            new IDPRequestHandler(spConfig, null);
            Assert.fail("Exception should have been thrown for missing mandatory UserAccount");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for missing client ID from SPConfig.
     */
    public void testMissingClientId() {
        final SPConfig spConfig = new SPConfig(null, TEST_CALLBACK_URL,
                TEST_CODE_CHALLENGE, TEST_SCOPES, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            new IDPRequestHandler(spConfig, buildTestUserAccount());
            Assert.fail("Exception should have been thrown for missing mandatory consumer key");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for missing callback URL from SPConfig.
     */
    public void testMissingCallbackUrl() {
        final SPConfig spConfig = new SPConfig(TEST_CONSUMER_KEY, null,
                TEST_CODE_CHALLENGE, TEST_SCOPES, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            new IDPRequestHandler(spConfig, buildTestUserAccount());
            Assert.fail("Exception should have been thrown for missing mandatory callback URL");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for missing code challenge from SPConfig.
     */
    public void testMissingCodeChallenge() {
        final SPConfig spConfig = new SPConfig(TEST_CONSUMER_KEY, TEST_CALLBACK_URL,
                null, TEST_SCOPES, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            new IDPRequestHandler(spConfig, buildTestUserAccount());
            Assert.fail("Exception should have been thrown for missing mandatory code challenge");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for missing scopes from SPConfig.
     */
    public void testMissingScopes() {
        final SPConfig spConfig = new SPConfig(TEST_CONSUMER_KEY, TEST_CALLBACK_URL,
                TEST_CODE_CHALLENGE, null, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            new IDPRequestHandler(spConfig, buildTestUserAccount());
            Assert.fail("Exception should have been thrown for missing mandatory scopes");
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Log.v(TAG, "Exception thrown as expected");
        }
    }

    /**
     * Test for valid SPConfig and valid UserAccount.
     */
    public void testValidParams() {
        final SPConfig spConfig = new SPConfig(TEST_CONSUMER_KEY, TEST_CALLBACK_URL,
                TEST_CODE_CHALLENGE, TEST_SCOPES, TEST_LOGIN_URL, TEST_USER_HINT);
        try {
            final IDPRequestHandler idpRequestHandler = new IDPRequestHandler(spConfig, buildTestUserAccount());
            assertNotNull("IDPRequestHandler should not be null", idpRequestHandler);
        } catch (IDPRequestHandler.IDPRequestHandlerException e) {
            Assert.fail("Exception should not have been thrown");
        }
    }

    private UserAccount buildTestUserAccount() {
        return UserAccountBuilder.getInstance().authToken(UserAccountTest.TEST_AUTH_TOKEN).
                refreshToken(UserAccountTest.TEST_REFRESH_TOKEN).loginServer(UserAccountTest.TEST_LOGIN_URL).
                idUrl(UserAccountTest.TEST_IDENTITY_URL).instanceServer(UserAccountTest.TEST_INSTANCE_URL).
                orgId(UserAccountTest.TEST_ORG_ID).userId(UserAccountTest.TEST_USER_ID).
                username(UserAccountTest.TEST_USERNAME).accountName(UserAccountTest.TEST_ACCOUNT_NAME).
                communityId(UserAccountTest.TEST_COMMUNITY_ID).communityUrl(UserAccountTest.TEST_COMMUNITY_URL).
                firstName(UserAccountTest.TEST_FIRST_NAME).lastName(UserAccountTest.TEST_LAST_NAME).
                displayName(UserAccountTest.TEST_DISPLAY_NAME).email(UserAccountTest.TEST_EMAIL).
                photoUrl(UserAccountTest.TEST_PHOTO_URL).thumbnailUrl(UserAccountTest.TEST_THUMBNAIL_URL).
                additionalOauthValues(null).build();
    }
}
