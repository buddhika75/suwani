/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.SessionController;
import com.divudi.data.dataStructure.ShiftTable;
import com.divudi.ejb.CommonFunctions;
import com.divudi.ejb.HumanResourceBean;
import com.divudi.entity.Staff;
import com.divudi.entity.hr.Roster;
import com.divudi.entity.hr.StaffShift;
import com.divudi.facade.StaffShiftFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class ShiftTableController implements Serializable {

    Date fromDate;
    Date toDate;
    Long dateRange;
    Roster roster;
    List<ShiftTable> shiftTables;
    @EJB
    HumanResourceBean humanResourceBean;
    @EJB
    CommonFunctions commonFunctions;
    @EJB
    StaffShiftFacade staffShiftFacade;
    @Inject
    SessionController sessionController;
    @Inject
    ShiftController shiftController;

    //FUNTIONS
    public void makeNull() {
        fromDate = null;
        toDate = null;
        dateRange = 0l;
        roster = null;
        shiftTables = null;
    }

    private boolean errorCheck() {
        if (getFromDate() == null || getToDate() == null) {
            return true;
        }

        return false;
    }

    private void saveStaffShift() {
        for (ShiftTable st : shiftTables) {
            for (StaffShift ss : st.getStaffShift()) {
//                if (ss.getShift() == null) {
//                    continue;
//                }

                ss.calShiftStartEndTime();

                if (ss.getId() == null) {
                    getStaffShiftFacade().create(ss);
                }
                ss.setPreviousStaffShift(getHumanResourceBean().calPrevStaffShift(ss));
                ss.setNextStaffShift(getHumanResourceBean().calFrwStaffShift(ss));
                getStaffShiftFacade().edit(ss);
            }
        }
    }

    public void save() {
        if (shiftTables == null) {
            return;
        }

        saveStaffShift();
        saveStaffShift();
    }

    public void createShiftTable() {
        if (errorCheck()) {
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

            for (Staff stf : getHumanResourceBean().fetchStaff(getRoster())) {
                List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, stf);
//                System.err.println("1");
                if (staffShifts.isEmpty()) {
//                    System.err.println("2");
                    for (int i = getRoster().getShiftPerDay(); i > 0; i--) {
//                        System.err.println("3");
                        StaffShift ss = new StaffShift();
                        ss.setStaff(stf);
                        ss.setShiftDate(nowDate);
                        netT.getStaffShift().add(ss);
                    }
                } else {
//                    System.err.println("4");
                    for (StaffShift ss : staffShifts) {
//                        System.err.println("6");
                        netT.getStaffShift().add(ss);
                    }
                }

            }

            System.err.println("BOOL " + netT.getFlag());
            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);
    }

    public void selectRosterLstener() {
        makeTableNull();
        getShiftController().setCurrentRoster(roster);

    }

    public void makeTableNull() {
        shiftTables = null;
    }

    //GETTER AND SETTERS
    public ShiftController getShiftController() {
        return shiftController;
    }

    public void setShiftController(ShiftController shiftController) {
        this.shiftController = shiftController;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public HumanResourceBean getHumanResourceBean() {
        return humanResourceBean;
    }

    public void setHumanResourceBean(HumanResourceBean humanResourceBean) {
        this.humanResourceBean = humanResourceBean;
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    public Long getDateRange() {
        return dateRange;
    }

    public void setDateRange(Long dateRange) {
        this.dateRange = dateRange;
    }

    public List<ShiftTable> getShiftTables() {
        return shiftTables;
    }

    public void setShiftTables(List<ShiftTable> shiftTables) {
        this.shiftTables = shiftTables;
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

    public StaffShiftFacade getStaffShiftFacade() {
        return staffShiftFacade;
    }

    public void setStaffShiftFacade(StaffShiftFacade staffShiftFacade) {
        this.staffShiftFacade = staffShiftFacade;
    }

}
