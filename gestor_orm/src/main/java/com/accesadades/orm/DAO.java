package com.accesadades.orm;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.accesadades.orm.model.Property;
import com.accesadades.orm.model.Property.PropertyProvider;
import com.accesadades.orm.util.Color;
import com.accesadades.orm.util.exceptions.DatabaseException;
import com.accesadades.orm.util.exceptions.ExitException;
import com.accesadades.orm.util.executables.Executable;

public class DAO<T extends PropertyProvider> {

    private static SessionFactory factory;
    private static Session session;

    public final Class<T> type;

    public DAO (Class<T> type) {
        if (factory == null) factory = HibernateUtil.getSessionFactory();
        if (session == null) session = factory.openSession();
        this.type = type;
    }


    public static void endSession() {
        if (session != null && session.isConnected()) {
            session.close();
        }
    }
    
    public T getById(Object id) {
        return session.getReference(type, id);
    }

    public List<T> getAll() {
        Query<T> query = session.createQuery("FROM "+type.getSimpleName(), type);
        return query.list();
    }

    public List<T> filterBy(Property<?> field, Object value) {

        String hql = String.format("FROM %s WHERE %s = :val", 
            type.getSimpleName(), 
            field.name);

        Query<T> query = session.createQuery(hql, type);
        query.setParameter("val", value);

        return query.list();

    }

    public void save(T object) {
        executeAction(() -> session.persist(object));
    }

    public void update(T object) {
        executeAction(() -> session.merge(object));
    }

    public void remove(T object) {
        executeAction(() -> session.remove(object));
    }

    public T newInstance() throws ExitException {
        try {
            // Esto es para hacer new T(); pero java no permite eso pues T es muchas cosas
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Color.RED.println("Error intern del DAO: La entitat no té Pojo -> "+type.getSimpleName());
            throw new ExitException("Error del programa, Pojo requerit");
        }
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
        if (session != null) {

            session.beginTransaction();
            session.createNativeQuery("DROP DATABASE aero").executeUpdate();
            session.getTransaction().commit();

            session.close();
        }
        if (factory != null) factory.close();

        factory = null;
        session = null;

        
    }

    public static List<?> getAllFrom (Class<?> clazz) {
        Query<?> query = session.createQuery("FROM "+clazz.getSimpleName(), clazz);
        return query.list();
    }


    

}
