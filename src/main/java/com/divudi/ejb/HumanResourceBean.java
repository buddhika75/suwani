/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.ejb;

import com.divudi.data.dataStructure.DateRange;
import com.divudi.data.dataStructure.ExtraDutyCount;
import com.divudi.data.dataStructure.OtNormalSpecial;
import com.divudi.data.hr.DayType;
import com.divudi.data.dataStructure.StaffMonthDay;
import com.divudi.data.hr.ExtraDutyType;
import com.divudi.data.hr.FingerPrintRecordType;
import com.divudi.data.hr.PaysheetComponentType;
import com.divudi.data.hr.Times;
import com.divudi.entity.Staff;
import com.divudi.entity.WebUser;
import com.divudi.entity.hr.FingerPrintRecord;
import com.divudi.entity.hr.PaysheetComponent;
import com.divudi.entity.hr.PhDate;
import com.divudi.entity.hr.Roster;
import com.divudi.entity.hr.Shift;
import com.divudi.entity.hr.StaffLeave;
import com.divudi.entity.hr.StaffPaysheetComponent;
import com.divudi.entity.hr.StaffSalary;
import com.divudi.entity.hr.StaffSalaryComponant;
import com.divudi.entity.hr.StaffShift;
import com.divudi.facade.FingerPrintRecordFacade;
import com.divudi.facade.PaysheetComponentFacade;
import com.divudi.facade.PhDateFacade;
import com.divudi.facade.ShiftFacade;
import com.divudi.facade.StaffFacade;
import com.divudi.facade.StaffLeaveFacade;
import com.divudi.facade.StaffPaysheetComponentFacade;
import com.divudi.facade.StaffSalaryComponantFacade;
import com.divudi.facade.StaffSalaryFacade;
import com.divudi.facade.StaffShiftFacade;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TemporalType;

/**
 *
 * @author safrin
 */
@Stateless
public class HumanResourceBean {

    private LinkedList<Staff> staffLink;
    @EJB
    private StaffPaysheetComponentFacade staffPaysheetComponentFacade;
    @EJB
    private PaysheetComponentFacade paysheetComponentFacade;
    @EJB
    private StaffSalaryFacade staffSalaryFacade;
    @EJB
    private StaffFacade staffFacade;
    @EJB
    private StaffShiftFacade staffShiftFacade;
    @EJB
    private PhDateFacade phDateFacade;
    @EJB
    private StaffLeaveFacade staffLeaveFacade;
    @EJB
    private FingerPrintRecordFacade fingerPrintRecordFacade;
    @EJB
    ShiftFacade shiftFacade;
    ///////////////////
    @EJB
    private CommonFunctions commonFunctions;
    @EJB
    private FinalVariables finalVariables;

//    public StaffShift findInTime(FingerPrintRecord r) {
//        Date recordedDate = r.getRecordTimeStamp();
//        Calendar min = Calendar.getInstance();
//        min.setTime(recordedDate);
//        min.add(Calendar.HOUR, -1);
//
//        Calendar max = Calendar.getInstance();
//        max.setTime(recordedDate);
//        max.add(Calendar.HOUR, 2);
//
//        Map m = new HashMap();
//        m.put("f", min.getTime());
//        m.put("t", max.getTime());
//        m.put("s", r.getStaff());
//        m.put("ftp", FingerPrintRecordType.Varified);
//        String sql = "Select ss from StaffShift ss "
//                + " where ss.retired=false "
//                + " and ss.staff=:s "
//                + " and ss.previousStaffShift is null "
//                + " and ss.fingerPrintRecordType=:ftp "
//                + " and ss.shiftStartTime between :f and :t";
//        StaffShift ss = getStaffShiftFacade().findFirstBySQL(sql, m, TemporalType.TIMESTAMP);
//        //   System.err.println("Fetch Staff 1 : " + ss);
//        return ss;
//    }
    public FingerPrintRecord findInTimeRecord(StaffShift staffShift) {
        if (staffShift == null) {
            return null;
        }

        if (staffShift.getPreviousStaffShift() != null) {
            return null;
        }

        if (staffShift.getShiftStartTime() == null) {
            return null;
        }

        Date startTime = staffShift.getShiftStartTime();
        Calendar min = Calendar.getInstance();
        min.setTime(startTime);
        min.add(Calendar.HOUR, -2);

        Calendar max = Calendar.getInstance();
        max.setTime(startTime);
        max.add(Calendar.HOUR, 1);

        Map m = new HashMap();
        m.put("f", min.getTime());
        m.put("t", max.getTime());
        m.put("s", staffShift.getStaff());
        m.put("ftp", FingerPrintRecordType.Varified);
        String sql = "Select ss from FingerPrintRecord ss "
                + " where ss.retired=false "
                + " and ss.staff=:s "
                + " and ss.loggedRecord is not null"
                + " and ss.fingerPrintRecordType=:ftp "
                + " and ss.recordTimeStamp between :f and :t";
        FingerPrintRecord ss = getFingerPrintRecordFacade().findFirstBySQL(sql, m, TemporalType.TIMESTAMP);
        System.err.println("findInTimeRecordWithOutDayOffSleeping : " + ss);
        return ss;
    }

    public FingerPrintRecord findOutTimeRecord(StaffShift staffShift) {
        if (staffShift == null) {
            return null;
        }

        if (staffShift.getNextStaffShift() != null) {
            return null;
        }

        if (staffShift.getShiftEndTime() == null) {
            return null;
        }

        Date endTime = staffShift.getShiftEndTime();
        Calendar min = Calendar.getInstance();
        min.setTime(endTime);
        min.add(Calendar.HOUR, -1);

        Calendar max = Calendar.getInstance();
        max.setTime(endTime);
        max.add(Calendar.HOUR, 2);

        Map m = new HashMap();
        m.put("f", min.getTime());
        m.put("t", max.getTime());
        m.put("s", staffShift.getStaff());
        m.put("ftp", FingerPrintRecordType.Varified);
        String sql = "Select ss from FingerPrintRecord ss "
                + " where ss.retired=false "
                + " and ss.staff=:s"
                + " and ss.loggedRecord is not null"
                + " and ss.fingerPrintRecordType=:ftp "
                + " and ss.recordTimeStamp between :f and :t";
        FingerPrintRecord ss = getFingerPrintRecordFacade().findFirstBySQL(sql, m, TemporalType.TIMESTAMP);
        System.err.println("findOutTimeRecordWithoutDayOffSleeping : " + ss);
        return ss;
    }

    public List<StaffShift> fetchStaffShift(Date date, Staff staff) {
        Map m = new HashMap();
        m.put("date", date);
        m.put("s", staff);
        String sql = "Select ss from StaffShift ss "
                + " where ss.retired=false "
                + " and ss.staff=:s "
                + " and ss.shiftDate=:date";

        return getStaffShiftFacade().findBySQL(sql, m, TemporalType.DATE);
    }

