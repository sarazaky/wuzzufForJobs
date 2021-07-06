package wuzzufJobs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "title", "company" })
public class Job implements Serializable{
	private static final long serialVersionUID = 1L;
	private String title;
    private String company;
    private String location;
    private String type;
    private String level;
    private String yearsExp;
    private String country;
    private String skills;
    
    
    public Job(String title, String company, String location, String type, String level, String yearsExp,
			String country, String skills) {
		this.title = title;
		this.company = company;
		this.location = location;
		this.type = type;
		this.level = level;
		this.yearsExp = yearsExp;
		this.country = country;
		this.skills = skills;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}

	public String getYearsExp() {
		return yearsExp;
	}

	public void setYearsExp(String yearsExp) {
		this.yearsExp = yearsExp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}
	
	@Override
	public String toString() {
		return "Job [title=" + title + ", company=" + company + ", location=" + location + ", type=" + type + ", level="
				+ level + ", yearsExp=" + yearsExp + ", country=" + country + ", skills=" + skills + "]";
	}
}
