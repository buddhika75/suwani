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
import com.divudi.entity.hr.StaffBasics;
import com.divudi.entity.hr.StaffEmployment;
import com.divudi.entity.hr.StaffPaysheetComponent;
import com.divudi.entity.hr.StaffSalaryComponant;
import com.divudi.entity.hr.StaffWorkingDepartment;
import com.divudi.facade.PaysheetComponentFacade;
import com.divudi.facade.StaffEmploymentFacade;
import com.divudi.facade.StaffFacade;
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
public class StaffBasicController implements Serializable {

    private StaffPaysheetComponent current;
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
    @EJB
    private StaffEmploymentFacade staffEmploymentFacade;
    @EJB
    private StaffFacade staffFacade;
    ////////
    @Inject
    private SessionController sessionController;

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

        String sql = "Select s From StaffPaysheetComponent s where s.retired=false"
                + " and s.paysheetComponent.componentType=:tp and s.staff=:st "
                + " and s.toDate>:dt";

        HashMap hm = new HashMap();
        hm.put("tp", PaysheetComponentType.BasicSalary);
        hm.put("st", getCurrent().getStaff());
        hm.put("dt", getCurrent().getFromDate());
        List<StaffPaysheetComponent> tmp = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!tmp.isEmpty()) {
            getRepeatedComponent().addAll(tmp);
        }

        if (!getRepeatedComponent().isEmpty()) {
            UtilityController.addErrorMessage("There is already Basic salay defined please finalize his ending date range or extend it");
            UtilityController.addErrorMessage("To Change Basic salary value got Staff Employment change Staff");
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
        if (getCurrent().getStaff() == null) {
            UtilityController.addErrorMessage("Select Staff");
            return true;
        }

        if (getCurrent().getFromDate() == null) {
            UtilityController.addErrorMessage("Select From Date");
            return true;
        }

        if (checkStaff()) {
            return true;
        }

        return false;
    }

    public void remove() {
        getCurrent().setRetired(true);
        getCurrent().setRetiredAt(new Date());
        getCurrent().setRetirer(getSessionController().getLoggedUser());
        getStaffPaysheetComponentFacade().edit(getCurrent());

        items = null;
        Staff s = getCurrent().getStaff();
        current = null;
        getCurrent(s);
    }

    private void updateExistingSalary() {
        String sql = "Select s From StaffPaysheetComponent s where s.retired=false"
                + " and s.paysheetComponent.componentType=:tp and s.staff=:st "
                + " and s.fromDate<:dt and s.toDate is null";

        HashMap hm = new HashMap();
        hm.put("tp", PaysheetComponentType.BasicSalary);
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

        getCurrent().setCreatedAt(new Date());
        getCurrent().setCreater(getSessionController().getLoggedUser());

        getStaffPaysheetComponentFacade().create(getCurrent());

        updateStaffEmployment();
        updateExistingSalary();

        Staff s = getCurrent().getStaff();
        current = null;
        items = null;
        getCurrent(s);

    }

    private void updateStaffEmployment() {

        if (getCurrent().getStaff().getStaffEmployment() == null) {
            //   //System.out.println("ceate A :");
            StaffEmployment se = new StaffEmployment();
            se.setCreatedAt(new Date());
            se.setCreater(getSessionController().getLoggedUser());
            se.setFromDate(new Date());
            se.setStaff(getCurrent().getStaff());
            getStaffEmploymentFacade().create(se);

            getCurrent().getStaff().setStaffEmployment(se);
            getStaffFacade().edit(getCurrent().getStaff());
        }
        //   //System.out.println("ceate B :");
        createComponent();
        // //System.out.println("ceate C :");
        getStaffEmploymentFacade().edit(getCurrent().getStaff().getStaffEmployment());
    }

    private void createComponent() {

        //System.out.println("ceate D :" + getCurrent().getStaff().getStaffEmployment());     
        StaffBasics tmp = new StaffBasics();
        tmp.setCreatedAt(new Date());
        tmp.setCreater(getSessionController().getLoggedUser());
        tmp.setFromDate(new Date());
        tmp.setBasic(getCurrent());
        tmp.setStaffEmployment(getCurrent().getStaff().getStaffEmployment());
        getCurrent().getStaff().getStaffEmployment().getStaffBasics().add(tmp);

    }

    public void makeNull() {
        current = null;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;
    }

    public void makeNullWithoutFromDate() {
        current = null;
        filteredStaffPaysheet = null;
        items = null;
        selectedStaffComponent = null;
        repeatedComponent = null;

    }

    public void makeItemNul() {
        items = null;
    }

    public List<StaffPaysheetComponent> getItems() {
        if (items == null) {
            String sql = "Select s from StaffPaysheetComponent s"
                    + " where s.retired=false and s.paysheetComponent.componentType=:tp"
                    + " and s.staff=:st order by s.fromDate";
            //and (s.toDate>= :td or s.toDate is null)
            HashMap hm = new HashMap();
            // hm.put("td", getToDate());
            //    hm.put("fd", getFromDate());
            hm.put("st", getCurrent().getStaff());
            hm.put("tp", PaysheetComponentType.BasicSalary);

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

    public List<StaffPaysheetComponent> getItems2() {
        if (items == null) {
            String sql = "Select s from StaffPaysheetComponent s"
                    + " where s.retired=false and s.paysheetComponent.componentType=:tp";
            //and (s.toDate>= :td or s.toDate is null)
            HashMap hm = new HashMap();
            // hm.put("td", getToDate());
            //    hm.put("fd", getFromDate());
            //  hm.put("st", getCurrent().getStaff());
            hm.put("tp", PaysheetComponentType.BasicSalary);

            items = getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);

        }

        return items;
    }

    public PaysheetComponent getBasicCompnent() {
        String sql = "Select pc From PaysheetComponent pc where pc.retired=false and pc.componentType=:tp";
        HashMap hm = new HashMap();
        hm.put("tp", PaysheetComponentType.BasicSalary);

        return getPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

    }

    public StaffBasicController() {
    }

    public StaffPaysheetComponent getCurrent() {
        if (current == null) {
            current = new StaffPaysheetComponent();
            current.setPaysheetComponent(getBasicCompnent());
        }
        return current;
    }

    public StaffPaysheetComponent getCurrent(Staff s) {
        if (current == null) {
            current = new StaffPaysheetComponent();
            current.setPaysheetComponent(getBasicCompnent());
            current.setStaff(s);
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

    public List<StaffPaysheetComponent> getRepeatedComponent() {
        if (repeatedComponent == null) {
            repeatedComponent = new ArrayList<>();
        }
        return repeatedComponent;
    }

    public void setRepeatedComponent(List<StaffPaysheetComponent> repeatedComponent) {
        this.repeatedComponent = repeatedComponent;
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

    public StaffEmploymentFacade getStaffEmploymentFacade() {
        return staffEmploymentFacade;
    }

    public void setStaffEmploymentFacade(StaffEmploymentFacade staffEmploymentFacade) {
        this.staffEmploymentFacade = staffEmploymentFacade;
    }

    public StaffFacade getStaffFacade() {
        return staffFacade;
    }

    public void setStaffFacade(StaffFacade staffFacade) {
        this.staffFacade = staffFacade;
    }
}
