package controllers;

import java.util.List;

import models.Account;
import models.Journey;
import models.Trip;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Result;
import domain.AccountManager;
import domain.TripManager;

public class AccountService extends Application 
{
	// ---------------------------------------------//

	public static Result getAccount()
	{
		JsonNode params = request().body().asJson();
		JsonNode userJson = params.get("user");
		
		Account account = AccountManager.getAccount(userJson);
		
		return ok(gson.toJson(account));
	}

	// ---------------------------------------------//
	
	public static Result getUser()
	{
		JsonNode params = request().body().asJson();
		String userUID = params.get("userUID").asText();
		
		Account account = AccountManager.getUser(userUID);
		Logger.debug(account.getEmail());
		return ok(gson.toJson(account));
	}

	// ---------------------------------------------//
	
	public static Result getTrips()
	{
		JsonNode params = request().body().asJson();
		JsonNode userJson = params.get("user");
		
		List<Trip> trips = TripManager.getTrips(userJson);
		
		return ok(gson.toJson(trips));
	}
	
	// ---------------------------------------------//
	
	public static Result getTrip()
	{
		JsonNode params = request().body().asJson();
		String tripId = params.get("tripId").asText();
		
		Trip trip = TripManager.getTrip(tripId);
		
		return ok(gson.toJson(trip));
	}
	
	// ---------------------------------------------//
	
	public static Result getJourney()
	{
		JsonNode params = request().body().asJson();
		String journeyId = params.get("journeyId").asText();
		
		Journey journey = TripManager.getJourney(journeyId);
		
		return ok(gson.toJson(journey));
	}

	// ---------------------------------------------//
	
	public static Result saveUser()
	{
		JsonNode params = request().body().asJson();
		JsonNode userJson = params.get("user");
		
		Account account = AccountManager.saveUser(userJson);
		return ok(gson.toJson(account));
	}
	
	// ---------------------------------------------//
	
	public static Result createTrip()
	{
		JsonNode params = request().body().asJson();
		JsonNode userJson = params.get("user");
		JsonNode tripJson = params.get("trip");
		
		Trip trip = TripManager.createTrip(userJson, tripJson);
		
		return ok(gson.toJson(trip));
	}
	
	// ---------------------------------------------//
	
	public static Result createTransport()
	{
		JsonNode params = request().body().asJson();
		String tripId = params.get("tripId").asText();
		JsonNode transportJson = params.get("transport");
		
		Journey transport = TripManager.createTransport(tripId, transportJson);
		
		return ok(gson.toJson(transport));
	}
	
	// ---------------------------------------------//
	
	public static Result createDestination()
	{
		JsonNode params = request().body().asJson();
		String tripId = params.get("tripId").asText();
		JsonNode destinationJson = params.get("destination");
		
		Journey destination = TripManager.createDestination(tripId, destinationJson);
		
		return ok(gson.toJson(destination));
	}
	
	// ---------------------------------------------//
	
	public static Result editJourney()
	{
		JsonNode params = request().body().asJson();
		JsonNode journeyJson = params.get("journey");
		
		Journey journey = TripManager.editJourney(journeyJson);
		
		return ok(gson.toJson(journey));
	}
	
	// ---------------------------------------------//
	
	public static Result deleteJourney()
	{
		JsonNode params = request().body().asJson();
		String tripId = params.get("tripId").asText();
		String journeyId  = params.get("journeyId").asText();
		
		TripManager.deleteJourney(tripId, journeyId);
		
		return ok();
	}
	
	
//	// ---------------------------------------------//
//	
//	public static Result createJourney()
//	{
//		JsonNode params = request().body().asJson();
//		String tripId = params.get("tripId").asText();
//		JsonNode journeyJson = params.get("journey");
//		
//		Journey journey = TripManager.createJourney(tripId, journeyJson);
//		
//		return ok(gson.toJson(journey));
//	}
//	
	// ---------------------------------------------//
	// DEPRECATED
//	
//	public static Result verifyTripitProfile()
//	{
//		JsonNode params = request().body().asJson();
//		JsonNode tripitProfileJson = params.get("tripitProfile");
//		JsonNode userJson = params.get("user");
//		
//		String result = AccountManager.verifyTripitProfile(userJson, tripitProfileJson);
//		
//		return ok(result);
//	}
	
	// ---------------------------------------------//
	
//	public static Result refreshTrips()
//	{
//		JsonNode params = request().body().asJson();
//		JsonNode userJson = params.get("user");
//		
//		List<Trip> trips = TripManager.refreshTrips(userJson);
//		return ok(gson.toJson(trips));
//	}
}