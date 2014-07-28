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
public class StaffPaySheetComponentController implements Serializable {

    private StaffPaysheetComponent current;
    private StaffPaysheetComponent removingEntry;
    ////////////////
    private List<StaffPaysheetComponent> filteredStaff;
    private List<StaffPaysheetComponent> items;
    /////////////////
    @EJB
    private StaffPaysheetComponentFacade staffPaysheetComponentFacade;
    @EJB
    private PaysheetComponentFacade paysheetComponentFacade;
    ////////
    @Inject
    private SessionController sessionController;

    public void onEdit(RowEditEvent event) {
        StaffPaysheetComponent tmp = (StaffPaysheetComponent) event.getObject();
        tmp.setLastEditedAt(new Date());
        tmp.setLastEditor(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(tmp);

    }

    private boolean checkStaff() {

        String sql = "Select s From StaffPaysheetComponent s where s.retired=false"
                + " and s.paysheetComponent=:tp and s.staff=:st and s.toDate>:dt";
        HashMap hm = new HashMap();
        hm.put("tp", getCurrent().getPaysheetComponent());
        hm.put("st", getCurrent().getStaff());
        hm.put("dt", getCurrent().getToDate());
        List<StaffPaysheetComponent> tmp = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!tmp.isEmpty()) {
            UtilityController.addErrorMessage("There is already Component define for " + getCurrent().getStaff().getPerson().getNameWithTitle() + " for this date range u can edit or remove add new one ");
            return true;
        }


        return false;
    }

    private boolean errorCheck() {

        if (checkStaff()) {
            return true;
        }

        if (getCurrent().getPaysheetComponent() == null) {
            UtilityController.addErrorMessage("Check Component Name");
            return true;
        }
        if (getCurrent().getToDate() == null || getCurrent().getFromDate() == null) {
            UtilityController.addErrorMessage("Check Date");
            return true;
        }

        if (getCurrent().getStaff() == null) {
            UtilityController.addErrorMessage("Check Staff");
            return true;
        }

        return false;
    }

    public void remove() {
        getRemovingEntry().setRetired(true);
        getRemovingEntry().setRetiredAt(new Date());
        getRemovingEntry().setRetirer(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(getRemovingEntry());

    }

    public void save() {
        if (errorCheck()) {
            return;
        }

        if (getCurrent().getId() == null) {
            getCurrent().setCreatedAt(new Date());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getStaffPaysheetComponentFacade().create(getCurrent());
        } else {
            getStaffPaysheetComponentFacade().edit(getCurrent());
        }

        makeItemNull();
    }

    public void makeNull() {
        current = null;
        items = null;
        filteredStaff = null;
    }

    public List<StaffPaysheetComponent> getItems() {
        //   //System.out.println("getting Lst ");
        //   //System.out.println("Staff  : " + getCurrent().getStaff());
        if (items == null) {

            String sql = "Select s from StaffPaysheetComponent s"
                    + " where s.retired=false and s.staff=:st ";
            HashMap hm = new HashMap();
            hm.put("st", getCurrent().getStaff());

            items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);
        }

        return items;
    }

    public void makeItemNull() {
        items = null;
    }

    public List<PaysheetComponent> getCompnent() {
        String sql = "Select pc From PaysheetComponent pc where pc.retired=false and "
                + " (pc.componentType!=:tp1 and pc.componentType!=:tp2) ";
        HashMap hm = new HashMap();
        hm.put("tp1", PaysheetComponentType.BasicSalary);
        hm.put("tp2", PaysheetComponentType.LoanInstallemant);
        return getPaysheetComponentFacade().findBySQL(sql, hm);

    }

    public StaffPaySheetComponentController() {
    }

    public StaffPaysheetComponent getCurrent() {
        if (current == null) {
            current = new StaffPaysheetComponent();
        }
        return current;
    }

    public void setCurrent(StaffPaysheetComponent current) {
        items = null;
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

    public List<StaffPaysheetComponent> getFilteredStaff() {
        return filteredStaff;
    }

    public void setFilteredStaff(List<StaffPaysheetComponent> filteredStaff) {
        this.filteredStaff = filteredStaff;
    }

    public StaffPaysheetComponent getRemovingEntry() {
        return removingEntry;
    }

    public void setRemovingEntry(StaffPaysheetComponent removingEntry) {
        this.removingEntry = removingEntry;
    }
}
