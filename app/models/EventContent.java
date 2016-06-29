package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

import com.google.gson.annotations.Expose;

@Entity
public class EventContent extends Model { 

	// -----------------------------------------------------------------------------------------------//
	// event type
	public static int ANNOUNCEMENT= 0;
	public static int DILIGIS 		= 1;
	public static int MESSAGE 		= 2;
	public static int INVITATION 	= 3;
	public static int MEETING 		= 4;

	// -----------------------------------------------------------------------------------------------//

	@Id
	@Expose
	private String uid;

	@Expose
	private int type;
	
	@Expose
	private Long timestamp;

	
	@Expose
	private String text;

	// -----------------------------------------------------------------------------------------------//

	@OneToMany
	private List<Event> linkedEvents;

	// -----------------------------------------------------------------------------------------------//

	public static Model.Finder<String, EventContent> find = new Finder<String, EventContent>(String.class, EventContent.class);

	// -----------------------------------------------------------------------------------------------//
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setLinkedEvents(List<Event> events) {
		this.linkedEvents = events;
	}
	
	// -----------------------------------------------------------------------------------------------//


	private static final long serialVersionUID = -8425213041824976820L;
	
}
