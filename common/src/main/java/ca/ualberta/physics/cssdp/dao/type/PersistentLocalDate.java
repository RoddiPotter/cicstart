package ca.ualberta.physics.cssdp.dao.type;

import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.LocalDate;

public class PersistentLocalDate implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { java.sql.Types.DATE };
	}

	@Override
	public Class<LocalDate> returnedClass() {
		return LocalDate.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {

		if (x == y) {
			return true;
		}

		if (x == null || y == null) {
			return false;
		}

		LocalDate ldt1 = (LocalDate) x;
		LocalDate ldt2 = (LocalDate) y;

		return ldt1.equals(ldt2);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		LocalDate ldt = (LocalDate) x;
		return ldt.hashCode();
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		Date date = rs.getDate(names[0]);
		if (date != null) {
			LocalDate localDate = new LocalDate(date);
			return localDate;
		} else {
			return null;
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value != null) {
			LocalDate localDate = (LocalDate) value;
			Date date = new Date(localDate.toDate().getTime());
			st.setDate(index, date);
		} else {
			st.setObject(index, null);
		}

	}

}
