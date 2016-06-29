package domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Account;
import models.Event;
import models.EventContent;
import models.Journey;
import models.utils.EventPerson;
import utils.Utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

public class EventManager extends Manager{

	//------------------------------------------------------------------------------------//
	
	public static List<Event> getLastEvents(String email) 
	{
		List<Journey> journeys  = Journey.find.where().ilike("trip.traveler.email", email).findList();
		
		/**
		 * All events + their content if they are :
		 * 	- common announcement (but not mine)
		 * 	- events linked to my journeys (but not announcement, nor sent/read messages)
		 */
		return Event.find
			.fetch("content")
			.where()
			.or(
				Expr.and(
					Expr.eq("status", Event.COMMON_ANNOUNCEMENT),
					Expr.not(Expr.contains("sender", email) )
				),
				Expr.and(
					Expr.and(
						Expr.not(Expr.eq("status", Event.COMMON_ANNOUNCEMENT) ),
						Expr.not(Expr.eq("status", Event.READ_EVENT) )
					),
					Expr.in("journey", journeys)
				)
			)
			.orderBy("uid desc")
			.setMaxRows(20)
			.findList();
	}

	//------------------------------------------------------------------------------------//
	
	public static List<Event> createDiligis(Journey journey1, Journey journey2) 
	{
		String text = "Found a match for your ";
		
		switch(journey1.getType()){
			case Journey.DESTINATION:
				text += "journey to " + journey1.getLocationName();
				break;

			case Journey.FLIGHT:
				text += "flight   " + journey1.getNumber();
				break;
			
			case Journey.TRAIN:
				text += "train   " + journey1.getNumber();
				break;
		}
		
		EventContent content = new EventContent();
		content.setUid(Utils.generateUID());
		content.setText(text);
		content.setType(EventContent.DILIGIS);
		content.setTimestamp(new Date().getTime());
		Ebean.save(content);
		
		return createEvents(journey1, journey2, content);
	}
	
	//------------------------------------------------------------------------------------//

	public static void announce(Account account) {
		
		EventContent content = new EventContent();
		content.setUid(Utils.generateUID());
//		content.setText("New user : " + account.getHeadline() + ", " + account.getIndustry());
		content.setText("Welcome " + account.getHeadline() + " !");
		content.setType(EventContent.ANNOUNCEMENT);
		content.setTimestamp(new Date().getTime());
		Ebean.save(content);
		
		EventPerson sender = new EventPerson();
		sender.setLinkedinUID(account.getLinkedinUID());
		sender.setUid(account.getUid());
		sender.setName(account.getName());
		sender.setHeadline(account.getHeadline());
		sender.setIndustry(account.getIndustry());
		sender.setEmail(account.getEmail());
		sender.setTripId(null);
		sender.setJourneyId(null);
		
		Event event = new Event();
		event.setUid(Utils.generateUID());
		event.setStatus(Event.COMMON_ANNOUNCEMENT);
		event.setJourney(null);
		event.setContent(content);
		event.setSender(gson.toJson(sender));
		
		Ebean.save(event);
	}
	
	
	//------------------------------------------------------------------------------------//
	
	public static void announce(Journey journey) {
		
		EventContent content = new EventContent();
		content.setUid(Utils.generateUID());
		content.setType(EventContent.ANNOUNCEMENT);
		content.setTimestamp(new Date().getTime());

		switch(journey.getType()){
			case Journey.DESTINATION :
				content.setText("New trip to " + journey.getLocationName());
				break;

			case Journey.FLIGHT:
				content.setText("Flight from " + journey.getStartAirport() + " to " + journey.getEndAirport() );
				break;
			
			case Journey.TRAIN :
				content.setText("Train from " + journey.getPreviousLocationName());
				break;
		}
		
		Ebean.save(content);

		EventPerson sender = new EventPerson();
		sender.setUid				(journey.getTrip().getTraveler().getUid());
		sender.setLinkedinUID	(journey.getTrip().getTraveler().getLinkedinUID());
		sender.setName				(journey.getTrip().getTraveler().getName());
		sender.setHeadline		(journey.getTrip().getTraveler().getHeadline());
		sender.setIndustry		(journey.getTrip().getTraveler().getIndustry());
		sender.setEmail			(journey.getTrip().getTraveler().getEmail());
		sender.setTripId			(journey.getTrip().getTripId());
		sender.setJourneyId		(journey.getJourneyId());
		
		Event event = new Event();
		event.setUid(Utils.generateUID());
		event.setStatus(Event.COMMON_ANNOUNCEMENT);
		event.setJourney(journey);
		event.setContent(content);
		event.setSender(gson.toJson(sender));

		Ebean.save(event);
	}

