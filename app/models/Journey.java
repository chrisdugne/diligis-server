package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.db.ebean.Model;

import com.google.gson.annotations.Expose;

@Entity
public class Journey extends Model { 

	// -----------------------------------------------------------------------------------------------//

	public static final int DESTINATION	 	= 1;
	public static final int FLIGHT 			= 2;
	public static final int TRAIN 			= 3;

	// -----------------------------------------------------------------------------------------------//

	@Id
	@Expose
	private String journeyId;
	
	@Expose
	private int type;

	@Expose
	private Long startTime;
	@Expose
	private Long endTime;
	
	@Expose
	private String previousLocationName;
	@Expose
	private String nextLocationName;
	@Expose
	private String locationName;

	@Expose
	private String number;
	
	@Expose
	private String startAirport;
	@Expose
	private String endAirport;
	@Expose
	private String airline;
	@Expose
	private String seat;
	
	
	@Expose
	private String railcar;
	
	@Expose
	private Double latitude;
	@Expose
	private Double longitude;

	@Expose
	@OneToMany( cascade = CascadeType.ALL )
	@OrderBy(value = "uid desc")
	private List<Event> events;
	
	// -----------------------------------------------------------------------------------------------//

	@ManyToOne
	private Trip trip;
	
	// -----------------------------------------------------------------------------------------------//
	// -- Queries
	
	public static Model.Finder<String, Journey> find = new Finder<String, Journey>(String.class, Journey.class);

	// -----------------------------------------------------------------------------------------------//
	
	public String getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(String journeyId) {
		this.journeyId = journeyId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public String getPreviousLocationName() {
		return previousLocationName;
	}

	public void setPreviousLocationName(String previousLocationName) {
		this.previousLocationName = previousLocationName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getRailcar() {
		return railcar;
	}

	public void setRailcar(String railcar) {
		this.railcar = railcar;
	}

	public String getNextLocationName() {
		return nextLocationName;
	}

	public void setNextLocationName(String nextLocationName) {
		this.nextLocationName = nextLocationName;
	}

	public String getStartAirport() {
		return startAirport;
	}

	public void setStartAirport(String startAirport) {
		this.startAirport = startAirport;
	}

	public String getEndAirport() {
		return endAirport;
	}

	public void setEndAirport(String endAirport) {
		this.endAirport = endAirport;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	// -----------------------------------------------------------------------------------------------//

   private static final long serialVersionUID = 4264130731711027658L;

}
