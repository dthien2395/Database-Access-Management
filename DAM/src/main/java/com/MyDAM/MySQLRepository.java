package com.MyDAM;

import com.DatabaseConnection;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dthien on 7/1/2017.
 */
public class MySQLRepository<T, ID extends Serializable> implements Repository<T, ID> {

    private final Class<T> genericType;

    public MySQLRepository() {
//        this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), MySQLRepository.class);
        this.genericType = (Class<T>)GenericTypeResolver.resolveTypeArguments(getClass(), MySQLRepository.class)[0];
    }

    public List<T> findAll() {
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection con = DatabaseConnection.getConnection();
        String table = genericType.getAnnotation(Table.class).name();
        String stm = "Select * from " + table;
        List<T> records = new ArrayList<T>();
        try {
            pst = con.prepareStatement(stm);
            pst.execute();
            rs = pst.getResultSet();

            Field[] fields = genericType.getDeclaredFields();

            while (rs.next()) {
                T t = genericType.newInstance();
                for(Field f : fields) {
                    f.setAccessible(true);
                    f.set(t, rs.getObject(f.getAnnotation(Column.class).name()));
                    f.setAccessible(false);
                }
                records.add(t);
            }
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {

        }
        return records;
    }

    public T findById(Long id) {
        return null;
    }
}
