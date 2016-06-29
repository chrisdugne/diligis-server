package domain;

import java.util.ArrayList;
import java.util.List;

import models.Account;
import models.Event;
import models.Journey;
import models.Trip;

import org.codehaus.jackson.JsonNode;

import play.Logger;

import utils.Utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;

public class TripManager {
	
	//------------------------------------------------------------------------------------//
	
	public static List<Trip> getTrips(JsonNode travelerJson) 
	{
		return getTraveler(travelerJson).getTrips();
	}

	//------------------------------------------------------------------------------------//

	public static Trip getTrip(String tripId) {
		return Trip.find
				.fetch("journeys")
				.where()
				.ilike("tripId", tripId)
				.findUnique();
   }

	//------------------------------------------------------------------------------------//

	public static Journey getJourney(String journeyId) {
		return Journey.find
				.where()
				.ilike("journeyId", journeyId)
				.findUnique();
   }

	//------------------------------------------------------------------------------------//

	public static Trip createTrip(JsonNode travelerJSON, JsonNode tripJSON) 
	{
		Trip trip = new Trip();
		Account traveler = getTraveler(travelerJSON);
		String tripId = Utils.generateUID();
		
		trip.setTripId		(tripId);
		trip.setStatus		(Trip.IN_CREATION);
		trip.setTraveler	(traveler);
		trip.setName		(tripJSON.get("name")		.asText());
		trip.setImageUrl	(tripJSON.get("imageUrl")	.asText());

		Ebean.save(trip);

		ArrayList<Journey> journeys = new ArrayList<Journey>();
		journeys.add(createFirstJourney(trip, tripJSON));
		
		trip.setJourneys(journeys);
		return trip;
   }

	private static Journey createFirstJourney(Trip trip, JsonNode tripJson) {
	   Journey journey = new Journey();
		String journeyId = Utils.generateUID();
		
	   journey.setJourneyId		(journeyId);
	   journey.setTrip			(trip);
	   journey.setType			(Journey.DESTINATION);
	   journey.setLatitude		(tripJson.get("locationLat").asDouble());
	   journey.setLongitude		(tripJson.get("locationLon").asDouble());
	   journey.setLocationName	(tripJson.get("locationName").asText());
	   
		Ebean.save(journey);
		return journey;
   }

	//------------------------------------------------------------------------------------//

	public static Journey createTransport(String tripId, JsonNode transportJson) {
		Trip trip = getTrip(tripId);
	   
		// 1st transport : start journey endtime is this transport startTime.
		if(trip.getJourneys().size() == 1){
			Journey start = trip.getJourneys().get(0);
			start.setStartTime(transportJson.get("startTime").asLong() - 12*60*60*1000);
			start.setEndTime(transportJson.get("startTime").asLong());
			Ebean.save(start);
			findDiligis(start);

			trip.setStatus(Trip.AVAILABLE);
			Ebean.save(trip);
		}

		Journey transport = new Journey();
		String id = Utils.generateUID();
		transport.setJourneyId(id);
		transport.setTrip(trip);
		transport.setPreviousLocationName(trip.getJourneys().get(trip.getJourneys().size()-1).getLocationName());
		
		fillTransport(transport, transportJson);
		Ebean.save(transport);
		EventManager.announce(transport);
		findDiligis(transport);
		
	   return transport;
	}

	//------------------------------------------------------------------------------------//

	public static Journey createDestination(String tripId, JsonNode destinationJson) {
		
		Trip trip = getTrip(tripId);
		Journey destination = new Journey();
		String id = Utils.generateUID();
		
		destination.setJourneyId(id);
		destination.setTrip(trip);
		destination.setPreviousLocationName(trip.getJourneys().get(trip.getJourneys().size()-1).getLocationName());
		
		fillDestination(destination, destinationJson);
		Ebean.save(destination);
		EventManager.announce(destination);
		findDiligis(destination);
		
	   return destination;
   }

	//=========================================================================================//

	public static void deleteJourney(String tripId, String journeyId) {

		Trip trip = getTrip(tripId);
		Journey journey = getJourney(journeyId);
		
		Ebean.delete(journey);
		
		if(trip.getJourneys().size() == 1){
			Ebean.delete(trip);
		}
   }
	
	//=========================================================================================//

	public static Journey editJourney(JsonNode journeyJson) {
		String journeyId = journeyJson.get("journeyId").asText();
		Journey journey = getJourney(journeyId);
	   
		switch(journey.getType()){
			case Journey.DESTINATION:
				fillDestination(journey, journeyJson);
				break;
			case Journey.FLIGHT:
			case Journey.TRAIN:
				fillTransport(journey, journeyJson);
				break;
		}

		Ebean.save(journey);
		EventManager.announce(journey);
		findDiligis(journey);
		
		return journey;
   }
	
	//=========================================================================================//
	