    private StaffShift findOutTime(FingerPrintRecord r) {
        Date recordedDate = r.getRecordTimeStamp();
        Calendar min = Calendar.getInstance();
        min.setTime(recordedDate);
        min.add(Calendar.HOUR, -1);

        Calendar max = Calendar.getInstance();
        max.setTime(recordedDate);
        max.add(Calendar.HOUR, 2);

        Map m = new HashMap();
        m.put("f", min.getTime());
        m.put("t", max.getTime());
        m.put("s", r.getStaff());

        String sql = "Select ss from StaffShift ss "
                + " where ss.retired=false "
                + " and ss.staff=:s "
                + " and ss.nextStaffShift is null "
                + " and  ss.shiftEndTime between :f and :t";
        StaffShift ss = getStaffShiftFacade().findFirstBySQL(sql, m, TemporalType.TIMESTAMP);
        return ss;
    }

    public StaffShift fetchStaffShift(FingerPrintRecord r) {

        return null;

        //     StaffShift s = new StaffShift();
    }

    public ShiftFacade getShiftFacade() {
        return shiftFacade;
    }

    public void setShiftFacade(ShiftFacade shiftFacade) {
        this.shiftFacade = shiftFacade;
    }

    public StaffShift fetchPrevStaffShift(StaffShift tmp) {
        System.err.println("Fetch Prev StaffShift");
        if (tmp.getShift() == null) {
            return null;
        }

        String sql = "Select s from StaffShift s "
                + " where s.retired=false"
                + " and s.staff=:st "
                + " and s.shiftDate=:date "
                + " and s.shift=:preSh";
        HashMap hm = new HashMap();
        hm.put("st", tmp.getStaff());
        hm.put("date", tmp.getShiftDate());
        hm.put("preSh", tmp.getShift().getPreviousShift());

        return getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

    }

    public StaffShift fetchStaffShift(Date date, Shift shift, Staff staff) {
        String sql = "Select s from StaffShift s "
                + " where s.retired=false"
                + " and s.staff=:st"
                + " and s.shift=:sh "
                + " and s.shiftDate=:date ";
        HashMap hm = new HashMap();
        hm.put("date", date);
        hm.put("sh", shift);
        hm.put("st", staff);

        return getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

    }

    public StaffShift fetchPrevDayStaffShift(StaffShift ss) {
        System.err.println("Fetch Prev Day Shift");
        if (ss == null) {
            return null;
        }

        if (ss.getShiftDate() == null) {
            return null;
        }

        if (ss.getShift() == null) {
            return null;
        }

        if (ss.getShift().getPreviousShift() != null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(ss.getShiftDate());
        cal.add(Calendar.DATE, -1);
        Date beforDate = cal.getTime();
        String sql = "select ss from  "
                + " StaffShift ss "
                + "where ss.shiftDate=:bDate"
                + " and  ss.staff=:st "
                + " and  ss.shift.nextShift is null";
        HashMap hm = new HashMap();
        hm.put("bDate", beforDate);
        hm.put("st", ss.getStaff());

        StaffShift tmp = getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            return null;
        }

        if (tmp.getShift() == null) {
            return null;
        }

        Calendar fetchShiftEndingTime = Calendar.getInstance();

        if (tmp.getShift().getEndingTime() == null) {
            return null;
        }

        fetchShiftEndingTime.setTime(tmp.getShift().getEndingTime());

        Calendar shiftStartTime = Calendar.getInstance();

        if (ss.getShift().getStartingTime() == null) {
            return null;
        }
        shiftStartTime.setTime(ss.getShift().getStartingTime());

        if (shiftStartTime.get(Calendar.HOUR_OF_DAY) == fetchShiftEndingTime.get(Calendar.HOUR_OF_DAY)) {
            return tmp;
        }

        return null;
    }
//
//    public double getMaxShiftOrder(StaffShift ss) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(ss.getShiftDate());
//        cal.add(Calendar.DATE, -1);
//        Date beforDate = cal.getTime();
//        String sql = "Select s from StaffShift s "
//                + " where s.shiftDate=:bDate ";
//        HashMap hm = new HashMap();
//        hm.put("bDate", beforDate);
////        hm.put("st", ss.getStaff());
//
//        List<StaffShift> tmp = getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
//        double max = 0;
//        for (StaffShift sst : tmp) {
//            if (sst.getShift().getShiftOrder() > max) {
//                max = sst.getShift().getShiftOrder();
//            }
//        }
//        System.err.println("max " + max);
//        return max;
//
//    }

    public StaffShift fetchFrvDayStaffShift(StaffShift ss) {
        //   System.err.println("Fetch Forward Day Shift");
        if (ss == null) {
            return null;
        }
        if (ss.getShiftDate() == null) {
            return null;
        }

        if (ss.getShift() == null) {
            return null;
        }

        if (ss.getShift().getNextShift() != null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(ss.getShiftDate());
        cal.add(Calendar.DATE, 1);
        Date afterDate = cal.getTime();
        String sql = "select ss  from  StaffShift ss "
                + " where ss.shiftDate=:bDate "
                + " and ss.staff=:st"
                + " and ss.shift.previousShift is null ";
        HashMap hm = new HashMap();
        hm.put("bDate", afterDate);
        hm.put("st", ss.getStaff());

        StaffShift tmp = getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            return null;
        }

        if (tmp.getShift() == null) {
            return null;
        }

        Calendar fetchShiftStartTime = Calendar.getInstance();

        if (tmp.getShift().getStartingTime() == null) {
            return null;
        }

        fetchShiftStartTime.setTime(tmp.getShift().getStartingTime());

        Calendar shiftEndTime = Calendar.getInstance();

        if (ss.getShift().getEndingTime() == null) {
            return null;
        }

        shiftEndTime.setTime(ss.getShift().getEndingTime());

        if (fetchShiftStartTime.get(Calendar.HOUR_OF_DAY) == shiftEndTime.get(Calendar.HOUR_OF_DAY)) {
            return tmp;
        }

        return null;

    }

