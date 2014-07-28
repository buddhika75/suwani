/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.report;

import com.divudi.bean.common.ServiceSubCategoryController;
import com.divudi.bean.common.SessionController;
import com.divudi.data.BillType;
import com.divudi.data.FeeType;
import com.divudi.data.PaymentMethod;
import com.divudi.data.dataStructure.BillItemWithFee;
import com.divudi.entity.Bill;
import com.divudi.entity.BillItem;
import com.divudi.entity.BilledBill;
import com.divudi.entity.CancelledBill;
import com.divudi.entity.Category;
import com.divudi.entity.Item;
import com.divudi.entity.RefundBill;
import com.divudi.entity.ServiceCategory;
import com.divudi.entity.ServiceSubCategory;
import com.divudi.facade.BillFeeFacade;
import com.divudi.facade.BillItemFacade;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class ServiceSummery implements Serializable {

    @Inject
    private SessionController sessionController;
    // private List<DailyCash> dailyCashs;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate;
    private Item service;
    private Category category;
    double count;
    double value;
    @EJB
    private BillItemFacade billItemFacade;
    @EJB
    private BillFeeFacade billFeeFacade;
    List<BillItem> billItems;

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Creates a new instance of ServiceSummery
     */
    public ServiceSummery() {
    }

    public double getServiceTot() {
        String sql;
        Map temMap = new HashMap();

        sql = "select sum(bi.feeValue) FROM BillFee bi where  bi.bill.institution=:ins and "
                + " bi.bill.billType= :bTp and bi.fee.feeType=:ftp "
                + " and  bi.bill.createdAt between :fromDate and :toDate and bi.billItem.item=:itm"
                + " and ( bi.bill.paymentMethod = :pm1 or  bi.bill.paymentMethod = :pm2 "
                + " or  bi.bill.paymentMethod = :pm3 or  bi.bill.paymentMethod = :pm4)";
        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("ins", getSessionController().getInstitution());
        temMap.put("bTp", BillType.OpdBill);
        temMap.put("pm1", PaymentMethod.Cash);
        temMap.put("pm2", PaymentMethod.Card);
        temMap.put("pm3", PaymentMethod.Cheque);
        temMap.put("pm4", PaymentMethod.Slip);
        temMap.put("ftp", FeeType.OwnInstitution);
        temMap.put("itm", getService());
        //     List<BillItem> tmp = getBillItemFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);

        return getBillFeeFacade().findDoubleByJpql(sql, temMap, TemporalType.TIMESTAMP);

    }

    private long getCount(Bill bill) {
        String sql;
        Map temMap = new HashMap();
        sql = "select count(bi) FROM BillItem bi where bi.bill.billType=:bType and bi.item=:itm "
                + " and type(bi.bill)=:billClass and bi.bill.toInstitution=:ins and "
                + " ( bi.bill.paymentMethod = :pm1 or  bi.bill.paymentMethod = :pm2 "
                + " or  bi.bill.paymentMethod = :pm3 or  bi.bill.paymentMethod = :pm4) "
                + " and bi.bill.createdAt between :fromDate and :toDate order by bi.item.name";
        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("itm", getService());
        temMap.put("billClass", bill.getClass());
        temMap.put("bType", BillType.OpdBill);
        temMap.put("pm1", PaymentMethod.Cash);
        temMap.put("pm2", PaymentMethod.Card);
        temMap.put("pm3", PaymentMethod.Cheque);
        temMap.put("pm4", PaymentMethod.Slip);
        temMap.put("ins", getSessionController().getInstitution());
        return getBillItemFacade().countBySql(sql, temMap, TemporalType.TIMESTAMP);

    }

    public long getCountTotal() {
        long countTotal = 0;

        long billed = getCount(new BilledBill());
        long cancelled = getCount(new CancelledBill());
        long refunded = getCount(new RefundBill());

        countTotal = billed - (refunded + cancelled);

        //     //System.err.println("Billed : " + billed);
        //   //System.err.println("Cancelled : " + cancelled);
        //    //System.err.println("Refunded : " + refunded);
        //     //System.err.println("Gross Tot : " + countTotal);
        return countTotal;
    }

    private List<BillItem> getBillItem(BillType billType, Item item, boolean discharged) {
        String sql;
        Map temMap = new HashMap();

        sql = "select bi FROM BillItem bi "
                + " where  bi.bill.institution=:ins "
                + " and  bi.bill.billType= :bTp  "
                + " and bi.item=:itm ";

        if (billType != BillType.InwardBill) {
            sql += " and ( bi.bill.paymentMethod = :pm1 "
                    + " or  bi.bill.paymentMethod = :pm2 "
                    + " or  bi.bill.paymentMethod = :pm3"
                    + " or  bi.bill.paymentMethod = :pm4) ";

            temMap.put("pm1", PaymentMethod.Cash);
            temMap.put("pm2", PaymentMethod.Card);
            temMap.put("pm3", PaymentMethod.Cheque);
            temMap.put("pm4", PaymentMethod.Slip);

        }

        if (discharged) {
            sql += " and  bi.bill.patientEncounter.dateOfDischarge between :fromDate and :toDate ";
        } else {
            sql += " and  bi.bill.createdAt between :fromDate and :toDate ";
        }

        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("ins", getSessionController().getInstitution());
        temMap.put("bTp", billType);
        temMap.put("itm", item);
        List<BillItem> tmp = getBillItemFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);

        return tmp;

    }

    public void createServiceSummery() {
        serviceSummery = new ArrayList<>();
        for (BillItem i : getBillItem(BillType.OpdBill, service, false)) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            serviceSummery.add(bi);
        }

    }

    public void createServiceSummeryInwardAdded() {
        serviceSummery = new ArrayList<>();
        for (BillItem i : getBillItem(BillType.InwardBill, service, false)) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            serviceSummery.add(bi);
        }

    }

    public void createServiceSummeryInwardDischarged() {
        serviceSummery = new ArrayList<>();
        for (BillItem i : getBillItem(BillType.InwardBill, service, true)) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            serviceSummery.add(bi);
        }

    }

    List<BillItemWithFee> serviceSummery;

    public List<BillItemWithFee> getServiceSummery() {
        return serviceSummery;
    }

    public void setServiceSummery(List<BillItemWithFee> serviceSummery) {
        this.serviceSummery = serviceSummery;
    }

    private double calFee(BillItem bi, FeeType feeType) {
        HashMap hm = new HashMap();
        String sql = "Select sum(f.feeValue) from "
                + " BillFee f where "
                + " f.retired=false "
                + " and f.billItem=:b and "
                + " f.fee.feeType=:ftp";
        hm.put("b", bi);
        hm.put("ftp", feeType);

        return getBillFeeFacade().findDoubleByJpql(sql, hm, TemporalType.DATE);

    }

    List<BillItemWithFee> billItemWithFees;

    public List<BillItemWithFee> getBillItemWithFees() {
        return billItemWithFees;
    }

    public void setBillItemWithFees(List<BillItemWithFee> billItemWithFees) {
        this.billItemWithFees = billItemWithFees;
    }

    public void createServiceCategorySummery() {
        if (getCategory() == null) {
            return;
        }
        if (getToDate() == null || getFromDate() == null) {
            return;
        }

        billItemWithFees = new ArrayList<>();

        List<BillItem> list = calBillItems(BillType.OpdBill, false);

        for (BillItem i : list) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            billItemWithFees.add(bi);
        }

        calCountTotal(BillType.OpdBill, false);
        calServiceTot(BillType.OpdBill, false);

    }

    public void createServiceCategorySummeryInwardAdded() {
        if (getCategory() == null) {
            return;
        }
        if (getToDate() == null || getFromDate() == null) {
            return;
        }

        billItemWithFees = new ArrayList<>();

        List<BillItem> list = calBillItems(BillType.InwardBill, false);

        for (BillItem i : list) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            billItemWithFees.add(bi);
        }

        calCountTotal(BillType.InwardBill, false);
        calServiceTot(BillType.InwardBill, false);

    }

    public void createServiceCategorySummeryInwardDischarged() {
        if (getCategory() == null) {
            return;
        }
        if (getToDate() == null || getFromDate() == null) {
            return;
        }

        billItemWithFees = new ArrayList<>();

        List<BillItem> list = calBillItems(BillType.InwardBill, true);

        for (BillItem i : list) {
            BillItemWithFee bi = new BillItemWithFee();
            bi.setBillItem(i);
            bi.setProFee(calFee(i, FeeType.Staff));
            bi.setHospitalFee(calFee(i, FeeType.OwnInstitution));
            billItemWithFees.add(bi);
        }

        calCountTotal(BillType.InwardBill, true);
        calServiceTot(BillType.InwardBill, true);

    }

    public void createServiceInwardCategorySummery() {
        Map m = new HashMap();
        String sql = "SELECT bi FROM BillItem bi WHERE bi.retired=false and bi.createdAt between :fd and :td and bi.bill.billType=:bt";
        m.put("fd", fromDate);
        m.put("td", toDate);
        m.put("bt", BillType.InwardBill);

        billItems = getBillItemFacade().findBySQL(sql, m, TemporalType.DATE);

    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    @Inject
    ServiceSubCategoryController serviceSubCategoryController;

    public ServiceSubCategoryController getServiceSubCategoryController() {
        return serviceSubCategoryController;
    }

    public void setServiceSubCategoryController(ServiceSubCategoryController serviceSubCategoryController) {
        this.serviceSubCategoryController = serviceSubCategoryController;
    }

    private List<BillItem> calBillItems(BillType billType, boolean discharged) {
        if (getCategory() instanceof ServiceSubCategory) {
            return getBillItemByCategory(category, billType, discharged);
        }

        if (getCategory() instanceof ServiceCategory) {
            getServiceSubCategoryController().setParentCategory(getCategory());
            List<ServiceSubCategory> subCategorys = getServiceSubCategoryController().getItems();
            if (subCategorys.isEmpty()) {
                return getBillItemByCategory(getCategory(), billType, discharged);
            } else {
                Set<BillItem> setBillItem = new HashSet<>();
                for (ServiceSubCategory ssc : subCategorys) {
                    setBillItem.addAll(getBillItemByCategory(ssc, billType, discharged));
                }

                List<BillItem> tmpBillItems = new ArrayList<>();
                tmpBillItems.addAll(setBillItem);
                return tmpBillItems;
            }
        }

        return null;
    }

    private List<BillItem> getBillItemByCategory(Category cat, BillType billType, boolean discharged) {
        String sql;
        Map temMap = new HashMap();

        sql = "select bi FROM BillItem bi "
                + " where  bi.bill.institution=:ins"
                + " and  bi.bill.billType= :bTp  "
                + " and bi.item.category=:cat";

        if (billType != BillType.InwardBill) {
            sql += " and ( bi.bill.paymentMethod = :pm1 "
                    + " or  bi.bill.paymentMethod = :pm2 "
                    + " or  bi.bill.paymentMethod = :pm3"
                    + " or  bi.bill.paymentMethod = :pm4) ";

            temMap.put("pm1", PaymentMethod.Cash);
            temMap.put("pm2", PaymentMethod.Card);
            temMap.put("pm3", PaymentMethod.Cheque);
            temMap.put("pm4", PaymentMethod.Slip);

        }

        if (discharged) {
            sql += " and  bi.bill.patientEncounter.dateOfDischarge between :fromDate and :toDate ";
        } else {
            sql += " and  bi.bill.createdAt between :fromDate and :toDate ";
        }

        sql += " order by bi.item.name";
        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("ins", getSessionController().getInstitution());
        temMap.put("bTp", billType);

        temMap.put("cat", cat);
        List<BillItem> tmp = getBillItemFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);

        System.err.println("BILL " + tmp);
        return tmp;

    }

    public void calCountTotal(BillType billType, boolean discharged) {
        long countTotal = 0;
        long billed = 0l;
        long cancelled = 0l;
        long refunded = 0l;

        if (getCategory() instanceof ServiceSubCategory) {
            billed = getCount(new BilledBill(), billType, getCategory(), discharged);
            cancelled = getCount(new CancelledBill(), billType, getCategory(), discharged);
            refunded = getCount(new RefundBill(), billType, getCategory(), discharged);
        }

        if (getCategory() instanceof ServiceCategory) {
            getServiceSubCategoryController().setParentCategory(getCategory());
            List<ServiceSubCategory> subCategorys = getServiceSubCategoryController().getItems();
            if (subCategorys.isEmpty()) {
                billed = getCount(new BilledBill(), billType, getCategory(), discharged);
                cancelled = getCount(new CancelledBill(), billType, getCategory(), discharged);
                refunded = getCount(new RefundBill(), billType, getCategory(), discharged);
            } else {
                billed = 0l;
                cancelled = 0l;
                refunded = 0l;
                for (ServiceSubCategory ssc : subCategorys) {
                    billed += getCount(new BilledBill(), billType, ssc, discharged);
                    cancelled += getCount(new CancelledBill(), billType, ssc, discharged);
                    refunded += getCount(new RefundBill(), billType, ssc, discharged);
                }
            }
        }

        countTotal = billed - (refunded + cancelled);

        count = countTotal;
    }

    private long getCount(Bill bill, BillType billType, Category cat, boolean discharged) {
        String sql;
        Map temMap = new HashMap();
        sql = "select count(bi) FROM BillItem bi "
                + " where bi.bill.billType=:bType "
                + " and bi.item.category=:cat "
                + " and type(bi.bill)=:billClass "
                + " and bi.bill.toInstitution=:ins ";

        if (billType != BillType.InwardBill) {
            sql += " and ( bi.bill.paymentMethod = :pm1 "
                    + " or  bi.bill.paymentMethod = :pm2 "
                    + " or  bi.bill.paymentMethod = :pm3"
                    + " or  bi.bill.paymentMethod = :pm4) ";

            temMap.put("pm1", PaymentMethod.Cash);
            temMap.put("pm2", PaymentMethod.Card);
            temMap.put("pm3", PaymentMethod.Cheque);
            temMap.put("pm4", PaymentMethod.Slip);

        }

        if (discharged) {
            sql += " and bi.bill.patientEncounter.dateOfDischarge between :fromDate and :toDate";
        } else {
            sql += " and bi.bill.createdAt between :fromDate and :toDate";
        }

        sql += " order by bi.item.name";

        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("cat", cat);
        temMap.put("billClass", bill.getClass());
        temMap.put("bType", billType);
        temMap.put("ins", getSessionController().getInstitution());
        return getBillItemFacade().countBySql(sql, temMap, TemporalType.TIMESTAMP);

    }

    private double getServiceValue(Category cat, BillType billType, boolean discharged) {
        String sql;
        Map temMap = new HashMap();

        sql = "select sum(bi.feeValue) FROM BillFee bi "
                + " where  bi.bill.institution=:ins"
                + " and  bi.bill.billType= :bTp "
                + " and bi.fee.feeType=:ftp "
                + " and bi.billItem.item.category=:cat";

        if (billType != BillType.InwardBill) {
            sql += " and ( bi.bill.paymentMethod = :pm1 "
                    + " or  bi.bill.paymentMethod = :pm2 "
                    + " or  bi.bill.paymentMethod = :pm3"
                    + " or  bi.bill.paymentMethod = :pm4) ";

            temMap.put("pm1", PaymentMethod.Cash);
            temMap.put("pm2", PaymentMethod.Card);
            temMap.put("pm3", PaymentMethod.Cheque);
            temMap.put("pm4", PaymentMethod.Slip);

        }

        if (discharged) {
            sql += " and  bi.bill.patientEncounter.dateOfDischarge between :fromDate and :toDate";
        } else {
            sql += " and  bi.bill.createdAt between :fromDate and :toDate";
        }

        temMap.put("toDate", getToDate());
        temMap.put("fromDate", getFromDate());
        temMap.put("ins", getSessionController().getInstitution());
        temMap.put("bTp", billType);
        temMap.put("ftp", FeeType.OwnInstitution);
        temMap.put("cat", cat);
        //     List<BillItem> tmp = getBillItemFacade().findBySQL(sql, temMap, TemporalType.TIMESTAMP);

        return getBillFeeFacade().findDoubleByJpql(sql, temMap, TemporalType.TIMESTAMP);

    }

    private void calServiceTot(BillType billType, boolean discharged) {

        if (getCategory() instanceof ServiceSubCategory) {
            value = getServiceValue(getCategory(), billType, discharged);
        }

        if (getCategory() instanceof ServiceCategory) {
            getServiceSubCategoryController().setParentCategory(getCategory());
            List<ServiceSubCategory> subCategorys = getServiceSubCategoryController().getItems();
            if (subCategorys.isEmpty()) {
                value = getServiceValue(getCategory(), billType, discharged);
            } else {
                value = 0;
                for (ServiceSubCategory ssc : subCategorys) {
                    value += getServiceValue(ssc, billType, discharged);
                }
            }
        }

    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
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

    public Item getService() {
        return service;
    }

    public void setService(Item service) {
        this.service = service;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public BillFeeFacade getBillFeeFacade() {
        return billFeeFacade;
    }

    public void setBillFeeFacade(BillFeeFacade billFeeFacade) {
        this.billFeeFacade = billFeeFacade;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
