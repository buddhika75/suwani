/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.common;

import java.util.TimeZone;
import com.divudi.facade.MessageFacade;
import com.divudi.entity.Message;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.inject.Named;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class MessageController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private MessageFacade ejbFacade;
    List<Message> selectedItems;
    private Message current;
    private List<Message> items = null;

    public String contactUs() {
        current = new Message();
        return "contact-us";
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            String username = sessionController.applicationPreference.getGmailUserName();
            String password = sessionController.applicationPreference.getGmailPassword();
            return new PasswordAuthentication(username, password);
        }
    }

    public String sendMessage() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getInstance(props, auth);
        try {
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getCurrent().getSenderEmail()));
            message.setRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(getSessionController().getApplicationPreference().getContactEmail()));
            message.setSubject(getCurrent().getSubject());
            message.setText(getCurrent().getMessageBody());
            Transport.send(message);
            JsfUtil.addSuccessMessage("Message Sent successfully");
            getFacade().create(current);
            current = null;
            return contactUs();
        } catch (MessagingException e) {
            JsfUtil.addErrorMessage("Message sending failed. Please try some other contact method. \n" + e.getMessage());
            return "";
        }

    }

    public void saveSelected() {
        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(getCurrent());
            UtilityController.addSuccessMessage("Updated");
        } else {
            getCurrent().setCreatedAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getFacade().create(getCurrent());
            UtilityController.addSuccessMessage("Saved");
        }
    }

    public MessageFacade getEjbFacade() {
        return ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public MessageController() {
    }

    public Message getCurrent() {
        if (current == null) {
            current = new Message();
        }
        return current;
    }

    public void setCurrent(Message current) {
        this.current = current;
    }

    public void delete() {
        if (getCurrent() != null) {
            getCurrent().setRetired(true);
            getCurrent().setRetiredAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            getCurrent().setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(getCurrent());
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing To Delete");
        }
        current = null;
        getCurrent();
    }

    private MessageFacade getFacade() {
        return ejbFacade;
    }

    public List<Message> getItems() {
        items = getFacade().findAll(true);
        return items;
    }

    @FacesConverter("messageConverter")
    public static class MessageConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MessageController controller = (MessageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "messageController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Message) {
                Message o = (Message) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + MessageController.class.getName());
            }
        }
    }

    /**
     *
     */
    @FacesConverter(forClass = Message.class)
    public static class MessageControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MessageController controller = (MessageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "messageController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Message) {
                Message o = (Message) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + MessageController.class.getName());
            }
        }
    }
}
