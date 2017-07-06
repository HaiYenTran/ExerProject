package com.ericsson.becrux.base.common.utils;

import com.ericsson.becrux.base.common.testexec.TestStatus;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Class used for sending e-mail messages.
 */
public class EmailSender {

    private static final String smtpParamName = "mail.smtp.host";
    private static final String defaultHost = "localhost";
    private static final String defaultSender = "iles@ciengine.com";
    private static final String charset = "utf-8";
    private static final String mimeSubtype = "html";

    private String from;
    private Session session;

    /**
     * Creates an object and set the sender address ({@value #defaultSender} is used)
     * and SMTP host ({@value #defaultHost} is used)
     */
    public EmailSender() {
        this(null, null);
    }

    /**
     * Creates an object and set the sender address and SMTP host
     * ({@value #defaultHost} is used).
     *
     * @param from sender e-mail address; if it is null, the{@value #defaultSender} will be used
     */
    public EmailSender(String from) {
        this(null, from);
    }

    /**
     * Creates an object and set the sender address and SMTP host.
     *
     * @param host SMTP host address; if it is null, the {@value #defaultHost} will be used
     * @param from sender e-mail address; if it is null, the {@value #defaultSender} will be used
     */
    public EmailSender(String host, String from) {
        this.from = (from == null) ? defaultSender : from;
        Properties properties = System.getProperties();
        properties.setProperty(smtpParamName, (host == null) ? defaultHost : host);
        this.session = Session.getDefaultInstance(properties);
    }

    /**
     * Send e-mail notification.
     *
     * @param subject e-mail subject
     * @param body    e-mail content (encoding: {@value #charset}, format: {@value #mimeSubtype})
     * @param to      collection of 'TO' recipients
     * @param cc      collection of 'CC' recipients
     * @param bcc     collection of 'BCC' recipients
     * @throws MessagingException when no recipients specified or error during sending occurred
     */
    public void send(String subject, String body, Collection<String> to, Collection<String> cc, Collection<String> bcc)
            throws MessagingException {

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setSubject((subject == null) ? "" : subject);
        message.setText((body == null) ? "" : body, charset, mimeSubtype);

        if (to != null)
            for (String address : to)
                if (address != null)
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
        if (cc != null)
            for (String address : cc)
                if (address != null)
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(address));
        if (bcc != null)
            for (String address : bcc)
                if (address != null)
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(address));

        if (message.getAllRecipients() == null || message.getAllRecipients().length == 0)
            throw new MessagingException("No recipients specified.");
        else
            Transport.send(message);
    }

    /**
     * Send e-mail notification.
     *
     * @param subject e-mail subject
     * @param body    e-mail content (encoding: {@value #charset}, format: {@value #mimeSubtype})
     * @param to      collection of recipients
     * @throws MessagingException when no recipients specified or error during sending occurred
     */
    public void send(String subject, String body, Collection<String> to) throws MessagingException {
        send(subject, body, to, null, null);
    }

    /**
     * Send e-mail notification.
     *
     * @param subject e-mail subject
     * @param body    e-mail content (encoding: {@value #charset}, format: {@value #mimeSubtype})
     * @param to      recipients
     * @throws MessagingException when no recipients specified or error during sending occurred
     */
    public void send(String subject, String body, String... to) throws MessagingException {
        send(subject, body, (to == null) ? null : Arrays.asList(to));
    }

    // TODO: need refactor for more generic
