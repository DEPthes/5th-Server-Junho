package org.depth;



public class Main {
    public static void main(String[] args){
        //형변환, 삼항연산자 실습
        int x = 3;
        int y = 5;
        int max = (x>y)?x:y;
        System.out.println(max);

        double z = (double) y;

        //메쏘드 오버로딩 실습
        _OverLoading oL = new _OverLoading();
        System.out.println(oL.add(1,2));
        System.out.println(oL.add(1,2,3));
        System.out.println(oL.add(1.1,y));

        // 다향성과 메소드오버라이딩 실습
        Person student = new Student();
        student.introduce();

        //클래스 실습
        Person person = new Person();
        person.name = "철수";
        person.age = 20;
        person.introduce();
        Person.personCount = 10;
        Person.printPersonCount();

        // enum 열거형 실습
        person.setGender(person.gender.MALE);
        switch (person.gender){
            case MALE: System.out.println("남자");break;
            case FEMALE: System.out.println("여자");break;
        }

    }
}

