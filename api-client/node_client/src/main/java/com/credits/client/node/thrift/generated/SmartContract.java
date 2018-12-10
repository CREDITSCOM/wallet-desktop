/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.credits.client.node.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-12-10")
public class SmartContract implements org.apache.thrift.TBase<SmartContract, SmartContract._Fields>, java.io.Serializable, Cloneable, Comparable<SmartContract> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SmartContract");

  private static final org.apache.thrift.protocol.TField ADDRESS_FIELD_DESC = new org.apache.thrift.protocol.TField("address", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField DEPLOYER_FIELD_DESC = new org.apache.thrift.protocol.TField("deployer", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField SMART_CONTRACT_DEPLOY_FIELD_DESC = new org.apache.thrift.protocol.TField("smartContractDeploy", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField OBJECT_STATE_FIELD_DESC = new org.apache.thrift.protocol.TField("objectState", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField CREATE_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("createTime", org.apache.thrift.protocol.TType.I64, (short)5);
  private static final org.apache.thrift.protocol.TField TRANSACTIONS_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("transactionsCount", org.apache.thrift.protocol.TType.I32, (short)6);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SmartContractStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SmartContractTupleSchemeFactory();

  public java.nio.ByteBuffer address; // required
  public java.nio.ByteBuffer deployer; // required
  public SmartContractDeploy smartContractDeploy; // required
  public java.nio.ByteBuffer objectState; // required
  public long createTime; // required
  public int transactionsCount; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ADDRESS((short)1, "address"),
    DEPLOYER((short)2, "deployer"),
    SMART_CONTRACT_DEPLOY((short)3, "smartContractDeploy"),
    OBJECT_STATE((short)4, "objectState"),
    CREATE_TIME((short)5, "createTime"),
    TRANSACTIONS_COUNT((short)6, "transactionsCount");

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
        case 1: // ADDRESS
          return ADDRESS;
        case 2: // DEPLOYER
          return DEPLOYER;
        case 3: // SMART_CONTRACT_DEPLOY
          return SMART_CONTRACT_DEPLOY;
        case 4: // OBJECT_STATE
          return OBJECT_STATE;
        case 5: // CREATE_TIME
          return CREATE_TIME;
        case 6: // TRANSACTIONS_COUNT
          return TRANSACTIONS_COUNT;
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
  private static final int __CREATETIME_ISSET_ID = 0;
  private static final int __TRANSACTIONSCOUNT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ADDRESS, new org.apache.thrift.meta_data.FieldMetaData("address", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Address")));
    tmpMap.put(_Fields.DEPLOYER, new org.apache.thrift.meta_data.FieldMetaData("deployer", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "Address")));
    tmpMap.put(_Fields.SMART_CONTRACT_DEPLOY, new org.apache.thrift.meta_data.FieldMetaData("smartContractDeploy", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, SmartContractDeploy.class)));
    tmpMap.put(_Fields.OBJECT_STATE, new org.apache.thrift.meta_data.FieldMetaData("objectState", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.CREATE_TIME, new org.apache.thrift.meta_data.FieldMetaData("createTime", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Time")));
    tmpMap.put(_Fields.TRANSACTIONS_COUNT, new org.apache.thrift.meta_data.FieldMetaData("transactionsCount", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SmartContract.class, metaDataMap);
  }

  public SmartContract() {
  }

  public SmartContract(
    java.nio.ByteBuffer address,
    java.nio.ByteBuffer deployer,
    SmartContractDeploy smartContractDeploy,
    java.nio.ByteBuffer objectState,
    long createTime,
    int transactionsCount)
  {
    this();
    this.address = org.apache.thrift.TBaseHelper.copyBinary(address);
    this.deployer = org.apache.thrift.TBaseHelper.copyBinary(deployer);
    this.smartContractDeploy = smartContractDeploy;
    this.objectState = org.apache.thrift.TBaseHelper.copyBinary(objectState);
    this.createTime = createTime;
    setCreateTimeIsSet(true);
    this.transactionsCount = transactionsCount;
    setTransactionsCountIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SmartContract(SmartContract other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetAddress()) {
      this.address = org.apache.thrift.TBaseHelper.copyBinary(other.address);
    }
    if (other.isSetDeployer()) {
      this.deployer = org.apache.thrift.TBaseHelper.copyBinary(other.deployer);
    }
    if (other.isSetSmartContractDeploy()) {
      this.smartContractDeploy = new SmartContractDeploy(other.smartContractDeploy);
    }
    if (other.isSetObjectState()) {
      this.objectState = org.apache.thrift.TBaseHelper.copyBinary(other.objectState);
    }
    this.createTime = other.createTime;
    this.transactionsCount = other.transactionsCount;
  }

  public SmartContract deepCopy() {
    return new SmartContract(this);
  }

  @Override
  public void clear() {
    this.address = null;
    this.deployer = null;
    this.smartContractDeploy = null;
    this.objectState = null;
    setCreateTimeIsSet(false);
    this.createTime = 0;
    setTransactionsCountIsSet(false);
    this.transactionsCount = 0;
  }

  public byte[] getAddress() {
    setAddress(org.apache.thrift.TBaseHelper.rightSize(address));
    return address == null ? null : address.array();
  }

  public java.nio.ByteBuffer bufferForAddress() {
    return org.apache.thrift.TBaseHelper.copyBinary(address);
  }

  public SmartContract setAddress(byte[] address) {
    this.address = address == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(address.clone());
    return this;
  }

  public SmartContract setAddress(java.nio.ByteBuffer address) {
    this.address = org.apache.thrift.TBaseHelper.copyBinary(address);
    return this;
  }

  public void unsetAddress() {
    this.address = null;
  }

  /** Returns true if field address is set (has been assigned a value) and false otherwise */
  public boolean isSetAddress() {
    return this.address != null;
  }

  public void setAddressIsSet(boolean value) {
    if (!value) {
      this.address = null;
    }
  }

  public byte[] getDeployer() {
    setDeployer(org.apache.thrift.TBaseHelper.rightSize(deployer));
    return deployer == null ? null : deployer.array();
  }

  public java.nio.ByteBuffer bufferForDeployer() {
    return org.apache.thrift.TBaseHelper.copyBinary(deployer);
  }

  public SmartContract setDeployer(byte[] deployer) {
    this.deployer = deployer == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(deployer.clone());
    return this;
  }

  public SmartContract setDeployer(java.nio.ByteBuffer deployer) {
    this.deployer = org.apache.thrift.TBaseHelper.copyBinary(deployer);
    return this;
  }

  public void unsetDeployer() {
    this.deployer = null;
  }

  /** Returns true if field deployer is set (has been assigned a value) and false otherwise */
  public boolean isSetDeployer() {
    return this.deployer != null;
  }

  public void setDeployerIsSet(boolean value) {
    if (!value) {
      this.deployer = null;
    }
  }

  public SmartContractDeploy getSmartContractDeploy() {
    return this.smartContractDeploy;
  }

  public SmartContract setSmartContractDeploy(SmartContractDeploy smartContractDeploy) {
    this.smartContractDeploy = smartContractDeploy;
    return this;
  }

  public void unsetSmartContractDeploy() {
    this.smartContractDeploy = null;
  }

  /** Returns true if field smartContractDeploy is set (has been assigned a value) and false otherwise */
  public boolean isSetSmartContractDeploy() {
    return this.smartContractDeploy != null;
  }

  public void setSmartContractDeployIsSet(boolean value) {
    if (!value) {
      this.smartContractDeploy = null;
    }
  }

  public byte[] getObjectState() {
    setObjectState(org.apache.thrift.TBaseHelper.rightSize(objectState));
    return objectState == null ? null : objectState.array();
  }

  public java.nio.ByteBuffer bufferForObjectState() {
    return org.apache.thrift.TBaseHelper.copyBinary(objectState);
  }

  public SmartContract setObjectState(byte[] objectState) {
    this.objectState = objectState == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(objectState.clone());
    return this;
  }

  public SmartContract setObjectState(java.nio.ByteBuffer objectState) {
    this.objectState = org.apache.thrift.TBaseHelper.copyBinary(objectState);
    return this;
  }

  public void unsetObjectState() {
    this.objectState = null;
  }

  /** Returns true if field objectState is set (has been assigned a value) and false otherwise */
  public boolean isSetObjectState() {
    return this.objectState != null;
  }

  public void setObjectStateIsSet(boolean value) {
    if (!value) {
      this.objectState = null;
    }
  }

  public long getCreateTime() {
    return this.createTime;
  }

  public SmartContract setCreateTime(long createTime) {
    this.createTime = createTime;
    setCreateTimeIsSet(true);
    return this;
  }

  public void unsetCreateTime() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __CREATETIME_ISSET_ID);
  }

  /** Returns true if field createTime is set (has been assigned a value) and false otherwise */
  public boolean isSetCreateTime() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __CREATETIME_ISSET_ID);
  }

  public void setCreateTimeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __CREATETIME_ISSET_ID, value);
  }

  public int getTransactionsCount() {
    return this.transactionsCount;
  }

  public SmartContract setTransactionsCount(int transactionsCount) {
    this.transactionsCount = transactionsCount;
    setTransactionsCountIsSet(true);
    return this;
  }

  public void unsetTransactionsCount() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TRANSACTIONSCOUNT_ISSET_ID);
  }

  /** Returns true if field transactionsCount is set (has been assigned a value) and false otherwise */
  public boolean isSetTransactionsCount() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TRANSACTIONSCOUNT_ISSET_ID);
  }

  public void setTransactionsCountIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TRANSACTIONSCOUNT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case ADDRESS:
      if (value == null) {
        unsetAddress();
      } else {
        if (value instanceof byte[]) {
          setAddress((byte[])value);
        } else {
          setAddress((java.nio.ByteBuffer)value);
        }
      }
      break;

    case DEPLOYER:
      if (value == null) {
        unsetDeployer();
      } else {
        if (value instanceof byte[]) {
          setDeployer((byte[])value);
        } else {
          setDeployer((java.nio.ByteBuffer)value);
        }
      }
      break;

    case SMART_CONTRACT_DEPLOY:
      if (value == null) {
        unsetSmartContractDeploy();
      } else {
        setSmartContractDeploy((SmartContractDeploy)value);
      }
      break;

    case OBJECT_STATE:
      if (value == null) {
        unsetObjectState();
      } else {
        if (value instanceof byte[]) {
          setObjectState((byte[])value);
        } else {
          setObjectState((java.nio.ByteBuffer)value);
        }
      }
      break;

    case CREATE_TIME:
      if (value == null) {
        unsetCreateTime();
      } else {
        setCreateTime((java.lang.Long)value);
      }
      break;

    case TRANSACTIONS_COUNT:
      if (value == null) {
        unsetTransactionsCount();
      } else {
        setTransactionsCount((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ADDRESS:
      return getAddress();

    case DEPLOYER:
      return getDeployer();

    case SMART_CONTRACT_DEPLOY:
      return getSmartContractDeploy();

    case OBJECT_STATE:
      return getObjectState();

    case CREATE_TIME:
      return getCreateTime();

    case TRANSACTIONS_COUNT:
      return getTransactionsCount();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ADDRESS:
      return isSetAddress();
    case DEPLOYER:
      return isSetDeployer();
    case SMART_CONTRACT_DEPLOY:
      return isSetSmartContractDeploy();
    case OBJECT_STATE:
      return isSetObjectState();
    case CREATE_TIME:
      return isSetCreateTime();
    case TRANSACTIONS_COUNT:
      return isSetTransactionsCount();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof SmartContract)
      return this.equals((SmartContract)that);
    return false;
  }

  public boolean equals(SmartContract that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_address = true && this.isSetAddress();
    boolean that_present_address = true && that.isSetAddress();
    if (this_present_address || that_present_address) {
      if (!(this_present_address && that_present_address))
        return false;
      if (!this.address.equals(that.address))
        return false;
    }

    boolean this_present_deployer = true && this.isSetDeployer();
    boolean that_present_deployer = true && that.isSetDeployer();
    if (this_present_deployer || that_present_deployer) {
      if (!(this_present_deployer && that_present_deployer))
        return false;
      if (!this.deployer.equals(that.deployer))
        return false;
    }

    boolean this_present_smartContractDeploy = true && this.isSetSmartContractDeploy();
    boolean that_present_smartContractDeploy = true && that.isSetSmartContractDeploy();
    if (this_present_smartContractDeploy || that_present_smartContractDeploy) {
      if (!(this_present_smartContractDeploy && that_present_smartContractDeploy))
        return false;
      if (!this.smartContractDeploy.equals(that.smartContractDeploy))
        return false;
    }

    boolean this_present_objectState = true && this.isSetObjectState();
    boolean that_present_objectState = true && that.isSetObjectState();
    if (this_present_objectState || that_present_objectState) {
      if (!(this_present_objectState && that_present_objectState))
        return false;
      if (!this.objectState.equals(that.objectState))
        return false;
    }

    boolean this_present_createTime = true;
    boolean that_present_createTime = true;
    if (this_present_createTime || that_present_createTime) {
      if (!(this_present_createTime && that_present_createTime))
        return false;
      if (this.createTime != that.createTime)
        return false;
    }

    boolean this_present_transactionsCount = true;
    boolean that_present_transactionsCount = true;
    if (this_present_transactionsCount || that_present_transactionsCount) {
      if (!(this_present_transactionsCount && that_present_transactionsCount))
        return false;
      if (this.transactionsCount != that.transactionsCount)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetAddress()) ? 131071 : 524287);
    if (isSetAddress())
      hashCode = hashCode * 8191 + address.hashCode();

    hashCode = hashCode * 8191 + ((isSetDeployer()) ? 131071 : 524287);
    if (isSetDeployer())
      hashCode = hashCode * 8191 + deployer.hashCode();

    hashCode = hashCode * 8191 + ((isSetSmartContractDeploy()) ? 131071 : 524287);
    if (isSetSmartContractDeploy())
      hashCode = hashCode * 8191 + smartContractDeploy.hashCode();

    hashCode = hashCode * 8191 + ((isSetObjectState()) ? 131071 : 524287);
    if (isSetObjectState())
      hashCode = hashCode * 8191 + objectState.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(createTime);

    hashCode = hashCode * 8191 + transactionsCount;

    return hashCode;
  }

  @Override
  public int compareTo(SmartContract other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetAddress()).compareTo(other.isSetAddress());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddress()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.address, other.address);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetDeployer()).compareTo(other.isSetDeployer());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeployer()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deployer, other.deployer);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSmartContractDeploy()).compareTo(other.isSetSmartContractDeploy());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSmartContractDeploy()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.smartContractDeploy, other.smartContractDeploy);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetObjectState()).compareTo(other.isSetObjectState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetObjectState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.objectState, other.objectState);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetCreateTime()).compareTo(other.isSetCreateTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCreateTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.createTime, other.createTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTransactionsCount()).compareTo(other.isSetTransactionsCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTransactionsCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.transactionsCount, other.transactionsCount);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("SmartContract(");
    boolean first = true;

    sb.append("address:");
    if (this.address == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.address, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("deployer:");
    if (this.deployer == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.deployer, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("smartContractDeploy:");
    if (this.smartContractDeploy == null) {
      sb.append("null");
    } else {
      sb.append(this.smartContractDeploy);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("objectState:");
    if (this.objectState == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.objectState, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("createTime:");
    sb.append(this.createTime);
    first = false;
    if (!first) sb.append(", ");
    sb.append("transactionsCount:");
    sb.append(this.transactionsCount);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (address == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'address' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (smartContractDeploy != null) {
      smartContractDeploy.validate();
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SmartContractStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SmartContractStandardScheme getScheme() {
      return new SmartContractStandardScheme();
    }
  }

  private static class SmartContractStandardScheme extends org.apache.thrift.scheme.StandardScheme<SmartContract> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SmartContract struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ADDRESS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.address = iprot.readBinary();
              struct.setAddressIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DEPLOYER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.deployer = iprot.readBinary();
              struct.setDeployerIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SMART_CONTRACT_DEPLOY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.smartContractDeploy = new SmartContractDeploy();
              struct.smartContractDeploy.read(iprot);
              struct.setSmartContractDeployIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // OBJECT_STATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.objectState = iprot.readBinary();
              struct.setObjectStateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // CREATE_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.createTime = iprot.readI64();
              struct.setCreateTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // TRANSACTIONS_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.transactionsCount = iprot.readI32();
              struct.setTransactionsCountIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SmartContract struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.address != null) {
        oprot.writeFieldBegin(ADDRESS_FIELD_DESC);
        oprot.writeBinary(struct.address);
        oprot.writeFieldEnd();
      }
      if (struct.deployer != null) {
        oprot.writeFieldBegin(DEPLOYER_FIELD_DESC);
        oprot.writeBinary(struct.deployer);
        oprot.writeFieldEnd();
      }
      if (struct.smartContractDeploy != null) {
        oprot.writeFieldBegin(SMART_CONTRACT_DEPLOY_FIELD_DESC);
        struct.smartContractDeploy.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.objectState != null) {
        oprot.writeFieldBegin(OBJECT_STATE_FIELD_DESC);
        oprot.writeBinary(struct.objectState);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(CREATE_TIME_FIELD_DESC);
      oprot.writeI64(struct.createTime);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TRANSACTIONS_COUNT_FIELD_DESC);
      oprot.writeI32(struct.transactionsCount);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SmartContractTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SmartContractTupleScheme getScheme() {
      return new SmartContractTupleScheme();
    }
  }

  private static class SmartContractTupleScheme extends org.apache.thrift.scheme.TupleScheme<SmartContract> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SmartContract struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeBinary(struct.address);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetDeployer()) {
        optionals.set(0);
      }
      if (struct.isSetSmartContractDeploy()) {
        optionals.set(1);
      }
      if (struct.isSetObjectState()) {
        optionals.set(2);
      }
      if (struct.isSetCreateTime()) {
        optionals.set(3);
      }
      if (struct.isSetTransactionsCount()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetDeployer()) {
        oprot.writeBinary(struct.deployer);
      }
      if (struct.isSetSmartContractDeploy()) {
        struct.smartContractDeploy.write(oprot);
      }
      if (struct.isSetObjectState()) {
        oprot.writeBinary(struct.objectState);
      }
      if (struct.isSetCreateTime()) {
        oprot.writeI64(struct.createTime);
      }
      if (struct.isSetTransactionsCount()) {
        oprot.writeI32(struct.transactionsCount);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SmartContract struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.address = iprot.readBinary();
      struct.setAddressIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.deployer = iprot.readBinary();
        struct.setDeployerIsSet(true);
      }
      if (incoming.get(1)) {
        struct.smartContractDeploy = new SmartContractDeploy();
        struct.smartContractDeploy.read(iprot);
        struct.setSmartContractDeployIsSet(true);
      }
      if (incoming.get(2)) {
        struct.objectState = iprot.readBinary();
        struct.setObjectStateIsSet(true);
      }
      if (incoming.get(3)) {
        struct.createTime = iprot.readI64();
        struct.setCreateTimeIsSet(true);
      }
      if (incoming.get(4)) {
        struct.transactionsCount = iprot.readI32();
        struct.setTransactionsCountIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