	//------------------------------------------------------------------------------------//

	public static void sendMessage(String text, String senderEmail, String journeyId) {

		Account senderAccount 	= Account.find.where().ilike("email", senderEmail).findUnique();
		Journey journeyFrom  	= Journey.find.where().ilike("journeyId", senderAccount.getUid()).findUnique();  // the user's dummy trip
		Journey journeyTo  		= Journey.find.where().ilike("journeyId", journeyId).findUnique();
		
		EventPerson sender = new EventPerson();
		sender.setUid					(senderAccount.getUid());
		sender.setLinkedinUID		(senderAccount.getLinkedinUID());
		sender.setName					(senderAccount.getName());
		sender.setHeadline			(senderAccount.getHeadline());
		sender.setIndustry			(senderAccount.getIndustry());
		sender.setEmail				(senderAccount.getEmail());
		sender.setTripId				(senderAccount.getLinkedinUID());
		sender.setJourneyId			(journeyId);
		
		postMessage(text, journeyTo, journeyFrom, sender);
	}
	
	//------------------------------------------------------------------------------------//
	
	public static void sendAnswer(String text, String contentUID, String journeyFromId) {

		Event eventLinked  = Event.find
				.where()
				.not(Expr.eq("journey.journeyId", journeyFromId))
				.ilike("content.uid", contentUID)
				.findUnique();
		
		Journey journeyFrom  = Journey.find.where().ilike("journeyId", journeyFromId).findUnique();
		Journey journeyTo 	= eventLinked.getJourney();
		
		EventPerson sender = new EventPerson();
		sender.setUid					(journeyFrom.getTrip().getTraveler().getUid());
		sender.setLinkedinUID		(journeyFrom.getTrip().getTraveler().getLinkedinUID());
		sender.setName					(journeyFrom.getTrip().getTraveler().getName());
		sender.setHeadline			(journeyFrom.getTrip().getTraveler().getHeadline());
		sender.setIndustry			(journeyFrom.getTrip().getTraveler().getIndustry());
		sender.setEmail				(journeyFrom.getTrip().getTraveler().getEmail());
		sender.setTripId				(journeyFrom.getTrip().getTripId());
		sender.setJourneyId			(journeyFrom.getJourneyId());

		EventPerson recepient = new EventPerson();
		recepient.setUid				(journeyTo.getTrip().getTraveler().getUid());
		recepient.setLinkedinUID	(journeyTo.getTrip().getTraveler().getLinkedinUID());
		recepient.setName				(journeyTo.getTrip().getTraveler().getName());
		recepient.setHeadline		(journeyTo.getTrip().getTraveler().getHeadline());
		recepient.setIndustry		(journeyTo.getTrip().getTraveler().getIndustry());
		recepient.setEmail			(journeyTo.getTrip().getTraveler().getEmail());
		recepient.setTripId			(journeyTo.getTrip().getTripId());
		recepient.setJourneyId		(journeyTo.getJourneyId());

		postMessage(text, journeyTo, journeyFrom, sender);
   }

	//------------------------------------------------------------------------------------//
	
