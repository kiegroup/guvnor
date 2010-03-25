package org.drools.factconstraint.model;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class Person {
    private String name;
    private int age;
    private double salary;

    public Person(String name, int age,double salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    

    
}
