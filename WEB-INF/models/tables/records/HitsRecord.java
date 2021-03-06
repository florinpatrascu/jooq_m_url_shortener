/**
 * This class is generated by jOOQ
 */
package models.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HitsRecord extends org.jooq.impl.UpdatableRecordImpl<models.tables.records.HitsRecord> implements org.jooq.Record3<java.lang.Integer, java.lang.Integer, java.sql.Timestamp> {

	private static final long serialVersionUID = 1447021021;

	/**
	 * Setter for <code>PUBLIC.HITS.ID</code>.
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>PUBLIC.HITS.ID</code>.
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>PUBLIC.HITS.URL_ID</code>.
	 */
	public void setUrlId(java.lang.Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>PUBLIC.HITS.URL_ID</code>.
	 */
	public java.lang.Integer getUrlId() {
		return (java.lang.Integer) getValue(1);
	}

	/**
	 * Setter for <code>PUBLIC.HITS.CREATED_AT</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>PUBLIC.HITS.CREATED_AT</code>.
	 */
	public java.sql.Timestamp getCreatedAt() {
		return (java.sql.Timestamp) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.lang.Integer, java.lang.Integer, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.lang.Integer, java.lang.Integer, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return models.tables.Hits.HITS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field2() {
		return models.tables.Hits.HITS.URL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field3() {
		return models.tables.Hits.HITS.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value2() {
		return getUrlId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value3() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HitsRecord value1(java.lang.Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HitsRecord value2(java.lang.Integer value) {
		setUrlId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HitsRecord value3(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HitsRecord values(java.lang.Integer value1, java.lang.Integer value2, java.sql.Timestamp value3) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached HitsRecord
	 */
	public HitsRecord() {
		super(models.tables.Hits.HITS);
	}

	/**
	 * Create a detached, initialised HitsRecord
	 */
	public HitsRecord(java.lang.Integer id, java.lang.Integer urlId, java.sql.Timestamp createdAt) {
		super(models.tables.Hits.HITS);

		setValue(0, id);
		setValue(1, urlId);
		setValue(2, createdAt);
	}
}
