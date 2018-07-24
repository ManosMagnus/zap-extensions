package org.zaproxy.zap.extension.websocket.treemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zaproxy.zap.extension.websocket.ExtensionWebSocket;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.analyzers.LazyJsonAnalyzer;
import org.zaproxy.zap.extension.websocket.treemap.analyzers.structures.LazyJsonStructure;
import org.zaproxy.zap.testutils.TestUtils;

public class LazyJsonAnalyzerUnitTest extends TestUtils{
	
	@Before
	public void initialize() throws Exception {
		super.setUpZap();
		super.setUpLog();
	}
	
	
	@Override
	protected void setUpMessages() {
		mockMessages(new ExtensionWebSocket());
	}
	
	
	@Test
	public void shouldGetTheStructure() throws Exception {
		LazyJsonAnalyzer lazyJsonAnalyzer = new LazyJsonAnalyzer();
		String object;
		object = "{\"t\":\"d\",\"d\":{\"r\":9,\"a\":\"n\",\"b\":{\"p\":\"/presets/default\"}}}";
		WebSocketMessageDTO message = new WebSocketMessageDTO();
		
		message.opcode = WebSocketMessage.OPCODE_TEXT;
		message.payload = object;
		Assert.assertTrue(lazyJsonAnalyzer.recognizer(message));
		LazyJsonStructure lazyJsonStructure = (LazyJsonStructure) lazyJsonAnalyzer.getPayloadStructure(message);
		
		Assert.assertEquals(lazyJsonAnalyzer.getJsonStructure(lazyJsonStructure), lazyJsonAnalyzer.getJsonStructure(lazyJsonStructure));
	}
}
