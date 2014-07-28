/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.report;

import com.divudi.bean.common.DepartmentController;
import com.divudi.bean.common.UtilityController;
import com.divudi.bean.inward.AdmissionTypeController;
import com.divudi.data.BillType;
import com.divudi.data.FeeType;
import com.divudi.data.PaymentMethod;
import com.divudi.data.dataStructure.DepartmentPayment;
import com.divudi.data.table.String1Value2;
import com.divudi.data.table.String1Value3;
import com.divudi.data.table.String3Value2;
import com.divudi.bean.common.BillBeanController;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.Bill;
import com.divudi.entity.BillItem;
import com.divudi.entity.Category;
import com.divudi.entity.Department;
import com.divudi.entity.Institution;
import com.divudi.entity.Item;
import com.divudi.entity.Speciality;
import com.divudi.entity.inward.AdmissionType;
import com.divudi.facade.BillFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class BookKeepingSummery implements Serializable {

    Date fromDate;
    Date toDate;
    Institution institution;
    @EJB
    CommonFunctions commonFunctions;
    @Inject
    BillBeanController billBean;
    @EJB
    BillFacade billFacade;
    //List
    private List<String1Value3> opdList;
    List<String1Value2> outSideFees;
    private List<String1Value2> pharmacySales;
    List<String1Value2> collections2Hos;
    List<String1Value2> finalValues;
    List<String3Value2> inwardCollections;
    private List<Bill> agentCollections;
    List<Bill> creditCardBill;
    List<Bill> slipBill;
    List<Bill> chequeBill;
    List<BillItem> creditCompanyCollections;
    List<DepartmentPayment> departmentProfessionalPayments;
    List<String1Value2> inwardProfessionalPayments;
    //Value
    double opdHospitalTotal;
    double outSideFeeTotal;
    double pharmacyTotal;
    double inwardPaymentTotal;
    double agentPaymentTotal;
    double creditCompanyTotal;
    double pettyCashTotal;
    double departmentProfessionalPaymentTotal;
    double inwardProfessionalPaymentTotal;
    double creditCardTotal;
    double chequeTotal;
    double slipTotal;
    double grantTotal;

    public void makeNull() {
        //List
        opdList = null;
        outSideFees = null;
        pharmacySales = null;
        collections2Hos = null;
        finalValues = null;
        inwardCollections = null;
        agentCollections = null;
        creditCardBill = null;
        slipBill = null;
        chequeBill = null;
        creditCompanyCollections = null;
        departmentProfessionalPayments = null;
        inwardProfessionalPayments = null;
        //Value
        opdHospitalTotal = 0;
        outSideFeeTotal = 0;
        pharmacyTotal = 0;
        inwardPaymentTotal = 0;
        agentPaymentTotal = 0;
        creditCompanyTotal = 0;
        pettyCashTotal = 0;
        departmentProfessionalPaymentTotal = 0;
        inwardProfessionalPaymentTotal = 0;
        creditCardTotal = 0;
        chequeTotal = 0;
        slipTotal = 0;
        grantTotal = 0;

    }

    public double getInwardProfessionalPaymentTotal() {
        return inwardProfessionalPaymentTotal;
    }

    public void setInwardProfessionalPaymentTotal(double inwardProfessionalPaymentTotal) {
        this.inwardProfessionalPaymentTotal = inwardProfessionalPaymentTotal;
    }

    public double getDepartmentProfessionalPaymentTotal() {
        return departmentProfessionalPaymentTotal;
    }

    public void setDepartmentProfessionalPaymentTotal(double departmentProfessionalPaymentTotal) {
        this.departmentProfessionalPaymentTotal = departmentProfessionalPaymentTotal;
    }

    public List<DepartmentPayment> getDepartmentPayments() {
        if (departmentProfessionalPayments == null) {
            departmentProfessionalPayments = new ArrayList<>();
        }
        return departmentProfessionalPayments;
    }

    public void setDepartmentPayments(List<DepartmentPayment> departmentPayments) {
        this.departmentProfessionalPayments = departmentPayments;
    }

    public List<String1Value2> getFinalValues() {
        if (finalValues == null) {
            finalValues = new ArrayList<>();
        }
        return finalValues;
    }

    public void setFinalValues(List<String1Value2> finalValues) {
        this.finalValues = finalValues;
    }

    public double getCreditCardTotal() {
        return creditCardTotal;
    }

    public void setCreditCardTotal(double creditCardTotal) {
        this.creditCardTotal = creditCardTotal;
    }

    public double getChequeTotal() {
        return chequeTotal;
    }

    public void setChequeTotal(double chequeTotal) {
        this.chequeTotal = chequeTotal;
    }

    public double getSlipTotal() {
        return slipTotal;
    }

    public void setSlipTotal(double slipTotal) {
        this.slipTotal = slipTotal;
    }

    public List<Bill> getCreditCardBill() {
        return creditCardBill;
    }

    public void setCreditCardBill(List<Bill> creditCardBill) {
        this.creditCardBill = creditCardBill;
    }

    public List<Bill> getSlipBill() {
        return slipBill;
    }

    public void setSlipBill(List<Bill> slipBill) {
        this.slipBill = slipBill;
    }

    public List<Bill> getChequeBill() {
        return chequeBill;
    }

    public void setChequeBill(List<Bill> chequeBill) {
        this.chequeBill = chequeBill;
    }

    public double getPettyCashTotal() {
        return pettyCashTotal;
    }

    public void setPettyCashTotal(double pettyCashTotal) {
        this.pettyCashTotal = pettyCashTotal;
    }

    public double getPettyTotal() {
        return pettyCashTotal;
    }

    public void setPettyTotal(double pettyTotal) {
        this.pettyCashTotal = pettyTotal;
    }

    public double getCreditCompanyTotal() {
        return creditCompanyTotal;
    }

    public void setCreditCompanyTotal(double creditCompanyTotal) {
        this.creditCompanyTotal = creditCompanyTotal;
    }

    public double getAgentPaymentTotal() {
        return agentPaymentTotal;
    }

    public void setAgentPaymentTotal(double agentPaymentTotal) {
        this.agentPaymentTotal = agentPaymentTotal;
    }

    public double getInwardPaymentTotal() {
        return inwardPaymentTotal;
    }

    public void setInwardPaymentTotal(double inwardPaymentTotal) {
        this.inwardPaymentTotal = inwardPaymentTotal;
    }

    public double getPharmacyTotal() {
        return pharmacyTotal;
    }

    public void setPharmacyTotal(double pharmacyTotal) {
        this.pharmacyTotal = pharmacyTotal;
    }

    public double getOutSideFeeTotal() {
        return outSideFeeTotal;
    }

    public void setOutSideFeeTotal(double outSideFeeTotal) {
        this.outSideFeeTotal = outSideFeeTotal;
    }

    public double getGrantTotal() {
        return grantTotal;
    }

    public void setGrantTotal(double grantTotal) {
        this.grantTotal = grantTotal;
    }

    public List<String1Value2> getCollections2Hos() {
        if (collections2Hos == null) {
            collections2Hos = new ArrayList<>();
        }
        return collections2Hos;
    }

    public void setCollections2Hos(List<String1Value2> collections2Hos) {
        this.collections2Hos = collections2Hos;
    }

    public List<BillItem> getCreditCompanyCollections() {
        if (creditCompanyCollections == null) {
            creditCompanyCollections = new ArrayList<>();
        }
        return creditCompanyCollections;
    }

    public void setCreditCompanyCollections(List<BillItem> creditCompanyCollections) {
        this.creditCompanyCollections = creditCompanyCollections;
    }

    public List<Bill> getAgentCollections() {
        if (agentCollections == null) {
            agentCollections = new ArrayList<>();
        }
        return agentCollections;
    }

    public void setAgentCollections(List<Bill> agentCollections) {
        this.agentCollections = agentCollections;
    }

    public List<String3Value2> getInwardCollections() {
        if (inwardCollections == null) {
            inwardCollections = new ArrayList<>();
        }
        return inwardCollections;
    }

    public void setInwardCollections(List<String3Value2> inwardCollections) {
        this.inwardCollections = inwardCollections;
    }

    public List<String1Value2> getOutSideFees() {
        if (outSideFees == null) {
            outSideFees = new ArrayList<>();
        }
        return outSideFees;
    }

    public void setOutSideFees(List<String1Value2> outSideFees) {
        this.outSideFees = outSideFees;
    }

    public List<String1Value2> getPharmacySales() {
        if (pharmacySales == null) {
            pharmacySales = new ArrayList<>();
        }
        return pharmacySales;
    }

    public void setPharmacySales(List<String1Value2> pharmacySales) {
        this.pharmacySales = pharmacySales;
    }

    public double getOpdHospitalTotal() {
        return opdHospitalTotal;
    }

    public void setOpdHospitalTotal(double opdHospitalTotal) {
        this.opdHospitalTotal = opdHospitalTotal;
    }

    public List<String1Value3> getOpdList() {
        if (opdList == null) {
            opdList = new ArrayList<>();
        }
        return opdList;
    }

    public void setOpdList(List<String1Value3> opdList) {
        this.opdList = opdList;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            fromDate = getCommonFunctions().getStartOfDay(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
        }
        return fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = getCommonFunctions().getEndOfDay(Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime());
        }
        return toDate;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public BillBeanController getBillBean() {
        return billBean;
    }

    public void setBillBean(BillBeanController billBean) {
        this.billBean = billBean;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

//    public void createOPdListDayEndTable() {
//        System.err.println("createOPdCategoryTable");
//        opdList = new ArrayList<>();
//        for (Category cat : getBillBean().fetchBilledOpdCategory(fromDate, toDate, institution)) {
//            System.err.println("Cat " + cat.getName() + " TIME " + new Date());
//            System.err.println("##################");
//            for (Item i : getBillBean().fetchBilledOpdItem(cat, fromDate, toDate, institution)) {
//                //   System.err.println("Item " + i.getName() + " TIME " + new Date());
//            
//                double count = getBillBean().calBilledItemCount(i, getFromDate(), getToDate(), getInstitution());
//                double hos = getBillBean().calFeeValue(i, FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());
//                //     double pro = getFee(i, FeeType.Staff);
//
//                if (count != 0) {
//                    String1Value2 newD = new String1Value2();
//                    newD.setString(i.getName());
//                    newD.setValue1(count);
//                    newD.setValue2(hos);
//                    newD.setSummery(false);
//                    opdList.add(newD);
//                }
//            }
//
//            double catDbl = getBillBean().calFeeValue(cat, FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());
//
//            if (catDbl != 0) {
//                String1Value2 newD = new String1Value2();
//                newD.setString(cat.getName() + " Total : ");
//                newD.setValue2(catDbl);
//                newD.setSummery(true);
//                opdList.add(newD);
//            }
//
//        }
//
//    }
    public void createOPdListDayEndTable() {
        System.err.println("createOPdCategoryTable");
        opdList = new ArrayList<>();
        for (Category cat : getBillBean().fetchBilledOpdCategory(fromDate, toDate, institution)) {
            System.err.println("Cat " + cat.getName() + " TIME " + new Date());
            System.err.println("##################");

            List<Object[]> list = getBillBean().fetchBilledOpdItem(cat, FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());

            if (list == null) {
                continue;
            }

            for (Object[] obj : list) {
                Item item = (Item) obj[0];
                Double dbl = (Double) obj[1];
                double count = 0;

                if (dbl != null && dbl != 0) {
                    count = getBillBean().calBilledItemCount(item, getFromDate(), getToDate(), getInstitution());
                }

                String1Value3 newD = new String1Value3();
                newD.setString(item.getName());
                newD.setValue1(count);
                newD.setValue2(dbl);
                newD.setSummery(false);
                opdList.add(newD);

            }

            double catDbl = getBillBean().calFeeValue(cat, FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());

            if (catDbl != 0) {
                String1Value3 newD = new String1Value3();
                newD.setString(cat.getName() + " Total : ");
                newD.setValue2(catDbl);
                newD.setSummery(true);
                opdList.add(newD);
            }

        }

    }

    public void createOPdListWithProDayEndTable() {
        System.err.println("createOPdCategoryTable");
        opdList = new ArrayList<>();
        for (Category cat : getBillBean().fetchBilledOpdCategory(fromDate, toDate, institution)) {
            System.err.println("Cat " + cat.getName() + " TIME " + new Date());
            System.err.println("##################");

            List<Item> list = getBillBean().fetchBilledOpdItem(cat, getFromDate(), getToDate(), getInstitution());

            if (list == null) {
                continue;
            }

            for (Item item : list) {

                double hosFee = 0;
                double proFee = 0;
                double count = 0;

                hosFee = getBillBean().calFeeValue(item, FeeType.OwnInstitution, fromDate, toDate, institution);
                proFee = getBillBean().calFeeValue(item, FeeType.Staff, fromDate, toDate, institution);
                count = getBillBean().calBilledItemCount(item, getFromDate(), getToDate(), getInstitution());

                String1Value3 newD = new String1Value3();
                newD.setString(item.getName());
                newD.setValue1(count);
                newD.setValue2(hosFee);
                newD.setValue3(proFee);
                newD.setSummery(false);
                opdList.add(newD);

            }

            double catDbl = getBillBean().calFeeValue(cat, getFromDate(), getToDate(), getInstitution());

            if (catDbl != 0) {
                String1Value3 newD = new String1Value3();
                newD.setString(cat.getName() + " Total : ");
                newD.setValue3(catDbl);
                newD.setSummery(true);
                opdList.add(newD);
            }

        }

    }

    public void createOPdListMonthEndTable() {
        System.err.println("createOPdCategoryTable");
        opdList = new ArrayList<>();

        List<Object[]> list = getBillBean().fetchFeeValue(FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());

        if (list == null || list.isEmpty()) {
            return;
        }

        for (Object[] obj : list) {
            Category cat = (Category) obj[0];
            Double dbl = (Double) obj[1];

            String1Value3 newD = new String1Value3();
            newD.setString(cat.getName());
            newD.setValue2(dbl);
            opdList.add(newD);

        }
    }

    public void createOPdListMonthEndTableWithPro() {
        System.err.println("createOPdCategoryTable");
        opdList = new ArrayList<>();

        List<Object[]> list = getBillBean().fetchFeeValue(getFromDate(), getToDate(), getInstitution());

        if (list == null || list.isEmpty()) {
            return;
        }

        for (Object[] obj : list) {
            Category cat = (Category) obj[0];
            Double dbl = (Double) obj[1];

            String1Value3 newD = new String1Value3();
            newD.setString(cat.getName());
            newD.setValue2(dbl);
            opdList.add(newD);

        }
    }

    public void createOutSideFee() {
        System.err.println("Out side Fees");
        outSideFees = new ArrayList<>();
        List<Object[]> list = getBillBean().fetchOutSideInstitutionFees(getFromDate(), getToDate(), getInstitution());

        for (Object[] obj : list) {
            String1Value2 newRow = new String1Value2();
            Department dep = ((Department) obj[0]);
            Double value = (Double) obj[1];
            System.err.println("ins " + dep);
            System.err.println("VAL " + value);
            newRow.setString(dep.getName());
            newRow.setValue1(value);

            if (value != 0) {
                getOutSideFees().add(newRow);
            }
        }
    }

    public void createOutSideFeeWithPro() {
        System.err.println("Out side Fees");
        outSideFees = new ArrayList<>();
        List<Object[]> list = getBillBean().fetchOutSideDepartment(getFromDate(), getToDate(), getInstitution());

        for (Object[] obj : list) {
            String1Value2 newRow = new String1Value2();
            Department department = (Department) obj[0];
            double hosFee = (double) obj[1];
            double proFee = (double) obj[2];

            newRow.setString(department.getName());
            newRow.setValue1(hosFee);
            newRow.setValue2(proFee);

            if (proFee != 0 || hosFee != 0) {
                getOutSideFees().add(newRow);
            }
        }
    }

    @Inject
    DepartmentController departmentController;

    public DepartmentController getDepartmentController() {
        return departmentController;
    }

    public void setDepartmentController(DepartmentController departmentController) {
        this.departmentController = departmentController;
    }

    private void createPharmacySale() {
        System.err.println("createPharmacySale");
        pharmacySales = new ArrayList<>();

        //System.err.println("DEP " + d.getName());
        List<Object[]> list = getBillBean().fetchDepartmentSale(getFromDate(), getToDate(), getInstitution());

        for (Object[] obj : list) {
            String1Value2 newRow = new String1Value2();
            Department department = ((Department) obj[0]);
            Double value = (Double) obj[1];

            if (department != null) {
                newRow.setString(department.getName());
            }

            if (value != null) {
                newRow.setValue1(value);
            }

            if (value != null) {
                getPharmacySales().add(newRow);
            }
        }

    }

    @Inject
    AdmissionTypeController admissionTypeController;

    public AdmissionTypeController getAdmissionTypeController() {
        return admissionTypeController;
    }

    public void setAdmissionTypeController(AdmissionTypeController admissionTypeController) {
        this.admissionTypeController = admissionTypeController;
    }

    public void createInwardCollection() {
        System.err.println("createInwardCollection");
        inwardCollections = new ArrayList<>();
        for (AdmissionType at : getAdmissionTypeController().getItems()) {
            Double grantDbl = getBillBean().calInwardPaymentTotal(at, fromDate, toDate, institution);

            //HEADER
            String3Value2 newRow = new String3Value2();
            newRow.setString1(at.getName() + " : ");
            newRow.setSummery(true);

            if (grantDbl != 0) {
                getInwardCollections().add(newRow);
            }

            //BILLS
            for (Bill b : getBillBean().fetchInwardPaymentBills(at, fromDate, toDate, institution)) {
                newRow = new String3Value2();
                newRow.setString1(b.getPatientEncounter().getBhtNo());
                newRow.setString2(b.getInsId());
                newRow.setString3(b.getPatientEncounter().getPatient().getPerson().getName());

                Double dbl = b.getNetTotal();
                newRow.setValue1(dbl);

                getInwardCollections().add(newRow);
            }

            //FOOTER
            newRow = new String3Value2();
            newRow.setString1(at.getName() + " Total : ");
            newRow.setSummery(true);

            newRow.setValue2(grantDbl);

            if (grantDbl != 0) {
                getInwardCollections().add(newRow);
            }

        }
    }

    public void createInwardCollectionMonth() {
        System.err.println("createInwardCollection");
        inwardCollections = new ArrayList<>();
        for (AdmissionType at : getAdmissionTypeController().getItems()) {
            System.err.println("1 " + at.getName());
            String3Value2 newRow = new String3Value2();
            newRow.setString1(at.getName());
            //    newRow.setSummery(true);
            Double grantDbl = getBillBean().calInwardPaymentTotal(at, getFromDate(), getToDate(), getInstitution());
            newRow.setValue2(grantDbl);

            if (grantDbl != 0) {
                getInwardCollections().add(newRow);
            }

        }
    }

    public void calGrantTotal2HosWithoutPro() {
        grantTotal = 0.0;

        grantTotal = opdHospitalTotal
                + outSideFeeTotal
                + pharmacyTotal
                + inwardPaymentTotal
                + agentPaymentTotal
                + creditCompanyTotal
                + pettyCashTotal;

    }

    private void createCollections2Hos() {
        System.err.println("createCollections2Hos");
        collections2Hos = new ArrayList<>();
        String1Value2 dd;
        /////////////////
        dd = new String1Value2();
        dd.setString("Collection For the Day");
        calGrantTotal2HosWithoutPro();
        Double dbl = getGrantTotal();
        dbl = dbl - pettyCashTotal;
        dd.setValue1(dbl);
        collections2Hos.add(dd);
        //////////////////////
        dd = new String1Value2();
        dd.setString("Petty cash Payments");
        dbl = pettyCashTotal;
        dd.setValue1(dbl);
        collections2Hos.add(dd);

    }

    private void createCollections2HosMonth() {
        System.err.println("createCollections2Hos");
        collections2Hos = new ArrayList<>();
        String1Value2 dd;
        ////////////////
        dd = new String1Value2();
        dd.setString("Agent Collections ");
        dd.setValue1(agentPaymentTotal);
        collections2Hos.add(dd);
        //////////////////
        dd = new String1Value2();
        dd.setString("Credit Company Collections ");
        dd.setValue1(creditCompanyTotal);
        collections2Hos.add(dd);
        /////////////////
        dd = new String1Value2();
        dd.setString("Collection For the Day");
        calGrantTotal2HosWithoutPro();
        Double dbl = getGrantTotal();
        dbl = dbl - pettyCashTotal;
        dd.setValue1(dbl);
        collections2Hos.add(dd);
        //////////////////////
        dd = new String1Value2();
        dd.setString("Petty cash Payments");
        dbl = pettyCashTotal;
        dd.setValue1(dbl);
        collections2Hos.add(dd);

    }

    public void createCashCategoryWithoutProDay() {
        makeNull();
        long lng = getCommonFunctions().getDayCount(getFromDate(), getToDate());

        if (Math.abs(lng) > 2) {
            UtilityController.addErrorMessage("Date Range is too Long");
            return;
        }

        createOPdListDayEndTable();
        createOutSideFee();
        createPharmacySale();
        createInwardCollection();
        agentCollections = agentCollections = getBillBean().fetchBills(BillType.AgentPaymentReceiveBill, getFromDate(), getToDate(), getInstitution());
        creditCompanyCollections = getBillBean().fetchBillItems(BillType.CashRecieveBill, fromDate, toDate, institution);
        ///////////////////
        opdHospitalTotal = getBillBean().calFeeValue(FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());
        outSideFeeTotal = getBillBean().calOutSideInstitutionFees(fromDate, toDate, institution);
        pharmacyTotal = getBillBean().calInstitutionSale(fromDate, toDate, institution);
        inwardPaymentTotal = getBillBean().calInwardPaymentTotal(fromDate, toDate, institution);
        agentPaymentTotal = getBillBean().calBillTotal(BillType.AgentPaymentReceiveBill, fromDate, toDate, institution);
        creditCompanyTotal = getBillBean().calBillTotal(BillType.CashRecieveBill, fromDate, toDate, institution);
        pettyCashTotal = getBillBean().calBillTotal(BillType.PettyCash, fromDate, toDate, institution);
        createCollections2Hos();

//        createDoctorPaymentInward();

        createDoctorPaymentInwardByCategoryAndSpeciality();

        creditCardBill = getBillBean().fetchBills(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeBill = getBillBean().fetchBills(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipBill = getBillBean().fetchBills(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        /////////////////
        inwardProfessionalPaymentTotal = getBillBean().calDoctorPaymentInward(fromDate, toDate);
        creditCardTotal = getBillBean().calBillTotal(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeTotal = getBillBean().calBillTotal(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipTotal = getBillBean().calBillTotal(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        createFinalSummery();
    }

    public void createDoctorPaymentOpd() {
        departmentProfessionalPayments = new ArrayList<>();
        List<Object[]> list = getBillBean().fetchDoctorPayment(fromDate, toDate, BillType.OpdBill);

        for (Object[] obj : list) {
            Department department = (Department) obj[0];
            double dbl = (Double) obj[1];

            DepartmentPayment newRow = new DepartmentPayment();
            newRow.setDepartment(department);
            newRow.setTotalPayment(dbl);

            if (dbl != 0) {
                departmentProfessionalPayments.add(newRow);
            }

        }

    }

    public List<DepartmentPayment> getDepartmentProfessionalPayments() {
        return departmentProfessionalPayments;
    }

    public void setDepartmentProfessionalPayments(List<DepartmentPayment> departmentProfessionalPayments) {
        this.departmentProfessionalPayments = departmentProfessionalPayments;
    }

    public List<String1Value2> getInwardProfessionalPayments() {
        return inwardProfessionalPayments;
    }

    public void setInwardProfessionalPayments(List<String1Value2> inwardProfessionalPayments) {
        this.inwardProfessionalPayments = inwardProfessionalPayments;
    }

    public void createDoctorPaymentInwardByCategoryAndSpeciality() {
        professionalPaymentsByAdmissionTypeAndCategorys = new ArrayList<>();
        String sql = "Select b.paidForBillFee.bill.patientEncounter.admissionType.name,"
                + " b.paidForBillFee.staff.speciality.name , sum(b.netValue) "
                + " FROM BillItem b "
                + " where b.retired=false "
                + " and b.bill.billType=:bType "
                + " and (b.paidForBillFee.bill.billType=:refType1 "
                + " or b.paidForBillFee.bill.billType=:refType2 )"
                + " and b.bill.createdAt between :fromDate and :toDate"
                + " group by b.paidForBillFee.bill.patientEncounter.admissionType.name, b.paidForBillFee.staff.speciality.name "
                + " order by b.paidForBillFee.bill.patientEncounter.admissionType.name, b.paidForBillFee.staff.speciality.name ";
        HashMap hm = new HashMap();
        hm.put("bType", BillType.PaymentBill);
        hm.put("refType1", BillType.InwardBill);
        hm.put("refType2", BillType.InwardProfessional);
        hm.put("fromDate", fromDate);
        hm.put("toDate", toDate);
     //   System.out.println("hm = " + hm);
     //   System.out.println("sql = " + sql);
        List<Object[]> objs = getBillFacade().findAggregates(sql, hm, TemporalType.TIMESTAMP);
     //   System.out.println("objs = " + objs);
        ProfessionalPaymentsByAdmissionTypeAndCategory thisPro = null;
        ProfessionalPaymentsByAdmissionTypeAndCategory prePro = null;
        ProfessionalPaymentsByAdmissionTypeAndCategory addPro = null;

        double admittionTypeTptal = 0.0;
        double totalProfessinal = 0.0;

        for (Object[] o : objs) {
            thisPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
            thisPro.setAdmissionType(o[0].toString());
            thisPro.setSpeciality(o[1].toString());

            try {
                thisPro.setSpecialityValie(Double.valueOf(o[2].toString()));
            } catch (NumberFormatException e) {
                thisPro.setSpecialityValie(0.0);
            }

            totalProfessinal = totalProfessinal + thisPro.getSpecialityValie();

            if (prePro == null) {
                admittionTypeTptal = admittionTypeTptal + thisPro.getSpecialityValie();
                prePro = thisPro;

                addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
                addPro.setAdmissionType(thisPro.getAdmissionType());
                professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);

                addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
                addPro.setSpeciality(thisPro.getSpeciality());
                addPro.setSpecialityValie(thisPro.getSpecialityValie());
                professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);
            } else {
                if (prePro.getAdmissionType().equals(thisPro.getAdmissionType())) {
                    admittionTypeTptal = admittionTypeTptal + thisPro.getSpecialityValie();

                    addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
                    addPro.setSpeciality(thisPro.getSpeciality());
                    addPro.setSpecialityValie(thisPro.getSpecialityValie());
                    professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);

                    prePro = thisPro;
                } else {

                    addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
                    addPro.setAdmissionType(thisPro.getAdmissionType());
                    addPro.setAdmissionTypeValue(admittionTypeTptal);
                    professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);

                    admittionTypeTptal = thisPro.getSpecialityValie();

                    addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
                    addPro.setSpeciality(thisPro.getSpeciality());
                    addPro.setSpecialityValie(thisPro.getSpecialityValie());
                    professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);

                    prePro = thisPro;
                }
            }

        }

        addPro = new ProfessionalPaymentsByAdmissionTypeAndCategory();
        addPro.setAdmissionTypeValue(admittionTypeTptal);
        professionalPaymentsByAdmissionTypeAndCategorys.add(addPro);

        inwardProfessionalPaymentTotal = totalProfessinal;

    }

    public void createDoctorPaymentInward() {
        inwardProfessionalPayments = new ArrayList<>();
        List<Object[]> list = getBillBean().fetchDoctorPaymentInward(fromDate, toDate);

        for (Object[] obj : list) {
            AdmissionType admissionType = (AdmissionType) obj[0];
            double dbl = (Double) obj[1];

            String1Value2 header = new String1Value2();
            header.setSummery(true);
            header.setString(admissionType.getName());

            if (dbl != 0) {
                inwardProfessionalPayments.add(header);
            }

            List<Object[]> listInner = getBillBean().fetchDoctorPaymentInward(admissionType, fromDate, toDate);

            for (Object[] objIn : listInner) {
                Speciality speciality = (Speciality) objIn[0];
                dbl = (Double) objIn[1];

                if (dbl != 0) {
                    String1Value2 data = new String1Value2();
                    data.setString(speciality.getName());
                    data.setValue1(dbl);
                    inwardProfessionalPayments.add(data);
                }
            }

            String1Value2 footer = new String1Value2();
            footer.setSummery(true);
            footer.setString(admissionType.getName() + " Total : ");
            footer.setValue2(dbl);

            if (dbl != 0) {
                inwardProfessionalPayments.add(footer);
            }

        }

    }

    public void createCashCategoryWithProDay() {
        makeNull();
        long lng = getCommonFunctions().getDayCount(getFromDate(), getToDate());

        if (Math.abs(lng) > 2) {
            UtilityController.addErrorMessage("Date Range is too Long");
            return;
        }

        createOPdListWithProDayEndTable();
        createOutSideFeeWithPro();
        createPharmacySale();
        createInwardCollection();
        agentCollections = agentCollections = getBillBean().fetchBills(BillType.AgentPaymentReceiveBill, getFromDate(), getToDate(), getInstitution());
        creditCompanyCollections = getBillBean().fetchBillItems(BillType.CashRecieveBill, fromDate, toDate, institution);
        createDoctorPaymentOpd();
        createDoctorPaymentInward();
        ///////////////////
        opdHospitalTotal = getBillBean().calFeeValue(getFromDate(), getToDate(), getInstitution());
        outSideFeeTotal = getBillBean().calOutSideInstitutionFeesWithPro(fromDate, toDate, institution);
        pharmacyTotal = getBillBean().calInstitutionSale(fromDate, toDate, institution);
        inwardPaymentTotal = getBillBean().calInwardPaymentTotal(fromDate, toDate, institution);
        agentPaymentTotal = getBillBean().calBillTotal(BillType.AgentPaymentReceiveBill, fromDate, toDate, institution);
        creditCompanyTotal = getBillBean().calBillTotal(BillType.CashRecieveBill, fromDate, toDate, institution);
        pettyCashTotal = getBillBean().calBillTotal(BillType.PettyCash, fromDate, toDate, institution);
        createCollections2Hos();
        departmentProfessionalPaymentTotal = getBillBean().calDoctorPayment(fromDate, toDate, BillType.OpdBill);
        inwardProfessionalPaymentTotal = getBillBean().calDoctorPaymentInward(fromDate, toDate);
        creditCardBill = getBillBean().fetchBills(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeBill = getBillBean().fetchBills(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipBill = getBillBean().fetchBills(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        /////////////////
        creditCardTotal = getBillBean().calBillTotal(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeTotal = getBillBean().calBillTotal(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipTotal = getBillBean().calBillTotal(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        createFinalSummery();
    }

    public void createCashCategoryWithoutProMonth() {
        makeNull();
        long lng = getCommonFunctions().getDayCount(getFromDate(), getToDate());

        if (Math.abs(lng) > 32) {
            UtilityController.addErrorMessage("Date Range is too Long");
            return;
        }

        createOPdListMonthEndTable();
        createOutSideFee();
        createPharmacySale();
        createInwardCollectionMonth();
        ///////////////////
        opdHospitalTotal = getBillBean().calFeeValue(FeeType.OwnInstitution, getFromDate(), getToDate(), getInstitution());
        outSideFeeTotal = getBillBean().calOutSideInstitutionFees(fromDate, toDate, institution);
        pharmacyTotal = getBillBean().calInstitutionSale(fromDate, toDate, institution);
        inwardPaymentTotal = getBillBean().calInwardPaymentTotal(fromDate, toDate, institution);
        agentPaymentTotal = getBillBean().calBillTotal(BillType.AgentPaymentReceiveBill, fromDate, toDate, institution);
        creditCompanyTotal = getBillBean().calBillTotal(BillType.CashRecieveBill, fromDate, toDate, institution);
        pettyCashTotal = getBillBean().calBillTotal(BillType.PettyCash, fromDate, toDate, institution);
        createCollections2HosMonth();
        createDoctorPaymentInward();
        /////////////////
        inwardProfessionalPaymentTotal = getBillBean().calDoctorPaymentInward(fromDate, toDate);
        creditCardTotal = getBillBean().calBillTotal(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeTotal = getBillBean().calBillTotal(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipTotal = getBillBean().calBillTotal(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        createFinalSummeryMonth();
    }

    public void createCashCategoryWithProMonth() {
        makeNull();
        long lng = getCommonFunctions().getDayCount(getFromDate(), getToDate());

        if (Math.abs(lng) > 32) {
            UtilityController.addErrorMessage("Date Range is too Long");
            return;
        }

        createOPdListMonthEndTableWithPro();
        createOutSideFeeWithPro();
        createPharmacySale();
        createInwardCollectionMonth();
        agentCollections = getBillBean().fetchBills(BillType.AgentPaymentReceiveBill, getFromDate(), getToDate(), getInstitution());
        creditCompanyCollections = getBillBean().fetchBillItems(BillType.CashRecieveBill, fromDate, toDate, institution);
        createDoctorPaymentOpd();
        createDoctorPaymentInward();
        ///////////////////
        opdHospitalTotal = getBillBean().calFeeValue(getFromDate(), getToDate(), getInstitution());
        outSideFeeTotal = getBillBean().calOutSideInstitutionFeesWithPro(fromDate, toDate, institution);
        pharmacyTotal = getBillBean().calInstitutionSale(fromDate, toDate, institution);
        inwardPaymentTotal = getBillBean().calInwardPaymentTotal(fromDate, toDate, institution);
        agentPaymentTotal = getBillBean().calBillTotal(BillType.AgentPaymentReceiveBill, fromDate, toDate, institution);
        creditCompanyTotal = getBillBean().calBillTotal(BillType.CashRecieveBill, fromDate, toDate, institution);
        pettyCashTotal = getBillBean().calBillTotal(BillType.PettyCash, fromDate, toDate, institution);
        createCollections2HosMonth();
        departmentProfessionalPaymentTotal = getBillBean().calDoctorPayment(fromDate, toDate, BillType.OpdBill);
        inwardProfessionalPaymentTotal = getBillBean().calDoctorPaymentInward(fromDate, toDate);
        creditCardBill = getBillBean().fetchBills(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeBill = getBillBean().fetchBills(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipBill = getBillBean().fetchBills(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        /////////////////
        creditCardTotal = getBillBean().calBillTotal(PaymentMethod.Card, getFromDate(), getToDate(), getInstitution());
        chequeTotal = getBillBean().calBillTotal(PaymentMethod.Cheque, getFromDate(), getToDate(), getInstitution());
        slipTotal = getBillBean().calBillTotal(PaymentMethod.Slip, getFromDate(), getToDate(), getInstitution());
        createFinalSummeryMonth();
    }

    private void createFinalSummery() {
        System.err.println("createFinalSummery");
        finalValues = new ArrayList<>();
        String1Value2 dd;
        ////////              
        dd = new String1Value2();
        dd.setString("Net Cash");
        Double tmp = grantTotal - (creditCardTotal + slipTotal + chequeTotal + departmentProfessionalPaymentTotal + inwardProfessionalPaymentTotal);
        dd.setValue1(tmp);
        finalValues.add(dd);

        dd = new String1Value2();
        dd.setString("Lab Handover Total");

        finalValues.add(dd);

        dd = new String1Value2();
        dd.setString("Final Cash");
        finalValues.add(dd);

        dd = new String1Value2();
        dd.setString("C/F Cash Balance");

        finalValues.add(dd);

    }

    private void createFinalSummeryMonth() {
        System.err.println("createFinalSummery");
        finalValues = new ArrayList<>();
        String1Value2 dd;
        ////////       
        dd = new String1Value2();
        dd.setString("Card Total ");
        dd.setValue1(0 - creditCardTotal);
        finalValues.add(dd);
        ///////////
        dd = new String1Value2();
        dd.setString("Cheque Total ");
        dd.setValue1(0 - chequeTotal);
        finalValues.add(dd);
        ///////////
        dd = new String1Value2();
        dd.setString("Slip Total ");
        dd.setValue1(0 - slipTotal);
        finalValues.add(dd);
        ///////////

        dd = new String1Value2();
        dd.setString("Net Cash");
        Double tmp = grantTotal - (creditCardTotal + slipTotal + chequeTotal + departmentProfessionalPaymentTotal + inwardProfessionalPaymentTotal);
        dd.setValue1(tmp);
        finalValues.add(dd);

        dd = new String1Value2();
        dd.setString("Lab Handover Total");

        finalValues.add(dd);

        dd = new String1Value2();
        dd.setString("Final Cash");

        finalValues.add(dd);

    }

    /**
     * Creates a new instance of BookKeepingSummery
     */
    public BookKeepingSummery() {
    }

    List<ProfessionalPaymentsByAdmissionTypeAndCategory> professionalPaymentsByAdmissionTypeAndCategorys;

    public List<ProfessionalPaymentsByAdmissionTypeAndCategory> getProfessionalPaymentsByAdmissionTypeAndCategorys() {
        return professionalPaymentsByAdmissionTypeAndCategorys;
    }

    public void setProfessionalPaymentsByAdmissionTypeAndCategorys(List<ProfessionalPaymentsByAdmissionTypeAndCategory> professionalPaymentsByAdmissionTypeAndCategorys) {
        this.professionalPaymentsByAdmissionTypeAndCategorys = professionalPaymentsByAdmissionTypeAndCategorys;
    }

    public class ProfessionalPaymentsByAdmissionTypeAndCategory {

        String admissionType;
        String speciality;
        double specialityValie;
        double admissionTypeValue;
        double totalValue;
        String strSpecialityValue;
        String strAdmissionTypeValue;

        public String getStrSpecialityValue() {
            if (specialityValie == 0.0) {
                return "";
            } else {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                return df.format(Math.abs(specialityValie));
            }
        }

        public void setStrSpecialityValue(String strSpecialityValue) {
            this.strSpecialityValue = strSpecialityValue;
        }

        public String getStrAdmissionTypeValue() {
            if (admissionTypeValue == 0.0) {
                return "";
            } else {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                return df.format(Math.abs(admissionTypeValue));
            }
        }

        public void setStrAdmissionTypeValue(String strAdmissionTypeValue) {
            this.strAdmissionTypeValue = strAdmissionTypeValue;
        }

        public String getAdmissionType() {
            return admissionType;
        }

        public void setAdmissionType(String admissionType) {
            this.admissionType = admissionType;
        }

        public String getSpeciality() {
            return speciality;
        }

        public void setSpeciality(String speciality) {
            this.speciality = speciality;
        }

        public double getSpecialityValie() {
            return specialityValie;
        }

        public void setSpecialityValie(double specialityValie) {
            this.specialityValie = specialityValie;
        }

        public double getAdmissionTypeValue() {
            return admissionTypeValue;
        }

        public void setAdmissionTypeValue(double admissionTypeValue) {
            this.admissionTypeValue = admissionTypeValue;
        }

        public double getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(double totalValue) {
            this.totalValue = totalValue;
        }

    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

}
