/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.divudi.facade;

import com.divudi.entity.memberShip.AllowedPaymentMethod;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author safrin
 */
@Stateless
public class AllowedPaymentMethodFacade extends AbstractFacade<AllowedPaymentMethod> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AllowedPaymentMethodFacade() {
        super(AllowedPaymentMethod.class);
    }
    
}
