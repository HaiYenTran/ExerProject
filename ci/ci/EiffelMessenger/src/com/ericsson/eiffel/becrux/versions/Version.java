package com.ericsson.eiffel.becrux.versions;

public abstract class Version implements Comparable<Version>
{	
	protected static final String VERSION_MISSING = "Version can not be null or empty";
	public static Version create(String version) throws IllegalArgumentException
	{
		if(version == null || version.length() <= 0)
			throw new IllegalArgumentException("Version cannot be null or empty");
		
		if(version.charAt(0) == 'R' || version.charAt(0) == 'P')
			return new RStateVersion(version);
		else if(version.charAt(0) >= '0' && version.charAt(0) <= '9')
			return new SequentialVersion(version);
		else
			throw new IllegalArgumentException("Unrecognized version type");
	}
	
	public abstract String getVersion();
	
	public abstract void setVersion(String version);
	
	public abstract Version stepFirst();
	
	public abstract Version stepLast();
}