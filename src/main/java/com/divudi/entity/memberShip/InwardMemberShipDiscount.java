/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.entity.memberShip;

import com.divudi.entity.*;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


/**
 *
 * @author buddhika
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class InwardMemberShipDiscount extends PriceMatrix implements Serializable {
    private static final long serialVersionUID = 1L;
    
}
