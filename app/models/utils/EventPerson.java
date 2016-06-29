package models.utils;

import com.google.gson.annotations.Expose;

public class EventPerson{ 

   // -----------------------------------------------------------------------------------------------//

	@Expose
	private String email;
	@Expose
	private String name;

	@Expose
	private String uid;
	@Expose
	private String linkedinUID;
	
	@Expose
	private String headline;
	@Expose
	private String industry;

	@Expose
	private String tripId;
	@Expose
	private String journeyId;

	// -----------------------------------------------------------------------------------------------//
	
	public String getTripId() {
		return tripId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}
	
	public String getJourneyId() {
		return journeyId;
	}

	public void setJourneyId(String journeyId) {
		this.journeyId = journeyId;
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


}
