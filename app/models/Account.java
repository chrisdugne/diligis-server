package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.db.ebean.Model;

import com.google.gson.annotations.Expose;

@Entity
public class Account extends Model { 
 
	// -----------------------------------------------------------------------------------------------//

	@Id
	@Expose
	private String uid;
	
	@Expose
	private String email;
	@Expose
	private String name;

	@Expose
	private String linkedinUID;
	
	@Expose
	private String headline;
	@Expose
	private String industry;

	// -----------------------------------------------------------------------------------------------//
	
	@Expose
	@OneToMany(mappedBy = "traveler", cascade = CascadeType.ALL  )
	@OrderBy(value = "tripId asc")
	private List<Trip> trips;
	
	// -----------------------------------------------------------------------------------------------//
	// -- Queries
	
	public static Model.Finder<String, Account> find = new Finder<String, Account>(String.class, Account.class);

	// -----------------------------------------------------------------------------------------------//
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinkedinUID() {
		return linkedinUID;
	}

	public void setLinkedinUID(String linkedinUID) {
		this.linkedinUID = linkedinUID;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public List<Trip> getTrips() {
		return trips;
	}

	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}

	// -----------------------------------------------------------------------------------------------//

   private static final long serialVersionUID = -6130575622095948407L;	
}