	private static Account getTraveler(JsonNode travelerJson) 
	{
		String email = travelerJson.get("email").asText();
		ExpressionList<Account> accounts = Account.find
				.fetch("trips")
				.fetch("trips.journeys")
				.fetch("trips.journeys.events")
				.fetch("trips.journeys.events.content")
				.where()
				.ilike("email", email);

		return accounts.findUnique();
	}

	//=========================================================================================//

	private static void fillTransport(Journey transport, JsonNode transportJson) 
	{
		transport.setType 		( transportJson.get("type").asInt());
		transport.setStartTime	( transportJson.get("startTime").asLong());
		transport.setEndTime 	( transportJson.get("endTime").asLong());
		
		if(transportJson.get("airline") != null)
			transport.setAirline	( transportJson.get("airline").asText());

		if(transportJson.get("startAirport") != null)
			transport.setStartAirport( transportJson.get("startAirport").asText());

		if(transportJson.get("endAirport") != null)
			transport.setEndAirport( transportJson.get("endAirport").asText());

		if(transportJson.get("number") != null)
			transport.setNumber 	( transportJson.get("number").asText());
		
		if(transportJson.get("seat") != null)
			transport.setSeat 		( transportJson.get("seat").asText());
		
		if(transportJson.get("railcar") != null)
			transport.setRailcar	( transportJson.get("railcar").asText());
		
	}

	//=========================================================================================//
	
	private static void fillDestination(Journey destination, JsonNode destinationJson) 
	{
		destination.setType 			( Journey.DESTINATION );
		destination.setStartTime	( destinationJson.get("startTime")	.asLong() );
		destination.setEndTime 		( destinationJson.get("endTime")		.asLong() );

		if(destinationJson.get("locationName") != null)
			destination.setLocationName	( destinationJson.get("locationName")	.asText()	);
		if(destinationJson.get("locationLat") != null)
			destination.setLatitude			( destinationJson.get("locationLat")	.asDouble()	);
		if(destinationJson.get("locationLon") != null)
			destination.setLongitude		( destinationJson.get("locationLon")	.asDouble()	);
		
	}

	//=========================================================================================//
	
	public static void findDiligis(Journey journey) {
		
		Long endTime = journey.getEndTime() == null ? journey.getStartTime() + 12*60*60*1000 : journey.getEndTime();
		Logger.debug("startTime " + journey.getStartTime());
		Logger.debug("endTime " + endTime);
		
		List<Journey> diligis;
		ExpressionList<Journey> query = Journey.find.where()
				.not(Expr.eq("trip.traveler.uid", journey.getTrip().getTraveler().getUid()))
				.or(
					Expr.and(
						Expr.isNull("endTime"),
						Expr.or(
							Expr.between("startTime", journey.getStartTime() - 12*60*60*1000, journey.getStartTime()),
							Expr.between("startTime",  journey.getStartTime(), journey.getStartTime() + 12*60*60*1000)
						)
					),
					Expr.and(
						Expr.isNotNull("endTime"),
						Expr.or(
							Expr.or(
								Expr.between("startTime",  journey.getStartTime(), endTime),
								Expr.between("endTime",    journey.getStartTime(), endTime)
							),	
							Expr.and(
								Expr.lt("startTime",  journey.getStartTime()),
								Expr.gt("endTime",    endTime)
							)
						)
					)
				);
		
		switch(journey.getType()){

			case Journey.FLIGHT :
			case Journey.TRAIN :
				query = query.and(
						Expr.eq("number",  journey.getNumber()),
						Expr.isNotNull("number")
					);
				break;

			case Journey.DESTINATION:
				query = query.and(
						Expr.between("latitude",  journey.getLatitude()  - 0.12, journey.getLatitude()  + 0.12),
						Expr.between("longitude", journey.getLongitude() - 0.24, journey.getLongitude() + 0.24)
					);
				break;
				
		}
		
		diligis = query
				.orderBy("startTime")
				.findList();

		Logger.debug("found " + diligis.size() + " diligis");

		//-------------------------------------------------------------//
		
		for (Journey dil : diligis){
			if(!areLinked(journey, dil)){
				List<Event> events = EventManager.createDiligis(journey, dil);
				journey.getEvents().add(events.get(0));
				dil.getEvents().add(events.get(1));
				
				Ebean.save(dil);
			}
		}

		//-------------------------------------------------------------//

		Ebean.save(journey);
	}

	//=========================================================================================//
	
	private static boolean areLinked(Journey journey, Journey dil) {
		
		for(Event event : journey.getEvents()){
			for(Event other : dil.getEvents()){
				if(event.getContent().getUid().equals(other.getContent().getUid())){
					return true;
				}
			}
		}
		
		return false;
   }

