package com.ericsson.becrux.base.common.utils;

import hudson.model.BuildBadgeAction;

/**
 * <p>
 * Class that adds badge to job build in Jenkins GUI (visible in a job's builds history).
 * Badge require the badge.jelly file in the proper location in resources
 * (similarly to build steps and config.jelly and global.jelly files).</p>
 * <p>
 * Example of usage:
 * {@link hudson.model.AbstractBuild build}
 * .{@link hudson.model.AbstractBuild#addAction(hudson.model.Action)
 * addAction(new BecruxBuildBadgeAction(...))}</p>
 */
public class BecruxBuildBadgeAction implements BuildBadgeAction {

    private String text;
    private String textColor;
    private String fontWeight;
    private String background;
    private String borderWidth;
    private String borderStyle;
    private String borderColor;

    /**
     * Create badge with CSS style.
     *
     * @param text        badge text message
     * @param textColor   <tt>color</tt>
     * @param fontWeight  <tt>font-weight</tt>
     * @param background  <tt>background-color</tt>
     * @param borderWidth <tt>border</tt> (1st arg)
     * @param borderStyle <tt>border</tt> (2nd arg)
     * @param borderColor <tt>border</tt> (3rd arg)
     */
    public BecruxBuildBadgeAction(String text, String textColor, String fontWeight, String background,
                                  String borderWidth, String borderStyle, String borderColor) {
        this.text = text;
        this.textColor = textColor;
        this.fontWeight = fontWeight;
        this.background = background;
        this.borderWidth = borderWidth;
        this.borderStyle = borderStyle;
        this.borderColor = borderColor;
    }

    /**
     * Create badge with default CSS style.
     *
     * @param text badge text message
     */
    public BecruxBuildBadgeAction(String text) {
        this(text, "black", "bold", "yellow", "1px", "solid", "black");
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getUrlName() {
        return "";
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }

    public String getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(String borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getText() {
        return text;
    }

}
