package com.cs.sis.controller;

import javax.persistence.EntityManager;

/**
 *
 * @author Lettiery
 */
public abstract class Gerador {
    
    
    protected static EntityManager getEntityManager(){
        return ControllerEntity.getEntityManager();
    }
}
