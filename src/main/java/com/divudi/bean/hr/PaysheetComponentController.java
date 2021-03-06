/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.data.hr.PaysheetComponentType;
import com.divudi.facade.PaysheetComponentFacade;
import com.divudi.entity.hr.PaysheetComponent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Named;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.TemporalType;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class PaysheetComponentController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private PaysheetComponentFacade ejbFacade;
    List<PaysheetComponent> selectedItems;
    private PaysheetComponent current;    
    private List<PaysheetComponent> items = null;
    String selectText = "";

   

    public List<PaysheetComponent> getSelectedItems() {
        selectedItems = getFacade().findBySQL("select c from PaysheetComponent c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        return selectedItems;
    }

    public List<PaysheetComponent> completePaysheetComponent(String qry) {
        List<PaysheetComponent> a = null;
        if (qry != null) {
            a = getFacade().findBySQL("select c from PaysheetComponent c where c.retired=false and upper(c.name) like '%" + qry.toUpperCase() + "%' order by c.name");
        }
        if (a == null) {
            a = new ArrayList<PaysheetComponent>();
        }
        return a;
    }

    public void prepareAdd() {
        current = new PaysheetComponent();
    }

    public void setSelectedItems(List<PaysheetComponent> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    private boolean checkComponent() {
        String sql = "";
        sql = "Select s From PaysheetComponent s "
                + " where s.retired=false "
                + " and s.componentType=:ct ";
        HashMap hm = new HashMap();
        switch (getCurrent().getComponentType()) {
            case BasicSalary:
                hm.put("ct", PaysheetComponentType.BasicSalary);
                break;
            case OT:
                hm.put("ct", PaysheetComponentType.OT);
                break;
            case ExtraDuty:
                hm.put("ct", PaysheetComponentType.ExtraDuty);
                break;
            case No_Pay_Deduction:
                hm.put("ct", PaysheetComponentType.No_Pay_Deduction);
                break;
            default:
                hm.put("ct", null);
        }

        PaysheetComponent tmp = getEjbFacade().findFirstBySQL(sql, hm);

        if (tmp != null) {
            return true;
        } else {
            return false;
        }

    }

    public void saveSelected() {
        if (checkComponent()) {
            UtilityController.addErrorMessage("This Component Type Already Exist");
            return;
        }


        if (getCurrent().getComponentType() == null) {
            UtilityController.addErrorMessage("Pls Select Compnent Type");
            return;
        }

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {

            getFacade().edit(current);
            UtilityController.addSuccessMessage("savedOldSuccessfully");
        } else {
            current.setCreatedAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("savedNewSuccessfully");
        }
        recreateModel();
        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public PaysheetComponentFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PaysheetComponentFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public PaysheetComponentController() {
    }

    public PaysheetComponent getCurrent() {
        if (current == null) {
            current = new PaysheetComponent();
        }
        return current;
    }

    public void setCurrent(PaysheetComponent current) {
        this.current = current;
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("DeleteSuccessfull");
        } else {
            UtilityController.addSuccessMessage("NothingToDelete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private PaysheetComponentFacade getFacade() {
        return ejbFacade;
    }

    public List<PaysheetComponent> getItems() {
        items = getFacade().findAll("name", true);
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = PaysheetComponent.class)
    public static class PaysheetComponentConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PaysheetComponentController controller = (PaysheetComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "paysheetComponentController");
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
            if (object instanceof PaysheetComponent) {
                PaysheetComponent o = (PaysheetComponent) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PaysheetComponentController.class.getName());
            }
        }
    }

    @FacesConverter("paysheetComponentCon")
    public static class PaysheetComponentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PaysheetComponentController controller = (PaysheetComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "paysheetComponentController");
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
            if (object instanceof PaysheetComponent) {
                PaysheetComponent o = (PaysheetComponent) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PaysheetComponentController.class.getName());
            }
        }
    }
}
