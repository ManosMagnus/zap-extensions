package org.zaproxy.zap.extension.websocket.treemap.analyzers;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.analyzers.structures.LazyJsonStructure;
import org.zaproxy.zap.extension.websocket.treemap.analyzers.structures.utilities.ClassValuePair;
import org.zaproxy.zap.extension.websocket.treemap.analyzers.structures.PayloadStructure;
import org.zaproxy.zap.extension.websocket.utility.InvalidUtf8Exception;

import java.lang.reflect.Type;
import java.util.*;

public class LazyJsonAnalyzer implements PayloadAnalyzer{
	
	
	
	private final static Logger LOGGER = Logger.getLogger(LazyJsonStructure.class);
	
	private static final JsonParser JSON_PARSER = new JsonParser();
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
	private static final String NAME = LazyJsonAnalyzer.class.getSimpleName();
	
	private Gson gson;
	
	public LazyJsonAnalyzer(){
		GSON_BUILDER.registerTypeAdapter(LazyJsonStructure.class, new JsonStructureAdaptor());
		gson = GSON_BUILDER.setFieldNamingPolicy(
				FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}
	
	@Override
	public PayloadStructure getPayloadStructure(WebSocketMessageDTO webSocketMessageDTO) throws Exception {
		
		try {
			return gson.fromJson(webSocketMessageDTO.getReadablePayload(),LazyJsonStructure.class);
		} catch (InvalidUtf8Exception e) {
			throw new Exception(e.toString(),e);
		}
	}
	
	public String getJsonStructure(PayloadStructure payloadStructure){
		return gson.toJson(payloadStructure,LazyJsonStructure.class);
	}
	
	@Override
	public boolean recognizer(WebSocketMessageDTO webSocketMessageDTO) {
		if(webSocketMessageDTO.opcode == WebSocketMessage.OPCODE_TEXT){
			try {
				JSON_PARSER.parse(webSocketMessageDTO.getReadablePayload());
				return true;
			}catch (Exception e){
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getLeafName(PayloadStructure payloadStructure) {
		return getJsonStructure(( (LazyJsonStructure) payloadStructure).getTheAbstractMap());
	}
	
	class JsonStructureAdaptor implements JsonDeserializer<LazyJsonStructure>, JsonSerializer<LazyJsonStructure>{
		
		@Override
		public LazyJsonStructure deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException
		{
			LazyJsonStructure jsonStructure = new LazyJsonStructure();
			
			JsonObject jsonObject = json.getAsJsonObject();
			JsonElement element;
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
			{
				element = entry.getValue();
				if (element.isJsonPrimitive()) {
					if(element.getAsJsonPrimitive().isBoolean()){
						jsonStructure.putObject(entry.getKey(),element.getAsBoolean());
					}else if(element.getAsJsonPrimitive().isNumber()){
						jsonStructure.putObject(entry.getKey(),element.getAsNumber());
					}else if(element.getAsJsonPrimitive().isString()) {
						jsonStructure.putObject(entry.getKey(), element.getAsString());
					}else {
						throw new JsonParseException("it's a meaningful message");
					}
				} else if (element.isJsonObject()) {
					jsonStructure.putObject(entry.getKey(), context.deserialize(element.getAsJsonObject(), unused));
				} else if(element.isJsonNull()){
					jsonStructure.putObject(entry.getKey(), null);
				} else if(element.isJsonArray()){
					ClassValuePair list = new ClassValuePair(new ArrayList<>());
					for (JsonElement jsonElement : element.getAsJsonArray()){
						if(jsonElement.isJsonObject()){
							list.addToList(context.deserialize(element.getAsJsonObject(), unused));
						}else{
							if(jsonElement.getAsJsonPrimitive().isBoolean()){
								list.addToList(jsonElement.getAsBoolean());
							}else if(jsonElement.getAsJsonPrimitive().isNumber()){
								list.addToList(jsonElement.getAsNumber());
							}else if(jsonElement.getAsJsonPrimitive().isString()) {
								list.addToList(jsonElement.getAsString());
							}else {
								throw new JsonParseException("it's a meaningful message");
							}
						}
					}
					jsonStructure.put(entry.getKey(),list);
				}
				else {
					throw new JsonParseException("it's a meaningful message");
				}
			}
			
			return jsonStructure;
		}
		
		@Override
		public JsonElement serialize(LazyJsonStructure lazyJsonStructure, Type type, JsonSerializationContext jsonSerializationContext) {
			Iterator<Map.Entry<String, ClassValuePair> > iterator = lazyJsonStructure.entrySet().iterator();
			JsonObject result = new JsonObject();
			while (iterator.hasNext()){
				Map.Entry<String,ClassValuePair> entry = iterator.next();
				if(entry.getValue().getaClass().equals(LazyJsonStructure.class)){
				
						result.add(entry.getKey(), jsonSerializationContext.serialize(entry.getValue().getValue(),type));
				
				}else if (entry.getValue().getaClass().equals(List.class)){
					
					List<ClassValuePair> list = entry.getValue().getList();
					
					for(ClassValuePair tuple2 : list){
					
						JsonArray jsonArray = new JsonArray();
						
						if(tuple2.getaClass().equals(LazyJsonStructure.class)){
							jsonArray.add(jsonSerializationContext.serialize(tuple2.getValue(),type));
						}else if(tuple2.getaClass().equals(String.class)){
							jsonArray.add((String) tuple2.getValue());
						}else if(tuple2.getaClass().equals(LazilyParsedNumber.class)){
							jsonArray.add((LazilyParsedNumber) tuple2.getValue());
						}else if(tuple2.getaClass().equals(Boolean.class)){
							jsonArray.add((Boolean) tuple2.getValue());
						}else if (tuple2.getaClass() == null){
							jsonArray.add((JsonElement) null);
						}else {
							throw new JsonParseException("it's a meaningful message");
						}
					}
				}else if(entry.getValue().getaClass().equals(String.class)){
					result.addProperty(entry.getKey(), (String) entry.getValue().getValue());
				}else if(entry.getValue().getaClass().equals(LazilyParsedNumber.class)){
					result.addProperty(entry.getKey(), (LazilyParsedNumber) entry.getValue().getValue());
				}else if(entry.getValue().getValueList().equals(Boolean.class)){
					result.addProperty(entry.getKey(), (Boolean) entry.getValue().getValue());
				}else if(entry.getValue().getaClass() == null){
					result.add(entry.getKey(), null);
				}
			}
			return result;
		}
	}
}