	private static void postMessage(String text, Journey journeyTo, Journey journeyFrom, EventPerson sender) {

		EventContent content = new EventContent();
		content.setUid(Utils.generateUID());
		content.setText(text);
		content.setType(EventContent.MESSAGE);
		content.setTimestamp(new Date().getTime());
		Ebean.save(content);

		EventPerson recepient = new EventPerson();
		recepient.setUid				(journeyTo.getTrip().getTraveler().getUid());
		recepient.setLinkedinUID	(journeyTo.getTrip().getTraveler().getLinkedinUID());
		recepient.setName				(journeyTo.getTrip().getTraveler().getName());
		recepient.setHeadline		(journeyTo.getTrip().getTraveler().getHeadline());
		recepient.setIndustry		(journeyTo.getTrip().getTraveler().getIndustry());
		recepient.setEmail			(journeyTo.getTrip().getTraveler().getEmail());
		recepient.setTripId			(journeyTo.getTrip().getTripId());
		recepient.setJourneyId		(journeyTo.getJourneyId());
		
		Event event = new Event();
		event.setUid(Utils.generateUID());
		event.setStatus(Event.NEW_EVENT);
		event.setJourney(journeyTo);
		event.setContent(content);
		event.setSender(gson.toJson(sender));
		event.setRecepient(gson.toJson(recepient));
		Ebean.save(event);

		Event copy = new Event();
		copy.setUid(Utils.generateUID());
		copy.setStatus(Event.READ_EVENT);
		copy.setJourney(journeyFrom);
		copy.setContent(content);
		copy.setSender(gson.toJson(sender));
		copy.setRecepient(gson.toJson(recepient));
		
		Ebean.save(copy);
	}
	
	//------------------------------------------------------------------------------------//

	public static void readEvent(String uid) {
		Event event  = Event.find.where().ilike("uid", uid).findUnique();
		event.setStatus(Event.READ_EVENT);
		Ebean.save(event);
   }
	
	//------------------------------------------------------------------------------------//
	
	private static List<Event> createEvents(Journey journey1, Journey journey2, EventContent content) 
	{
		EventPerson sender1 = new EventPerson();
		sender1.setUid				(journey1.getTrip().getTraveler().getUid());
		sender1.setTripId			(journey1.getTrip().getTripId());
		sender1.setJourneyId		(journey1.getJourneyId());
		sender1.setLinkedinUID	(journey1.getTrip().getTraveler().getLinkedinUID());
		sender1.setName			(journey1.getTrip().getTraveler().getName());
		sender1.setHeadline		(journey1.getTrip().getTraveler().getHeadline());
		sender1.setIndustry		(journey1.getTrip().getTraveler().getIndustry());
		sender1.setEmail			(journey1.getTrip().getTraveler().getEmail());
		
		EventPerson sender2 = new EventPerson();
		sender2.setUid				(journey2.getTrip().getTraveler().getUid());
		sender2.setTripId			(journey2.getTrip().getTripId());
		sender2.setJourneyId		(journey2.getJourneyId());
		sender2.setLinkedinUID	(journey2.getTrip().getTraveler().getLinkedinUID());
		sender2.setName			(journey2.getTrip().getTraveler().getName());
		sender2.setHeadline		(journey2.getTrip().getTraveler().getHeadline());
		sender2.setIndustry		(journey2.getTrip().getTraveler().getIndustry());
		sender2.setEmail			(journey2.getTrip().getTraveler().getEmail());
		
		Event event1 = new Event();
		Event event2 = new Event();
		
		event1.setUid(Utils.generateUID());
		event2.setUid(Utils.generateUID());

		event1.setStatus(Event.NEW_EVENT);
		event2.setStatus(Event.NEW_EVENT);
		
		event1.setJourney(journey1);
		event1.setSender(gson.toJson(sender2));
		
		event2.setJourney(journey2);
		event2.setSender(gson.toJson(sender1));

		event1.setContent(content);
		event2.setContent(content);
		
		Ebean.save(event1);
		Ebean.save(event2);
		
		List<Event> events = new ArrayList<Event>();
		events.add(event1);
		events.add(event2);
		return events;
	}

}