	//------------------------------------------------------------------------------------//
	// DEPRECATED v1
	
//	public static List<Trip> refreshTrips(JsonNode travelerJson) 
//	{
//		Account traveler = getTraveler(travelerJson);
//		
//		List<Trip> tripsResult = new ArrayList<Trip>();
//		JsonNode tripsJSON = travelerJson.get("trips");
//
//		//--------------------------------------------------------------------------------------------//
//		// lookin for dummy trip
//
//		for(Trip storedTrip : traveler.getTrips()){
//			if(storedTrip.getTripId().equals(traveler.getUid())){
//				Logger.debug("dummy trip");
//				Trip dummyTrip  = Trip.find.fetch("events").fetch("events.content").where().ilike("tripitId", traveler.getUid()).findUnique();
//				tripsResult.add(dummyTrip);
//				break;
//			}
//		}
//		
//		//--------------------------------------------------------------------------------------------//
//			
//		if(tripsJSON == null){
//			Logger.debug("NO TRIPS on tripit");
//			return tripsResult;
//		}
//		
//		//--------------------------------------------------------------------------------------------//
//		// save or update
//			
//		Iterator<JsonNode> trips = tripsJSON.getElements();
//		while(trips.hasNext()){
//			
//			JsonNode trip = trips.next();
//				
//			boolean yetRecordedTrip = false;
//
//			for(Trip storedTrip : traveler.getTrips()){
//				
//				if(storedTrip.getTripId().equals(trip.get("id").asText())){
//					Logger.debug("refreshing " + trip.get("displayName").asText());
//					tripsResult.add(refreshTrip(storedTrip, trip));
//					yetRecordedTrip = true;
//					break;
//				}
//			}
//			
//			if(!yetRecordedTrip){
//				Logger.debug("new trip " + trip.get("displayName").asText());
//				tripsResult.add(recordTrip(traveler, trip));
//			}
//		}
//		
//		//--------------------------------------------------------------------------------------------//
//		// delete
//		
//		for(Trip storedTrip : traveler.getTrips()){
//			boolean stillOnTripit = false;
//
//			trips = tripsJSON.getElements();
//			while(trips.hasNext()){
//				JsonNode trip = trips.next();
//				
//				if(storedTrip.getTripId().equals(trip.get("id").asText())
//				|| storedTrip.getTripId().equals(traveler.getUid())){ // user dummy trip
//					stillOnTripit = true;
//					break;
//				}
//			}
//
//			if(!stillOnTripit){
//				Logger.debug("removed trip " + storedTrip.getAddress());
//				Ebean.delete(storedTrip);
//			}
//		}
//
//		//--------------------------------------------------------------------------------------------//
//		
//		return tripsResult;
//	}

	//------------------------------------------------------------------------------------//

//	public static Journey createJourney(String tripId, JsonNode journeyJson) {
//	   Trip trip = getTrip(tripId);
//	   
//	   Journey journey = new Journey();
//		String journeyId = Utils.generateUID();
//		
//	   journey.setJourneyId(journeyId);
//	   journey.setTrip(trip);
//	   
//	   if(trip.getJourneys().size() > 0)
//	   	journey.setPreviousLocationName(trip.getJourneys().get(trip.getJourneys().size()-1).getLocationName());
//	   
//		storeJourneyAndAnnouce(journey, journeyJson);
//	   
//	   return journey;
//   }
	//=========================================================================================//

//	private static void storeJourneyAndAnnouce(Journey journey, JsonNode journeyJSON) 
//	{
//		fillJourney(journey, journeyJSON);
//		Ebean.save(journey);
//		EventManager.announce(journey);
//		
////		findDiligis(journey);
//	}

//	private static void fillJourney(Journey journey, JsonNode journeyJson) 
//	{
//		journey.setType 		( journeyJson.get("type").asInt());
//		journey.setStartTime	( journeyJson.get("startTime").asLong());
//	   
//		if(journeyJson.get("endTime") != null)
//			journey.setEndTime 	( journeyJson.get("endTime").asLong());
//
//		if(journeyJson.get("locationName") != null)
//			journey.setLocationName	( journeyJson.get("locationName").asText());
//
//		if(journeyJson.get("locationLat") != null)
//			journey.setLatitude	( journeyJson.get("locationLat").asDouble());
//		if(journeyJson.get("locationLon") != null)
//			journey.setLongitude	( journeyJson.get("locationLon").asDouble());
//	   
//		if(journeyJson.get("number") != null)
//			journey.setNumber 	( journeyJson.get("number").asText());
//		if(journeyJson.get("seat") != null)
//			journey.setSeat 		( journeyJson.get("seat").asText());
//		if(journeyJson.get("railcar") != null)
//			journey.setRailcar	( journeyJson.get("railcar").asText());
//
//   }

	//=========================================================================================//
//	
//	private static Journey refreshJourney(Journey journey, JsonNode journeyJSON) 
//	{
//		storeJourneyAndAnnouce(journey, journeyJSON);
//		return journey;
//   }
		
}
