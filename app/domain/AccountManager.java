package domain;

import models.Account;
import models.Journey;
import models.Trip;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.Transactional;
import utils.Utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

public class AccountManager {

	//------------------------------------------------------------------------------------//
	
	public static Account getAccount(JsonNode userJson)
	{
		String email = userJson.get("email").asText();
		
		ExpressionList<Account> accounts = Account.find
				.fetch("trips")
				.fetch("trips.journeys")
				.fetch("trips.journeys.events")
				.fetch("trips.journeys.events.content")
				.where()
				.ilike("email", email);
		
		if(accounts.findRowCount() == 1)
			return accounts.findUnique();
		else 
			return createNewAccount(userJson);
		
	}

	//------------------------------------------------------------------------------------//
	
	public static Account getUser(String userUID)
	{
		ExpressionList<Account> accounts = Account.find
				.fetch("trips")
				.fetch("trips.journeys")
				.fetch("trips.journeys.events")
				.fetch("trips.journeys.events.content")
				.where()
				.ilike("uid", userUID);
		
		return accounts.findUnique();
	}

	//------------------------------------------------------------------------------------//

	@Transactional
	private static Account createNewAccount(JsonNode userJson) 
	{
		String email 			= userJson.get("email").asText();
		String linkedinUID 	= userJson.get("linkedinUID").asText();
		String name 			= userJson.get("name").asText();
		String headline 		= userJson.get("headline").asText();
		String industry 		= userJson.get("industry").asText();
		
		String accountUID = Utils.generateUID();
		
		Account account = new Account();
		account.setEmail(email);
		account.setName(name);
		account.setLinkedinUID(linkedinUID);
		account.setHeadline(headline);
		account.setIndustry(industry);
		account.setUid(accountUID);
	
		Ebean.save(account);
//	   EventManager.announce(account);

	   // ---- dummy trip/journey : to link new messages
	   Trip trip = new Trip();
	   trip.setTraveler(account);
	   trip.setTripId(accountUID);
	   Ebean.save(trip);

	   Journey journey = new Journey();
	   journey.setTrip(trip);
	   journey.setJourneyId(accountUID);
	   Ebean.save(journey);
	   
		return account;
	}
	
	//------------------------------------------------------------------------------------//

	public static Account saveUser(JsonNode userJson) {
		
		String email 			= userJson.get("email").asText();
		String linkedinUID 	= userJson.get("linkedinUID").asText();
		String name 			= userJson.get("name").asText();
		String headline 		= userJson.get("headline").asText();
		String industry 		= userJson.get("industry").asText();

		Account existingUser = Account.find
				.fetch("trips")
				.fetch("trips.journeys")
				.fetch("trips.journeys.events")
				.fetch("trips.journeys.events.content")
				.where()
				.ilike("linkedinUID", linkedinUID)
				.findUnique();
		
		if(existingUser != null){
			Logger.debug("existingUser");
			existingUser.setEmail(email);
			existingUser.setName(name);
			existingUser.setLinkedinUID(linkedinUID);
			existingUser.setHeadline(headline);
			existingUser.setIndustry(industry);
			
			Ebean.save(existingUser);
			return existingUser;
		}
		else{
			Logger.debug("createNewAccount");
			return createNewAccount(userJson);
		}

//	DEPRECATED
// with account from diligis signup
//		
//		else{
//			Account user = Account.find
//					.fetch("trips")
//					.fetch("trips.journeys")
//					.fetch("trips.journeys.events")
//					.fetch("trips.journeys.events.content")
//					.where()
//					.ilike("email", oldEmail)
//					.findUnique();
//			
//			if(user == null){
//				Logger.debug("createNewAccount");
//				return createNewAccount(userJson);
//			}
//			else{
//				Logger.debug("found user");
//				user.setEmail(email);
//				user.setName(name);
//				user.setLinkedinUID(linkedinUID);
//				user.setHeadline(headline);
//				user.setIndustry(industry);
//				
//				Ebean.save(user);
//				return user;
//			}
//			
//		}
   }
	
	//------------------------------------------------------------------------------------//
	
	/**
	 * Verifies the link between an account and tripit
	 * 
	 * -> if the user has no tripitRef : new link
	 *  -> verifies if the ref is not linked with another account
	 *  	-> return "exist" if there is another account yet
	 *    -> return "ok" if not, after having linked tripit with this account 
	 * 
	 * -> if the user has a tripitref : verifies if this is the same
	 *    -> return "ok" if this is the same
	 *  	-> return the email of the linked tripit if not.
	 * 
	 * @param userJson
	 * @param tripitProfileJson
	 * @return

	public static String verifyTripitProfile(JsonNode userJson, JsonNode tripitProfileJson) {
		String ref = tripitProfileJson.get("ref").asText();
		String tripitEmail = tripitProfileJson.get("email").asText();
		String email = userJson.get("email").asText();
		
		ExpressionList<Account> accounts = Account.find.where().ilike("email", email);
		Account user = accounts.findUnique();
		
		if(user.getTripitRef() == null){
			if(existTripitRef(ref))
				return "exist";
			else{
				user.setTripitRef(ref);
				user.setTripitEmail(tripitEmail);
				Ebean.save(user);
				return "ok";
			}
		}
		else{
			if(user.getTripitRef().equals(ref))
				return "ok";
			else{
				return user.getTripitEmail();
			}
		}
   }
	

	// verify if ref exists yet
	private static Boolean existTripitRef(String ref) {
		ExpressionList<Account> accounts = Account.find.where().ilike("tripitRef", ref);
		Account user = accounts.findUnique();
		
	   return user != null;
   }
   */
}
