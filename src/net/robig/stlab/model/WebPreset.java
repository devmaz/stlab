package net.robig.stlab.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.robig.logging.Logger;
import net.robig.net.XmlParser.XmlElement;

public class WebPreset {
	static DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
	static Logger log=new Logger(WebPreset.class);
	private int id=0;
	String title="";
	String tags="";
	String description="";
	float voteAvg=0;
	int voteCount=0;
	int voteSum=0;
	boolean alreadyVoted=false;
	Date voted=null;
	Date created=new Date();
	StPreset preset=null;
	WebUser owner=null;
	public static WebPreset fromXml(XmlElement presetElement) throws InvalidXmlException{
		WebPreset wp=new WebPreset();
		try {
			String title=null;
			String data=null;
			try {
				wp.setId(Integer.parseInt(presetElement.getAttribute("id")));
			}catch(Exception ex){
				log.debug("Error parsing XML: id "+ex);
				throw new InvalidXmlException("Error parsing XML id not found: "+ex+" "+ex.getMessage());
			}
			try {
				title=presetElement.getAttribute("title");
				assert title!=null;
			}catch(Exception ex){
				log.debug("Error parsing XML: title attribute not found "+ex);
				throw new InvalidXmlException("Error parsing XML: title attribute not found: "+ex+" "+ex.getMessage());
			}
			try {
				data=presetElement.find("data").get(0).getText();
			}catch(Exception ex){
				log.debug("Error parsing XML: preset data element "+ex);
				throw new InvalidXmlException("Error parsing XML: preset data not found: "+ex+" "+ex.getMessage());
			}
			try{
				wp.description=presetElement.find("description").get(0).getText();
			}catch(Exception ex){
				log.debug("Error parsing XML: description element "+ex);
				throw new InvalidXmlException("Error parsing XML: description element not found: "+ex+" "+ex.getMessage());
			}
			
			try {
				long ts_created=Long.parseLong(presetElement.getAttribute("created"));
				wp.created=new Date(ts_created);
			}catch(Exception ex){
				log.debug("Error parsing XML: created date attribute "+ex);
				throw new InvalidXmlException("Error parsing XML: date attribute not found: "+ex+" "+ex.getMessage());
			}
			StPreset p=new StPreset();
			p.parseParameters(data);
			p.setName(title);
			wp.title=title;
			wp.preset=p;
			try {
				XmlElement owner=presetElement.find("owner").get(0);
				wp.owner=WebUser.fromXml(owner);
			}catch(Exception ex){
				log.debug("Error parsing XML: preset owner element! "+ex);
				throw new InvalidXmlException("Error parsing XML: preset owner element not found! "+ex+" "+ex.getMessage());
			}
			XmlElement votesElement=null;
			try {
				votesElement=presetElement.find("votes").get(0);
			}catch(Exception ex){
				log.debug("Error parsing XML: cannot find votes element");
				throw new InvalidXmlException("Error parsing vote avg in XML: "+ex+" "+ex.getMessage());
			}
			try {
				wp.voteAvg=Float.parseFloat(votesElement.getAttribute("avg"));
			}catch(Exception ex){
				log.debug("Error parsing XML: vote avg attribute not found! "+ex);
				throw new InvalidXmlException("Error parsing XML: vote avg attibute not found! "+ex+" "+ex.getMessage());
			}
			try {
				wp.voteSum=Integer.parseInt(votesElement.getAttribute("sum"));
			}catch(Exception ex){
				log.debug("Error parsing XML: vote sum attribute not found! "+ex);
//				throw new InvalidXmlException("Error parsing XML: vote sum attibute not found! "+ex+" "+ex.getMessage());
			}
			try {
				wp.voteCount=Integer.parseInt(votesElement.getAttribute("count"));
			}catch(Exception ex){
				log.debug("Error parsing XML: vote count attribute not found! "+ex);
				throw new InvalidXmlException("Error parsing XML: vote count attibute not found! "+ex+" "+ex.getMessage());
			}
			try {
				long voted=Long.parseLong(votesElement.getAttribute("voted"));
				wp.alreadyVoted=voted>0;
				wp.voted=new Date(voted);
			}catch(Exception ex){
				log.debug("Error parsing XML: vote date attribute not found! "+ex);
				throw new InvalidXmlException("Error parsing XML: vote date attibute not found! "+ex+" "+ex.getMessage());
			}
			try {
				if(wp.voteCount>0){
					for(XmlElement e: votesElement.find("vote")){
						int value=Integer.parseInt(e.getAttribute("value"));
						String comment=e.getAttribute("comment");
						WebUser user=WebUser.fromXml(e.find("user").get(0));
						log.debug("vote: "+value+" from "+user.getUsername()+" wrote: "+comment);
					}
				}
			}catch(Exception ex){
				log.debug("Error parsing XML: failed to get attached votes! "+ex);
				throw new InvalidXmlException("Error parsing XML: failed to get attached votes! "+ex+" "+ex.getMessage());
			}
		}catch(Exception ex){
			log.debug("Exception ocoured parsing XML!");
			throw new InvalidXmlException(ex.getMessage());
		}
		return wp;
	}
	