    public StaffShift fetchFrwStaffShift(StaffShift tmp) {
        //   System.err.println("Fetch Forward Staff Shift");
        if (tmp == null) {
            return null;
        }

        if (tmp.getShift() == null) {
            return null;
        }

        String sql = "Select s from StaffShift s "
                + " where s.retired=false "
                + " and s.staff=:st "
                + " and s.shiftDate=:date  "
                + " and s.shift=:frwSh";
        HashMap hm = new HashMap();
        hm.put("st", tmp.getStaff());
        hm.put("date", tmp.getShiftDate());
        hm.put("frwSh", tmp.getShift().getNextShift());
        return getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

    }

//    public List<StaffShift> fetchStaffShift(Date d, Roster roster) {
//        // List<StaffShift> tmp=new ArrayList<>();
//        String sql;
//        HashMap hm = new HashMap();
//
//        sql = "select st From StaffShift st "
//                + " where st.retired=false"
//                + " and st.shiftDate=:dt "
//                + " and st.shift.roster=:rs";
//        hm.put("dt", d);
//        hm.put("rs", roster);
//        List<StaffShift> tmp = getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
//        for (StaffShift sst : tmp) {
//            updateStaffShift(sst);
//            sst.setFingerPrintRecordList(fetchMissedFingerFrintRecord(sst));
//        }
//        return tmp;
//    }
//    public List<StaffShift> fetchStaffShift(Date d, Staff staff) {
//        // List<StaffShift> tmp=new ArrayList<>();
//        String sql;
//        HashMap hm = new HashMap();
//
//        sql = "select st From StaffShift st "
//                + " where st.retired=false"
//                + " and st.shiftDate=:dt "
//                + " and st.staff=:st";
//        hm.put("dt", d);
//        hm.put("st", staff);
//        List<StaffShift> tmp = getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
//        System.err.println("Staff Shift " + tmp);
//        return tmp;
//    }
    public List<StaffShift> fetchStaffShiftWithShift(Date d, Staff staff) {
        // List<StaffShift> tmp=new ArrayList<>();
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false"
                + " and st.shiftDate=:dt "
                + " and st.staff=:st"
                + " and st.shift is not null"
                + " order by st.shift.shiftOrder,st.shift.id";
        hm.put("dt", d);
        hm.put("st", staff);
        List<StaffShift> tmp = getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
        System.err.println("fetchStaffShiftWithShift:: " + tmp);
        return tmp;
    }

    public StaffShift calPrevStaffShift(StaffShift tmp) {
        //  System.err.println("Cal Prev Shift");

        if (tmp == null) {
            return null;
        }

        if (tmp.getShift() == null) {
            return null;
        }

        StaffShift preShift = fetchPrevStaffShift(tmp);
        if (preShift != null) {
            // System.err.println("This Staff continue From : " + preShift.getShift().getName());
            return preShift;
        }

        StaffShift preDayStaffShift = fetchPrevDayStaffShift(tmp);

        return preDayStaffShift;

    }

//       private StaffShift calPrevStaffShift(StaffShift tmp) {
//
//        StaffShift preShift = fetchPrevStaffShift(tmp);
//        
//        
//        
//        
//        StaffShift preDayStaffShift = fetchPrevDayStaffShift(tmp);
//
//        if (preDayStaffShift != null) {
//            preDayStaffShift.setContinuedTo(tmp);
//            getStaffShiftFacade().edit(preDayStaffShift);
//        }
//
//        if (preShift != null) {
//            System.err.println("This Staff continue From : " + preShift.getShift().getName());
//            return preShift;
//        } else if (preDayStaffShift != null && tmp.getShift().getShiftOrder() == 0) {
//            System.err.println("This Staff continue From : " + preDayStaffShift.getShift().getName());
//            return preDayStaffShift;
//        } else {
//            return null;
//        }
//    }
    public StaffShift calFrwStaffShift(StaffShift tmp) {
        //System.err.println("Cal Forward Staff Shift");
        if (tmp == null) {
            return null;
        }

        if (tmp.getShift() == null) {
            return null;
        }

        StaffShift frwStaffShift = fetchFrwStaffShift(tmp);
        if (frwStaffShift != null) {
            //  System.err.println("This Staff continue To : " + frwStaffShift.getShift().getName());
            return frwStaffShift;
        }

        StaffShift frwDayStaffShift = fetchFrvDayStaffShift(tmp);

        return frwDayStaffShift;

    }

//      public StaffShift calFrwStaffShift(StaffShift tmp) {
//        if (tmp == null) {
//            return null;
//        }
//
//        StaffShift frwStaffShift = fetchFrwStaffShift(tmp);
//        StaffShift frwDayStaffShift = fetchFrvDayStaffShift(tmp);
//
//        if (frwDayStaffShift != null) {
//            frwDayStaffShift.setContinuedFrom(tmp);
//            getStaffShiftFacade().edit(frwDayStaffShift);
//        }
//
//        if (frwStaffShift != null) {
//            UtilityController.addSuccessMessage("This Staff continue to : " + frwStaffShift.getShift().getName());
//            return frwStaffShift;
//        } else if (frwDayStaffShift != null && tmp.getShift().getShiftOrder() == getMaxShiftOrder(tmp)) {
//            System.err.println("This Staff continue to : " + frwDayStaffShift.getShift().getName());
//            return frwDayStaffShift;
//        } else {
//            return null;
//        }
//    }
    public void updateStaffShift(StaffShift tmp) {
        StaffShift prevStaffShift = calPrevStaffShift(tmp);
        if (prevStaffShift != null) {
            tmp.setPreviousStaffShift(prevStaffShift);
        } else {
            tmp.setPreviousStaffShift(null);
        }

        StaffShift frwStaffShift = calFrwStaffShift(tmp);
        if (frwStaffShift != null) {
            tmp.setNextStaffShift(frwStaffShift);
        } else {
            tmp.setNextStaffShift(null);
        }

        if (tmp.getStartRecord().getId() == null) {
            getFingerPrintRecordFacade().create(tmp.getStartRecord());
        } else {
            getFingerPrintRecordFacade().edit(tmp.getStartRecord());
        }

        if (tmp.getEndRecord().getId() == null) {
            getFingerPrintRecordFacade().create(tmp.getEndRecord());
        } else {
            getFingerPrintRecordFacade().edit(tmp.getEndRecord());
        }

        getStaffShiftFacade().edit(tmp);
    }

    public List<Shift> fetchShift(Roster roster) {
        String sql = "Select s From Shift s "
                + " Where s.retired=false "
                + " and s.roster=:rs ";
        //    + " order by s.shiftOrder  ";
        HashMap hm = new HashMap();
        hm.put("rs", roster);

        return getShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
    }

    public List<Staff> fetchStaff(Roster roster) {
        String sql = "Select s From Staff s "
                + " Where s.retired=false "
                + " and s.roster=:rs "
                + " order by s.person.name  ";
        HashMap hm = new HashMap();
        hm.put("rs", roster);
        List<Staff> list = getStaffFacade().findBySQL(sql, hm);
        System.err.println("Staff " + list);
        return list;
    }

    public List<FingerPrintRecord> fetchMissedFingerFrintRecord(StaffShift sst) {
        //   System.err.println("Date : " + sst.getShiftDate());
        List<FingerPrintRecord> tmp;
        HashMap hm = new HashMap();

        Calendar frm = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        frm.setTime(getCommonFunctions().getStartOfDay(sst.getShiftDate()));
        frm.add(Calendar.DATE, -1);

        to.setTime(getCommonFunctions().getEndOfDay(sst.getShiftDate()));
        to.add(Calendar.DATE, 1);

        ////////////////
        String sql = "Select fpr from"
                + " FingerPrintRecord fpr "
                + " where fpr.fingerPrintRecordType=:tp "
                + " and fpr.retired=false"
                + " and  fpr.staff=:st"
                + " and fpr.recordTimeStamp between :fd and :td "
                + " and fpr.loggedRecord is not null"
                + " and fpr.staffShift is null ";
        hm.put("tp", FingerPrintRecordType.Varified);
        hm.put("st", sst.getStaff());
        hm.put("fd", frm.getTime());
        hm.put("td", to.getTime());
        tmp = getFingerPrintRecordFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
        System.err.println("fetchMissedFingerFrintRecord:: " + tmp);
        return tmp;
    }

