/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.inward;

import com.divudi.entity.Bill;
import com.divudi.entity.BillFee;
import com.divudi.entity.BillItem;
import com.divudi.entity.PatientEncounter;
import com.divudi.entity.PatientItem;
import com.divudi.entity.inward.PatientRoom;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class BhtSummeryFinalizedController implements Serializable {

    PatientEncounter patientEncounter;
    List<PatientRoom> patientRooms;
    List<PatientItem> patientItems;
    List<BillItem> billItems;
    List<BillItem> pharmacyItems;
    List<BillFee> proBillFee;
    List<BillFee> assistBillFee;
    List<Bill> outSideBills;
    List<Bill> paymentBills;
    Bill bill;
    @Inject
    InwardBeanController inwardBean;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public void selectLitener() {
        if (patientEncounter == null) {
            return;
        }

        bill = patientEncounter.getFinalBill();
    }

    public List<BillItem> getPharmacyItems() {
        return pharmacyItems;
    }

    public void setPharmacyItems(List<BillItem> pharmacyItems) {
        this.pharmacyItems = pharmacyItems;
    }

    public List<Bill> getOutSideBills() {
        return outSideBills;
    }

    public void setOutSideBills(List<Bill> outSideBills) {
        this.outSideBills = outSideBills;
    }

    public List<BillFee> getProBillFee() {
        return proBillFee;
    }

    public void setProBillFee(List<BillFee> proBillFee) {
        this.proBillFee = proBillFee;
    }

    public List<BillFee> getAssistBillFee() {
        return assistBillFee;
    }

    public void setAssistBillFee(List<BillFee> assistBillFee) {
        this.assistBillFee = assistBillFee;
    }

    public List<Bill> getPaymentBills() {
        return paymentBills;
    }

    public void setPaymentBills(List<Bill> paymentBills) {
        this.paymentBills = paymentBills;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public List<PatientItem> getPatientItems() {
        return patientItems;
    }

    public void setPatientItems(List<PatientItem> patientItems) {
        this.patientItems = patientItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    public List<PatientRoom> getPatientRooms() {
        return patientRooms;
    }

    public void setPatientRooms(List<PatientRoom> patientRooms) {
        this.patientRooms = patientRooms;
    }

    public InwardBeanController getInwardBean() {
        return inwardBean;
    }

    public void setInwardBean(InwardBeanController inwardBean) {
        this.inwardBean = inwardBean;
    }

    public PatientEncounter getPatientEncounter() {
        return patientEncounter;
    }

    public void setPatientEncounter(PatientEncounter patientEncounter) {
        this.patientEncounter = patientEncounter;
    }

    public void makeNullList() {
        patientRooms = null;
        billItems = null;
        proBillFee = null;
        assistBillFee = null;
        outSideBills = null;
        paymentBills = null;
        pharmacyItems = null;
    }

    public void makeNull() {
        patientEncounter = null;
        makeNullList();
    }

    public void createTablesFinalized() {
        makeNullList();
        patientRooms = getInwardBean().fetchPatientRoomAll(getPatientEncounter());
        billItems = getInwardBean().fetchBillItems(getPatientEncounter());
        patientItems = getInwardBean().fetchPatientItem(getPatientEncounter());
        outSideBills = getInwardBean().fetchOutSideBill(getPatientEncounter());
        proBillFee = getInwardBean().createProfesionallFee(getPatientEncounter());
        assistBillFee = getInwardBean().createDoctorAndNurseFee(getPatientEncounter());
        paymentBills = getInwardBean().fetchPaymentBill(getPatientEncounter());
        pharmacyItems = getInwardBean().fetchPharmacyIssueBillItem(getPatientEncounter());

    }

    /**
     * Creates a new instance of BhtSummeryFinalizedController
     */
    public BhtSummeryFinalizedController() {
    }

}
