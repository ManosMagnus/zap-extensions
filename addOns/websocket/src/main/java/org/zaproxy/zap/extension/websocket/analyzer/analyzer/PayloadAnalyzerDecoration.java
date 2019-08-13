/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2019 The ZAP Development Team
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
package org.zaproxy.zap.extension.websocket.analyzer.analyzer;

import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.analyzer.structure.PayloadStructure;
import org.zaproxy.zap.utils.EnableableInterface;

public class PayloadAnalyzerDecoration implements PayloadAnalyzer, EnableableInterface {

    private final PayloadAnalyzer payloadAnalyzer;

    private boolean isEnabled = false;

    public PayloadAnalyzerDecoration(PayloadAnalyzer payloadAnalyzer) {
        this.payloadAnalyzer = payloadAnalyzer;
    }

    @Override
    public String getName() {
        return payloadAnalyzer.getName();
    }

    @Override
    public int getId() {
        return payloadAnalyzer.getId();
    }

    @Override
    public PayloadStructure parse(WebSocketMessageDTO message) {
        return payloadAnalyzer.parse(message);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        return false;
        //        if (this == o) return true;
        //        if (o == null || getClass() != o.getClass()) return false;
        //        PayloadAnalyzerDecoration that = (PayloadAnalyzerDecoration) o;
        //        return isEnabled == that.isEnabled &&
        //                com.google.common.base.Objects.equal(payloadAnalyzer,
        // that.payloadAnalyzer);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
