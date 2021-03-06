package examples.models;

import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.content.commons.annotations.Content;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document
public class Claim {

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@org.springframework.data.annotation.Id
	private String claimId = UUID.randomUUID().toString();

	private String lastName;
	private String firstName;
	
	@Content
	@Embedded
	private ClaimForm claimForm = new ClaimForm();

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public ClaimForm getClaimForm() {
		return claimForm;
	}

	public void setClaimForm(ClaimForm claimForm) {
		this.claimForm = claimForm;
	}
}
