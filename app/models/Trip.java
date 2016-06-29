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
public class Trip extends Model { 

	// -----------------------------------------------------------------------------------------------//

	public static final int IN_CREATION = 0;
	public static final int AVAILABLE   = 1;
	
	// -----------------------------------------------------------------------------------------------//

	@Id
	@Expose
	private String tripId;
	
	@Expose
	private String name;
	@Expose
	private String imageUrl;

	@Expose
	private int status;
	
	@Expose
	private Long lastModified;

	@Expose
	@OneToMany( cascade = CascadeType.ALL )
	@OrderBy(value = "startTime asc")
	private List<Journey> journeys;
	
	// -----------------------------------------------------------------------------------------------//

	@ManyToOne
	private Account traveler;
	
	// -----------------------------------------------------------------------------------------------//
	// -- Queries
	
	public static Model.Finder<String, Trip> find = new Finder<String, Trip>(String.class, Trip.class);

	// -----------------------------------------------------------------------------------------------//

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public List<Journey> getJourneys() {
		return journeys;
	}

	public void setJourneys(List<Journey> journeys) {
		this.journeys = journeys;
	}

	public Account getTraveler() {
		return traveler;
	}

	public void setTraveler(Account traveler) {
		this.traveler = traveler;
	}
	
	// -----------------------------------------------------------------------------------------------//

   private static final long serialVersionUID = 4264130731711027658L;	
}