    public Staff fetchStaff(String code) {
        Map m = new HashMap();
        m.put("d", code);
        String sql = "select s from Staff s "
                + " where s.retired=false"
                + " and s.code=:d";
        return getStaffFacade().findFirstBySQL(sql, m);
    }

    public FingerPrintRecord fetchFingerPrintRecord(Staff staff, Date timeStamp, FingerPrintRecordType fingerPrintRecordType) {
        HashMap m = new HashMap();
        String jpql = "select r from FingerPrintRecord r "
                + " where r.retired=false "
                + " and r.fingerPrintRecordType=:p "
                + " and r.staff=:s "
                + " and r.recordTimeStamp=:t";
        m = new HashMap();
        m.put("s", staff);
        m.put("t", timeStamp);
        m.put("p", fingerPrintRecordType);
        FingerPrintRecord fpr = getFingerPrintRecordFacade().findFirstBySQL(jpql, m, TemporalType.TIMESTAMP);
        System.err.println("Logged Fetched " + fpr);
        return fpr;
    }

    public FingerPrintRecord fetchFingerPrintRecord(Staff staff, Date timeStamp, FingerPrintRecordType fingerPrintRecordType, FingerPrintRecord fingerPrintRecordLogged) {
        HashMap m = new HashMap();
        String jpql = "select r from FingerPrintRecord r "
                + " where r.retired=false "
                + " and r.fingerPrintRecordType=:p "
                + " and r.staff=:s "
                + " and r.recordTimeStamp=:t"
                + " and r.loggedRecord=:lfr";
        m = new HashMap();
        m.put("s", staff);
        m.put("t", timeStamp);
        m.put("p", fingerPrintRecordType);
        m.put("lfr", fingerPrintRecordLogged);
        FingerPrintRecord fpr = getFingerPrintRecordFacade().findFirstBySQL(jpql, m, TemporalType.TIMESTAMP);
        System.err.println("Fetched Varified " + fpr);
        return fpr;
    }

    public Shift fetchShift(Staff staff, Date timeStamp) {
        HashMap m = new HashMap();
        String jpql = "select r.shift "
                + " from StaffShift r "
                + " where r.retired=false "
                + " and r.shiftDate=:dt "
                + " and r.staff=:s ";
        //  m = new HashMap();
        m.put("s", staff);
        m.put("dt", timeStamp);
        Shift fpr = getShiftFacade().findFirstBySQL(jpql, m, TemporalType.DATE);

        //  System.err.println("Fetched Shift " + fpr);
        return fpr;
    }

    public FingerPrintRecord createFingerPrintRecordLogged(Staff staff, Date timeStamp, WebUser webUser) {
        FingerPrintRecord fingerPrintRecord;
        fingerPrintRecord = fetchFingerPrintRecord(staff, timeStamp, FingerPrintRecordType.Logged);

        if (fingerPrintRecord == null) {
            fingerPrintRecord = new FingerPrintRecord();
            fingerPrintRecord.setCreatedAt(Calendar.getInstance().getTime());
            fingerPrintRecord.setCreater(webUser);
            fingerPrintRecord.setFingerPrintRecordType(FingerPrintRecordType.Logged);
            fingerPrintRecord.setStaff(staff);
            fingerPrintRecord.setRecordTimeStamp(timeStamp);
            getFingerPrintRecordFacade().create(fingerPrintRecord);
        }

        return fingerPrintRecord;
    }

    public FingerPrintRecord createFingerPrintRecordVarified(WebUser webUser, FingerPrintRecord loggedFingerPrintRecord) {
        if (loggedFingerPrintRecord == null) {
            return null;
        }

        FingerPrintRecord fingerPrintRecord;
        fingerPrintRecord = fetchFingerPrintRecord(loggedFingerPrintRecord.getStaff(), loggedFingerPrintRecord.getRecordTimeStamp(), FingerPrintRecordType.Varified, loggedFingerPrintRecord);

        if (fingerPrintRecord == null) {
            fingerPrintRecord = new FingerPrintRecord();
            fingerPrintRecord.setCreatedAt(Calendar.getInstance().getTime());
            fingerPrintRecord.setCreater(webUser);
            fingerPrintRecord.setFingerPrintRecordType(FingerPrintRecordType.Varified);
            fingerPrintRecord.setStaff(loggedFingerPrintRecord.getStaff());
            fingerPrintRecord.setRecordTimeStamp(loggedFingerPrintRecord.getRecordTimeStamp());
            fingerPrintRecord.setLoggedRecord(loggedFingerPrintRecord);
            getFingerPrintRecordFacade().create(fingerPrintRecord);
        }

        return fingerPrintRecord;
    }

