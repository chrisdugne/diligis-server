package controllers;

import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.createDestination;
import views.html.createTransport;
import views.html.createTrip;
import views.html.editDestination;
import views.html.editTransport;
import views.html.writeMessage;
import views.html.website.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Application extends Controller
{
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	// ---------------------------------------------//

	public static Result home()
	{
		return ok(home.render());
	}

	// ---------------------------------------------//
	
	public static Result createTrip()
	{
		return ok(createTrip.render());
	}
	
	// ---------------------------------------------//
	
	public static Result createDestination()
	{
		Map<String, String[]> queryParameters = request().queryString();
		Long startTime = Long.parseLong(queryParameters.get("startTime")[0]);

		return ok(createDestination.render(startTime));
	}
	
	public static Result createTransport()
	{
		Map<String, String[]> queryParameters = request().queryString();
		Boolean firstTransport = Boolean.parseBoolean(queryParameters.get("firstTransport")[0]);
		Long startTime = Long.parseLong(queryParameters.get("startTime")[0]);
		
		return ok(createTransport.render(firstTransport, startTime));
	}

	// ---------------------------------------------//
	
	public static Result editDestination()
	{
		Map<String, String[]> queryParameters = request().queryString();
		
		if(queryParameters.get("cancel") != null)
			return ok();
		
		Long startTime 	= Long.parseLong(queryParameters.get("startTime")[0]);
		Long endTime 		= Long.parseLong(queryParameters.get("endTime")[0]);
		String location 	= queryParameters.get("location")[0];
		
		return ok(editDestination.render(startTime, endTime, location));
	}
	
	public static Result editTransport()
	{
		Map<String, String[]> queryParameters = request().queryString();

		if(queryParameters.get("cancel") != null)
			return ok();
		
		Boolean lastTransport = Boolean.parseBoolean(queryParameters.get("lastTransport")[0]);
		Long startTime 	= Long.parseLong(queryParameters.get("startTime")[0]);
		Long endTime 		= Long.parseLong(queryParameters.get("endTime")[0]);
		int type 			= Integer.parseInt(queryParameters.get("type")[0]);
		String number 		= queryParameters.get("number")[0];
		String airline 	= queryParameters.get("airline")[0];
		String startAirport 	= queryParameters.get("startAirport")[0];
		String endAirport = queryParameters.get("endAirport")[0];
		String seat 		= queryParameters.get("seat")[0];
		String railcar 	= queryParameters.get("railcar")[0];
		
		return ok(editTransport.render(lastTransport, startTime, endTime, type, airline, startAirport, endAirport, number, seat, railcar));
	}
	
	// ---------------------------------------------//
	
	public static Result writeMessage()
	{
		return ok(writeMessage.render());
	}

}