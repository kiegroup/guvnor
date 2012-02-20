/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.server.rules;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.Cheese;

public class SomeFact {
    private static final long serialVersionUID = 510l;
    private String            name;
    private String            likes;
    private String            hair;
    private int               age;
    private char              sex;
    private boolean           alive;
    private String            status;
    private Cheese            cheese;
    private Date              date;
    private Boolean           dead;
    private List<SomeFact>    factList         = new ArrayList<SomeFact>();
    private List<String>      factListString   = new ArrayList<String>();

    private EnumClass         anEnum;

    private byte              primitiveByte;
    private double            primitiveDouble;
    private float             primitiveFloat;
    private int               primitiveInteger;
    private long              primitiveLong;
    private short             primitiveShort;

    private BigDecimal        objectBigDecimal;
    private BigInteger        objectBigInteger;
    private Byte              objectByte;
    private Double            objectDouble;
    private Float             objectFloat;
    private Integer           objectInteger;
    private Long              objectLong;
    private Short             objectShort;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public void setCheese(Cheese cheese) {
        this.cheese = cheese;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SomeFact> getFactList() {
        return factList;
    }

    public List<String> getFactListString() {
        return factListString;
    }

    public void setFactListString(List<String> factListString) {
        this.factListString = factListString;
    }

    public void setFactList(List<SomeFact> factList) {
        this.factList = factList;
    }

    public EnumClass getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(EnumClass anEnum) {
        this.anEnum = anEnum;
    }

    public List<SomeFact> aMethod(int anInt) {
        return null;
    }

    public byte getPrimitiveByte() {
        return primitiveByte;
    }

    public void setPrimitiveByte(byte primitiveByte) {
        this.primitiveByte = primitiveByte;
    }

    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    public void setPrimitiveDouble(double primitiveDouble) {
        this.primitiveDouble = primitiveDouble;
    }

    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    public void setPrimitiveFloat(float primitiveFloat) {
        this.primitiveFloat = primitiveFloat;
    }

    public int getPrimitiveInteger() {
        return primitiveInteger;
    }

    public void setPrimitiveInteger(int primitiveInteger) {
        this.primitiveInteger = primitiveInteger;
    }

    public long getPrimitiveLong() {
        return primitiveLong;
    }

    public void setPrimitiveLong(long primitiveLong) {
        this.primitiveLong = primitiveLong;
    }

    public short getPrimitiveShort() {
        return primitiveShort;
    }

    public void setPrimitiveShort(short primitiveShort) {
        this.primitiveShort = primitiveShort;
    }

    public BigDecimal getObjectBigDecimal() {
        return objectBigDecimal;
    }

    public void setObjectBigDecimal(BigDecimal objectBigDecimal) {
        this.objectBigDecimal = objectBigDecimal;
    }

    public BigInteger getObjectBigInteger() {
        return objectBigInteger;
    }

    public void setObjectBigInteger(BigInteger objectBigInteger) {
        this.objectBigInteger = objectBigInteger;
    }

    public Byte getObjectByte() {
        return objectByte;
    }

    public void setObjectByte(Byte objectByte) {
        this.objectByte = objectByte;
    }

    public Double getObjectDouble() {
        return objectDouble;
    }

    public void setObjectDouble(Double objectDouble) {
        this.objectDouble = objectDouble;
    }

    public Float getObjectFloat() {
        return objectFloat;
    }

    public void setObjectFloat(Float objectFloat) {
        this.objectFloat = objectFloat;
    }

    public Integer getObjectInteger() {
        return objectInteger;
    }

    public void setObjectInteger(Integer objectInteger) {
        this.objectInteger = objectInteger;
    }

    public Long getObjectLong() {
        return objectLong;
    }

    public void setObjectLong(Long objectLong) {
        this.objectLong = objectLong;
    }

    public Short getObjectShort() {
        return objectShort;
    }

    public void setObjectShort(Short objectShort) {
        this.objectShort = objectShort;
    }

}
