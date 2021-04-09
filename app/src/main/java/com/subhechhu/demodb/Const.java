package com.subhechhu.demodb;

public class Const {

    public static String getType(int type) {
        switch (type) {
            case 0:
                return " Kg";
            case 1:
                return " L";
            case 2:
                return " Item(s)";
        }
        return "";
    }
}
