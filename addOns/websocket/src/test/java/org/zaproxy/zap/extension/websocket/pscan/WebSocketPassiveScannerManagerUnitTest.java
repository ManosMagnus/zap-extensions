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
package org.zaproxy.zap.extension.websocket.pscan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import org.zaproxy.zap.extension.websocket.alerts.AlertManager;
import org.zaproxy.zap.testutils.WebSocketTestUtils;

public class WebSocketPassiveScannerManagerUnitTest extends WebSocketTestUtils {

    private WebSocketPassiveScannerManager wsPscanManager;

    @Before
    public void setUp() {
        wsPscanManager = new WebSocketPassiveScannerManager(mock(AlertManager.class));
        super.setUpLog();
    }

    @Test
    public void shouldHaveNoScannerByDefault() {
        assertFalse(wsPscanManager.getIterator().hasNext());
    }

    @Test
    public void shouldAddWebSocketPassiveScanner() {
        // Given
        WebSocketPassiveScanner wsScanner = mock(WebSocketPassiveScanner.class);
        // When
        WebSocketPassiveScanner wsPlugin = wsPscanManager.add(wsScanner);
        // Then
        assertNotNull(wsPlugin);
        assertTrue(wsPscanManager.getIterator().hasNext());
    }

    @Test
    public void shouldIgnorePassiveScannerWithSameName() {
        // Given
        // Scanner 1
        WebSocketPassiveScanner wsScanner1 = mock(WebSocketPassiveScanner.class);
        when(wsScanner1.getName()).thenReturn("WebSocketPassiveScanner-1");
        when(wsScanner1.getId()).thenReturn(1);
        // Scanner 2
        WebSocketPassiveScanner wsScanner2 = mock(WebSocketPassiveScanner.class);
        when(wsScanner2.getName()).thenReturn("WebSocketPassiveScanner-1");
        when(wsScanner2.getId()).thenReturn(2);

        // When
        WebSocketPassiveScanner wsPlugin1 = wsPscanManager.add(wsScanner1);
        WebSocketPassiveScanner wsPlugin2 = wsPscanManager.add(wsScanner2);

        // Then
        assertTrue(wsPscanManager.isScannerContained(wsPlugin1));
        assertNull(wsPlugin2);
        assertFalse(wsPscanManager.isScannerContained(wsPlugin2));
    }

    @Test
    public void shouldRemovePassiveScanner() {
        // Given
        WebSocketPassiveScanner scanner1 = mock(WebSocketPassiveScanner.class);
        when(scanner1.getName()).thenReturn("WsScanner-1");
        when(scanner1.getId()).thenReturn(1);
        WebSocketPassiveScanner wsPlugin1 = wsPscanManager.add(scanner1);

        WebSocketPassiveScanner scanner2 = mock(WebSocketPassiveScanner.class);
        when(scanner2.getName()).thenReturn("WsScanner-2");
        when(scanner2.getId()).thenReturn(2);
        WebSocketPassiveScanner wsPlugin2 = wsPscanManager.add(scanner2);

        // When
        boolean result = wsPscanManager.removeScanner(wsPlugin2);

        // Then
        assertNotNull(wsPlugin1);
        assertTrue(result);
        assertFalse(wsPscanManager.isScannerContained(wsPlugin2));
    }

    @Test
    public void shouldAllowToChangeWhileIterating() {
        // Given
        WebSocketPassiveScanner scanner1 = mock(WebSocketPassiveScanner.class);
        when(scanner1.getName()).thenReturn("WsScanner-1");
        when(scanner1.getId()).thenReturn(1);
        WebSocketPassiveScanner wsPlugin1 = wsPscanManager.add(scanner1);

        WebSocketPassiveScanner scanner2 = mock(WebSocketPassiveScanner.class);
        when(scanner2.getName()).thenReturn("WsScanner-2");
        when(scanner2.getId()).thenReturn(2);
        WebSocketPassiveScanner wsPlugin2 = wsPscanManager.add(scanner2);

        // When
        Iterator<WebSocketPassiveScanner> iterator = wsPscanManager.getIterator();
        while (iterator.hasNext()) {
            WebSocketPassiveScanner iScanner = iterator.next();
            wsPscanManager.removeScanner(iScanner);
            wsPscanManager.add(iScanner);
        }

        // Then
        assertTrue(wsPscanManager.isScannerContained(wsPlugin1));
        assertTrue(wsPscanManager.isScannerContained(wsPlugin2));
    }

    @Test
    public void shouldDisableScanner() {
        // Given
        WebSocketPassiveScanner scanner1 = mock(WebSocketPassiveScanner.class);
        when(scanner1.getName()).thenReturn("WsScanner-1");
        when(scanner1.getId()).thenReturn(1);

        // When
        wsPscanManager.setAllPluginPassiveScannersEnabled(true);
        wsPscanManager.setPassiveScanEnabled(scanner1, false);

        // Then
        Iterator<WebSocketPassiveScanner> iterator = wsPscanManager.getEnabledIterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldIterateOnlyOverEnabledScanners() {
        // Given
        WebSocketPassiveScanner scannerEnabled = mock(WebSocketPassiveScanner.class);
        when(scannerEnabled.getName()).thenReturn("WsScanner-Enabled");
        when(scannerEnabled.getId()).thenReturn(1);

        WebSocketPassiveScanner scannerNot1 = mock(WebSocketPassiveScanner.class);
        when(scannerNot1.getName()).thenReturn("WsScanner-not-1");
        when(scannerNot1.getId()).thenReturn(2);

        WebSocketPassiveScanner scannerNot2 = mock(WebSocketPassiveScanner.class);
        when(scannerNot2.getName()).thenReturn("WsScanner-not-1");
        when(scannerNot2.getId()).thenReturn(2);

        // When
        wsPscanManager.setAllPluginPassiveScannersEnabled(true);
        wsPscanManager.setPassiveScanEnabled(scannerNot1, false);
        wsPscanManager.setPassiveScanEnabled(scannerNot2, false);

        // Then
        Iterator<WebSocketPassiveScanner> iterator = wsPscanManager.getEnabledIterator();
        while (iterator.hasNext()) {
            assertTrue(iterator.next().equals(scannerEnabled));
        }
    }
}
