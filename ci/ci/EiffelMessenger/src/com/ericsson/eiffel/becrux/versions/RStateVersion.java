package com.ericsson.eiffel.becrux.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

public class RStateVersion extends Version{
	public static enum RStateType {
		ORDINARY, VERIFICATION, PRELIMINARY, SPECIAL_DIGITS, SPECIAL_LETTER, SPECIAL_PRELIMINARY
	}
	
	public static enum TokenType {
		PRIMARY, MAJOR, MINOR, SUFFIX
	}

	private String version;

	private String primary;
	private String major;
	private String minor;
	private String suffix;
	private RStateType type;
	private static final String VALID_TWO_DIGITS = "^/[1-9][0-9]?$";
	private static final String VALID_CONSONANTS = "^[B-DF-HJ-NSTVXZ]$";
	private static final String VALID_LETTERS = "^[A-HJ-NS-VX-Z]$";

	public RStateVersion(String version) throws IllegalArgumentException {
		if(version == null || version.isEmpty())
    		throw new IllegalArgumentException(VERSION_MISSING);
		this.version = version;
		validateRState();
	}
	
	public String getPrimary() {
		return primary;
	}

	public String getMajor() {
		return major;
	}
	public String getMinor() {
		return minor;
	}

	public String getSuffix() {
		return suffix;
	}