    public List<StaffShift> fetchPreviousDayShift(StaffShift sst) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sst.getShiftDate());
        cal.add(Calendar.DATE, -1);
        Date pre = cal.getTime();

        String sql = "Select ss From StaffShift ss "
                + " where ss.shiftDate=:date "
                + " and ss.shift=:ds ";
        HashMap hm = new HashMap();
        hm.put("date", pre);
        hm.put("ds", sst.getShift());

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);

    }

    public List<StaffLeave> getStaffLeave(Staff staff, Date frmDate, Date toDate) {

        String sql = "Select s From StaffLeave s"
                + " where s.retired=false "
                + " and s.staff=:st"
                + " and s.leaveDate between :frm and :to ";
        HashMap hm = new HashMap();
        hm.put("st", staff);
        hm.put("frm", frmDate);
        hm.put("to", toDate);

        return getStaffLeaveFacade().findBySQL(sql, hm, TemporalType.DATE);
    }

    public boolean isHoliday(Date d) {
        String sql = "Select d From PhDate d "
                + " Where d.retired=false"
                + " and d.phDate=:dtd";
        HashMap hm = new HashMap();
        hm.put("dtd", d);
        List<PhDate> tmp = getPhDateFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!tmp.isEmpty()) {
            return true;
        }

        return false;
    }

    public double calWorkedDuraion(StaffShift ss) {
        long endTime = 0l;
        long startTime = 0l;
        long result;

        if (ss.getEndRecord() != null && ss.getEndRecord().getRecordTimeStamp() != null) {
            endTime = ss.getEndRecord().getRecordTimeStamp().getTime();
        }

        if (ss.getStartRecord() != null && ss.getStartRecord().getRecordTimeStamp() != null) {
            startTime = ss.getStartRecord().getRecordTimeStamp().getTime();
        }

        result = endTime - startTime;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(result);

        return cal.get(Calendar.MINUTE);
    }

    public StaffMonthDay calculateDayType(Staff staff, Date frm, Date to) {

        StaffMonthDay tmp = new StaffMonthDay();
        tmp.setStaff(staff);
        for (StaffShift ds : getStaffShift(staff, frm, to)) {
            tmp.setRosteredTime(tmp.getRosteredTime() + (ds.getShift().getDurationHour() * 60));
            //  tmp.setWorkedTime(tmp.getWorkedTime() + calWorkedDuraion(ds));
            switch (getDayType(ds.getShiftDate())) {
                case Weekday:
                    tmp.setWeekdays(tmp.getWeekdays() + 1);
                    break;
                case Saturday:
                    tmp.setSaturday(tmp.getSaturday() + 1);
                    break;
                case Sunday:
                    tmp.setSunday(tmp.getSunday() + 1);
                    break;
                case Holiday:
                    tmp.setHoliday(tmp.getHoliday() + 1);
                    break;
            }
        }

        return tmp;

    }

    public boolean checkDateWithDayType(Date date, Shift ds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayValue = cal.get(Calendar.DAY_OF_WEEK);

        if (isHoliday(date) && ds.getDayType() == DayType.Holiday) {
            return true;
        }

        if (!isHoliday(date) && (dayValue == 2 || dayValue == 3 || dayValue == 4 || dayValue == 5 || dayValue == 6) && ds.getDayType() == DayType.Weekday) {
            return true;
        }

        if (!isHoliday(date) && dayValue == 7 && ds.getDayType() == DayType.Saturday) {
            return true;
        }

        if (!isHoliday(date) && dayValue == 1 && ds.getDayType() == DayType.Sunday) {
            return true;
        }

        if (ds.getDayType() == DayType.All) {
            return true;
        }

        return false;
    }

    public DayType getDayType(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayValue = cal.get(Calendar.DAY_OF_WEEK);

        if (isHoliday(date)) {
            return DayType.Holiday;
        }

        if (!isHoliday(date) && (dayValue == 2 || dayValue == 3 || dayValue == 4 || dayValue == 5 || dayValue == 6)) {
            return DayType.Weekday;
        }

        if (!isHoliday(date) && dayValue == 7) {
            return DayType.Saturday;
        }

        if (!isHoliday(date) && dayValue == 1) {
            return DayType.Sunday;
        }

        return DayType.All;
    }

    public List<StaffShift> getStaffShift(Staff staff, Date frm, Date to) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st"
                + " where st.retired=false"
                + " and st.shiftDate between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.DATE);
    }

    public List<StaffShift> getStaffShiftFromRecordForSlaryCalculation(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and st.consideredForSalary!=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<StaffShift> getStaffShiftFromRecordForExraDutyCalculation(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and st.consideredForExtraDuty!=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<StaffShift> getStaffShiftFromRecordSlaryCalculated(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and st.consideredForSalary=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<StaffShift> getStaffShiftFromRecordForOtCalculation(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and  st.consideredForOt!=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<StaffShift> getStaffShiftFromRecordOtCalculated(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and  st.consideredForOt=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<StaffShift> getStaffShiftFromRecordExtraDutyCalculated(Date frm, Date to, Staff staff) {
        String sql;
        HashMap hm = new HashMap();

        sql = "select st From StaffShift st "
                + " where st.retired=false "
                + " and  st.consideredForExtraDuty=true "
                + " and st.startRecord.recordTimeStamp between :frmD and :toD "
                + " and st.staff=:staf";
        hm.put("frmD", frm);
        hm.put("toD", to);
        hm.put("staf", staff);

        return getStaffShiftFacade().findBySQL(sql, hm, TemporalType.TIMESTAMP);
    }

    public List<Staff> getStaffForSalary() {
        String temSql;
        List<Staff> tmp;
        temSql = "SELECT i FROM Staff i "
                + " where i.retired=false "
                + " and i.person is not null "
                + " and i.person.name is not null "
                + " order by i.person.name";
        tmp = getStaffFacade().findBySQL(temSql);
        return tmp;
    }

    public void setEpf(StaffSalaryComponant ssc, double epf, double epfCompany) {
        if (ssc.getStaffPaysheetComponent().getPaysheetComponent().isIncludedForEpf()) {
            double tmp = ssc.getComponantValue() * (epf / 100.0);
            ssc.setEpfValue(tmp);

            tmp = ssc.getComponantValue() * (epfCompany / 100.0);
            ssc.setEpfCompanyValue(tmp);

        }
    }

    public void setEtf(StaffSalaryComponant ssc, double etf, double etfCompany) {
        if (ssc.getStaffPaysheetComponent().getPaysheetComponent().isIncludedForEpf()) {
            double tmp = ssc.getComponantValue() * (etf / 100.0);
            ssc.setEtfValue(tmp);

            tmp = ssc.getComponantValue() * (etfCompany / 100.0);
            ssc.setEtfCompanyValue(tmp);

        }
    }

    public boolean checkExistingSalary(Date fromDate, Date toDate, Staff staff) {
        String sql = "Select s From StaffSalary s "
                + " where s.retired=false "
                + " and s.staff=:st "
                + " and s.salaryCycle.salaryFromDate>=:fd "
                + " and s.salaryCycle.salaryToDate<=:td";

        HashMap hm = new HashMap<>();
        hm.put("fd", fromDate);
        hm.put("td", toDate);
        hm.put("st", staff);

        List<StaffSalary> tmp = getStaffSalaryFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!tmp.isEmpty()) {
            updateItems(tmp, staff);
            return true;
        }

        return false;
    }

    private void updateItems(List<StaffSalary> items, Staff staff) {
        for (StaffSalary st : items) {
            if (st.getStaff().getId() == staff.getId()) {
                st.setExist(true);
            }
        }

    }

    public List<StaffPaysheetComponent> fetchStaffPaysheetComponent(Staff staff, Date toDate) {
        String sql = "Select s From StaffPaysheetComponent s "
                + " where s.retired=false "
                + " and s.staff=:st"
                + " and (s.paysheetComponent.componentType!=:bs1 "
                + " and s.paysheetComponent.componentType!=:bs2"
                + " and s.paysheetComponent.componentType!=:bs3 "
                + " and s.paysheetComponent.componentType!=:bs4 )"
                + " and (s.toDate>=:dt or s.toDate is null)";
        HashMap hm = new HashMap();
        hm.put("st", staff);
        hm.put("dt", toDate);
        hm.put("bs1", PaysheetComponentType.BasicSalary);
        hm.put("bs2", PaysheetComponentType.OT);
        hm.put("bs3", PaysheetComponentType.ExtraDuty);
        hm.put("bs4", PaysheetComponentType.No_Pay_Deduction);
        return getStaffPaysheetComponentFacade().findBySQL(sql, hm, TemporalType.DATE);
    }

    @EJB
    StaffSalaryComponantFacade staffSalaryComponantFacade;

    public StaffSalaryComponantFacade getStaffSalaryComponantFacade() {
        return staffSalaryComponantFacade;
    }

    public void setStaffSalaryComponantFacade(StaffSalaryComponantFacade staffSalaryComponantFacade) {
        this.staffSalaryComponantFacade = staffSalaryComponantFacade;
    }

    public List<StaffSalaryComponant> fetchStaffSalaryComponent(StaffSalary staffSalary) {
        String sql = "Select s From StaffSalaryComponant s "
                + " where s.retired=false "
                + " and s.staffSalary=:st ";
        HashMap hm = new HashMap();
        hm.put("st", staffSalary);
        return getStaffSalaryComponantFacade().findBySQL(sql, hm);
    }

    public List<StaffShift> fetchStaffShifts(StaffSalary staffSalary) {
        String sql = "Select s From StaffShift s "
                + " where s.retired=false "
                + " and s.staffSalary=:st ";
        HashMap hm = new HashMap();
        hm.put("st", staffSalary);
        return getStaffShiftFacade().findBySQL(sql, hm);
    }

    public StaffPaysheetComponent getBasic(Staff staff) {
        //System.err.println("Getting Basic " + staff.getStaffEmployment());

        String sql;
        HashMap hm;
        StaffPaysheetComponent tmp;

        sql = "Select s From StaffPaysheetComponent s"
                + " where s.retired=false "
                + " and  s.staff=:stf "
                + " and s.paysheetComponent.componentType=:type "
                + " and s.fromDate<=:cu  "
                + " and s.toDate>=:cu ";

        hm = new HashMap();
        hm.put("stf", staff);
        hm.put("type", PaysheetComponentType.BasicSalary);
        hm.put("cu", new Date());
        tmp = getStaffPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            sql = "Select s From StaffPaysheetComponent s "
                    + " where s.retired=false and "
                    + " s.staff=:stf "
                    + " and s.paysheetComponent.componentType=:type"
                    + " and s.fromDate<=:cu "
                    + " and s.toDate is null ";

            hm = new HashMap();
            hm.put("stf", staff);
            hm.put("type", PaysheetComponentType.BasicSalary);
            hm.put("cu", new Date());
            tmp = getStaffPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        }

        return tmp;

    }

    public StaffPaysheetComponent getComponent(Staff staff, WebUser user, PaysheetComponentType paysheetComponentType) {
        String sql;
        HashMap hm;
        StaffPaysheetComponent tmp;

        sql = "Select s From StaffPaysheetComponent s "
                + " where s.retired=false "
                + " and s.staff=:stf "
                + " and s.paysheetComponent.componentType=:type ";

        hm = new HashMap();
        hm.put("stf", staff);
        hm.put("type", paysheetComponentType);
        tmp = getStaffPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {

            tmp = new StaffPaysheetComponent();
            tmp.setCreatedAt(new Date());
            tmp.setCreater(user);
            tmp.setPaysheetComponent(getComponentName(user, paysheetComponentType));
            tmp.setStaff(staff);
            getStaffPaysheetComponentFacade().create(tmp);
        }

        return tmp;

    }

//    public double calOt(Date date) {
//        DateRange dateRange = getCommonFunctions().getDateRangeForOT(date);
//        //System.err.println("From : " + dateRange.getFromDate());
//        //System.err.println("To : " + dateRange.getToDate());
//
//        return 0.0;
//    }
    private StaffShift fetchExistingStaffShift(Date date) {
        String sql = "select s from StaffShift s "
                + " where s.retired=false "
                + " and s.consideredForOt=false "
                + " and s.shiftStartTime=:d";

        HashMap hm = new HashMap();
        hm.put("d", date);

        StaffShift ss = getStaffShiftFacade().findFirstBySQL(sql, hm, TemporalType.TIMESTAMP);

        return ss;
    }

    private StaffShift createNewStaffShift(StaffShift ss, Calendar date) {
        Date tmp = getCommonFunctions().getEndOfDay(date.getTime());
        Calendar time = Calendar.getInstance();
        time.setTime(tmp);
        time.add(Calendar.MILLISECOND, 1);
        //System.err.println("Start Time : " + time.getTime());

        StaffShift fetched = fetchExistingStaffShift(time.getTime());
        if (fetched != null) {
            return fetched;
        }

        StaffShift stf = new StaffShift();
        stf.copy(ss);
        stf.setShiftDate(date.getTime());
        stf.setCreatedAt(new Date());
        stf.setCreater(ss.getCreater());

        //   stf.setShiftStartTime(time.getTime());
        stf.setStartRecord(createFingerPrint(stf));

        getStaffShiftFacade().create(stf);

        return stf;
    }

    private FingerPrintRecord createFingerPrint(StaffShift ss) {
        FingerPrintRecord fpr = new FingerPrintRecord();
        fpr.setCreatedAt(new Date());
        fpr.setFingerPrintRecordType(FingerPrintRecordType.Varified);
        fpr.setStaff(ss.getStaff());
        //fpr.setRecordTimeStamp(ss.getShiftStartTime());
        fpr.setRecordTimeStamp(ss.getShift().getStartingTime());
        fpr.setStaffShift(ss);
        fpr.setTimes(Times.inTime);
        getFingerPrintRecordFacade().create(fpr);
        return fpr;
    }

    public List<StaffShift> getOrCreateRecordsForOt(Date fromDate, Date toDate, Staff staff) {
        List<StaffShift> staffShifts = getStaffShiftFromRecordForOtCalculation(fromDate, toDate, staff);
        System.err.println("OT SHIFT  " + staffShifts);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(toDate);

        loopThroughStaffShift(staffShifts, lastDate, toDate);

        return staffShifts;
    }

    public List<StaffShift> getOrCreateRecordsForSalary(Date fromDate, Date toDate, Staff staff) {
        List<StaffShift> staffShifts = getStaffShiftFromRecordForSlaryCalculation(fromDate, toDate, staff);
        System.err.println("SALARY SHIFT  " + staffShifts);
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(toDate);

        loopThroughStaffShift(staffShifts, lastDate, toDate);

        return staffShifts;
    }

    public List<StaffShift> getOrCreateRecordsForExtraDuty(Date fromDate, Date toDate, Staff staff) {
        List<StaffShift> staffShifts = getStaffShiftFromRecordForExraDutyCalculation(fromDate, toDate, staff);
        System.err.println("EXTRA DUTY SHIFT  " + staffShifts);
        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(toDate);

        loopThroughStaffShift(staffShifts, lastDate, toDate);

        return staffShifts;
    }

    public void loopThroughStaffShift(List<StaffShift> staffShifts, Calendar lastDate, Date toDate) {
        for (StaffShift ss : staffShifts) {

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            start.setTime(ss.getStartRecord().getRecordTimeStamp());
            if (ss.getEndRecord() != null) {
                start.setTime(ss.getEndRecord().getRecordTimeStamp());
            }

            if ((ss.getStartRecord() != null
                    && ss.getEndRecord() != null
                    && start.get(Calendar.DATE) == lastDate.get(Calendar.DATE)
                    && end.get(Calendar.DATE) != lastDate.get(Calendar.DATE))
                    ////////////////////////////
                    || (ss.getStartRecord() != null
                    && ss.getEndRecord() == null
                    && start.get(Calendar.DATE) == lastDate.get(Calendar.DATE))) {

                //create new Staffshft Record
                createNewStaffShift(ss, lastDate);
                //Update EndRecord Time
                ss.getEndRecord().setRecordTimeStamp(getCommonFunctions().getEndOfDay(toDate));
                getFingerPrintRecordFacade().edit(ss.getEndRecord());
                //Update Staff shift
//                ss.setShiftEndTime(getCommonFunctions().getEndOfDay(dateRange.getToDate()));               
                getStaffShiftFacade().edit(ss);
            }
        }

    }

    public OtNormalSpecial calOt(Date date, Staff staff) {
        OtNormalSpecial otNormalSpecial = new OtNormalSpecial();

        DateRange dateRange = getCommonFunctions().getDateRangeForOT(date);

        otNormalSpecial.setDateRange(dateRange);

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getOrCreateRecordsForOt(dateRange.getFromDate(), dateRange.getToDate(), staff);

        double otMaxMinute = getFinalVariables().getOtTime() * 4 * 60;
        double workedMinute = 0.0;

        if (staffShifts == null) {
            return otNormalSpecial;
        }

        for (StaffShift ser : staffShifts) {
            Calendar start = Calendar.getInstance();
            start.setTime(ser.getStartRecord().getRecordTimeStamp());
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(ser.getEndRecord().getRecordTimeStamp());

            int netTime = endTime.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
            workedMinute += netTime;

            ser.setConsideredForOt(Boolean.TRUE);
            System.err.println("SETTING OT TRUE");
            getStaffShiftFacade().edit(ser);

        }

        if (workedMinute > (staff.getWorkingHour() * 4 * 60)) {
            otNormalSpecial.setSpecialMin(otMaxMinute - (staff.getWorkingHour() * 4 * 60));
        }

        if (workedMinute > otMaxMinute) {
            otNormalSpecial.setNormalMin(workedMinute - otMaxMinute);
        }

        return otNormalSpecial;

    }

    public OtNormalSpecial calculateOt(Date fromDate, Date toDate, Staff staff) {
        OtNormalSpecial otNormalSpecial = new OtNormalSpecial();

        otNormalSpecial.getDateRange().setFromDate(fromDate);
        otNormalSpecial.getDateRange().setToDate(toDate);

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getOrCreateRecordsForOt(fromDate, toDate, staff);

        double otMaxMinute = getFinalVariables().getOtTime() * 4 * 60;
        double workedMinute = 0.0;

        for (StaffShift ser : staffShifts) {
            Calendar start = Calendar.getInstance();
            start.setTime(ser.getStartRecord().getRecordTimeStamp());
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(ser.getEndRecord().getRecordTimeStamp());

            int netTime = endTime.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
            workedMinute += netTime;

            ser.setConsideredForOt(Boolean.TRUE);
            System.err.println("SETTING OT TRUE");
            getStaffShiftFacade().edit(ser);
        }

        if (workedMinute > (staff.getWorkingHour() * 4 * 60)) {
            otNormalSpecial.setSpecialMin(otMaxMinute - (staff.getWorkingHour() * 4 * 60));
        }

        if (workedMinute > otMaxMinute) {
            otNormalSpecial.setNormalMin(workedMinute - otMaxMinute);
        }

        return otNormalSpecial;

    }

    public void calculateBasic(Date fromDate, Date toDate, Staff staff) {

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getOrCreateRecordsForSalary(fromDate, toDate, staff);

//        double otMaxMinute = getFinalVariables().getOtTime() * 4 * 60;
//        double workedMinute = 0.0;
        for (StaffShift ser : staffShifts) {
//            Calendar start = Calendar.getInstance();
//            start.setTime(ser.getStartRecord().getRecordTimeStamp());
//            Calendar endTime = Calendar.getInstance();
//            endTime.setTime(ser.getEndRecord().getRecordTimeStamp());
//
//            int netTime = endTime.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
//            workedMinute += netTime;

            ser.setConsideredForSalary(Boolean.TRUE);
            System.err.println("SETTING SALARY TRUE");
            getStaffShiftFacade().edit(ser);
        }
    }

    public void calculateExtraDuty(Date fromDate, Date toDate, Staff staff) {

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getOrCreateRecordsForExtraDuty(fromDate, toDate, staff);

//        double otMaxMinute = getFinalVariables().getOtTime() * 4 * 60;
//        double workedMinute = 0.0;
        for (StaffShift ser : staffShifts) {
//            Calendar start = Calendar.getInstance();
//            start.setTime(ser.getStartRecord().getRecordTimeStamp());
//            Calendar endTime = Calendar.getInstance();
//            endTime.setTime(ser.getEndRecord().getRecordTimeStamp());
//
//            int netTime = endTime.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
//            workedMinute += netTime;

            ser.setConsideredForExtraDuty(Boolean.TRUE);
            System.err.println("SETTING SALARY TRUE");
            getStaffShiftFacade().edit(ser);
        }
    }

    public List<ExtraDutyCount> calExtraDuty(Date date, Staff staff) {

        DateRange dateRange = new DateRange();
        dateRange.setFromDate(getCommonFunctions().getStartOfMonth(date));
        dateRange.setToDate(getCommonFunctions().getEndOfMonth(date));

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getStaffShift(staff, dateRange.getFromDate(), dateRange.getToDate());

        List<ExtraDutyCount> extraDutyCounts = new ArrayList<>();

        for (ExtraDutyType edt : ExtraDutyType.values()) {
            ExtraDutyCount newD = new ExtraDutyCount();
            newD.setExtraDutyType(edt);
            newD.setCount(calDutyCount(edt, staffShifts));
            extraDutyCounts.add(newD);
        }

        return extraDutyCounts;

    }

    public List<ExtraDutyCount> calExtraDuty(Date fromDate, Date toDate, Staff staff) {

        DateRange dateRange = new DateRange();
        dateRange.setFromDate(fromDate);
        dateRange.setToDate(toDate);

        //System.err.println("From : " + dateRange.getFromDate());
        //System.err.println("To : " + dateRange.getToDate());
        List<StaffShift> staffShifts = getStaffShift(staff, dateRange.getFromDate(), dateRange.getToDate());

        List<ExtraDutyCount> extraDutyCounts = new ArrayList<>();

        for (ExtraDutyType edt : ExtraDutyType.values()) {
            ExtraDutyCount newD = new ExtraDutyCount();
            newD.setExtraDutyType(edt);
            newD.setCount(calDutyCount(edt, staffShifts));
            extraDutyCounts.add(newD);
        }

        return extraDutyCounts;

    }

    private double calDutyCount(ExtraDutyType extraDutyType, List<StaffShift> staffShifts) {
        if (extraDutyType == ExtraDutyType.DayOff) {
            return getDayOffs(staffShifts);
        }

        if (extraDutyType == ExtraDutyType.SleepingDay) {
            return getSleepingDays(staffShifts);
        }

        if (extraDutyType == ExtraDutyType.MurchantileHoliday) {
            return getHoliDayCount(staffShifts, DayType.MurchantileHoliday);
        }

        if (extraDutyType == ExtraDutyType.Poya) {
            return getHoliDayCount(staffShifts, DayType.Poya);
        }

        if (extraDutyType == ExtraDutyType.PublicHoliday) {
            return getHoliDayCount(staffShifts, DayType.PublicHoliday);
        }

        return 0.0;
    }

    private double getDayOffs(List<StaffShift> list) {
        double tmp = 0.0;
        for (StaffShift sst : list) {
            if (sst.isDayOff()) {
                tmp++;
            }
        }

        return tmp;
    }

    private double getSleepingDays(List<StaffShift> list) {
        double tmp = 0.0;
        for (StaffShift sst : list) {
            if (sst.isSleepingDay()) {
                tmp++;
            }
        }

        return tmp;
    }

    private double getHoliDayCount(List<StaffShift> list, DayType phType) {
        double tmp = 0.0;
        for (StaffShift sst : list) {
            if (getHolidayType(sst.getShiftDate(), phType)) {
                tmp++;
            }
        }

        return tmp;
    }

    public boolean getHolidayType(Date d, DayType phType) {
        String sql = "Select d From PhDate d "
                + " Where d.retired=false "
                + " and d.phDate=:dtd "
                + " and d.phType=:tp";
        HashMap hm = new HashMap();
        hm.put("dtd", d);
        hm.put("tp", phType);
        List<PhDate> tmp = getPhDateFacade().findBySQL(sql, hm, TemporalType.DATE);

        if (!tmp.isEmpty()) {
            return true;
        }

        return false;
    }

    private PaysheetComponent getComponentName(WebUser user, PaysheetComponentType paysheetComponentType) {
        String sql;
        HashMap hm;
        PaysheetComponent tmp;

        sql = "Select s From PaysheetComponent s"
                + "  where s.retired=false "
                + " and  s.componentType=:type ";

        hm = new HashMap();
        hm.put("type", paysheetComponentType);
        tmp = getPaysheetComponentFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            tmp = new PaysheetComponent();
            tmp.setCreatedAt(new Date());
            tmp.setComponentType(paysheetComponentType);
            tmp.setCreater(user);
            tmp.setName(paysheetComponentType.toString());

            getPaysheetComponentFacade().create(tmp);
        }

        return tmp;
    }

    public StaffSalary getStaffSalary(Staff s, Date date) {

        String sql = "Select s From StaffSalary s "
                + " where s.retired=false "
                + " and s.staff=:s "
                + " and s.salaryCycle.salaryFromDate>=:fd "
                + " and s.salaryCycle.salaryToDate<=:td";

        HashMap hm = new HashMap<>();
        hm.put("fd", getCommonFunctions().getStartOfMonth(date));
        hm.put("td", getCommonFunctions().getEndOfMonth(date));
        hm.put("s", s);

        StaffSalary tmp = getStaffSalaryFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            tmp = new StaffSalary();
            tmp.setStaff(s);
        }

        return tmp;
    }

    public StaffSalary getStaffSalary(Staff s, Date fromDate, Date toDate) {

        String sql = "Select s From StaffSalary s "
                + " where s.retired=false "
                + " and s.staff=:s "
                + " and s.salaryCycle.salaryFromDate>=:fd "
                + " and s.salaryCycle.salaryToDate<=:td";

        HashMap hm = new HashMap<>();
        hm.put("fd", fromDate);
        hm.put("td", toDate);
        hm.put("s", s);

        StaffSalary tmp = getStaffSalaryFacade().findFirstBySQL(sql, hm, TemporalType.DATE);

        if (tmp == null) {
            tmp = new StaffSalary();
            tmp.setStaff(s);
        }

        return tmp;
    }

    public StaffPaysheetComponentFacade getStaffPaysheetComponentFacade() {
        return staffPaysheetComponentFacade;
    }

    public void setStaffPaysheetComponentFacade(StaffPaysheetComponentFacade staffPaysheetComponentFacade) {
        this.staffPaysheetComponentFacade = staffPaysheetComponentFacade;
    }

    public LinkedList<Staff> getStaffLink() {
        return staffLink;
    }

    public void setStaffLink(LinkedList<Staff> staffLink) {
        this.staffLink = staffLink;
    }

    public StaffSalaryFacade getStaffSalaryFacade() {
        return staffSalaryFacade;
    }

    public void setStaffSalaryFacade(StaffSalaryFacade staffSalaryFacade) {
        this.staffSalaryFacade = staffSalaryFacade;
    }

    public StaffFacade getStaffFacade() {
        return staffFacade;
    }

    public void setStaffFacade(StaffFacade staffFacade) {
        this.staffFacade = staffFacade;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public StaffShiftFacade getStaffShiftFacade() {
        return staffShiftFacade;
    }

    public void setStaffShiftFacade(StaffShiftFacade staffShiftFacade) {
        this.staffShiftFacade = staffShiftFacade;
    }

    public PhDateFacade getPhDateFacade() {
        return phDateFacade;
    }

    public void setPhDateFacade(PhDateFacade phDateFacade) {
        this.phDateFacade = phDateFacade;
    }

    public StaffLeaveFacade getStaffLeaveFacade() {
        return staffLeaveFacade;
    }

    public void setStaffLeaveFacade(StaffLeaveFacade staffLeaveFacade) {
        this.staffLeaveFacade = staffLeaveFacade;
    }

    public PaysheetComponentFacade getPaysheetComponentFacade() {
        return paysheetComponentFacade;
    }

    public void setPaysheetComponentFacade(PaysheetComponentFacade paysheetComponentFacade) {
        this.paysheetComponentFacade = paysheetComponentFacade;
    }

    public FingerPrintRecordFacade getFingerPrintRecordFacade() {
        return fingerPrintRecordFacade;
    }

    public void setFingerPrintRecordFacade(FingerPrintRecordFacade fingerPrintRecordFacade) {
        this.fingerPrintRecordFacade = fingerPrintRecordFacade;
    }

    public FinalVariables getFinalVariables() {
        return finalVariables;
    }

    public void setFinalVariables(FinalVariables finalVariables) {
        this.finalVariables = finalVariables;
    }
}
