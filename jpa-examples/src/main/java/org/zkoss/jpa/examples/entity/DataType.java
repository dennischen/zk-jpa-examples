package org.zkoss.jpa.examples.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "a_datatype")
public class DataType implements SimpleId<Integer>  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;
	public Integer getId() {
		return id;
	}

	String title;
	
	@Size(min=1,max=10)
	@NotNull
	String typeString;

	@NotNull
	Boolean typeBoolean;

	@Max(999)
	@NotNull
	Integer typeInteger;

	@Max(999999)
	@NotNull
	Long typeLong;

	@DecimalMax("99999.9999")
	@NotNull
	Double typeDouble;

	@DecimalMax("999.9999")
	@NotNull
	Float typeFloat;

	@DecimalMax("999999999999.999")
	@NotNull
	@Column(precision = 15, scale = 3)
	BigDecimal typeBigDecimal;

	@Temporal(TemporalType.DATE)
	@NotNull
	Date typeDate;

	@Temporal(TemporalType.TIME)
	@NotNull
	Date typeTime;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	Date typeDateTime;

	@Enumerated(EnumType.ORDINAL)
	@NotNull
	AEnum typeEnum;

	public DataType() {
	}

	public DataType(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public Boolean getTypeBoolean() {
		return typeBoolean;
	}

	public void setTypeBoolean(Boolean typeBoolean) {
		this.typeBoolean = typeBoolean;
	}

	public Integer getTypeInteger() {
		return typeInteger;
	}

	public void setTypeInteger(Integer typeInteger) {
		this.typeInteger = typeInteger;
	}

	public Long getTypeLong() {
		return typeLong;
	}

	public void setTypeLong(Long typeLong) {
		this.typeLong = typeLong;
	}

	public Double getTypeDouble() {
		return typeDouble;
	}

	public void setTypeDouble(Double typeDouble) {
		this.typeDouble = typeDouble;
	}

	public Float getTypeFloat() {
		return typeFloat;
	}

	public void setTypeFloat(Float typeFloat) {
		this.typeFloat = typeFloat;
	}

	public BigDecimal getTypeBigDecimal() {
		return typeBigDecimal;
	}

	public void setTypeBigDecimal(BigDecimal typeBigDecimal) {
		this.typeBigDecimal = typeBigDecimal;
	}

	public Date getTypeDate() {
		return typeDate;
	}

	public void setTypeDate(Date typeDate) {
		this.typeDate = typeDate;
	}

	public Date getTypeTime() {
		return typeTime;
	}

	public void setTypeTime(Date typeTime) {
		this.typeTime = typeTime;
	}

	public Date getTypeDateTime() {
		return typeDateTime;
	}

	public void setTypeDateTime(Date typeDateTime) {
		this.typeDateTime = typeDateTime;
	}

	public AEnum getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(AEnum typeEnum) {
		this.typeEnum = typeEnum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataType other = (DataType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
