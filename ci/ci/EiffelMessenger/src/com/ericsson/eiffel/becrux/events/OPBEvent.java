package com.ericsson.eiffel.becrux.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ericsson.eiffel.becrux.utils.EventValidationResult;
import com.ericsson.eiffel.becrux.versions.Version;

/**
 * OK for promoting new baseline request event.
 *
 *
 */
public class OPBEvent extends Event {

	private List<String> products = new ArrayList<>();
	private List<String> baselines = new ArrayList<>();
	private boolean vote;
	private String signum;
	private String comment;

	public OPBEvent() {
		super(OPBEvent.class.getSimpleName());
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

	public List<Version> getBaselines() {
		return Collections.unmodifiableList(baselines.stream().map(Version::create).collect(Collectors.toList()));
	}

	public void setBaselines(List<Version> baselines) {
		this.baselines = baselines.stream().map(b -> b.getVersion()).collect(Collectors.toList());
	}

	public boolean getVote() {
		return vote;
	}

	public void setVote(boolean vote) {
		this.vote = vote;
	}

	public String getSignum() {
		return signum;
	}

	public void setSignum(String signum) {
		this.signum = signum;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public EventValidationResult validate() {

		EventValidationResult result = new EventValidationResult();
		if(products == null)
			result.addError("List of products is null");
		else if(products.isEmpty())
			result.addError("List of products is empty");
		else if(products.stream().anyMatch(p -> p == null))
			result.addError("There is a null product in the list of products");
		if(baselines == null)
			result.addError("List of baselines is null");
		else if(baselines.isEmpty())
			result.addError("List of baselines is empty");
		else {
			for(String baseline : baselines) {
				try {
					Version.create(baseline);
				} catch(Exception ex) {
					result.addError(ex.getMessage());
				}
			}
		}
		if(signum == null)
			result.addError("Signum is null");
		else if(signum.isEmpty())
			result.addError("Signum is empty");

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((baselines == null) ? 0 : baselines.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
		result = prime * result + ((signum == null) ? 0 : signum.hashCode());
		result = prime * result + (vote ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OPBEvent other = (OPBEvent) obj;
		if (baselines == null) {
			if (other.baselines != null)
				return false;
		} else if (!baselines.equals(other.baselines))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (products == null) {
			if (other.products != null)
				return false;
		} else if (!products.equals(other.products))
			return false;
		if (signum == null) {
			if (other.signum != null)
				return false;
		} else if (!signum.equals(other.signum))
			return false;
		if (vote != other.vote)
			return false;
		return true;
	}
}
