package com.ericsson.becrux.base.common.configuration;

import hudson.util.FormValidation;
import org.junit.Test;

import java.text.Normalizer;
import java.util.jar.JarFile;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link FormValidator}
 */
public class FormValidatorTest {
    private void assertHtmlMarkup(String renderedHtml) {
        assertTrue(renderedHtml.contains((CharSequence)"does not exist") ||
                   renderedHtml.contains((CharSequence)"It is recommended"));
    }

    @Test
    public void testValidationEmail() {
        String validEmail = "dummy@gmail.com";
        FormValidation validate = FormValidator.isValidEmail(validEmail);
        assertTrue(validate.kind.equals(FormValidation.ok().kind));

        String invalidEmail = "dummygmail.com";
        validate = FormValidator.isValidEmail(invalidEmail);
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
    }

    @Test
    public void testIsEmptyEmptyInput() {
        FormValidation validate = FormValidator.isEmpty("");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"This field cannot be empty"));
    }

    @Test
    public void testIsEmptyValidInput() {
        FormValidation validate = FormValidator.isEmpty("Test");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
    }

    @Test
    public void testIsDirEmptyInput() {
        FormValidation validate = FormValidator.isDir("");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertHtmlMarkup(validate.renderHtml());
    }

    @Test
    public void testIsDirNonDirInput() throws IOException {
        File file = new File("test.txt");
        file.createNewFile();
        FormValidation validate = FormValidator.isDir(file.getAbsolutePath());
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"No such directory"));
        file.delete();
    }

    @Test
    public void testIsDirValidInput() throws IOException {
        File file = new File("./testdir/");
        file.mkdir();
        FormValidation validate = FormValidator.isDir(file.getAbsolutePath());
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"confirmed"));
        file.delete();
    }

    @Test
    public void testIsFileEmptyInput() {
        FormValidation validate = FormValidator.isFile("");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertHtmlMarkup(validate.renderHtml());
    }

    @Test
    public void testIsFileNonFileInput() throws IOException {
        File file = new File("./testdir/");
        file.mkdir();
        FormValidation validate = FormValidator.isFile(file.getAbsolutePath());
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains("No such file"));
        file.delete();
    }

    @Test
    public void testIsFileValidInput() throws IOException {
        File file = new File("test.txt");
        file.createNewFile();
        FormValidation validate = FormValidator.isFile(file.getAbsolutePath());
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains("confirmed"));
        file.delete();
    }

    @Test
    public void testIsFileExecutableEmptyInput() {
        FormValidation validate = FormValidator.isFileExecutable("");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertHtmlMarkup(validate.renderHtml());
    }

   /* @Test
    public void testIsFileExecutableNonExecFile() throws IOException {
        File file = new File("testnonexec.tmp");
        file.createNewFile();
        file.deleteOnExit();
        FormValidation validate = FormValidator.isFileExecutable(file.getAbsolutePath());
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains("is not a proper executable"));
    }*/

    @Test
    public void testIsFileExecutableExecFile() throws IOException {
        File file = new File("testexec.bat");
        file.createNewFile();
        file.deleteOnExit();
        file.setExecutable(true, false);
        FormValidation validate = FormValidator.isFileExecutable(file.getAbsolutePath());
        assertTrue(validate.kind.equals(FormValidation.ok().kind));

        assertTrue(validate.renderHtml().contains("confirmed"));
    }

    @Test
    public void testUseDefault() {
        FormValidation validate = FormValidator.useDefault("");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().equals(""));
        validate = FormValidator.useDefault("Test");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
    }

    @Test
    public void testIsCorrectNumber() {
        FormValidation validate = FormValidator.isCorrectNumber("One");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"Only digits are allowed in this field."));
        validate = FormValidator.isCorrectNumber("1");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
    }

    @Test
    public void testIsCorrectIdNumber() {
        FormValidation validate = FormValidator.isCorrectIdNumber("One");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"Only digits are allowed in this field."));
        validate = FormValidator.isCorrectIdNumber("000");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"All zeros are not allowed in this field."));
        validate = FormValidator.isCorrectNumber("1");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
    }

    @Test
    public void testIsValidViseChannel() {
        FormValidation validate = FormValidator.isValidViseChannel(null);
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"Vise Channel can not be null."));
        validate = FormValidator.isValidViseChannel("test");
        assertTrue(!validate.kind.equals(FormValidation.ok().kind));
        assertTrue(validate.renderHtml().contains((CharSequence)"Vise Channel has incorrect format."));
        validate = FormValidator.isCorrectNumber("0308");
        assertTrue(validate.kind.equals(FormValidation.ok().kind));
    }
}
