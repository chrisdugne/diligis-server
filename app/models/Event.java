package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import play.db.ebean.Model;

import com.google.gson.annotations.Expose;

@Entity
public class Event extends Model { 

   // -----------------------------------------------------------------------------------------------//
	// status
	public static int COMMON_ANNOUNCEMENT = 0;

	public static int READ_EVENT = 11;
	public static int NEW_EVENT = 22;

	public static int INVITATION_REFUSED = 33;

	public static int DILIGIS_SEEN = 44;
	public static int IRRELEVANT_DILIGIS = 55;
	
	// -----------------------------------------------------------------------------------------------//

	@Id
	@Expose
	private String uid;

	@Expose
	private int status;

	@Expose
	@ManyToOne
	@OrderBy(value = "uid desc")
	private EventContent content;

	@Expose
	private String sender;

	@Expose
	private String recepient;
	
	// -----------------------------------------------------------------------------------------------//

	@ManyToOne
	private Journey journey;

	// -----------------------------------------------------------------------------------------------//
	
	public static Model.Finder<String, Event> find = new Finder<String, Event>(String.class, Event.class);

	// -----------------------------------------------------------------------------------------------//
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Journey getJourney() {
		return journey;
	}

	public void setJourney(Journey journey) {
		this.journey = journey;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecepient() {
		return recepient;
	}

	public void setRecepient(String recepient) {
		this.recepient = recepient;
	}

	public EventContent getContent() {
		return content;
	}

	public void setContent(EventContent content) {
		this.content = content;
	}
	
	// -----------------------------------------------------------------------------------------------//

   private static final long serialVersionUID = 3991738064737538421L;

}
