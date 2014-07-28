/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.common;

import com.divudi.data.BillType;
import com.divudi.data.InvestigationItemValueType;
import com.divudi.data.PaymentMethod;
import com.divudi.data.Sex;
import com.divudi.data.Title;
import com.divudi.data.hr.DayType;
import com.divudi.data.hr.PaysheetComponentType;
import com.divudi.data.inward.AdmissionTypeEnum;
import com.divudi.data.inward.InwardChargeType;
import com.divudi.data.inward.PatientEncounterComponentType;
import com.divudi.entity.PaymentScheme;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class EnumController implements Serializable {

    private PaymentScheme paymentScheme;

    public DayType[] getDayTypeForShift() {
        DayType[] dayTypes = {DayType.Normal, DayType.DayOff, DayType.SleepingDay};

        return dayTypes;
    }

    public DayType[] getDayTypesForPh() {
        DayType[] dayTypes = {DayType.MurchantileHoliday, DayType.Poya, DayType.PublicHoliday};

        return dayTypes;
    }

    public InvestigationItemValueType[] getInvestigationItemValueTypes() {
        return InvestigationItemValueType.values();
    }

    public PaysheetComponentType[] getAddingComponentTypes() {
        return PaysheetComponentType.addition.children();

    }

    public PaysheetComponentType[] getDiductionComponentTypes() {
        return PaysheetComponentType.subtraction.children();

    }

    public PaysheetComponentType[] getPaysheetComponentTypes() {
        return PaysheetComponentType.values();
    }

    public Title[] getTitle() {
        return Title.values();
    }

    public Sex[] getSex() {
        return Sex.values();
    }

    public PaymentMethod[] getPaymentMethodForAdmission() {
        PaymentMethod[] tmp = {PaymentMethod.Credit, PaymentMethod.Cash};
        return tmp;
    }

    public InwardChargeType[] getInwardChargeTypes() {
        return InwardChargeType.values();
    }

    public InwardChargeType[] getInwardChargeTypesForSetting() {
        InwardChargeType[] b = {
            InwardChargeType.AdmissionFee,
            InwardChargeType.Medicine,
            InwardChargeType.BloodTransfusioncharges,
            InwardChargeType.Immunization,
            InwardChargeType.Equipment,
            InwardChargeType.MealCharges,
            InwardChargeType.OperationTheatreCharges,
            InwardChargeType.LarbourRoomCharges,
            InwardChargeType.ETUCharges,
            InwardChargeType.TreatmentCharges,
            InwardChargeType.IntensiveCareManagement,
            InwardChargeType.AmbulanceCharges,
            InwardChargeType.HomeVisiting,
            InwardChargeType.GeneralIssuing,
            InwardChargeType.WardProcedures,
            InwardChargeType.ReimbursementCharges,
            InwardChargeType.DressingCharges,
            InwardChargeType.OxygenCharges,
            InwardChargeType.physiotherapy,
            InwardChargeType.Laboratory,
            InwardChargeType.X_Ray,
            InwardChargeType.CT,
            InwardChargeType.Scanning,
            InwardChargeType.ECG_EEG,
            InwardChargeType.MedicalServices,
            InwardChargeType.AdministrationCharge,
            InwardChargeType.LinenCharges,
            InwardChargeType.MaintainCharges,
            InwardChargeType.MedicalCareICU,
            InwardChargeType.MOCharges,
            InwardChargeType.NursingCharges,
            InwardChargeType.RoomCharges,
            InwardChargeType.CardiacMonitoring,
            InwardChargeType.Nebulisation,
            InwardChargeType.Echo,
            InwardChargeType.SyringePump,
            InwardChargeType.ExerciseECG,
            InwardChargeType.OtherCharges};

        return b;
    }

    public PatientEncounterComponentType[] getPatientEncounterComponentTypes() {
        return PatientEncounterComponentType.values();
    }

    public BillType[] getCashFlowBillTypes() {
        BillType[] b = {
            BillType.OpdBill,
            BillType.PaymentBill,
            BillType.PettyCash,
            BillType.CashRecieveBill,
            BillType.AgentPaymentReceiveBill,
            BillType.InwardPaymentBill,
            BillType.PharmacySale,
            BillType.PharmacyPurchaseBill,
            BillType.GrnPayment,};

        return b;
    }

    public BillType[] getPharmacyBillTypes() {
        BillType[] b = {
            BillType.PharmacyGrnBill,
            BillType.PharmacyGrnReturn,
            BillType.PharmacyOrder,
            BillType.PharmacyOrderApprove,
            BillType.PharmacyPre,
            BillType.PharmacyPurchaseBill,
            BillType.PharmacySale,
            BillType.PharmacyAdjustment,
            BillType.PurchaseReturn,
            BillType.GrnPayment,
            BillType.PharmacyTransferRequest,
            BillType.PharmacyTransferIssue,
            BillType.PharmacyIssue};

        return b;
    }

    public PaymentMethod[] getPaymentMethods() {
        PaymentMethod[] p = {
            PaymentMethod.Cash,
            PaymentMethod.Card,
            PaymentMethod.Cheque,
            PaymentMethod.Slip,
            PaymentMethod.Credit,};

        return p;
    }

    public PaymentMethod[] getPaymentMethodsWithoutCredit() {
        PaymentMethod[] p = {PaymentMethod.Cash,
            PaymentMethod.Card,
            PaymentMethod.Cheque,
            PaymentMethod.Slip};

        return p;
    }

    public PaymentMethod[] getPaymentMethodsForPo() {
        PaymentMethod[] p = {PaymentMethod.Cash, PaymentMethod.Credit};

        return p;
    }

//    public boolean checkPaymentScheme(PaymentScheme scheme, String paymentMathod) {
//        if (scheme != null && scheme.getPaymentMethod() != null) {
//            //System.err.println("Payment Scheme : " + scheme.getPaymentMethod());
//            //System.err.println("Payment Method : " + PaymentMethod.valueOf(paymentMathod));
//            if (scheme.getPaymentMethod().equals(PaymentMethod.valueOf(paymentMathod))) {
//                //System.err.println("Returning True");
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        return false;
//
//    }
//    public boolean checkPaymentScheme(String paymentMathod) {
//        if (getPaymentScheme() != null && getPaymentScheme().getPaymentMethod() != null) {
//            //System.err.println("Payment Scheme : " +getPaymentScheme().getPaymentMethod());
//            //System.err.println("Payment Method : " + PaymentMethod.valueOf(paymentMathod));
//            if (getPaymentScheme().getPaymentMethod().equals(PaymentMethod.valueOf(paymentMathod))) {
//                //System.err.println("Returning True");
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        return false;
//
//    }
    public boolean checkPaymentMethod(PaymentMethod paymentMethod, String paymentMathodStr) {
        if (paymentMethod != null) {
            //System.err.println("Payment method : " + paymentMethod);
            //System.err.println("Payment Method String : " + PaymentMethod.valueOf(paymentMathodStr));
            if (paymentMethod.equals(PaymentMethod.valueOf(paymentMathodStr))) {
                //System.err.println("Returning True");
                return true;
            } else {
                return false;
            }
        }

        return false;

    }

    public AdmissionTypeEnum[] getAdmissionTypeEnum() {
        return AdmissionTypeEnum.values();
    }

    /**
     * Creates a new instance of EnumController
     */
    public EnumController() {
    }

    public PaymentScheme getPaymentScheme() {
        return paymentScheme;
    }

    public void setPaymentScheme(PaymentScheme paymentScheme) {
        this.paymentScheme = paymentScheme;
    }

}
