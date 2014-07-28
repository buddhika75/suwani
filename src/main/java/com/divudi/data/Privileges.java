/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author www.divudi.com
 */
public enum Privileges {
    //Main Menu Privileges
    Opd,
    Inward,
    Lab,
    Pharmacy,
    Payment,
    Hr,
    Reports,
    User,
    Admin,
    Channelling,
    Clinical,
    Store,
    Search,
    CashTransaction,
    //Submenu Privileges
    OpdBilling,
    OpdBillSearch,
    OpdBillItemSearch,
    OpdReprint,
    OpdCancel,
    OpdReturn,
    OpdReactivate,
    InwardAdmissions,
    InwardAdmissionsAdmission,
    InwardAdmissionsEditAdmission,
    InwardAdmissionsInwardAppoinment,
    InwardRoom,
    InwardRoomRoomOccupency,
    InwardRoomRoomChange,
    InwardRoomGurdianRoomChange,
    InwardServicesAndItems,
    InwardServicesAndItemsAddServices,
    InwardServicesAndItemsAddOutSideCharges,
    InwardServicesAndItemsAddProfessionalFee,
    InwardServicesAndItemsAddTimedServices,
    InwardBilling,
    InwardBillingInterimBill,
    InwardBillingInterimBillSearch,
    InwardSearch,
    InwardSearchServiceBill,
    InwardSearchProfessionalBill,
    InwardSearchFinalBill,
    InwardReport,
    InwardAdministration,
    InwardAdditionalPrivilages,
    InwardBillSearch,
    InwardBillItemSearch,
    InwardBillReprint,
    InwardCancel,
    InwardReturn,
    InwardReactivate,
    InwardCheck,
    InwardUnCheck,
    ShowInwardFee,
    
    LabBilling,
    LabBillSearch,
    LabBillItemSearch,
    LabBillCancelling,
    LabBillReturning,
    LabBillReprint,
    LabBillRefunding,
    LabBillReactivating,
    LabSampleCollecting,
    LabSampleReceiving,
    LabReportFormatEditing,
    LabDataentry,
    LabAutherizing,
    LabDeAutherizing,
    LabPrinting,
    LabReprinting,
    LabSummeriesLevel1,
    LabSummeriesLevel2,
    LabSummeriesLevel3,
    LabReportSearchOwn,
    LabReportSearchAll,
    LabReceive,
    LabEditPatient,
    PaymentBilling,
    PaymentBillSearch,
    PaymentBillReprint,
    PaymentBillCancel,
    PaymentBillRefund,
    PaymentBillReactivation,
    ReportsSearchCashCardOwn,
    ReportsSearchCreditOwn,
    ReportsItemOwn,
    ReportsSearchCashCardOther,
    ReportSearchCreditOther,
    ReportsItemOther,
    PharmacyOrderCreation,
    PharmacyOrderApproval,
    PharmacyOrderCancellation,
    PharmacySale,
    PharmacySaleReprint,
    PharmacySaleCancel,
    PharmacySaleReturn,
    PharmacyInwardBilling,
    PharmacyInwardBillingCancel,
    PharmacyInwardBillingReturn,
    PharmacyGoodReceive,
    PharmacyGoodReceiveCancel,
    PharmacyGoodReceiveReturn,
    PharmacyGoodReceiveEdit,
    PharmacyPurchase,
    PharmacyPurchaseReprint,
    PharmacyPurchaseCancellation,
    PharmacyPurchaseReturn,
    PharmacyStockAdjustment,
    PharmacyReAddToStock,
    PharmacyDealorPayment,
    PharmacySearch,
    PharmacyReports,
    PharmacyTransfer,
    PharmacySummery,
    PharmacyAdministration,
    PharmacySetReorderLevel,
    Theatre,
    TheatreAddSurgery,
    TheatreBilling,
    ClinicalPatientSummery,
    ClinicalPatientDetails,
    ClinicalPatientPhoto,
    ClinicalVisitDetail,
    ClinicalVisitSummery,
    ClinicalHistory,
    ClinicalAdministration,
    ChannelAdd,
    ChannelCancel,
    ChannelRefund,
    ChannelReturn,
    ChannelView,
    ChannelDoctorPayments,
    ChannelDoctorPaymentCancel,
    ChannelViewHistory,
    ChannelCreateSessions,
    ChannelManageSessions,
    ChannelAdministration,
    ChannelAgencyReports,
    AdminManagingUsers,
    AdminInstitutions,
    AdminStaff,
    AdminItems,
    AdminPrices,
    ChangeProfessionalFee,
    ChangeCollectingCentre,
    StoreIssue,
    StoreIssueInwardBilling,
    StoreIssueSearchBill,
    StoreIssueBillItems,
    StorePurchase,
    StorePurchaseOrder,
    StorePurchaseOrderApprove,
    StorePurchaseGRNRecive,
    StorePurchaseGRNReturn,
    StorePurchasePurchase,
    StoreTransfer,
    StoreTransferRequest,
    StoreTransferIssue,
    StoreTransferRecive,
    StoreTransferReport,
    StoreAdjustment,
    StoreAdjustmentDepartmentStock,
    StoreAdjustmentStaffStock,
    StoreAdjustmentPurchaseRate,
    StoreAdjustmentSaleRate,
    StoreDealorPayment,
    StoreDealorPaymentDueSearch,
    StoreDealorPaymentDueByAge,
    StoreDealorPaymentPayment,
    StoreDealorPaymentPaymentGRN,
    StoreDealorPaymentPaymentGRNSelect,
    StoreDealorPaymentGRNDoneSearch,
    StoreSearch,
    StoreReports,
    StoreSummery,
    StoreAdministration,
    SearchGrand,
    CashTransactionCashIn,
    CashTransactionCashOut,
    CashTransactionListToCashRecieve,
    ChannellingChannelBooking,
    ChannellingPastBooking,
    ChannellingBookedList,
    ChannellingDoctorLeave,
    ChannellingChannelSheduling,
    ChannellingChannelAgentFee,
    ChannellingDoctorSessionView,
    ChannellingPayment,
    ChannellingPaymentPayDoctor,
    ChannellingPaymentDueSearch,
    ChannellingPaymentDoneSearch,
    MemberShip,
    MembershipSchemes,
    MemberShipInwardMemberShip,
    MemberShipInwardMemberShipSchemesDicounts,
    MemberShipInwardMemberShipInwardMemberShipReport,
    MemberShipOpdMemberShipDis,
    MemberShipOpdMemberShipDisByDepartment,
    MemberShipOpdMemberShipDisByCategory,
    MemberShipOpdMemberShipDisOpdMemberShipReport,

}
