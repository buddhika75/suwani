/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.data.hr.PaysheetComponentType;
import com.divudi.entity.Staff;
import com.divudi.entity.hr.PaysheetComponent;
import com.divudi.entity.hr.StaffPaysheetComponent;
import com.divudi.facade.PaysheetComponentFacade;
import com.divudi.facade.StaffPaysheetComponentFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.TemporalType;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class StaffPaySheetComponentAllController implements Serializable {

    private StaffPaysheetComponent current;
    private PaysheetComponent paysheetComponent;
    private Date fromDate;
    private Date toDate;
    private double staffPaySheetComponentValue;
    ////////////////
    private List<StaffPaysheetComponent> filteredStaffPaysheet;
    private List<StaffPaysheetComponent> items;
    private List<StaffPaysheetComponent> repeatedComponent;
    private List<StaffPaysheetComponent> selectedStaffComponent;
    /////////////////
    @EJB
    private StaffPaysheetComponentFacade staffPaysheetComponentFacade;
    @EJB
    private PaysheetComponentFacade paysheetComponentFacade;
    ////////
    @Inject
    private SessionController sessionController;
    @Inject
    private StaffController staffController;

    public void makeItemNull() {
        items = null;
        filteredStaffPaysheet = null;
        selectedStaffComponent = null;
    }

    public void removeAll() {
        for (StaffPaysheetComponent spc : getSelectedStaffComponent()) {
            spc.setRetired(true);
            spc.setRetiredAt(new Date());
            spc.setRetirer(getSessionController().getLoggedUser());
            getStaffPaysheetComponentFacade().edit(spc);
        }

        makeNull();
    }

    private boolean checkStaff() {
        repeatedComponent = null;
        for (Staff s : getStaffController().getSelectedList()) {
            String sql = "Select s From StaffPaysheetComponent s where s.retired=false"
                    + " and s.paysheetComponent=:tp and s.staff=:st and (s.toDate>=:dt or s.toDate is null)";
            HashMap hm = new HashMap();
            hm.put("tp", getPaysheetComponent());
            hm.put("st", s);
            hm.put("dt", getToDate());
            List<StaffPaysheetComponent> tmp = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

            if (!tmp.isEmpty()) {
                getRepeatedComponent().addAll(tmp);
            }

        }

        if (!getRepeatedComponent().isEmpty()) {
            UtilityController.addErrorMessage("There is already " + getPaysheetComponent().getName() + " defined please finalize his ending date range && add new one or remove");
            items = null;
            return true;
        }

        return false;
    }

    public void onEdit(RowEditEvent event) {
        StaffPaysheetComponent tmp = (StaffPaysheetComponent) event.getObject();
        tmp.setLastEditedAt(new Date());
        tmp.setLastEditor(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(tmp);

    }

    private boolean errorCheck() {

        if (getStaffController().getSelectedList() == null) {
            UtilityController.addErrorMessage("Please select staff");
            return true;
        }

        if (checkStaff()) {
            return true;
        }

        if (getFromDate() == null) {
            UtilityController.addErrorMessage("Check Date");
            return true;
        }

        return false;
    }

    public void remove() {
        getCurrent().setRetired(true);
        getCurrent().setRetiredAt(new Date());
        getCurrent().setRetirer(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(getCurrent());

        current = null;
        items = null;
    }

    private void updateExistingComponent() {
        String sql = "Select s From StaffPaysheetComponent s where s.retired=false"
                + " and s.paysheetComponent=:tp and s.staff=:st "
                + " and s.fromDate<:dt and s.toDate is null";

        HashMap hm = new HashMap();
        hm.put("tp", getPaysheetComponent());
        hm.put("st", getCurrent().getStaff());
        hm.put("dt", getCurrent().getFromDate());
        List<StaffPaysheetComponent> tmp = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        for (StaffPaysheetComponent ss : tmp) {
            ss.setToDate(getCurrent().getFromDate());
            getStaffPaysheetComponentFacade().edit(ss);
        }
    }

    public void save() {

        if (errorCheck()) {
            return;
        }

        for (Staff s : getStaffController().getSelectedList()) {
            StaffPaysheetComponent spc = new StaffPaysheetComponent();
            spc.setCreatedAt(new Date());
            spc.setCreater(getSessionController().getLoggedUser());
            spc.setFromDate(getFromDate());
            spc.setToDate(getToDate());
            spc.setPaysheetComponent(getPaysheetComponent());
            spc.setStaff(s);
            spc.setStaffPaySheetComponentValue(getStaffPaySheetComponentValue());

            getStaffPaysheetComponentFacade().create(spc);
        }

        updateExistingComponent();

        makeNullWithout();
    }

    public void makeNull() {
        current = null;
        paysheetComponent = null;
        fromDate = null;
        toDate = null;
        staffPaySheetComponentValue = 0.0;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;
        getStaffController().setSelectedList(null);
        getStaffController().setFilteredStaff(null);
    }

    public void makeNullWithout() {
        current = null;
        //    paysheetComponent = null;
        //   fromDate = null;
        //  toDate = null;
        staffPaySheetComponentValue = 0.0;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;
        getStaffController().setSelectedList(null);
        getStaffController().setFilteredStaff(null);
    }

    public List<StaffPaysheetComponent> getItems() {
        if (items == null) {
            String sql = "Select s from StaffPaysheetComponent s"
                    + " where s.retired=false and s.paysheetComponent=:tp";

            HashMap hm = new HashMap();
            //   hm.put("current", new Date());
            hm.put("tp", getPaysheetComponent());

            items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

            if (!getRepeatedComponent().isEmpty()) {
                for (StaffPaysheetComponent sp : items) {
                    for (StaffPaysheetComponent err : getRepeatedComponent()) {
                        if (sp.getId() == err.getId()) {
                            sp.setExist(true);
                            //System.out.println("settin");
                        }
                    }
                }
            }
        }

        return items;
    }

    public List<PaysheetComponent> getCompnent() {
        String sql = "Select pc From PaysheetComponent pc "
                + " where pc.retired=false"
                + " and pc.componentType!=:tp1 "
                + " and pc.componentType!=:tp2 "
                + " and pc.componentType!=:tp3 "
                + " and pc.componentType!=:tp4 ";
        HashMap hm = new HashMap();
        hm.put("tp1", PaysheetComponentType.BasicSalary);
        hm.put("tp2", PaysheetComponentType.LoanInstallemant);
        hm.put("tp3", PaysheetComponentType.OT);
        hm.put("tp4", PaysheetComponentType.ExtraDuty);

        return getPaysheetComponentFacade().findBySQL(sql, hm);

    }

    public StaffPaySheetComponentAllController() {
    }

    public StaffPaysheetComponent getCurrent() {
        if (current == null) {
            current = new StaffPaysheetComponent();
        }
        return current;
    }

    public void setCurrent(StaffPaysheetComponent current) {
        this.current = current;
    }

    public StaffPaysheetComponentFacade getStaffPaysheetComponentFacade() {
        return staffPaysheetComponentFacade;
    }

    public void setStaffPaysheetComponentFacade(StaffPaysheetComponentFacade staffPaysheetComponentFacade) {
        this.staffPaysheetComponentFacade = staffPaysheetComponentFacade;
    }

    public PaysheetComponentFacade getPaysheetComponentFacade() {
        return paysheetComponentFacade;
    }

    public void setPaysheetComponentFacade(PaysheetComponentFacade paysheetComponentFacade) {
        this.paysheetComponentFacade = paysheetComponentFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public List<StaffPaysheetComponent> getFilteredStaffPaysheet() {
        return filteredStaffPaysheet;
    }

    public void setFilteredStaffPaysheet(List<StaffPaysheetComponent> filteredStaffPaysheet) {
        this.filteredStaffPaysheet = filteredStaffPaysheet;
    }

    public PaysheetComponent getPaysheetComponent() {
        return paysheetComponent;
    }

    public void setPaysheetComponent(PaysheetComponent paysheetComponent) {
        this.paysheetComponent = paysheetComponent;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public double getStaffPaySheetComponentValue() {
        return staffPaySheetComponentValue;
    }

    public void setStaffPaySheetComponentValue(double staffPaySheetComponentValue) {
        this.staffPaySheetComponentValue = staffPaySheetComponentValue;
    }

    public List<StaffPaysheetComponent> getRepeatedComponent() {
        if (repeatedComponent == null) {
            repeatedComponent = new ArrayList<>();
        }
        return repeatedComponent;
    }

    public void setRepeatedComponent(List<StaffPaysheetComponent> repeatedComponent) {
        this.repeatedComponent = repeatedComponent;
    }

    public StaffController getStaffController() {
        return staffController;
    }

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    public List<StaffPaysheetComponent> getSelectedStaffComponent() {
        if (selectedStaffComponent == null) {
            selectedStaffComponent = new ArrayList<>();
        }
        return selectedStaffComponent;
    }

    public void setSelectedStaffComponent(List<StaffPaysheetComponent> selectedStaffComponent) {
        this.selectedStaffComponent = selectedStaffComponent;
    }
}
