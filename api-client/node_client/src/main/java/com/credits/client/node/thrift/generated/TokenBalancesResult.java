/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.credits.client.node.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2019-03-04")
public class TokenBalancesResult implements org.apache.thrift.TBase<TokenBalancesResult, TokenBalancesResult._Fields>, java.io.Serializable, Cloneable, Comparable<TokenBalancesResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TokenBalancesResult");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField BALANCES_FIELD_DESC = new org.apache.thrift.protocol.TField("balances", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TokenBalancesResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TokenBalancesResultTupleSchemeFactory();

  public com.credits.general.thrift.generated.APIResponse status; // required
  public java.util.List<TokenBalance> balances; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STATUS((short)1, "status"),
    BALANCES((short)2, "balances");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // STATUS
          return STATUS;
        case 2: // BALANCES
          return BALANCES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.credits.general.thrift.generated.APIResponse.class)));
    tmpMap.put(_Fields.BALANCES, new org.apache.thrift.meta_data.FieldMetaData("balances", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TokenBalance.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TokenBalancesResult.class, metaDataMap);
  }

  public TokenBalancesResult() {
  }

  public TokenBalancesResult(
    com.credits.general.thrift.generated.APIResponse status,
    java.util.List<TokenBalance> balances)
  {
    this();
    this.status = status;
    this.balances = balances;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TokenBalancesResult(TokenBalancesResult other) {
    if (other.isSetStatus()) {
      this.status = new com.credits.general.thrift.generated.APIResponse(other.status);
    }
    if (other.isSetBalances()) {
      java.util.List<TokenBalance> __this__balances = new java.util.ArrayList<TokenBalance>(other.balances.size());
      for (TokenBalance other_element : other.balances) {
        __this__balances.add(new TokenBalance(other_element));
      }
      this.balances = __this__balances;
    }
  }

  public TokenBalancesResult deepCopy() {
    return new TokenBalancesResult(this);
  }

  @Override
  public void clear() {
    this.status = null;
    this.balances = null;
  }

  public com.credits.general.thrift.generated.APIResponse getStatus() {
    return this.status;
  }

  public TokenBalancesResult setStatus(com.credits.general.thrift.generated.APIResponse status) {
    this.status = status;
    return this;
  }

  public void unsetStatus() {
    this.status = null;
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean value) {
    if (!value) {
      this.status = null;
    }
  }

  public int getBalancesSize() {
    return (this.balances == null) ? 0 : this.balances.size();
  }

  public java.util.Iterator<TokenBalance> getBalancesIterator() {
    return (this.balances == null) ? null : this.balances.iterator();
  }

  public void addToBalances(TokenBalance elem) {
    if (this.balances == null) {
      this.balances = new java.util.ArrayList<TokenBalance>();
    }
    this.balances.add(elem);
  }

  public java.util.List<TokenBalance> getBalances() {
    return this.balances;
  }

  public TokenBalancesResult setBalances(java.util.List<TokenBalance> balances) {
    this.balances = balances;
    return this;
  }

  public void unsetBalances() {
    this.balances = null;
  }

  /** Returns true if field balances is set (has been assigned a value) and false otherwise */
  public boolean isSetBalances() {
    return this.balances != null;
  }

  public void setBalancesIsSet(boolean value) {
    if (!value) {
      this.balances = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((com.credits.general.thrift.generated.APIResponse)value);
      }
      break;

    case BALANCES:
      if (value == null) {
        unsetBalances();
      } else {
        setBalances((java.util.List<TokenBalance>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return getStatus();

    case BALANCES:
      return getBalances();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case STATUS:
      return isSetStatus();
    case BALANCES:
      return isSetBalances();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TokenBalancesResult)
      return this.equals((TokenBalancesResult)that);
    return false;
  }

  public boolean equals(TokenBalancesResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    boolean this_present_balances = true && this.isSetBalances();
    boolean that_present_balances = true && that.isSetBalances();
    if (this_present_balances || that_present_balances) {
      if (!(this_present_balances && that_present_balances))
        return false;
      if (!this.balances.equals(that.balances))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetStatus()) ? 131071 : 524287);
    if (isSetStatus())
      hashCode = hashCode * 8191 + status.hashCode();

    hashCode = hashCode * 8191 + ((isSetBalances()) ? 131071 : 524287);
    if (isSetBalances())
      hashCode = hashCode * 8191 + balances.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(TokenBalancesResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetBalances()).compareTo(other.isSetBalances());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBalances()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.balances, other.balances);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TokenBalancesResult(");
    boolean first = true;

    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("balances:");
    if (this.balances == null) {
      sb.append("null");
    } else {
      sb.append(this.balances);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (status != null) {
      status.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TokenBalancesResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TokenBalancesResultStandardScheme getScheme() {
      return new TokenBalancesResultStandardScheme();
    }
  }

  private static class TokenBalancesResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<TokenBalancesResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TokenBalancesResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.status = new com.credits.general.thrift.generated.APIResponse();
              struct.status.read(iprot);
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // BALANCES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list158 = iprot.readListBegin();
                struct.balances = new java.util.ArrayList<TokenBalance>(_list158.size);
                TokenBalance _elem159;
                for (int _i160 = 0; _i160 < _list158.size; ++_i160)
                {
                  _elem159 = new TokenBalance();
                  _elem159.read(iprot);
                  struct.balances.add(_elem159);
                }
                iprot.readListEnd();
              }
              struct.setBalancesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TokenBalancesResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.status != null) {
        oprot.writeFieldBegin(STATUS_FIELD_DESC);
        struct.status.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.balances != null) {
        oprot.writeFieldBegin(BALANCES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.balances.size()));
          for (TokenBalance _iter161 : struct.balances)
          {
            _iter161.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TokenBalancesResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TokenBalancesResultTupleScheme getScheme() {
      return new TokenBalancesResultTupleScheme();
    }
  }

  private static class TokenBalancesResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<TokenBalancesResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TokenBalancesResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetStatus()) {
        optionals.set(0);
      }
      if (struct.isSetBalances()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetStatus()) {
        struct.status.write(oprot);
      }
      if (struct.isSetBalances()) {
        {
          oprot.writeI32(struct.balances.size());
          for (TokenBalance _iter162 : struct.balances)
          {
            _iter162.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TokenBalancesResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.status = new com.credits.general.thrift.generated.APIResponse();
        struct.status.read(iprot);
        struct.setStatusIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list163 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.balances = new java.util.ArrayList<TokenBalance>(_list163.size);
          TokenBalance _elem164;
          for (int _i165 = 0; _i165 < _list163.size; ++_i165)
          {
            _elem164 = new TokenBalance();
            _elem164.read(iprot);
            struct.balances.add(_elem164);
          }
        }
        struct.setBalancesIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

