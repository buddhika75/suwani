/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.facade;

import com.divudi.entity.Doctor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author buddhika
 */
@Stateless
public class DoctorFacade extends AbstractFacade<Doctor> {
    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DoctorFacade() {
        super(Doctor.class);
    }
    
}