	public RStateType getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if(version == null || version.isEmpty())
    		throw new IllegalArgumentException(VERSION_MISSING);
		String oldVersion = this.version;
		try {			
			this.version = version;
			validateRState();
		} catch(Exception ex) {
			this.version = oldVersion;
			throw ex;
		}
	}

	private void validateRState() {
		if (version.length() < 2 || version.length() > 7)
			throw new IllegalArgumentException(
					version + " is " + version.length() + " characters long but it should be [2,7] instead.");
		if (!version.matches("^[PR][A-HJ-NS-VX-Z0-9/]+$"))
			throw new IllegalArgumentException(
					"R-state: " + version + " did not match regular expression: ^[PR][A-HJ-NS-VX-Z0-9/]+$.");
		parseRState();
		if (minor.length() > 2 && minor.substring(1,3).matches(".?[AEUY].?")) {
			throw new IllegalArgumentException("Invalid vowels [AEUY] on 2 or 3 position in letters part: " + version);
		}
		identifyType();
	}

	private void parseRState() {
		Pattern pattern = Pattern.compile(
				"^([P,R])([1-9][0-9]{0,3})?([A-Z]{0,4})?(((0[1-9])|([1-9][0-9]{1,2}))|(/(([1-9][0-9]?)|[A-Z]))?$)");
		Matcher matcher = pattern.matcher(version);
		while (matcher.find()) {
			primary = (matcher.group(1) != null) ? matcher.group(1) : "";
			major = (matcher.group(2) != null) ? matcher.group(2) : "";
			minor = (matcher.group(3) != null) ? matcher.group(3) : "";
			suffix = (matcher.group(4) != null) ? matcher.group(4) : "";
		}
		if (!matcher.matches())
			throw new IllegalArgumentException(
					"Invalid R-state format: " + version + " according to the document 1092-212.");
	}

	private void identifyType() {
		if (primary.equals("R")) {
			if (suffix.isEmpty())
				type = RStateType.ORDINARY;
			else if (suffix.charAt(0) == '/') {
				if (major.isEmpty())
					throw new IllegalArgumentException("Digits part missing in special ordinary R-state: " + version);
				if (suffix.matches(VALID_TWO_DIGITS))
					type = RStateType.SPECIAL_DIGITS;
				else if (!minor.isEmpty())
					type = RStateType.SPECIAL_LETTER;
				else
					throw new IllegalArgumentException("Letters part missing in special ordinary R-state: " + version);
			} else {
				if (major.length() < 1 || major.length() > 3 || minor.length() < 1 || minor.length() > 2)
					throw new IllegalArgumentException(
							"Verification R-state must have from 1 to 3 digits on major and 1 to 2 letters on minor parts.");
				type = RStateType.VERIFICATION;
			}
		} else {
			if (major.isEmpty())
				throw new IllegalArgumentException("Missing digits part for preliminary R-state: " + version);
			if (suffix.isEmpty())
				type = RStateType.PRELIMINARY;
			else {
				if (suffix.matches(VALID_TWO_DIGITS))
					type = RStateType.SPECIAL_PRELIMINARY;
				else
					throw new IllegalArgumentException("Letters suffix for P-states are not allowed in: " + version);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((major == null) ? 0 : major.hashCode());
		result = prime * result + ((minor == null) ? 0 : minor.hashCode());
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RStateVersion other = (RStateVersion) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public int compareTo(Version other) {
		RStateVersion o = (RStateVersion) other;
		if(this.equals(o))
			return 0;
		if (type.equals(o.type)){
			if(major.length() != o.major.length())
				return major.length() - o.major.length();
			if(!major.equals(o.major))
				return major.compareTo(o.major);
			if(minor.length() != o.minor.length())
				return minor.length() - o.minor.length();
			if(!minor.equals(o.minor))
				return minor.compareTo(o.minor);
			if(suffix.length() != o.suffix.length())
				return suffix.length() - o.suffix.length();
			return suffix.compareTo(o.suffix);
		}
		boolean isPreliminaryWithOtherType = (getPrimary().equals("P") && o.getPrimary().equals("R"))
				|| (getPrimary().equals("R") && o.getPrimary().equals("P"));
		boolean isPreliminarySpecial = (getPrimary().equals("P") && o.getPrimary().equals("P"));
		boolean isOrdinaryVerification = (type.equals(RStateType.ORDINARY) && o.getType().equals(RStateType.VERIFICATION))
				|| (type.equals(RStateType.VERIFICATION) && o.getType().equals(RStateType.ORDINARY));
		boolean isOrdinarySpecialLetter =(type.equals(RStateType.ORDINARY) && o.getType().equals(RStateType.SPECIAL_LETTER))
				|| (type.equals(RStateType.SPECIAL_LETTER) && o.getType().equals(RStateType.ORDINARY));
		boolean isOrdinarySpecialDigit = (type.equals(RStateType.ORDINARY) && o.getType().equals(RStateType.SPECIAL_DIGITS))
				|| (type.equals(RStateType.SPECIAL_DIGITS) && o.getType().equals(RStateType.ORDINARY));
		
		boolean isVerificationSpecialDigit = (type.equals(RStateType.VERIFICATION) && o.getType().equals(RStateType.SPECIAL_DIGITS))
				|| (type.equals(RStateType.SPECIAL_DIGITS) && o.getType().equals(RStateType.VERIFICATION));
		boolean isVerificationSpecialLetter = (type.equals(RStateType.VERIFICATION) && o.getType().equals(RStateType.SPECIAL_LETTER))
				|| (type.equals(RStateType.SPECIAL_LETTER) && o.getType().equals(RStateType.VERIFICATION));
		boolean isSpecialDigitSpecialLetter = (type.equals(RStateType.SPECIAL_DIGITS) && o.getType().equals(RStateType.SPECIAL_LETTER))
				|| (type.equals(RStateType.SPECIAL_LETTER) && o.getType().equals(RStateType.SPECIAL_DIGITS));
		
		if (isOrdinaryVerification || isVerificationSpecialDigit || isVerificationSpecialLetter){
			if(major.length() != o.major.length())
				return major.length() - o.major.length();
			if(!major.equals(o.major))
				return major.compareTo(o.major);
			if(minor.length() != o.minor.length())
				return minor.length() - o.minor.length();
			if(!minor.equals(o.minor))
				return minor.compareTo(o.minor);
			return o.suffix.compareTo(suffix);
		}
		if (isOrdinarySpecialLetter || isOrdinarySpecialDigit || isPreliminarySpecial){
			if(major.length() != o.major.length())
				return major.length() - o.major.length();
			if(!major.equals(o.major))
				return major.compareTo(o.major);
			if(minor.length() != o.minor.length())
				return minor.length() - o.minor.length();
			if(!minor.equals(o.minor))
				return minor.compareTo(o.minor);
			return suffix.compareTo(o.suffix);
		}		
		if (isSpecialDigitSpecialLetter){
			if(major.length() != o.major.length())
				return major.length() - o.major.length();
			if(!major.equals(o.major))
				return major.compareTo(o.major);
			if(minor.length() != o.minor.length())
				return minor.length() - o.minor.length();
			if(!minor.equals(o.minor))
				return minor.compareTo(o.minor);
			throw new IllegalArgumentException();
		}		
		if (isPreliminaryWithOtherType){
			throw new IllegalArgumentException("Cannot compare preliminary with non preliminary without history of the product");
		}
		
		return 0;
	}
	
	public RStateVersion stepVersion(TokenType tokenType){
		switch(tokenType){
		case MAJOR:
			return stepMajor();
		case MINOR:
			return stepMinor();
		case SUFFIX:
			return stepSuffix();
		default:
			throw new IllegalArgumentException("Not handled step level");
		}
	}

	public RStateVersion stepSuffix() {
		if(suffix.isEmpty()) {
			throw new IllegalArgumentException("Cannot skip suffix part as it does not exist");
		}
		else {
			if(type.equals(RStateType.VERIFICATION)){
				int temp_suffix = Integer.parseInt(suffix);
				temp_suffix++;
				if(String.valueOf(temp_suffix).length() == 1)
					return new RStateVersion(primary+major+minor+"0"+String.valueOf(temp_suffix));
				return new RStateVersion(primary+major+minor+String.valueOf(temp_suffix));
			}
			if(suffix.matches(VALID_TWO_DIGITS)){
				int temp_suffix = Integer.parseInt(suffix.substring(1));
				temp_suffix++;
				return new RStateVersion(primary+major+minor+"/"+String.valueOf(temp_suffix));
			}
			else{
				char c = suffix.charAt(1);
				if (c == 'Z')
					throw new IllegalArgumentException("Suffix 'Z' cannot be stepped");
				do{
					c++;					
				}while(!String.valueOf(c).matches(VALID_LETTERS));
				return new RStateVersion(primary+major+minor+"/"+String.valueOf(c));
			}
		}
	}

	public RStateVersion stepMinor() {
		if(minor.isEmpty()){
			throw new IllegalArgumentException("Cannot skip letters part as it does not exist");
		}
		else{
			String temp_rest=resetToken(TokenType.SUFFIX, suffix);
			if(minor.equals("Z"))
				return new RStateVersion(primary+major+"AA"+temp_rest);
			else if(minor.equals("ZZ"))
				return new RStateVersion(primary+major+"ABB"+temp_rest);
			else if(minor.equals("ZZZ"))
				return new RStateVersion(primary+major+"ABBA"+temp_rest);
			else if(minor.equals("ZZZZ"))
				throw new IllegalArgumentException("Letters 'ZZZZ' cannot be stepped");
			char arr[]=minor.toCharArray();
			ArrayUtils.reverse(arr);
			boolean carry = true;
			int i=0;
			String condition;
			for(char c :arr){
				if(carry){
					if(c == 'Z'){
						carry = true;
						arr[i] = ((arr.length==3 && i<2) || (arr.length==4 && i>0 && i<3)) ? 'B' : 'A';
					}
					else{
						condition = ((arr.length==3 && i<2) || (arr.length==4 && i>0 && i<3)) ? VALID_CONSONANTS : VALID_LETTERS;
						do{
							c++;
						}while(!String.valueOf(c).matches(condition));
						arr[i] = c;
						carry = false;
					}
				}
				i++;
			}
			ArrayUtils.reverse(arr);
			return new RStateVersion(primary+major+String.valueOf(arr)+temp_rest);
		}
	}

	public RStateVersion stepMajor() {
		if(major.isEmpty())
			throw new IllegalArgumentException("Cannot skip digits part as it does not exists");
		else {
			int temp_major = Integer.parseInt(major);
			temp_major++;
			if(String.valueOf(temp_major).length() > 4)
				throw new IllegalArgumentException("Digits part cannot exceed 4 digits");
			if(type.equals(RStateType.VERIFICATION) && String.valueOf(temp_major).length() > 3)
				throw new IllegalArgumentException("In verification R-state major part can't be longer that 3 digits");
			String temp_minor=resetToken(TokenType.MINOR, minor);
			String temp_rest=resetToken(TokenType.SUFFIX, suffix);
			return new RStateVersion(primary+String.valueOf(temp_major)+temp_minor+temp_rest);
		}
	}
	
	private String resetToken(TokenType tokenType, String token) {
		if(token.isEmpty())
			return token;
		if(tokenType.equals(TokenType.MINOR))
			return "A";
		if(type.equals(RStateType.VERIFICATION))
			return "01";
		else if(type.equals(RStateType.SPECIAL_LETTER))
			return "/A";
		else return "/1";
	}

	@Override
	public Version stepFirst() {
		if(!major.isEmpty())
			return stepMajor();
		else 
			return stepMinor();
	}

	@Override
	public Version stepLast() {
		if(!suffix.isEmpty())
			return stepSuffix();
		else if(!minor.isEmpty())
			return stepMinor();
		else
			return stepMajor();
	}
}