//    /**
//     * Send e-mail notification basing on BTF value and INT test cases results.
//     *
//     * @param event   BTF value
//     * @param results INT test cases results map
//     * @param to      collection of 'TO' recipients
//     * @param cc      collection of 'CC' recipients
//     * @param bcc     collection of 'BCC' recipients
//     * @throws MessagingException when no recipients specified or error during sending occurred
//     */
//    public void send(BTFEvent event, Map<DownstreamTestExecJob.TestStatus, Integer> results, Collection<String> to, Collection<String> cc, Collection<String> bcc)
//            throws MessagingException {
//        send(generateSubject(event), generateBody(event, results), to, cc, bcc);
//    }
//
//    /**
//     * Send e-mail notification basing on BTF value and INT test cases results.
//     *
//     * @param event   BTF value
//     * @param results INT test cases results map
//     * @param to      collection of recipients
//     * @throws MessagingException when no recipients specified or error during sending occurred
//     */
//    public void send(BTFEvent event, Map<DownstreamTestExecJob.TestStatus, Integer> results, Collection<String> to) throws MessagingException {
//        send(event, results, to, null, null);
//    }
//
//    /**
//     * Send e-mail notification with ILES CI loop results.
//     * The subject and body will be generated basing on BTF value and INT test cases results.
//     *
//     * @param event   BTF value
//     * @param results INT test cases results map
//     * @param to      recipients
//     * @throws MessagingException when no recipients specified or error during sending occurred
//     */
//    public void send(BTFEvent event, Map<DownstreamTestExecJob.TestStatus, Integer> results, String... to) throws MessagingException {
//        send(event, results, (to == null) ? null : Arrays.asList(to));
//    }
//
//	/*
//     *
//	 * E-MAIL BODY CONTENT GENERATION
//	 *
//	 */
//
//    private String generateSubject(BTFEvent event) {
//        // generate subject of e-mail notification basing on BTF value phase status
//        return "Baseline loop finished: " + event.getPhaseStatus();
//    }
//
//    private String generateBody(BTFEvent event, Map<DownstreamTestExecJob.TestStatus, Integer> results) {
//        // generate body of e-mail notification basing on BTF content and test results
//        return body(
//                new StringBuilder()
//                        .append(buildBaselineSection(event.getProducts(), event.getBaselines()))
//                        .append(buildTestExecSection(results))
//                        .append(buildLinksList(event.getResults()))
//        ).toString();
//    }

    private StringBuilder buildBaselineSection(List<String> products, List<String> baselines) {
        // section with components baseline (name and version) tested in the ILES CI loop
        StringBuilder builder = new StringBuilder();
        List<StringBuilder> elements = new ArrayList<>();

        if (products != null && baselines != null
                && !products.isEmpty() && !baselines.isEmpty()
                && products.size() == baselines.size()) {

            for (int i = 0; i < products.size(); i++)
                elements.add(new StringBuilder()
                        .append(products.get(i)).append(" ")
                        .append(baselines.get(i)));

            builder.append(head("Nodes baseline:")).append(list(elements));
        }
        return builder;
    }

    private StringBuilder buildTestExecSection(Map<TestStatus, Integer> results) {
        // section with test results (map from TestExecDownstreamJob object)
        StringBuilder builder = new StringBuilder();
        List<StringBuilder> elements = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            results.forEach((k, v) -> elements.add(
                    new StringBuilder().append(k).append(": ").append(v)));
            builder.append(head("Test execution result:")).append(list(elements));
        }
        return builder;
    }

    private StringBuilder buildLinksList(List<String> links) {
        // section with links from BTF value ('results' field)
        StringBuilder builder = new StringBuilder();
        List<StringBuilder> elements = new ArrayList<>();

        if (links != null && !links.isEmpty() && links.stream().anyMatch(s -> s != null)) {
            links.stream().filter(s -> s != null)
                    .forEach(s -> elements.add(new StringBuilder().append(link(s))));
            builder.append(head("See more info in links:")).append(list(elements));
        }
        return builder;
    }

    /* 
     * 
     * HTML UTILS
     * 
     */

    private StringBuilder list(List<StringBuilder> elements) {
        // HTML list
        StringBuilder builder = new StringBuilder();

        if (elements != null && !elements.isEmpty()) {
            builder.append("<ul>");
            for (StringBuilder string : elements)
                builder.append("<li>").append(string).append("</li>");
            builder.append("</ul>");
        }
        return builder;
    }

    private StringBuilder head(String text) {
        // HTML head of section in e-mail content
        return new StringBuilder()
                .append("<p><b>")
                .append(text)
                .append("</b></p>");
    }

    private String br() {
        // HTML new line
        return "<br/>";
    }

    private StringBuilder body(StringBuilder content) {
        // HTML body with the hard-coded footer at the end
        return new StringBuilder().append("<html><body>")
                .append(content).append(br())
                .append("_________________________").append(br())
                .append("ILES CI Engine").append("</body></html>");
    }

    private StringBuilder link(String link, String text) {
        // HTML link (if text is null or empty, text will be the same as link)
        if (text == null || text.isEmpty())
            text = link;
        return new StringBuilder()
                .append("<a href=\"").append(link).append("\">")
                .append(text).append("</a>");
    }

    private StringBuilder link(String link) {
        // HTML link
        return link(link, link);
    }

}