	public String toString() {
		return "Preset #"+getId()+": �"+title+"� \n"+
			owner+"\n"+
			preset+"\n";
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StPreset getData() {
		return preset;
	}

	public void setData(StPreset preset) {
		this.preset = preset;
	}

	public static String bool2Str(boolean b){
		return b?"on&nbsp;":"off";
	}
	
	/**
	 * get a detailed html formated description
	 * @return
	 */
	public String toHtml(){
		return "<html><u>Description:</u><br/>"+
			getDescription()+
			"<br/>"+
			"Amp: "+getData().getAmpName()+" ("+getData().getAmpTypeName()+")<br/>"+
			"volume="+getData().getVolume()+" "+" gain="+getData().getGain()+"<br/>"+
			"treble="+getData().getTreble()+" middle="+getData().getMiddle()+" bass="+getData().getBass()+"<br/>" +
			"presence="+getData().getPresence()+" NR="+getData().getNoiseReduction()+"<br/>"+
			"cabinet "+bool2Str(getData().isCabinetEnabled())+": "+getData().getCabinetName()+"<br/>"+
			"pedal &nbsp;&nbsp;&nbsp;"+bool2Str(getData().isPedalEnabled())+": "+getData().getPedalEffectName()+" value="+getData().getPedalEdit()+"<br/>"+
			"reverb &nbsp;&nbsp;"+bool2Str(getData().isReverbEnabled())+": "+getData().getReverbEffectName()+" value="+getData().getReverbEdit()+"<br/>"+
			"delay &nbsp;&nbsp;&nbsp;"+bool2Str(getData().isDelayEnabled())+": "+getData().getDelayEffectName()+" depth="+getData().getDelayDepth()+"<br/>" +
					"&nbsp; feedback="+getData().getDelayFeedback()+" speed="+
					getData().getDelaySpeedString()+
			
			"</html>";
	}
	
	public String toBasicHtml(boolean isLoggedin){
		return "<html>"+
			"<u>Author:</u><br/>"+
			getOwner().getUsername()+
			"<br/>"+
			"<u>Created:</u><br/>"+
			formatter.format(getCreated())+"<br/>"+
			"<u>Votes:</u><br/>"+
			"avg: &nbsp;"+getVoteAvg()+"<br/>"+
			"count: "+getVoteCount()+"<br/>"+
			(isLoggedin?
					(hasAlreadyVoted()?"voted already on<br/>"+formatter.format(getVoted()):"not voted yet"):""
			)+
			"</html>";
			
	}
	
	public String toTopPanelHtml(boolean isLoggedin){
		return "<html>"+
			"<b>"+getTitle()+"</b><br/>"+
			"<br/><u>Description:</u><br/>"+
			getDescription().replace("\n", "</br>")+
			"<br/>by: "+getOwner().getUsername()+
			"<br/>"+
			"<u>Created:</u><br/>"+
			formatter.format(getCreated())+"<br/>"+
			getCreated()+"<br/>"+
			"<u>Votes:</u><br/>"+
			"avg: &nbsp;"+getVoteAvg()+"<br/>"+
			"count: "+getVoteCount()+"<br/>"+
			(isLoggedin?
					(hasAlreadyVoted()?"voted already on<br/>"+formatter.format(getVoted()):"not voted yet"):""
			)+
			"</html>";
			
	}

	public WebUser getOwner() {
		return owner;
	}

	public void setOwner(WebUser owner) {
		this.owner = owner;
	}

	public float getVoteAvg() {
		return voteAvg;
	}

	public void setVoteAvg(int voteAvg) {
		this.voteAvg = voteAvg;
	}
	
	public float getRating(){
		return getVoteAvg();
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getVoteSum() {
		return voteSum;
	}

	public void setVoteSum(int voteSum) {
		this.voteSum = voteSum;
	}

	public boolean hasAlreadyVoted() {
		return alreadyVoted;
	}
	
	public void setAlreadyVoted(boolean v) {
		alreadyVoted=v;
		if(v)voted=new Date();
	}

	public Date getVoted() {
		return voted;
	}

	public void setVoteAvg(float voteAvg) {
		this.voteAvg = voteAvg;
	}
}
