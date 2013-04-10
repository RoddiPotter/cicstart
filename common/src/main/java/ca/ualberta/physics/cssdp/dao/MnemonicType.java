package ca.ualberta.physics.cssdp.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import ca.ualberta.physics.cssdp.model.Mnemonic;

/**
 * Marshals Mnemonics back and forth to SQL data type VARCHAR
 */
public class MnemonicType implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { java.sql.Types.VARCHAR };
	}

	@Override
	public Class<Mnemonic> returnedClass() {
		return Mnemonic.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}

		if (x == null || y == null) {
			return false;
		}

		Mnemonic m1 = (Mnemonic) x;
		Mnemonic m2 = (Mnemonic) y;

		return m1.equals(m2);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		Mnemonic m = (Mnemonic) x;
		return m.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		String value = (String) rs.getString(names[0]);
		if (value != null) {
			Mnemonic m = new Mnemonic(value);
			return m;
		} else {
			return null;
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {

		if (value != null) {
			Mnemonic m = (Mnemonic) value;
			st.setObject(index, m.getValue());
		} else {
			st.setObject(index, null);
		}
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

}
