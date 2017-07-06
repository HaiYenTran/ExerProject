package com.ericsson.eiffel.becrux.core;

public class EiffelException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6106190991137386565L;

	public EiffelException()
	{
		super();
	}
	
	public EiffelException(String message)
	{
		super(message);
	}
	
	public EiffelException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public EiffelException(Throwable cause)
	{
		super(cause);
	}
}
