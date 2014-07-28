package com.divudi.entity.lab;

import com.divudi.data.CalculationType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Buddhika
 */
@Entity
public class IxCal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    InvestigationItem calIxItem;
    @Enumerated(EnumType.STRING)
    private CalculationType calculationType;
    private Double constantValue;
    @ManyToOne
    InvestigationItem valIxItem;
    Integer orderNo;
    
    public InvestigationItem getValIxItem() {
        return valIxItem;
    }

    public void setValIxItem(InvestigationItem valIxItem) {
        this.valIxItem = valIxItem;
    }

    public Double getConstantValue() {
        return constantValue;
    }

    public void setConstantValue(Double constantValue) {
        this.constantValue = constantValue;
    }

    public CalculationType getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(CalculationType calculationType) {
        this.calculationType = calculationType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvestigationItem getCalIxItem() {
        return calIxItem;
    }

    public void setCalIxItem(InvestigationItem calIxItem) {
        this.calIxItem = calIxItem;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IxCal)) {
            return false;
        }
        IxCal other = (IxCal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.divudi.entity.lab.IxCal[ id=" + id + " ]";
    }
}
