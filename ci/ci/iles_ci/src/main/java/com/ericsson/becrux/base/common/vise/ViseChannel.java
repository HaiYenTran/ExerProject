package com.ericsson.becrux.base.common.vise;

/**
 * The VISE channel definition.
 */
public class ViseChannel {

    private static final String prefix = "VISE0";
    private static final String channelRegex = "(" + prefix + ")?[1-9]\\d\\d";
    private static final String DEFAULT_IP_STRING = "1.1.1.0";
    private String fullName;
    private String shortName;
    private String wholeNumber;
    private int number;
    private String ipAddress;

    // The state of VISE channel used for managing.
    private State state;

    /**
     * Create a new VISE value object.
     *
     * @param viseChannel string which matches a regular expression: {@value #channelRegex}
     *                    (the full name with {@value #prefix} prefix or the last three digits)
     * @param ipAddress should match IPv4 format
     * @throws NullPointerException     if parameter is empty or null
     * @throws IllegalArgumentException if parameter does not match the regular expression
     */
    public ViseChannel(String viseChannel, String ipAddress) {

        if (viseChannel == null || viseChannel.isEmpty())
            throw new NullPointerException("Null/empty VISE value name provided.");

        if (viseChannel.matches(channelRegex)) {
            if (viseChannel.length() == 3) {
                this.fullName = prefix + viseChannel;
                this.shortName = viseChannel;
                this.wholeNumber = "0" + viseChannel;
            } else {
                this.fullName = viseChannel;
                this.shortName = viseChannel.substring(viseChannel.length() - 3);
                this.wholeNumber = "0" + this.shortName;
            }
            this.number = Integer.parseInt(this.shortName);
            if (ipAddress != null && !ipAddress.isEmpty()) {
                //TODO check IP format
                this.ipAddress = ipAddress;
            } else {
                throw new IllegalArgumentException("TTCN IP Address must have value.");
            }

            // when creat new VISE channel, default state all ways be AVAILABLE
            state = State.AVAILABLE;
        } else {
            throw new IllegalArgumentException("Illegal format of VISE value name.");
        }
    }

    /**
     * Create a new VISE value object.
     *
     * @param viseChannel number in the range 100-999
     * @param ipAddress ip address for VISE
     * @throws IllegalArgumentException if parameter is out of range
     */
    public ViseChannel(int viseChannel, String ipAddress) {
        this(Integer.toString(viseChannel), ipAddress);
    }

    public ViseChannel(int viseChannel) {
        this(Integer.toString(viseChannel), DEFAULT_IP_STRING);
    }

    public ViseChannel(String viseChannel) {
        this(viseChannel, DEFAULT_IP_STRING);
    }

    public static String getChannelRegex() {
        return channelRegex;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getWholeNumber() {
        return wholeNumber;
    }

    public int getNumber() {
        return number;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + number;
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
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
        ViseChannel other = (ViseChannel) obj;
        if (fullName == null) {
            if (other.fullName != null)
                return false;
        } else if (!fullName.equals(other.fullName))
            return false;
        if (number != other.number)
            return false;
        if (shortName == null) {
            if (other.shortName != null)
                return false;
        } else if (!shortName.equals(other.shortName))
            return false;
        return true;
    }

    @Override
    public String toString() { return fullName;}

    // add more state if need
    public enum State {
        AVAILABLE, RUNNING
    }
}
