package com.ericsson.eiffel.becrux.versions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SequentialVersion extends Version
{
    private List<String> version;
    
    public SequentialVersion(String version)
    {
    	setVersion(version);
    }
    
    public SequentialVersion(String first, String second, String... rest)
    {
    	setVersion(first, second, rest);
    }
    
    public SequentialVersion(String[] version)
    {
    	setVersion(version);
    }
    
    public SequentialVersion(List<String> version)
    {
    	setVersion(version);
    }
    
    public SequentialVersion(Integer first, Integer... rest)
    {
    	setVersion(first, rest);
    }
    
    public SequentialVersion(Integer[] version)
    {
    	setVersion(version);
    }
    
    @Override
    public String getVersion()
    {
    	return String.join(".", version);
    }
    
    @Override
    public void setVersion(String version)
    {
    	if(version == null || version.length() <= 0)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	setVersion(version.split("\\."));
    }
    
    public void setVersion(String first, String second, String... rest)
    {
    	if(first == null || first.length() <= 0 || second == null || second.length() <= 0 || rest == null)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	List<String> result = new ArrayList<>();
    	result.add(first);
    	result.add(second);
    	Arrays.stream(rest).forEach(result::add);
    	setVersion(result);
    }
    
    public void setVersion(String[] version)
    {
    	if(version == null || version.length <= 0)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	setVersion(Arrays.asList(version));
    }
    
    public void setVersion(List<String> version)
    {
    	if(version == null || version.size() <= 0)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	version.stream().forEach(c -> Integer.parseInt(c));
    	this.version = version;
    }
    
    public void setVersion(Integer first, Integer... rest)
    {
    	if(first == null || rest == null)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	List<String> result = new ArrayList<>();
    	result.add(first.toString());
    	Arrays.stream(rest).forEach(i -> result.add(i.toString()));
    	setVersion(result);
    }
    
    public void setVersion(Integer[] version)
    {
    	if(version == null || version.length <= 0)
    		throw new IllegalArgumentException(VERSION_MISSING);
    	setVersion(Arrays.stream(version).map(i -> i.toString()).collect(Collectors.toList()));
    }
    
    @Override
    public int compareTo(Version other)
    {
    	SequentialVersion that = (SequentialVersion)other;
        if(that == null)
            return 1;
        int length = Math.max(version.size(), that.version.size());
        for(int i = 0; i < length; ++i)
        {
        	int thisPart = i < version.size() ? Integer.parseInt(version.get(i)) : 0;
        	int thatPart = i < that.version.size() ? Integer.parseInt(that.version.get(i)) : 0;
        	if(thisPart < thatPart)
        		return -1;
        	if(thisPart > thatPart)
        		return 1;
        }
        return 0;
    }
    
    public Version stepVersion(int position)
    {
    	List<String> temp = version.stream().collect(Collectors.toList()); //Copy list
    	String s = temp.get(position); //Get number on specified position
    	int count = 0;
    	for(int i = 0; i < s.length() - 1; ++i) //Find out how many leading zeros there are
    	{
    		if(s.charAt(i) == '0')
    			count = Math.addExact(count, 1);
    		else
    			break;
    	}
    	int number = Integer.parseInt(s); //Get number from retrieved string on specified position
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < count; ++i) //Append leading zeros
    		builder.append('0'); //Append back leading zeros
    	builder.append(Math.addExact(number, 1)); //Append incremented number
    	temp.set(position, builder.toString()); //Put back into list
    	return new SequentialVersion(temp);
    }
    
    @Override
    public Version stepFirst()
    {
    	return stepVersion(0);
    }
    
    @Override
    public Version stepLast()
    {
    	return stepVersion(version.size() - 1);
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequentialVersion other = (SequentialVersion) obj;
		if (version == null)
		{
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}
