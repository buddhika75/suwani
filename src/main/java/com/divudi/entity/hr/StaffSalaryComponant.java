/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.entity.hr;

import com.divudi.data.dataStructure.OtNormalSpecial;
import com.divudi.data.hr.PaysheetComponentType;
import com.divudi.entity.WebUser;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Buddhika
 */
@Entity
public class StaffSalaryComponant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double generatedValue;
    private double modifiedValue;
    @Transient
    private double componantValue;
    private double epfValue;
    private double etfValue;
    private double epfCompanyValue;
    private double etfCompanyValue;
    //Created Properties
    @ManyToOne
    private WebUser creater;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;
    //Retairing properties
    private boolean retired;
    @ManyToOne
    private WebUser retirer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retiredAt;
    private String retireComments;
    @ManyToOne
    StaffPaysheetComponent staffPaysheetComponent;
    @ManyToOne(fetch = FetchType.LAZY)
    StaffSalary staffSalary;
    //////////
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastEditedAt;
    @ManyToOne
    private WebUser lastEditor;
    
            
            
    public double getComponantValue() {
        if (modifiedValue != 0.0) {
            componantValue = modifiedValue;
        } else {
            componantValue = generatedValue;
        }

        if (getStaffPaysheetComponent() != null
                && getStaffPaysheetComponent().getPaysheetComponent() != null && getStaffPaysheetComponent().getPaysheetComponent().getComponentType()!=null 
                && getStaffPaysheetComponent().getPaysheetComponent().getComponentType().is(PaysheetComponentType.subtraction)) {

            componantValue = 0 - componantValue;
        }

        return componantValue;
    }

    public void setComponantValue(double componantValue) {
        if (generatedValue == 0.0) {
            generatedValue = componantValue;
        } else {
            modifiedValue = componantValue;
        }

        this.componantValue = componantValue;
    }

    public double getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(double generatedValue) {
        this.generatedValue = generatedValue;
    }

    public WebUser getCreater() {
        return creater;
    }

    public void setCreater(WebUser creater) {
        this.creater = creater;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public WebUser getRetirer() {
        return retirer;
    }

    public void setRetirer(WebUser retirer) {
        this.retirer = retirer;
    }

    public Date getRetiredAt() {
        return retiredAt;
    }

    public void setRetiredAt(Date retiredAt) {
        this.retiredAt = retiredAt;
    }

    public String getRetireComments() {
        return retireComments;
    }

    public void setRetireComments(String retireComments) {
        this.retireComments = retireComments;
    }

    public StaffPaysheetComponent getStaffPaysheetComponent() {
        return staffPaysheetComponent;
    }

    public void setStaffPaysheetComponent(StaffPaysheetComponent staffPaysheetComponent) {
        this.staffPaysheetComponent = staffPaysheetComponent;
    }

    public StaffSalary getStaffSalary() {
        return staffSalary;
    }

    public void setStaffSalary(StaffSalary staffSalary) {
        this.staffSalary = staffSalary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof StaffSalaryComponant)) {
            return false;
        }
        StaffSalaryComponant other = (StaffSalaryComponant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.divudi.entity.hr.StaffSalaryComponant[ id=" + id + " ]";
    }

    public double getModifiedValue() {
        return modifiedValue;
    }

    public void setModifiedValue(double modifiedValue) {
        this.modifiedValue = modifiedValue;
    }

    public Date getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(Date lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public WebUser getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(WebUser lastEditor) {
        this.lastEditor = lastEditor;
    }

    public double getEpfValue() {
        return 0-epfValue;
    }

    public void setEpfValue(double epfValue) {
        this.epfValue = epfValue;
    }

    public double getEtfValue() {
        return 0-etfValue;
    }

    public void setEtfValue(double etfValue) {
        this.etfValue = etfValue;
    }

    public double getEpfCompanyValue() {
        return 0-epfCompanyValue;
    }

    public void setEpfCompanyValue(double epfCompanyValue) {
        this.epfCompanyValue = epfCompanyValue;
    }

    public double getEtfCompanyValue() {
        return 0-etfCompanyValue;
    }

    public void setEtfCompanyValue(double etfCompanyValue) {
        this.etfCompanyValue = etfCompanyValue;
    }

   
}
