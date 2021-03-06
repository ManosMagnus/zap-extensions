/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2018 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.pscanrulesAlpha;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.junit.Test;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.parosproxy.paros.network.HttpStatusCode;

public class InsecureFormLoadScannerUnitTest extends PassiveScannerTest<InsecureFormLoadScanner> {

    private HttpMessage createMessage() throws URIException {
        HttpRequestHeader requestHeader = new HttpRequestHeader();
        requestHeader.setURI(new URI("http://example.com", false));

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader(requestHeader);
        msg.getResponseHeader().setStatusCode(HttpStatusCode.OK);
        msg.getResponseHeader().setHeader(HttpResponseHeader.CONTENT_TYPE, "text/html");
        return msg;
    }

    @Override
    protected InsecureFormLoadScanner createScanner() {
        return new InsecureFormLoadScanner();
    }

    @Test
    public void shouldNotRaiseAlertIfResponseIsHttps() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.getRequestHeader().setSecure(true);
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void shouldNotRaiseAlertIfResponseIsNotStatusOk() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.getResponseHeader().setStatusCode(HttpStatusCode.NOT_ACCEPTABLE);
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void shouldNotRaiseAlertIfResponseIsNotHtml() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.getResponseHeader().setHeader(HttpHeader.CONTENT_TYPE, "application/json");
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void shouldNotRaiseAlertIfResponseContentTypeIsNull() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.getResponseHeader().setHeader(HttpHeader.CONTENT_TYPE, null);
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void shouldRaiseAlertIfFormActionIsSecure() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.setResponseBody(
                "<html><form name=\"someform\" action=\"https://example.com/processform\"></form</html>");
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(1));
    }

    @Test
    public void shouldNotRaiseAlertIfFormActionIsInsecure() throws URIException {
        // Given
        HttpMessage msg = createMessage();
        msg.setResponseBody(
                "<html><form name=\"someform\" action=\"http://example.com/processform\"></form</html>");
        // When
        rule.scanHttpResponseReceive(msg, -1, createSource(msg));
        // Then
        assertThat(alertsRaised.size(), equalTo(0));
    }
}
