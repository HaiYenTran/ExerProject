package com.ericsson.becrux.base.common.configuration;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import hudson.util.FormValidation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormValidator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static File toFile(String value) {
        File file;
        if (value.contains(" "))
            file = new File(value.substring(0, value.indexOf(" ")));
        else
            file = new File(value);
        return file;
    }

    private static FormValidation validateFile(String value) {
        File file = toFile(value);
        if (!file.exists())
            return FormValidation.errorWithMarkup("File <b>" + file + "</b> does not exist");
        if (value.contains("$"))
            return FormValidation.warning("It is recommended to avoid using variables.");
        if (!file.isAbsolute())
            return FormValidation.warning("It is recommended to use absolute path.");
        return null;
    }

    private static FormValidation validateNumber(String value) {
        if (!value.matches("[0-9]*"))
            return FormValidation.errorWithMarkup("Only digits are allowed in this field.");
        return null;
    }

    public static FormValidation isEmpty(String value) {
        if (value != null && value.isEmpty())
            return FormValidation.errorWithMarkup("This field cannot be empty.");
        return FormValidation.ok();
    }

    public static FormValidation isDir(String value) {
        if (validateFile(value) != null)
            return validateFile(value);
        File file = toFile(value);
        if (!(file.isDirectory()))
            return FormValidation.errorWithMarkup("No such directory: <b>" + file + "</b>.");
        return FormValidation.okWithMarkup("Directory <b>" + value + "</b> confirmed.");
    }

    public static FormValidation isFile(String value) {
        if (validateFile(value) != null)
            return validateFile(value);
        File file = toFile(value);
        if (!(file.isFile()))
            return FormValidation.errorWithMarkup("No such file: <b>" + file + "</b>.");
        return FormValidation.okWithMarkup("File <b>" + value + "</b> confirmed.");
    }

    public static FormValidation isFileExecutable(String value) {
        if (validateFile(value) != null)
            return validateFile(value);
        File file = toFile(value);
        if (!(file.isFile() && file.canExecute()))
            return FormValidation.errorWithMarkup(
                    "File <b>" + file + "</b> is not a proper executable.");
        return FormValidation.okWithMarkup("File <b>" + value + "</b> confirmed.");
    }

    public static FormValidation useDefault(String value) {
        if (value.isEmpty()) {
            return FormValidation.okWithMarkup("");
        }
        return FormValidation.ok();
    }

    public static FormValidation isCorrectNumber(String value) {
        if (validateNumber(value) != null)
            return validateNumber(value);
        return FormValidation.ok();
    }

    public static FormValidation isCorrectIdNumber(String value) {
        if (validateNumber(value) != null)
            return validateNumber(value);
        if (value.matches("0+"))
            return FormValidation.errorWithMarkup("All zeros are not allowed in this field.");
        return FormValidation.ok();
    }

    public static FormValidation isValidViseChannel(String value) {
        try {
            ViseChannel vise = new ViseChannel(value);
        } catch (NullPointerException e) {
            return FormValidation.errorWithMarkup("Vise Channel can not be null.");
        } catch (IllegalArgumentException e) {
            return FormValidation.errorWithMarkup("Vise Channel has incorrect format.");
        }
        return FormValidation.ok();
    }

    public static FormValidation isValidEmail(String value) {
        try {
            Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(value);
            if (!matcher.matches()) {
                return FormValidation.error("Invalid email format.");
            }
        } catch (Exception e) {
            return FormValidation.errorWithMarkup(e.getMessage());
        }

        return FormValidation.ok();
    }
}
