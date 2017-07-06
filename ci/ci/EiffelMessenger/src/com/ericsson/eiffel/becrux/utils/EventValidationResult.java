package com.ericsson.eiffel.becrux.utils;

import java.util.ArrayList;
import java.util.List;

public class EventValidationResult
{	
	//Whether event should be discarded (it won't be saved to internal queue)
	private boolean discardEvent;
	
	//Optional error field
	private List<String> errors = new ArrayList<>();
	
	public EventValidationResult()
	{
		this(false);
	}
	
	public EventValidationResult(boolean discardEvent)
	{
		this(discardEvent, (String)null);
	}
	
	public EventValidationResult(boolean discardEvent, String error)
	{
		this.discardEvent = discardEvent;
		if(error != null)
			addError(error);
	}
	
	public boolean getValidationPassed()
	{
		return errors.size() == 0;
	}
	
	public boolean hasValidationPassed()
	{
		return getValidationPassed();
	}
	
	public boolean getDiscardEvent()
	{
		return discardEvent;
	}
	
	public void setDiscardEvent(boolean discardEvent)
	{
		this.discardEvent = discardEvent;
	}
	
	public boolean canDiscardEvent()
	{
		return getDiscardEvent();
	}
	
	public List<String> getErrors()
	{
		return errors;
	}
	
	public void setErrors(List<String> errors)
	{
		if(errors == null)
			throw new NullPointerException("List of errors cannot be null");
		if(errors.stream().anyMatch(e -> e == null))
			throw new NullPointerException("One of errors is null");
		this.errors = errors;
	}
	
	public void addError(String error)
	{
		if(error == null)
			throw new NullPointerException("Error cannot be null");
		this.errors.add(error);
	}
}
