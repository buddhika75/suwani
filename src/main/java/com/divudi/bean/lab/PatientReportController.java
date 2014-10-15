/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.lab;

import com.divudi.bean.common.ItemForItemController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.TransferController;
import com.divudi.bean.common.UtilityController;
import com.divudi.bean.hr.StaffController;
import com.divudi.data.CalculationType;
import com.divudi.data.InvestigationItemType;
import com.divudi.data.InvestigationReportType;
import com.divudi.ejb.PatientReportBean;
import com.divudi.entity.lab.Investigation;
import com.divudi.entity.lab.InvestigationItem;
import com.divudi.entity.lab.IxCal;
import com.divudi.entity.lab.PatientInvestigation;
import com.divudi.entity.lab.PatientReport;
import com.divudi.entity.lab.PatientReportItemValue;
import com.divudi.entity.lab.TestFlag;
import com.divudi.facade.IxCalFacade;
import com.divudi.facade.PatientInvestigationFacade;
import com.divudi.facade.PatientInvestigationItemValueFacade;
import com.divudi.facade.PatientReportFacade;
import com.divudi.facade.PatientReportItemValueFacade;
import com.divudi.facade.TestFlagFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.inject.Named;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class PatientReportController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @Inject
    StaffController staffController;
    @Inject
    TransferController transferController;
    @EJB
    private PatientReportFacade ejbFacade;
    @EJB
    private PatientReportBean prBean;
    @EJB
    PatientInvestigationItemValueFacade piivFacade;
    @EJB
    PatientReportItemValueFacade pirivFacade;
    @EJB
    PatientInvestigationFacade piFacade;
    @EJB
    IxCalFacade ixCalFacade;
    @EJB
    private TestFlagFacade testFlagFacade;

    String selectText = "";
    @Inject
    ItemForItemController itemForItemController;
    private PatientInvestigation currentPtIx;
    private PatientReport currentPatientReport;
    Investigation currentReportInvestigation;
    Investigation alternativeInvestigation;
    List<PatientReportItemValue> patientReportItemValues;
    List<PatientReportItemValue> patientReportItemValuesValues;
    List<PatientReportItemValue> patientReportItemValuesFlags;
    List<PatientReportItemValue> patientReportItemValuesDynamicLabels;
    List<PatientReportItemValue> patientReportItemValuesCalculations;

    List<PatientReport> customerReports = new ArrayList<>();

    public List<PatientReport> getCustomerReports() {
        return customerReports;
    }

    public void setCustomerReports(List<PatientReport> customerReports) {
        this.customerReports = customerReports;
    }
    
    
    List<PatientInvestigation> customerPis;

    public List<PatientInvestigation> getCustomerPis() {
        return customerPis;
    }

    public void setCustomerPis(List<PatientInvestigation> customerPis) {
        this.customerPis = customerPis;
    }
    
    
    
    public String fillPatientReports(){
        String sql;
        Map m = new HashMap();
        m.put("phone", getSessionController().getPhoneNo());
        m.put("billno", getSessionController().getBillNo().toUpperCase());
        sql = "select pr from PatientInvestigation pr where pr.retired=false and "
                + "upper(pr.billItem.bill.patient.person.phone)=:phone and "
                + "upper(pr.billItem.bill.deptId)=:billno "
                + "order by pr.id desc ";
        System.out.println("m = " + m);
        System.out.println("sql = " + sql);
        customerPis = getPiFacade().findBySQL(sql, m,50);
        return "/reports_list";
    }
    
    
    public String patientReportSearch() {
        if (currentPatientReport == null || currentPatientReport.getPatientInvestigation() == null || currentPatientReport.getPatientInvestigation().getPatient() == null) {
            return "";
        }
        getTransferController().setPatient(currentPatientReport.getPatientInvestigation().getPatient());
        return "/lab_search_for_reporting_patient";
    }

    public String lastPatientReport(PatientInvestigation pi) {
        //System.out.println("last pt rpt");
        if (pi == null) {
            currentPatientReport = null;
            //System.out.println("pi is null");
            return "";
        }
        Investigation ix;
        ix = (Investigation) pi.getInvestigation().getReportedAs();
        //System.out.println("ix = " + ix);
        currentReportInvestigation = ix;
        currentPtIx = pi;
        String sql;
        Map m = new HashMap();
        sql = "select r from PatientReport r where r.patientInvestigation=:pi and r.retired=false order by r.id desc";
        ////System.out.println("sql = " + sql);
        m.put("pi", pi);
        ////System.out.println("m = " + m);
        PatientReport r = getFacade().findFirstBySQL(sql, m);
        ////System.out.println("r = " + r);
        if (r == null) {
            //System.out.println("r is null");
//            if (ix.getReportType()==InvestigationReportType.Microbiology ) {
            if (ix.getReportType() == InvestigationReportType.Microbiology) {
                r = createNewMicrobiologyReport(pi, ix);
            } else {
                r = createNewPatientReport(pi, ix);
            }
            //System.out.println("r = " + r);
            getCommonReportItemController().setCategory(ix.getReportFormat());
        } else {
            //System.out.println("r ok");
            //System.out.println("r = " + r);
            getCommonReportItemController().setCategory(currentReportInvestigation.getReportFormat());
        }
        currentPatientReport = r;
        if (currentPatientReport != null && currentPatientReport.getApproveUser() != null) {
            getStaffController().setCurrent(currentPatientReport.getApproveUser().getStaff());
        } else {
            getStaffController().setCurrent(null);
        }
        return "/lab_patient_report";
    }

    public List<PatientReportItemValue> getPatientReportItemValues() {
        String sql = "Select v from PatientReportItemValue v where v.patientReport=:r "
                + " order by v.investigationItem.cssTop";
        Map m = new HashMap();
        m.put("r", getCurrentPatientReport());
        patientReportItemValues = getPirivFacade().findBySQL(sql, m);
        // getPirivFacade
        return patientReportItemValues;
    }

    public StaffController getStaffController() {
        return staffController;
    }

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    private double findPtReportItemVal(InvestigationItem ii) {
        System.err.println("finding report item val");
        if (currentPatientReport == null) {
            UtilityController.addErrorMessage("No Report to calculate");
            return 0;
        }
        if (currentPatientReport.getPatientReportItemValues() == null) {
            UtilityController.addErrorMessage("Report Items values is null");
            return 0;
        }
        if (currentPatientReport.getPatientReportItemValues().isEmpty()) {
            UtilityController.addErrorMessage("Report Items values is empty");
            return 0;
        }
        //System.out.println("currentPatientReport = " + currentPatientReport);
        //System.out.println("currentPatientReport.getPatientReportItemValues() = " + currentPatientReport.getPatientReportItemValues());

        for (PatientReportItemValue priv : currentPatientReport.getPatientReportItemValues()) {
            if (priv != null) {
                //System.out.println("priv = " + priv);
                //System.out.println("priv in finding val is " + priv.getInvestigationItem().getName());
                //System.out.println("compairing are " + priv.getInvestigationItem().getId() + "  vs " + ii.getId());
                if (Objects.equals(priv.getInvestigationItem().getId(), ii.getId())) {
                    //System.out.println("double val is " + priv.getDoubleValue());
                    if (priv.getDoubleValue() == null) {
                        return 0.0;
                    }
                    return priv.getDoubleValue();
                }
            }
        }
        return 0.0;
    }

    public void calculate() {
        //System.out.println("Gong to calculate");
//        savePatientReport();
        //System.out.println("patient report saved under cal");
        if (currentPatientReport == null) {
            UtilityController.addErrorMessage("No Report to calculate");
            return;
        }
        //System.out.println("currentPatientReport is " + currentPatientReport);
        if (currentPatientReport.getPatientReportItemValues() == null) {
            UtilityController.addErrorMessage("Report Items values is null");
            return;
        }
        //System.out.println("currentPatientReport.getPatientReportItemValues() " + currentPatientReport.getPatientReportItemValues());
        if (currentPatientReport.getPatientReportItemValues().isEmpty()) {
            UtilityController.addErrorMessage("Report Items values is empty");
            return;
        }

        //System.out.println("Gong to calculate");
        for (PatientReportItemValue priv : currentPatientReport.getPatientReportItemValues()) {
            //System.out.println("priv " + priv.toString());
            if (priv.getInvestigationItem().getIxItemType() == InvestigationItemType.Calculation) {
                //System.out.println("priv ix " + priv.getInvestigationItem());
                String sql = "select i from IxCal i where i.calIxItem.id = " + priv.getInvestigationItem().getId();
                //System.out.println("sql is " + sql);
                List<IxCal> ixCals = getIxCalFacade().findBySQL(sql);
                double result = 0;
                Double lastVal = null;
                CalculationType ctype = null;
                //System.out.println("ixcals size is " + ixCals.size());
                for (IxCal c : ixCals) {
                    //System.out.println("c is " + c.getId());
                    if (c.getCalculationType() == CalculationType.Constant) {
                        //System.out.println("constant result before is " + result);
                        if (ctype == null && lastVal == null) {
                            result = c.getConstantValue();
                            //System.out.println("got constant val " + c.getConstantValue());
                        } else if (ctype == CalculationType.Addition) {
                            //System.out.println("result added, before that is " + result);
                            result = result + c.getConstantValue();
                            //System.out.println("result added, after that is " + result);
                        } else if (ctype == CalculationType.Substraction) {
                            //System.out.println("result sub, before that is " + result);
                            result = result - c.getConstantValue();
                            //System.out.println("result sub, after that is " + result);
                        } else if (ctype == CalculationType.Devision) {
                            //System.out.println("going to devide");
                            if (c.getConstantValue() != 0) {
                                result = result / c.getConstantValue();
                            }
                        } else if (ctype == CalculationType.Multiplication) {
                            result = result * c.getConstantValue();
                        }
                        //System.out.println("constant after before is " + result);
                    } else if (c.getCalculationType() == CalculationType.Value) {
                        //System.out.println("val result before is " + result);
                        //System.out.println("c val item is " + c.getValIxItem().getName());
                        double d = findPtReportItemVal(c.getValIxItem());
                        //System.out.println("d is " + d);
                        if (ctype == null && lastVal == null) {
                            result = d;
                        } else if (ctype == CalculationType.Addition) {
                            result = result + d;
                        } else if (ctype == CalculationType.Substraction) {
                            result = result - d;
                        } else if (ctype == CalculationType.Devision) {
                            if (d != 0) {
                                result = result / d;
                            }
                        } else if (ctype == CalculationType.Multiplication) {
                            result = result * d;
                        }
                        ////System.out.println("val result after is " + result);
                    } else {
                        ctype = c.getCalculationType();
                    }
                    priv.setDoubleValue(result);
                }
            } else if (priv.getInvestigationItem().getIxItemType() == InvestigationItemType.Flag) {
                priv.setStrValue(findFlagValue(priv));
            }

//            //System.out.println("priv = " + priv.getStrValue());
            getPirivFacade().edit(priv);
//            //System.out.println("priv = " + priv);
        }

//        getFacade().edit(currentPatientReport);
    }

    private PatientReportItemValue findItemValue(PatientReport pr, InvestigationItem ii) {
//        ////System.out.println("pr is " + pr + " and details");
//        ////System.out.println("ii is " + ii);
        PatientReportItemValue iv = null;

        if (pr != null && ii != null) {
//
//            ////System.out.println("pr ix is " + pr.getItem().getName());
//            ////System.out.println("pr pt is " + pr.getPatientInvestigation().getPatient().getPerson().getName());
//
//            ////System.out.println("ii name is  " + ii.getName());
//
//            ////System.out.println("pr.getPatientReportItemValues() is " + pr.getPatientReportItemValues());
            for (PatientReportItemValue v : pr.getPatientReportItemValues()) {
//                ////System.out.println("v is " + v);
//                ////System.out.println("v str value is " + v.getStrValue());
//                ////System.out.println("v dbl value is " + v.getDoubleValue());
//                ////System.out.println("v iis is " + v.getInvestigationItem());
//                ////System.out.println("v iis name is " + v.getInvestigationItem().getName());

                if (v.getInvestigationItem().equals(ii)) {
//                    ////System.out.println("v equals ii");
                    iv = v;
                } else {
//                    ////System.out.println("v is not compatible");
                }
            }
        }
//        ////System.out.println("iv returning is " + iv);
        return iv;
    }

    private String findFlagValue(PatientReportItemValue v) {
        Map m = new HashMap();
        String sql;
        m.put("s", v.getPatient().getPerson().getSex());
        m.put("f", v.getInvestigationItem());
//        m.put("a", v.getPatient().getAgeInDays());
        sql = "Select f from TestFlag f where f.retired=false and f.investigationItemOfFlagType=:f and f.sex=:s order by f.orderNo";
        List<TestFlag> fs = getTestFlagFacade().findBySQL(sql, m);
        for (TestFlag f : fs) {

            Long a = v.getPatient().getAgeInDays();
            //System.err.println("Age is a" + a);
            //System.err.println("From Age is " + f.getFromAge());
            //System.err.println("To Age is " + f.getToAge());

            ////System.out.println("flah low message " + f.getLowMessage());
            if (f.getFromAge() <= a && f.getToAge() >= a) {
                ////System.out.println("searching val");
                PatientReportItemValue val = findItemValue(currentPatientReport, f.getInvestigationItemOfValueType());
                ////System.out.println("val is " + val);
                if (val == null) {
                    ////System.out.println("val is null");
                    continue;
                }
                Double d = val.getDoubleValue();
                if (d == null || d == 0) {
                    try {
                        if (NumberUtils.isNumber(val.getStrValue())) {
                            d = Double.parseDouble(val.getStrValue());
                        } else {
                            d = 0.0;
                        }

                    } catch (NumberFormatException e) {
                        d = 0.0;
                    }
                }

                ////System.out.println("f is " + f);
                ////System.out.println("d is " + d);
                ////System.out.println("f is not null");
                ////System.out.println("fromVal is " + f.getFromVal());
                ////System.out.println("toVal is " + f.getToVal());
                if (f.getFromVal() > d) {
                    ////System.out.println("dddddddddddddd 1");
                    return f.getLowMessage();
                } else if (f.getToVal() < d) {
                    ////System.out.println("dddddddddddddd 2");
                    return f.getHighMessage();
                } else {
                    ////System.out.println("dddddddddddddd 3");
                    return f.getFlagMessage();
                }
            }
        }
        return "";
    }

    public IxCalFacade getIxCalFacade() {
        return ixCalFacade;
    }

    public void setIxCalFacade(IxCalFacade ixCalFacade) {
        this.ixCalFacade = ixCalFacade;
    }

    public Investigation getAlternativeInvestigation() {
        return alternativeInvestigation;
    }

    public void setAlternativeInvestigation(Investigation alternativeInvestigation) {
        PatientInvestigation pi = new PatientInvestigation();
        this.alternativeInvestigation = alternativeInvestigation;
    }

    public void onCellEdit(CellEditEvent event) {
        try {
        } catch (Exception e) {
            UtilityController.addErrorMessage(e.getMessage());
        }

    }

    public ItemForItemController getItemForItemController() {
        return itemForItemController;
    }

    public void setItemForItemController(ItemForItemController itemForItemController) {
        this.itemForItemController = itemForItemController;
    }

    public Investigation getCurrentReportInvestigation() {
        return currentReportInvestigation;
    }

    public void setCurrentReportInvestigation(Investigation currentReportInvestigation) {
        ////System.out.println("setting currentReportInvestigation - " + currentReportInvestigation.getName());
        this.currentReportInvestigation = currentReportInvestigation;
    }

    public PatientReportItemValueFacade getPirivFacade() {
        return pirivFacade;
    }

    public void setPirivFacade(PatientReportItemValueFacade pirivFacade) {
        this.pirivFacade = pirivFacade;
    }

    public PatientInvestigationItemValueFacade getPiivFacade() {
        return piivFacade;
    }

    public void setPiivFacade(PatientInvestigationItemValueFacade piivFacade) {
        this.piivFacade = piivFacade;
    }

    public PatientInvestigationFacade getPiFacade() {
        return piFacade;
    }

    public void setPiFacade(PatientInvestigationFacade piFacade) {
        this.piFacade = piFacade;
    }

    public void savePatientReportItemValues() {
        if (currentPatientReport != null) {
            for (PatientReportItemValue v : getCurrentPatientReport().getPatientReportItemValues()) {
            //System.out.println("saving ptrtiv + " + v);
                //System.out.println("saving ptrtiv Stre " + v.getStrValue());
                //System.out.println("saving ptrtiv Double " + v.getDoubleValue());
                //System.out.println("saving ptrtiv Lob " + v.getLobValue());
                getPirivFacade().edit(v);
            }
        }
//        if (currentPatientReport != null) {
//            getFacade().edit(currentPatientReport);
//        }
    }

    public void savePatientReport() {
        if (currentPatientReport == null || currentPtIx == null) {
            UtilityController.addErrorMessage("Nothing to save");
            return;
        }

        getCurrentPtIx().setDataEntered(true);
        currentPtIx.setDataEntryAt(Calendar.getInstance().getTime());
        currentPtIx.setDataEntryUser(getSessionController().getLoggedUser());
        currentPtIx.setDataEntryDepartment(getSessionController().getDepartment());

        //System.out.println("1. getPatientReportItemValues() = " + getPatientReportItemValues());
        //System.out.println("2. currentPatientReport.getReportItemValues() = " + currentPatientReport.getPatientReportItemValues());
        //System.out.println("3. currentPatientReport.getReportItemValues() = " + currentPatientReport.getPatientReportItemValues());
        currentPatientReport.setDataEntered(Boolean.TRUE);

        currentPatientReport.setDataEntryAt(Calendar.getInstance().getTime());
        currentPatientReport.setDataEntryDepartment(getSessionController().getLoggedUser().getDepartment());
        currentPatientReport.setDataEntryInstitution(getSessionController().getLoggedUser().getInstitution());
        currentPatientReport.setDataEntryUser(getSessionController().getLoggedUser());

        getFacade().edit(currentPatientReport);
        getPiFacade().edit(currentPtIx);

        UtilityController.addSuccessMessage("Saved");
    }

    public void approvePatientReport() {
        if (currentPatientReport == null) {
            UtilityController.addErrorMessage("Nothing to approve");
            return;
        }
        if (currentPatientReport.getDataEntered() == false) {
            UtilityController.addErrorMessage("First Save report");
            return;
        }

        getCurrentPtIx().setApproved(true);
        currentPtIx.setApproveAt(Calendar.getInstance().getTime());
        currentPtIx.setApproveUser(getSessionController().getLoggedUser());
        currentPtIx.setApproveDepartment(getSessionController().getDepartment());
        getPiFacade().edit(currentPtIx);
        currentPatientReport.setApproved(Boolean.FALSE);
        currentPatientReport.setApproved(Boolean.TRUE);
        currentPatientReport.setApproveAt(Calendar.getInstance().getTime());
        currentPatientReport.setApproveDepartment(getSessionController().getLoggedUser().getDepartment());
        currentPatientReport.setApproveInstitution(getSessionController().getLoggedUser().getInstitution());
        currentPatientReport.setApproveUser(getSessionController().getLoggedUser());
        getFacade().edit(currentPatientReport);
        getStaffController().setCurrent(getSessionController().getLoggedUser().getStaff());
        getTransferController().setStaff(getSessionController().getLoggedUser().getStaff());
        UtilityController.addSuccessMessage("Approved");
    }

    public void printPatientReport() {
        ////System.out.println("going to save as printed");
        if (currentPatientReport == null) {
            UtilityController.addErrorMessage("Nothing to approve");
            return;
        }
        currentPtIx.setPrinted(true);
        currentPtIx.setPrintingAt(Calendar.getInstance().getTime());
        currentPtIx.setPrintingUser(getSessionController().getLoggedUser());
        currentPtIx.setPrintingDepartment(getSessionController().getDepartment());
        getPiFacade().edit(currentPtIx);

        currentPatientReport.setPrinted(Boolean.TRUE);
        currentPatientReport.setPrintingAt(Calendar.getInstance().getTime());
        currentPatientReport.setPrintingDepartment(getSessionController().getLoggedUser().getDepartment());
        currentPatientReport.setPrintingInstitution(getSessionController().getLoggedUser().getInstitution());
        currentPatientReport.setPrintingUser(getSessionController().getLoggedUser());
        getFacade().edit(currentPatientReport);

    }

    public String getSelectText() {
        return selectText;
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public PatientReportFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PatientReportFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public PatientReportController() {
    }

    private PatientReportFacade getFacade() {
        return ejbFacade;
    }

    public PatientInvestigation getCurrentPtIx() {
        if (currentPtIx == null) {
            if (currentPatientReport != null) {
                currentPtIx = currentPatientReport.getPatientInvestigation();
            }

        }
        return currentPtIx;
    }

    public PatientReport createNewPatientReport(PatientInvestigation pi, Investigation ix) {
        //System.err.println("creating a new patient report");
        PatientReport r = null;
        if (pi != null && pi.getId() != null && ix != null) {
            r = new PatientReport();
            r.setCreatedAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            r.setCreater(getSessionController().getLoggedUser());
            r.setItem(ix);

            getEjbFacade().create(r);
            r.setPatientInvestigation(pi);

            getPrBean().addPatientReportItemValuesForReport(r);

            getEjbFacade().edit(r);
            setCurrentPatientReport(r);
            pi.getPatientReports().add(r);
            getCommonReportItemController().setCategory(ix.getReportFormat());
        } else {
            UtilityController.addErrorMessage("No ptIx or Ix selected to add");
        }
        return r;
    }

    public PatientReport createNewMicrobiologyReport(PatientInvestigation pi, Investigation ix) {
        //System.err.println("creating a new microbiology report");
        PatientReport r = null;
        if (pi != null && pi.getId() != null && ix != null) {
            r = new PatientReport();
            r.setCreatedAt(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
            r.setCreater(getSessionController().getLoggedUser());
            r.setItem(ix);
            getEjbFacade().create(r);
            r.setPatientInvestigation(pi);
            getPrBean().addMicrobiologyReportItemValuesForReport(r);
            getEjbFacade().edit(r);
            setCurrentPatientReport(r);
            pi.getPatientReports().add(r);
            getCommonReportItemController().setCategory(ix.getReportFormat());
        } else {
            UtilityController.addErrorMessage("No ptIx or Ix selected to add");
        }
        return r;
    }

    public void setCurrentPtIx(PatientInvestigation currentPtIx) {
        this.currentPtIx = currentPtIx;
    }

    public PatientReportBean getPrBean() {
        return prBean;
    }

    public void setPrBean(PatientReportBean prBean) {
        this.prBean = prBean;
    }
    @Inject
    ReportFormatController reportFormatController;
    @Inject
    CommonReportItemController commonReportItemController;

    public ReportFormatController getReportFormatController() {
        return reportFormatController;
    }

    public void setReportFormatController(ReportFormatController reportFormatController) {
        this.reportFormatController = reportFormatController;
    }

    public CommonReportItemController getCommonReportItemController() {
        return commonReportItemController;
    }

    public void setCommonReportItemController(CommonReportItemController commonReportItemController) {
        this.commonReportItemController = commonReportItemController;
    }

    public PatientReport getCurrentPatientReport() {
//        PatientReport cpt;
//        if (currentPatientReport != null && currentPatientReport.getId() != null && currentPatientReport.getId() != 0) {
//            cpt = getFacade().find(currentPatientReport.getId());
//            currentPatientReport = cpt;
//        }
        return currentPatientReport;
    }

    public void createNewReport(PatientInvestigation pi) {
        Investigation ix = (Investigation) pi.getInvestigation().getReportedAs();
        currentReportInvestigation = ix;
        currentPtIx = pi;
        if (ix.getReportType() == InvestigationReportType.Microbiology) {
            createNewMicrobiologyReport(pi, ix);
        } else {
            createNewPatientReport(pi, ix);
        }
        getCommonReportItemController().setCategory(ix.getReportFormat());

    }

    public String enterNewReportFormat(PatientInvestigation pi, Investigation ix) {
        currentReportInvestigation = ix;
        currentPtIx = pi;
        createNewPatientReport(pi, ix);
        getCommonReportItemController().setCategory(ix.getReportFormat());
        return "/lab_patient_report";
    }

    public void setCurrentPatientReport(PatientReport currentPatientReport) {
        this.currentPatientReport = currentPatientReport;
        if (currentPatientReport != null) {
            getCommonReportItemController().setCategory(currentPatientReport.getItem().getReportFormat());
            currentPtIx = currentPatientReport.getPatientInvestigation();
        }
    }

    public TestFlagFacade getTestFlagFacade() {
        return testFlagFacade;
    }

    public void setTestFlagFacade(TestFlagFacade testFlagFacade) {
        this.testFlagFacade = testFlagFacade;
    }

    public TransferController getTransferController() {
        return transferController;
    }

    public void setTransferController(TransferController transferController) {
        this.transferController = transferController;
    }

    @FacesConverter(forClass = PatientReport.class)
    public static class PatientReportControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PatientReportController controller = (PatientReportController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "patientReportController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            if (NumberUtils.isNumber(value)) {
                key = Long.valueOf(value);
            } else {
                key = 0l;
            }
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
            if (object instanceof PatientReport) {
                PatientReport o = (PatientReport) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PatientReportController.class.getName());
            }
        }
    }

}
