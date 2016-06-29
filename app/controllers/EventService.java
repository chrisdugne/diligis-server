package controllers;

import java.util.List;

import models.Event;

import org.codehaus.jackson.JsonNode;

import play.mvc.Result;
import domain.EventManager;

public class EventService extends Application 
{
	// ---------------------------------------------//

	public static Result getStream()
	{
		JsonNode params = request().body().asJson();
		JsonNode userJson = params.get("user");
		
		List<Event> events = EventManager.getLastEvents(userJson.get("email").asText());
		
		return ok(gson.toJson(events));
	}

	public static Result sendMessage()
	{
		JsonNode params 		= request().body().asJson();
		JsonNode messageJson = params.get("message");
		
		String text 			= messageJson.get("text").asText();
		String senderEmail  	= messageJson.get("senderEmail").asText();
		String journeyId 			= messageJson.get("journeyId").asText();
		
		EventManager.sendMessage(text, senderEmail, journeyId);
		
		return ok("message sent");
	}

	public static Result sendAnswer()
	{
		JsonNode params 		= request().body().asJson();
		JsonNode messageJson = params.get("message");
		
		String text 			= messageJson.get("text").asText();
		String contentUID 	= messageJson.get("contentUID").asText();
		String journeyFromId 	= messageJson.get("journeyId").asText();
		
		EventManager.sendAnswer(text, contentUID, journeyFromId);
		
		return ok("message sent");
	}

	public static Result readEvent()
	{
		JsonNode params 		= request().body().asJson();
		JsonNode eventJson 	= params.get("event");
		
		String uid = eventJson.get("uid").asText();
		
		EventManager.readEvent(uid);
		
		return ok();
	}

}