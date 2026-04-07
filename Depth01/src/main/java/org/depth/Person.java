package org.depth;

// 클래스 실습
public class Person {

    // 인스턴스 변수
    String name;
    int age;

    // 클래스 변수
    static int personCount = 0;

    // 인스턴스 메소드
    public void introduce(){
        System.out.println("이름:"+name);
        System.out.println("나이:"+age);
    }

    // 클래스 메소드
    public static void printPersonCount(){
        System.out.println(personCount);
    }

    // enum 열거형 실습
    public enum Gender{
        MALE,
        FEMALE
    }
    Gender gender;
    public void setGender(Gender gender){
        this.gender = gender;
    }
}
