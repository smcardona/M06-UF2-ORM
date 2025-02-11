package com.accesadades.orm;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.accesadades.orm.model.Property;
import com.accesadades.orm.util.exceptions.DatabaseException;
import com.accesadades.orm.util.executables.Executable;


public class DAO<T> {

    private static SessionFactory factory;
    private static Session session;

    public final Class<T> type;

    private DAO (Class<T> type) {
        if (factory == null) factory = HibernateUtil.getSessionFactory();
        if (session == null) session = factory.openSession();
        this.type = type;
    }

    public static DAO<?> with(Class<?> type) {
        return new DAO<>(type);
    }


    public static void endSession() {
        if (session != null && session.isConnected()) {
            session.close();
        }
    }
    
    public T getById(Object id) {
        return session.find(type, id);
    }

    public List<T> getAll() {
        Query<T> query = session.createQuery("FROM "+type.getSimpleName(), type);
        return query.list();
    }

    public long count() {
        Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + type.getSimpleName(), Long.class);
        return query.uniqueResult();
    }

    public void clearAll() {
        @SuppressWarnings("deprecation")
        Query<?> query = session.createQuery("DELETE FROM " + type.getSimpleName());
        executeAction(() -> query.executeUpdate());
    }

    public List<T> filterBy(Property<String> field, Object value) {

        String hql = String.format("FROM %s WHERE %s like :val", 
            type.getSimpleName(), 
            field.name);

        Query<T> query = session.createQuery(hql, type);
        query.setParameter("val", value);

        return query.list();

    }

    public void save(Object object) {
        executeAction(() -> session.persist(object));
    }

    public void update(Object object) {
        executeAction(() -> session.merge(object));
    }

    public void remove(Object object) {
        executeAction(() -> session.remove(object));
    }


    // Funcion que maneja excepciones generadas por la acción
    public void executeAction(Executable action) throws DatabaseException {
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            action.execute();
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            throw new DatabaseException(type.getSimpleName()+" "+e.getMessage());
        }
    }

    //! Funcion peligrosa: Hace que las demas instancias de DAO dejen de funcionar y directamente borra la DB
    @SuppressWarnings("deprecation")
    public static void finishEverything() {
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
        } 
        Transaction tr = null;
        try {
            tr = session.beginTransaction();
            session.createNativeQuery("DROP DATABASE IF EXISTS aero").executeUpdate();
            tr.commit();
            System.out.println("Base de dades eliminada");
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error intentar esborrar DB: " + e.getMessage());
        } finally {
            endSession();
        }

        factory = null;
        session = null;
    }

    //! Funcion peligrosa para borrar todos los datos de una tabla
    public static void clearTables(Class<?>[] tableClasses) {
        for (Class<?> type: tableClasses) {
            DAO.with(type).clearAll();
        }
    }
    

}
